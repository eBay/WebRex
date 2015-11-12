/*
    Copyright [2015-2016] eBay Software Foundation

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.ebayopensource.webrex.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ebayopensource.webrex.resource.ResourceModel.Slot;
import com.ebayopensource.webrex.resource.api.IAggregatedResource;
import com.ebayopensource.webrex.resource.api.IExternalResource;
import com.ebayopensource.webrex.resource.api.IInlineResource;
import com.ebayopensource.webrex.resource.api.IResource;

public class ResourceAggregator {

   private ResourceModel m_model;

   public ResourceAggregator(ResourceModel model) {
      m_model = model;
   }

   public List<String> getActiveSlotIds(String type) {
      List<Slot> slots = m_model.getSlots(type);

      List<String> list = new ArrayList<String>();
      for (Slot slot : slots) {
         if (slot.isActive()) {
            list.add(slot.getId());
         }
      }

      return list;
   }

   public List<IResource> getAfterSlotResources(String slotId, String slotType) {
      Map<String, List<IResource>> afters = m_model.getSlotAfterResources(slotId, slotType);
      if (!afters.isEmpty()) {
         List<IResource> list = new ArrayList<IResource>(3);
         for (Entry<String, List<IResource>> entry : afters.entrySet()) {
            IAggregatedResource aggResource = ResourceFactory.createAggregatedResource(entry.getValue());
            ((AggregatedResource) aggResource).setAggregationId(entry.getKey());
            list.add(aggResource);
         }

         return list;
      }

      return Collections.emptyList();
   }

   public List<IResource> getBeforeSlotResources(String slotId, String slotType) {
      Map<String, List<IResource>> befores = m_model.getSlotBeforeResources(slotId, slotType);
      if (!befores.isEmpty()) {
         List<IResource> list = new ArrayList<IResource>(3);
         for (Entry<String, List<IResource>> entry : befores.entrySet()) {
            IAggregatedResource aggResource = ResourceFactory.createAggregatedResource(entry.getValue());
            ((AggregatedResource) aggResource).setAggregationId(entry.getKey());
            list.add(aggResource);
         }

         return list;
      }

      return Collections.emptyList();
   }

   public ResourceModel getModel() {
      return m_model;
   }

   public IResource getResourceOutput(IResource resource) {
      boolean needRender = m_model.isResourceRenderable(resource);

      if (needRender) {
         return resource;
      } else {
         return null;
      }
   }

   public IResource getSlotResources(String slotId, String type) {
      List<IResource> resources = getSlotResourcesWithGroup(slotId, type);

      if (resources == null || resources.isEmpty()) {
         return null;
      }

      return resources.get(0);
   }

   public List<IResource> getSlotResourcesWithGroup(String slotId, String type) {
      List<IResource> resources = m_model.getSlotResources(slotId, type);

      if (resources.isEmpty()) {
         return Collections.emptyList();
      }

      List<IResource> aggResources = split(resources, slotId);

      return aggResources;
   }

   public boolean isRegisteredResource(IResource resource) {
      List<Slot> slots = m_model.getSlots(resource.getUrn().getType());
      for (Slot slot : slots) {
         if (slot.hasResource(resource)) {
            return true;
         }
      }
      return false;
   }

   public boolean registerResource(IResource resource, String target) {
      return m_model.registerResource(resource, target);
   }

   /**
    * the method for backward compatibility
    * @param target
    * @param resource
    * @return
    */
   public boolean registerResource(String target, IResource resource) {
      return m_model.registerResource(resource, target);
   }

   public void registerResource(String slotId, IResource resource, INSERTION position) {
      Slot slot = m_model.getSlot(slotId, resource.getUrn().getType());
      if (slot != null) {
         if (position == INSERTION.AFTER) {
            slot.addAfterSlot(slotId + "-after", resource);
         } else {
            slot.addBeforeSlot(slotId + "-before", resource);
         }
      }
   }

   public boolean registerSlot(String slotId, String slotType) {
      return m_model.registerSlot(slotId, slotType);
   }

   public void setDeferJsSlots(List<String> slotIds) {
      for (String slotId : slotIds) {
         Slot slot = m_model.getOrCreateSlot(slotId, ResourceTypeConstants.JS);
         slot.setDeferred(true);
      }
   }

   private List<IResource> split(List<IResource> resources, String aggId) {
      List<IResource> splittedResources = new ArrayList<IResource>();
      List<IResource> collectedResources = new ArrayList<IResource>(resources.size());

      int state = -1; // -1, 0, 1, 2
      int laststate = -1;
      for (IResource resource : resources) {
         if (resource instanceof IExternalResource) {
            IExternalResource extResource = (IExternalResource) resource;
            if (!extResource.canAggregate()) {
               state = 2;
            } else {
               state = 0;
            }
         } else if (resource instanceof IInlineResource) {
            state = 1;
         } else {
            state = 0; //normal
         }

         if (laststate == -1) {
            laststate = state;
         }

         //special handling for externalized resource
         if (state == 2) {
            if (!collectedResources.isEmpty()) {
               IAggregatedResource aggResources = ResourceFactory.createAggregatedResource(collectedResources);
               aggResources.setAggregationId(aggId);
               splittedResources.add(aggResources);
               collectedResources = new ArrayList<IResource>(128);
            }

            splittedResources.add(resource);
            state = -1;
            laststate = -1;
            continue;
         }

         if (laststate != state) {
            IAggregatedResource aggResources = ResourceFactory.createAggregatedResource(collectedResources);
            aggResources.setAggregationId(aggId);
            splittedResources.add(aggResources);
            laststate = state;
            collectedResources = new ArrayList<IResource>(128);
         }

         collectedResources.add(resource);
      }

      if (laststate == state && state != -1) {
         IAggregatedResource aggResources = ResourceFactory.createAggregatedResource(collectedResources);
         aggResources.setAggregationId(aggId);
         splittedResources.add(aggResources);
      }

      return splittedResources;
   }

   public enum INSERTION {
      BEFORE, AFTER
   }
}
