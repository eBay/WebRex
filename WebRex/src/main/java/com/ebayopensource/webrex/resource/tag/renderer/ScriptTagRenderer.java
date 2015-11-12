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

package com.ebayopensource.webrex.resource.tag.renderer;

import java.util.List;
import java.util.Map;

import com.ebayopensource.webrex.logging.ILogger;
import com.ebayopensource.webrex.logging.LoggerFactory;
import com.ebayopensource.webrex.resource.InlineResource;
import com.ebayopensource.webrex.resource.ResourceRuntimeContext;
import com.ebayopensource.webrex.resource.ResourceTypeConstants;
import com.ebayopensource.webrex.resource.api.IAggregatedResource;
import com.ebayopensource.webrex.resource.api.IExternalResource;
import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.api.IResourceContext;
import com.ebayopensource.webrex.resource.spi.IResourceTagRenderer;
import com.ebayopensource.webrex.resource.tag.BundleTag;
import com.ebayopensource.webrex.resource.tag.ITag;
import com.ebayopensource.webrex.resource.tag.SlotTag;
import com.ebayopensource.webrex.util.ScriptHelper;

public class ScriptTagRenderer implements IResourceTagRenderer {

   private static final String ID = "id";

   public static final String HTML_ID = "htmlid";

   public static final String EXTERNALIZED = "externalized";

   public static final String INLINE = "inline";

   public static final String INLINE_MINIFIED = "inlineMinified";

   public static final String EXCLUDE_INLINE_MINIFIED = "excludeInlineMinified";

   public static final String RENDER_TYPE = "renderType";

   public static final String TARGET = "target";

   private static ILogger s_logger = LoggerFactory.getLogger(ScriptTagRenderer.class);

   private String m_resourceType;

   public ScriptTagRenderer(String resourceType) {
      m_resourceType = resourceType;
   }

   private void convertKnownAttributes(Map<String, Object> dynamicAttributes) {
      if (dynamicAttributes != null) {
         dynamicAttributes.remove(RENDER_TYPE);
         dynamicAttributes.remove(ID);

         Object id = dynamicAttributes.get(HTML_ID);
         if (id != null) {
            dynamicAttributes.put(ID, id);
            dynamicAttributes.remove(HTML_ID);
         }
         dynamicAttributes.remove(TARGET);
      }
   }

   @Override
   public String render(ITag tag, IResource resource) {
      String renderType = (String) tag.getModel().getAttributes().get(RENDER_TYPE);
      //do not check body content on slot tag
      boolean inline = false;
      if (renderType != null) {
         if (EXTERNALIZED.equalsIgnoreCase(renderType)) {
            inline = false;
         } else if (INLINE.equalsIgnoreCase(renderType) || INLINE_MINIFIED.equalsIgnoreCase(renderType)
               || EXCLUDE_INLINE_MINIFIED.equalsIgnoreCase(renderType)) {
            inline = true;
         } else {
            throw new RuntimeException(String.format("Unsupported renderType(%s) on the tag(%s).", renderType, tag
                  .getClass().getSimpleName()));
         }
      } else if (tag instanceof SlotTag && resource instanceof IAggregatedResource) {
         //detect inline slot
         IAggregatedResource aggResource = (IAggregatedResource) resource;
         List<IResource> aggResources = aggResource.getResources();
         inline = true;
         for (IResource r : aggResources) {
            if (!(r instanceof InlineResource)) {
               inline = false;
               break;
            }
         }
      } else {
         inline = !(tag instanceof SlotTag) && !(tag instanceof BundleTag) && tag.getModel().getValue() == null;
      }

      //convert known tag attributes
      convertKnownAttributes(tag.getModel().getAttributes());

      return renderResource(tag, resource, inline, renderType);
   }

   private String renderResource(ITag tag, IResource resource, boolean inline, String renderType) {
      Map<String, Object> dynamicAttributes = tag.getModel().getAttributes();

      IResourceContext context = ResourceRuntimeContext.ctx().createResourceContext();

      setInlineOption(context);
      
      if (!inline) {
         //check and override the secure field
         Boolean secure = (Boolean) (dynamicAttributes.get("secure"));
         if (secure != null) {
            context.setSecure(secure);
            //prevent output secure field
            dynamicAttributes.remove("secure");
         }

         String url = null;

         if (resource instanceof IExternalResource) {
            url = ((IExternalResource) resource).getExternalUrl();
         } else {
            url = resource.getUrl(context);
         }

         if (url != null) {
            if (ResourceTypeConstants.CSS.equals(m_resourceType)) {
               return ScriptHelper.createCssScript(url, dynamicAttributes, tag.getEnv().getOutputType());
            } else {
               return ScriptHelper.createJsScript(url, dynamicAttributes);
            }
         } else {
            // fall back to inline text
            s_logger
                  .warn(String.format("Can't get externalized url with resource(%s), try to fallback to inline text.",
                        resource.toString()));
            String originalContent = resource.getOriginalContent();
            if (ResourceTypeConstants.CSS.equals(m_resourceType)) {
               return ScriptHelper.createInlineCssScript(originalContent, dynamicAttributes);
            } else {
               return ScriptHelper.createInlineJsScript(originalContent, dynamicAttributes);
            }
         }
      } else {
         if (renderType != null) {
            if (renderType.equalsIgnoreCase(INLINE_MINIFIED)) {
               context.setAttribute("get_handler_content", true);
            } else if (renderType.equalsIgnoreCase(EXCLUDE_INLINE_MINIFIED)) {
               context.setAttribute("get_handler_content", false);
            }

         }
         String originalContent = resource.getContent(context);
         if (ResourceTypeConstants.CSS.equals(m_resourceType)) {
            return ScriptHelper.createInlineCssScript(originalContent, dynamicAttributes);
         } else {
            return ScriptHelper.createInlineJsScript(originalContent, dynamicAttributes);
         }
      }
   }

   private void setInlineOption(IResourceContext context) {
      String attrName ="obfuscate_inl_agg_js";
      if (ResourceTypeConstants.CSS.equals(m_resourceType)) {
         attrName ="obfuscate_inl_agg_css";
      }
      
      Object inlineMinified = context.getAttribute(attrName);
      if (inlineMinified != null && inlineMinified instanceof Boolean) {
         context.setAttribute("get_handler_content", inlineMinified);
      }
   }

}
