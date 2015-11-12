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

package com.ebayopensource.webrex.resource.tag;

import java.util.List;

import com.ebayopensource.webrex.logging.ILogger;
import com.ebayopensource.webrex.logging.LoggerFactory;
import com.ebayopensource.webrex.resource.ResourceAggregator;
import com.ebayopensource.webrex.resource.ResourceRuntimeContext;
import com.ebayopensource.webrex.resource.api.IDeferRenderable;
import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.impl.DummyResource;
import com.ebayopensource.webrex.resource.impl.ResourceMarkerProcessor;
import com.ebayopensource.webrex.util.Markers;
import com.ebayopensource.webrex.util.ScriptHelper;

public class SlotTag extends ResourceTag {
   private static ILogger s_logger = LoggerFactory.getLogger(SlotTag.class);

   private static final String ID = "id";

   private static final String SLOT = "slot";

   public static final String SLOT_TAG = "SlotTag:";

   private String m_slotType;

   public SlotTag(String type) {
      super();
      m_slotType = type;
      getModel().setExpectedResourceType(type);
   }

   @Override
   public IResource build() {
      return DummyResource.DUMMY;
   }

   private String createKey(final String slotId) {
      return SLOT_TAG + m_slotType + ":" + slotId;
   }

   @Override
   public void end() {
      super.end();

      getEnv().removePageAttribute(SLOT);
   }

   public String getSlotType() {
      return m_slotType;
   }

   @Override
   public String render(IResource resoruce) {
      final ResourceRuntimeContext ctx = ResourceRuntimeContext.ctx();

      if (resoruce != null) {
         ResourceAggregator aggregator = ctx.getResourceAggregator();
         ITagModel model = getModel();
         final String slotId = (String) model.getAttributes().get(ID);
         aggregator.registerSlot(slotId, m_slotType);

         String key = createKey(slotId);
         String marker = Markers.forDefer().build(key);

         String slotTemplate = model.getContent();
         if (slotTemplate != null && slotTemplate.length() > 0) {
            ctx.registerDeferRenderable(key, new TemplateDeferRenderable(aggregator, slotTemplate));
         } else {
            ctx.registerDeferRenderable(key, new DeferRenderable(aggregator, slotId));
         }

         return marker;
      }

      return null;
   }

   public void setSlotType(String slotType) {
      m_slotType = slotType;
   }

   @Override
   public void start() {
      super.start();

      getEnv().setPageAttribute(SLOT, new SlotTemplateModel(this));
   }

   private class DeferRenderable implements IDeferRenderable {
      private final ResourceAggregator m_aggregator;

      private final String m_slotId;

      private DeferRenderable(ResourceAggregator aggregator, String slotId) {
         m_aggregator = aggregator;
         m_slotId = slotId;
      }

      @Override
      public String deferRender() {
         StringBuilder sb = new StringBuilder(1024);

         //handle before slot
         List<IResource> beforeResources = m_aggregator.getBeforeSlotResources(m_slotId, m_slotType);
         if (beforeResources != null) {
            for (IResource ref : beforeResources) {
               sb.append(SlotTag.super.render(ref));
            }
         }

         //handle slot
         List<IResource> resources = m_aggregator.getSlotResourcesWithGroup(m_slotId, m_slotType);
         if (resources != null) {
            for (IResource resource : resources) {
               sb.append(SlotTag.super.render(resource));
            }
         }

         //handle after slot
         List<IResource> afterResources = m_aggregator.getAfterSlotResources(m_slotId, m_slotType);
         if (afterResources != null) {
            for (IResource ref : afterResources) {
               sb.append(SlotTag.super.render(ref));
            }
         }

         return sb.toString();
      }

   }

   public static class SlotTemplateModel {
      private static final String URL_SUFFIX = ":url";

      private static final String CONTENT_KEY = ":content";

      private SlotTag m_tag;

      public SlotTemplateModel(SlotTag tag) {
         m_tag = tag;
      }

      public String getContent() {
         String id = (String) m_tag.getModel().getAttributes().get(ID);
         String key = m_tag.createKey(id) + CONTENT_KEY;
         String marker = Markers.forDefer().build(key);

         ResourceRuntimeContext ctx = ResourceRuntimeContext.ctx();
         ctx.registerDeferRenderable(key, new ContentDeferRenderable(ctx, m_tag, id));

         return marker;
      }

