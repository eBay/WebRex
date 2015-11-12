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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ebayopensource.webrex.resource.api.IAggregatedResource;
import com.ebayopensource.webrex.resource.api.IInlineResource;
import com.ebayopensource.webrex.resource.api.IResource;

public class ResourceModel {
   private String m_requestUri;

   //holding slots: type ==> slots
   private Map<String, List<Slot>> m_slotMap = new HashMap<String, List<Slot>>();

   private Set<String> m_renderedUrns = new HashSet<String>(128);

   //holding resources without target: type ==> urns
   private Map<String, Set<String>> m_noSlotResUrns = new HashMap<String, Set<String>>();

   private Set<String> m_renderedSysSlotIds = new HashSet<String>();

   //type ==> resources
   private Map<String, Set<String>> m_collectedResources = new HashMap<String, Set<String>>();

   public ResourceModel() {
   }

   public ResourceModel(String requestUri) {
      m_requestUri = requestUri;
   }

   private Set<String> collectResources(String resType) {
      Set<String> allResources = new LinkedHashSet<String>(128);

      List<Slot> slots = m_slotMap.get(resType);
      collectSlotResources(allResources, slots);

      Set<String> resourcesUrns = m_noSlotResUrns.get(resType);
      if (resourcesUrns != null && !resourcesUrns.isEmpty()) {
         allResources.addAll(resourcesUrns);
      }

      //dedup rendered resource
     // allResources.removeAll(m_renderedUrns);

      return allResources;
   }

   private void collectSlotResources(Set<String> resources, List<Slot> slots) {
      if (slots != null) {
         for (Slot slot : slots) {
            if (slot.isActive()) {
               collectSlotResources(resources, slot.getBeforeSlots());
               Set<String> urns = slot.getUrns();
               //TODO: support inline resource
               resources.addAll(urns);
               collectSlotResources(resources, slot.getAfterSlots());
            }
         }
      }
   }

   /**
    * It is for resource token
    * @param resType
    * @param enabled
    */
   public void enableCollectResources(String resType, boolean enabled) {
      if (enabled) {
         m_collectedResources.put(resType, null);
      } else {
         m_collectedResources.remove(resType);
      }
   }

   public Set<String> getCollectedResource(String resType) {
      return m_collectedResources.get(resType);
   }

   Slot getOrCreateSlot(String slotId, String type) {
      List<Slot> slots = m_slotMap.get(type);

      if (slots == null) {
         slots = new ArrayList<ResourceModel.Slot>();
         m_slotMap.put(type, slots);
      }

      for (Slot slot : slots) {
         if (slot.m_slotId.equals(slotId) && type.equals(slot.m_type)) {
            return slot;
         }
      }

      //default creation slot is not active
      Slot slot = new Slot(slotId, type);
      slots.add(slot);
      return slot;
   }

   public String getRequestUri() {
      return m_requestUri;
   }

   public Slot getSlot(String slotId, String type) {
      List<Slot> slots = m_slotMap.get(type);

      if (slots != null) {
         for (Slot slot : slots) {
            if (slot.m_slotId.equals(slotId) && slot.m_type.equals(type)) {
               return slot;
            }
         }
      }

      return null;
   }

   public Map<String, List<IResource>> getSlotAfterResources(String slotId, String type) {
      List<Slot> slots = m_slotMap.get(type);

      if (slots != null) {
         Map<String, List<IResource>> map = null;
         for (Slot slot : slots) {
            if (slot.isActive() && slot.getType().equals(type) && slot.getId().equals(slotId)) {
               List<Slot> afters = slot.getAfterSlots();
               if (afters != null) {
                  for (Slot afterSlot : afters) {
                     String sysId = afterSlot.getId();
                     if (m_renderedSysSlotIds.add(sysId)) {
                        List<IResource> resources = getSlotResources(afterSlot);

                        if (!resources.isEmpty()) {
                           if (map == null) {
                              map = new LinkedHashMap<String, List<IResource>>();
                           }

                           map.put(sysId, resources);
                        }
                     }
                  }
               }
            }
         }

         if (map != null) {
            return map;
         }
      }

      return Collections.emptyMap();
   }

   public Map<String, List<IResource>> getSlotBeforeResources(String slotId, String type) {
      List<Slot> slots = m_slotMap.get(type);

      if (slots != null) {
         Map<String, List<IResource>> map = null;
         for (Slot slot : slots) {
            if (slot.isActive() && slot.getType().equals(type) && slot.getId().equals(slotId)) {
               List<Slot> befores = slot.getBeforeSlots();
               if (befores != null) {
                  for (Slot beforeSlot : befores) {
                     String sysId = beforeSlot.getId();
                     if (m_renderedSysSlotIds.add(sysId)) {
                        List<IResource> resources = getSlotResources(beforeSlot);

                        if (!resources.isEmpty()) {
                           if (map == null) {
                              map = new LinkedHashMap<String, List<IResource>>();
                           }

                           map.put(sysId, resources);
                        }
                     }
                  }
               }
            }
         }

         if (map != null) {
            return map;
         }
      }

      return Collections.emptyMap();
   }

