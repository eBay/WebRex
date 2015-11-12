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

import java.util.Collections;
import java.util.Map;

import com.ebayopensource.webrex.logging.ILogger;
import com.ebayopensource.webrex.logging.LoggerFactory;
import com.ebayopensource.webrex.resource.FlashResource;
import com.ebayopensource.webrex.resource.ResourceRuntimeContext;
import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.api.IResourceContext;
import com.ebayopensource.webrex.resource.spi.IResourceTagRenderer;
import com.ebayopensource.webrex.resource.tag.ITag;
import com.ebayopensource.webrex.resource.tag.ITagModel;

public class FlashTagRenderer implements IResourceTagRenderer {
   private static ILogger s_logger = LoggerFactory.getLogger(FlashTagRenderer.class);

   @Override
   public String render(ITag tag, IResource resource) {
      if (resource instanceof FlashResource) {
         IResourceContext context = ResourceRuntimeContext.ctx().createResourceContext();

         //check and override the secure field
         Boolean secure = (Boolean) (tag.getModel().getAttributes().get("secure"));
         if(secure != null) {
            context.setSecure(secure);
            tag.getModel().getAttributes().remove("secure");
         }

         String url = resource.getUrl(context);

         if (url != null && url.length() > 0) {
            ITagModel model = tag.getModel();
            Map<String, Object> attributes = model.getAttributes();
            FlashResource flashResource = (FlashResource) resource;

            StringBuilder sb = new StringBuilder(url == null ? 0 : url.length() + 64);

            sb.append("<object classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000\"");

            if (attributes == null) {
               attributes = Collections.emptyMap();
            }

            String codebaseUrl = "http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=11,1,102,62";
            sb.append(" codebase=\"").append(codebaseUrl).append('"');

            Object width = attributes.get("width");
            Object height = attributes.get("height");

            if (width == null && flashResource.getWidth() >= 0) {
               width = flashResource.getWidth();
            }
            sb.append(" width=\"").append(width).append('"');

            if (height == null && flashResource.getHeight() >= 0) {
               height = flashResource.getHeight();
            }
            sb.append(" height=\"").append(height).append('"');

            Object id = attributes.get("id");
            if (id != null) {
               sb.append(" id=\"").append(id).append('"');
            }
            sb.append(">");
            sb.append("<param name=\"movie\"").append(" value=\"").append(url).append("\"/>");

            for (Map.Entry<String, Object> e : attributes.entrySet()) {
               String key = e.getKey();
               if (!"value".equalsIgnoreCase(key) && !"id".equalsIgnoreCase(key)) {
                  sb.append("<param name=\"").append(key).append("\" value=\"").append(e.getValue()).append('"')
                        .append("/>");
               }
            }

            sb.append("<embed src=\"").append(url).append('"');

            sb.append(" width=\"").append(width).append('"');
            sb.append(" height=\"").append(height).append('"');

            for (Map.Entry<String, Object> e : attributes.entrySet()) {
               String key = e.getKey();
               if (!"value".equalsIgnoreCase(key)) {
                  sb.append(' ').append(key).append("=\"").append(e.getValue()).append('"');
               }
            }

            sb.append(" type=\"application/x-shockwave-flash\" ");
            sb.append(" pluginspage=\"");

            String pluginspageUrl = "http://www.macromedia.com/go/getflashplayer";
            sb.append(pluginspageUrl).append("\">");
            sb.append("</embed></object>");

            return sb.toString();
         } else {
            s_logger.warn(String.format("Can't get flash resource(%s) url.", resource.getUrn().toString()));
         }
      }

      return "";
   }
}