      public String getUrl() {
         String id = (String) m_tag.getModel().getAttributes().get(ID);
         String key = m_tag.createKey(id) + URL_SUFFIX;
         String marker = Markers.forDefer().build(key);

         ResourceRuntimeContext ctx = ResourceRuntimeContext.ctx();
         ctx.registerDeferRenderable(key, new UrlDeferRenderable(ctx, m_tag, id));

         return marker;
      }

      private static final class ContentDeferRenderable implements IDeferRenderable {
         private ResourceRuntimeContext m_ctx;

         private String m_slotId;

         private SlotTag m_tag;

         private ContentDeferRenderable(ResourceRuntimeContext ctx, SlotTag tag, String slotId) {
            m_ctx = ctx;
            m_slotId = slotId;
            m_tag = tag;
         }

         @Override
         public String deferRender() {
            // JsContentDeferRenderable
            String key = m_tag.createKey(m_slotId);
            TemplateDeferRenderable templateRenderable = (TemplateDeferRenderable) m_ctx.getDeferRenderable(key);

            if (templateRenderable != null) {
               String content = templateRenderable.getContent();
               return content != null ? content : "";
            }

            return "";
         }
      }

      private static final class UrlDeferRenderable implements IDeferRenderable {
         private final ResourceRuntimeContext m_ctx;

         private final String m_slotId;

         private SlotTag m_tag;

         private UrlDeferRenderable(ResourceRuntimeContext ctx, SlotTag tag, String slotId) {
            m_ctx = ctx;
            m_slotId = slotId;
            m_tag = tag;
         }

         @Override
         public String deferRender() {
            String key = m_tag.createKey(m_slotId);
            TemplateDeferRenderable templateRenderable = (TemplateDeferRenderable) m_ctx.getDeferRenderable(key);

            if (templateRenderable != null) {
               String url = templateRenderable.getUrl();
               return url != null ? url : "";
            }
            return "";
         }
      }
   }

   private class TemplateDeferRenderable implements IDeferRenderable {
      private IResource m_resource;

      private String m_slotTemplate;

      private ResourceAggregator m_aggregator;

      private TemplateDeferRenderable(ResourceAggregator aggregator, String slotTemplate) {
         m_slotTemplate = slotTemplate;
         m_aggregator = aggregator;
      }

      @Override
      public String deferRender() {
         StringBuilder sb = new StringBuilder(1024);

         /*
         //handle before slot, don't support

         //handle slot resource
         m_resource = m_aggregator.getSlotResources((String) getModel().getAttributes().get("id"), m_slotType);

         if(m_resource != null) {
         	//handle slot template
         	StringBuilder slotTemplate = new StringBuilder(m_slotTemplate);
         	ResourceMarkerProcessor.INSTANCE.process(slotTemplate, null);
         	sb.append(ScriptHelper.createInlineJsScript(slotTemplate.toString(), getModel().getAttributes()));
         }

         //handle after slot, don't support

         return sb.toString();*/

         String slotId = (String) getModel().getAttributes().get("id");

         List<IResource> beforeResources = m_aggregator.getBeforeSlotResources(slotId, m_slotType);
         List<IResource> resources = m_aggregator.getSlotResourcesWithGroup(slotId, m_slotType);
         List<IResource> afterResources = m_aggregator.getAfterSlotResources(slotId, m_slotType);

         //handle before slot
         renderResources(sb, beforeResources);

         //TODO: refine template handling
         //handle slot, only get the first slot due to group issue
         StringBuilder dummy = new StringBuilder();
         renderResources(dummy, resources);
         int size = resources.size();
         if (size != 0) {
            if (size > 1) {
               String message = "Multiple resource bundles exists in slot(%s), only the first slot will be used in slot template.";
               s_logger.warn(String.format(message, slotId));
            }

            m_resource = resources.get(0);
            if (m_resource != null) {
               StringBuilder slotTemplate = new StringBuilder(m_slotTemplate);
               ResourceMarkerProcessor.INSTANCE.process(slotTemplate, null);

               sb.append(ScriptHelper.createInlineJsScript(slotTemplate.toString(), getModel().getAttributes()));
            }
         }

         //handle after slot
         renderResources(sb, afterResources);

         return sb.toString();
      }

      public String getContent() {
         return String.valueOf(m_resource.getContent(ResourceRuntimeContext.ctx().createResourceContext()));
      }

      public String getUrl() {
         return m_resource.getUrl(ResourceRuntimeContext.ctx().createResourceContext());
      }

      protected void renderResources(StringBuilder sb, List<IResource> beforeResources) {
         if (beforeResources != null) {
            for (IResource ref : beforeResources) {
               sb.append(SlotTag.super.render(ref));
            }
         }
      }

   }
}
