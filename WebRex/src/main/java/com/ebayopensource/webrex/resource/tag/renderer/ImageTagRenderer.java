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
import com.ebayopensource.webrex.resource.ImageResource;
import com.ebayopensource.webrex.resource.ResourceRuntimeContext;
import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.api.IResourceContext;
import com.ebayopensource.webrex.resource.spi.IResourceTagRenderer;
import com.ebayopensource.webrex.resource.tag.ITag;
import com.ebayopensource.webrex.resource.tag.ITagEnv.TagOutputType;

public class ImageTagRenderer implements IResourceTagRenderer {
   private static ILogger s_logger = LoggerFactory.getLogger(ImageTagRenderer.class);

   @Override
   public String render(ITag tag, IResource resource) {
      if (resource instanceof ImageResource) {
         IResourceContext context = ResourceRuntimeContext.ctx().createResourceContext();

         //check and override the secure field
         Boolean secure = (Boolean) (tag.getModel().getAttributes().get("secure"));
         if(secure != null) {
            context.setSecure(secure);
            tag.getModel().getAttributes().remove("secure");
         }

         String url = resource.getUrl(context);
         if (url != null && !url.isEmpty()) {
            Map<String, Object> attributes = tag.getModel().getAttributes();
            ImageResource imgResource = (ImageResource) resource;

            StringBuilder sb = new StringBuilder(url.length() + 64);
            sb.append("<img");

            if (attributes == null) {
               attributes = Collections.emptyMap();
            }

            sb.append(" src=\"").append(url).append('"');

            Object width = attributes.get("width");
            if (width == null && imgResource.getWidth() >= 0) {
               sb.append(" width=\"").append(imgResource.getWidth()).append('"');
            }

            Object height = attributes.get("height");
            if (height == null && imgResource.getHeight() >= 0) {
               sb.append(" height=\"").append(imgResource.getHeight()).append('"');
            }

            for (Map.Entry<String, Object> e : attributes.entrySet()) {
               String key = e.getKey();
               if (!"src".equalsIgnoreCase(key)) {
                  sb.append(' ').append(key).append("=\"").append(e.getValue()).append('"');
               }
            }

            if (tag.getEnv().getOutputType() == TagOutputType.html) {
               sb.append(">");
            } else {
               sb.append("/>");
            }

            return sb.toString();
         } else {
            s_logger.warn(String.format("Can't get image resource(%s) url.", resource.getUrn()));
         }
      }

      return "";
   }

}
