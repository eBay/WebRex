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

package com.ebayopensource.webrex.resource.impl;

import java.util.List;

import com.ebayopensource.webrex.resource.ResourceModel.Slot;
import com.ebayopensource.webrex.resource.ResourceAggregator;
import com.ebayopensource.webrex.resource.ResourceRuntimeContext;
import com.ebayopensource.webrex.resource.ResourceTypeConstants;
import com.ebayopensource.webrex.resource.api.IDeferRenderable;
import com.ebayopensource.webrex.resource.tag.SlotTag;

public class ResourceModelFlusher {
   private String m_jsResource;

   public String afterFlush() {
      resetResourceSlot(ResourceTypeConstants.CSS);
      resetResourceSlot(ResourceTypeConstants.JS);

      return m_jsResource;
   }

   public String beforeFlush() {
      StringBuilder cssBuilder = new StringBuilder(1024);

      prepareResourceSlot(cssBuilder, ResourceTypeConstants.CSS);
      StringBuilder jsBuilder = new StringBuilder(1024);
      prepareResourceSlot(jsBuilder, ResourceTypeConstants.JS);

      m_jsResource = jsBuilder.toString();

      return cssBuilder.toString();
   }

   private void deferRenderForFlush(StringBuilder builder, ResourceAggregator aggregator, ResourceRuntimeContext ctx,
         Slot slot) {
      String id = SlotTag.SLOT_TAG + slot.getType() + ":" + slot.getId();

      IDeferRenderable renderable = ctx.getDeferRenderable(id);
      if (renderable != null) {
         builder.append(renderable.deferRender());
      }
   }

   protected void prepareResourceSlot(StringBuilder builder, String type) {
      ResourceRuntimeContext ctx = ResourceRuntimeContext.ctx();
      ResourceAggregator aggregator = ctx.getResourceAggregator();

      List<Slot> slots = aggregator.getModel().getSlots(type);
      for (Slot slot : slots) {
         if (slot.isFlushed()) {
            if (!slot.getResources().isEmpty()) {
               deferRenderForFlush(builder, aggregator, ctx, slot);
            }
         }
      }
   }

   protected void resetResourceSlot(String type) {
      ResourceRuntimeContext ctx = ResourceRuntimeContext.ctx();
      ResourceAggregator aggregator = ctx.getResourceAggregator();
      List<Slot> slots = aggregator.getModel().getSlots(type);

      for (Slot slot : slots) {
         if (slot.isActive() && !slot.isDeferred()) {
            slot.getResources().clear();
         }
      }
   }
}