   private List<IResource> getSlotResources(Slot slot) {
      slot.setFlushed(true);

      //dedup resource has been rendered before
      List<IResource> resources = slot.getResources();
      List<IResource> deDupResources = new ArrayList<IResource>(resources.size());
      for (IResource resource : resources) {
         if (m_renderedUrns.add(resource.getUrn().toString())) {
            deDupResources.add(resource);
         }
      }

      //clear resources
      slot.clear();
      return deDupResources;
   }

   /**
    * After getting the slot resource, the slot flush status will be true
    */
   public List<IResource> getSlotResources(String slotId, String type) {
      List<Slot> slots = m_slotMap.get(type);

      if (slots != null) {
         for (Slot slot : slots) {
            if (slot.isActive() && slot.getType().equals(type) && slot.getId().equals(slotId)) {
               return getSlotResources(slot);
            }
         }
      }

      return Collections.emptyList();
   }

   public List<Slot> getSlots(String type) {
      List<Slot> list = m_slotMap.get(type);
      if (list == null) {
         return Collections.emptyList();
      } else {
         return list;
      }
   }

   private boolean hasSlot(List<Slot> slots, String bundleId) {
      if (slots == null) {
         return false;
      }

      for (Slot slot : slots) {
         if (slot.getId().equalsIgnoreCase(bundleId)) {
            return true;
         }
      }

      return false;
   }

   /**
    * Try to check resource from model whether it is renderable
    * 
    * @return true means it need to render resource manually by the invoker
    */
   public boolean isResourceRenderable(IResource resource) {
      List<Slot> slots = m_slotMap.get(resource.getUrn().getType());

      if (slots != null) {
         for (Slot slot : slots) {
            if ((slot.isActive() || slot.isDeferred()) && slot.hasResource(resource)) {
               return false;
            }
         }
      }

      return !m_renderedUrns.contains(resource.getUrn().toString());
   }

   public boolean isResourceRendered(IResource sample) {
      return m_renderedUrns.contains(sample.getUrn().toString());
   }

   public void processModel() {
      //handling common slot
      ResourceBundleConfig bundleConfig = ResourceRuntimeContext.ctx().getResourceContext().getConfig()
            .getResourceBundleConfig();

      //support common js
      processSystemResources(bundleConfig, ResourceTypeConstants.JS);
      //support common css
      processSystemResources(bundleConfig, ResourceTypeConstants.CSS);

      //collect resources for resource token support
      Set<String> types = m_collectedResources.keySet();
      for (String type : types) {
         m_collectedResources.put(type, collectResources(type));
      }
   }

   private void processSystemResources(ResourceBundleConfig bundleConfig, String resType) {
      Map<String, List<IResource>> bundle = bundleConfig.getSysBundles(resType);
      if (bundle != null && !bundle.isEmpty()) {
         List<Slot> jsSlots = m_slotMap.get(resType);

         if (jsSlots != null) {
            for (Slot slot : jsSlots) {
               if (slot.isActive() || slot.isDeferred()) {
                  List<IResource> resources = slot.getResources();
                  for (IResource resource : resources) {
                     String bundleId = bundleConfig.getSysBundleId(resource.getUrn().toString());
                     if (bundleId != null && !hasSlot(slot.getBeforeSlots(), bundleId)) {
                        slot.addBeforeSlot(bundleId, bundle.get(bundleId));
                     }
                  }
               }
            }
         }
      }
   }

   public boolean registerResource(IResource resource, String slotId) {
      if(resource == null){
         return false;
      }
      
      if (slotId == null || slotId.isEmpty()) {
         //null target resource handling
         Set<String> resourceUrns = m_noSlotResUrns.get(resource.getUrn().getType());
         if (resourceUrns == null) {
            resourceUrns = new LinkedHashSet<String>(10);
            m_noSlotResUrns.put(resource.getUrn().getType(), resourceUrns);
         }

         return !m_renderedUrns.contains(resource.getUrn().toString())
               && resourceUrns.add(resource.getUrn().toString());
      } else {
         Slot slot = getOrCreateSlot(slotId, resource.getUrn().getType());
         return slot.addResource(resource);
      }
   }

   public boolean registerSlot(String slotId, String type) {
      Slot slot = getOrCreateSlot(slotId, type);

      if (slot.isActive()) {
         return false;
      } else {
         slot.setActive(true);
         return true;
      }
   }

   public void reset() {
      m_requestUri = null;
      m_slotMap.clear();
      m_renderedUrns.clear();
      m_noSlotResUrns.clear();
      m_renderedSysSlotIds.clear();
      m_collectedResources.clear();
   }

   public void setRenderedResource(IResource resource) {
      if (resource instanceof IAggregatedResource) {
         List<IResource> children = ((IAggregatedResource) resource).getResources();
         for (IResource child : children) {
            if (!(resource instanceof IInlineResource)) {
               m_renderedUrns.add(child.getUrn().toString());
            }
         }
      } else {
         if (!(resource instanceof IInlineResource)) {
            m_renderedUrns.add(resource.getUrn().toString());
         }
      }
   }

   public void setRenderedResource(String urn) {
      m_renderedUrns.add(urn);
   }

   public void setRequestUri(String requestUri) {
      m_requestUri = requestUri;
   }

   @Override
   public String toString() {
      return "ResourceModel [m_requestUri=" + m_requestUri + ", m_slots=" + m_slotMap + "]";
   }

   public static class Slot {
      private String m_slotId;

      //resource type
      private String m_type;

      private List<IResource> m_resources = new ArrayList<IResource>(128);

      private List<Slot> m_beforeSlots;

      private List<Slot> m_afterSlots;

      private boolean m_active;

      //used for progressive rendering 
      private boolean m_flushed;

      private boolean m_deferred;

      //dedup slot urns
      private Set<String> m_urns = new LinkedHashSet<String>(128);

      public Slot(String slotId, String type) {
         m_slotId = slotId;
         m_type = type;
      }

      public void addAfterSlot(String slotId, IResource resource) {
         Slot afterSlot = getAfterSlot(slotId);

         afterSlot.addResource(resource);
      }

      public void addAfterSlot(String slotId, List<IResource> list) {
         Slot afterSlot = getAfterSlot(slotId);

         if (list != null && !list.isEmpty()) {
            for (IResource resource : list) {
               afterSlot.addResource(resource);
            }
         }
      }

      public void addBeforeSlot(String slotId, IResource resource) {
         Slot beforeSlot = getBeforeSlot(slotId);

         beforeSlot.addResource(resource);
      }

      public void addBeforeSlot(String slotId, List<IResource> list) {
         Slot beforeSlot = getBeforeSlot(slotId);

         if (list != null && !list.isEmpty()) {
            for (IResource resource : list) {
               beforeSlot.addResource(resource);
            }
         }
      }

      public boolean addResource(IResource resource) {
         String urnStr = resource.getUrn().toString();
         if ((!m_urns.contains(urnStr))) {
            m_resources.add(resource);
            m_urns.add(urnStr);
            return true;
         }
         return false;
      }

      public void clear() {
         m_resources.clear();
         m_urns.clear();
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         Slot other = (Slot) obj;
         if (m_slotId == null) {
            if (other.m_slotId != null)
               return false;
         } else if (!m_slotId.equals(other.m_slotId))
            return false;
         if (m_type != other.m_type)
            return false;
         return true;
      }

      private Slot getAfterSlot(String slotId) {
         if (m_afterSlots == null) {
            m_afterSlots = new ArrayList<ResourceModel.Slot>(3);
         }

         Slot afterSlot = null;
         for (Slot slot : m_afterSlots) {
            if (slot.getId().equalsIgnoreCase(slotId)) {
               afterSlot = slot;
               break;
            }
         }

         if (afterSlot == null) {
            afterSlot = new Slot(slotId, m_type);
         }

         m_afterSlots.add(afterSlot);

         return afterSlot;
      }

      public List<Slot> getAfterSlots() {
         return m_afterSlots;
      }

      private Slot getBeforeSlot(String slotId) {
         Slot beforeSlot = null;

         if (m_beforeSlots == null) {
            m_beforeSlots = new ArrayList<ResourceModel.Slot>(3);
         }
         for (Slot slot : m_beforeSlots) {
            if (slot.getId().equalsIgnoreCase(slotId)) {
               beforeSlot = slot;
               break;
            }
         }

         if (beforeSlot == null) {
            beforeSlot = new Slot(slotId, m_type);
         }

         m_beforeSlots.add(beforeSlot);
         return beforeSlot;
      }

      public List<Slot> getBeforeSlots() {
         return m_beforeSlots;
      }

      public String getId() {
         return m_slotId;
      }

      public List<IResource> getResources() {
         return m_resources;
      }

      public String getType() {
         return m_type;
      }

      public Set<String> getUrns() {
         return m_urns;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((m_slotId == null) ? 0 : m_slotId.hashCode());
         result = prime * result + ((m_type == null) ? 0 : m_type.hashCode());
         return result;
      }

      public boolean hasResource(IResource resource) {
         return m_urns.contains(resource.getUrn().toString());
      }

      public Boolean isActive() {
         return m_active;
      }

      public boolean isDeferred() {
         return m_deferred;
      }

      public boolean isFlushed() {
         return m_flushed;
      }

      public void setActive(boolean active) {
         m_active = active;
      }

      public void setDeferred(boolean deferred) {
         m_deferred = deferred;
      }

      public void setFlushed(Boolean flushed) {
         m_flushed = flushed;
      }

      public void setResources(List<IResource> resources) {
         m_resources = resources;
      }

      @Override
      public String toString() {
         return "Slot [m_slotId=" + m_slotId + ", m_type=" + m_type + ", m_active=" + m_active + ", m_flushed="
               + m_flushed + ", m_deferred=" + m_deferred + "]";
      }
   }
}
