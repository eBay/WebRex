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

import com.ebayopensource.webrex.resource.ResourceAggregator;
import com.ebayopensource.webrex.resource.ResourceFactory;
import com.ebayopensource.webrex.resource.ResourceRuntimeContext;
import com.ebayopensource.webrex.resource.api.IDeferRenderable;
import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.api.IResourceUrn;
import com.ebayopensource.webrex.util.Markers;

/**
 * Use script tag for js, css resources
 *
 */
public class UseScriptTag extends ResourceTag {
   private static final String TARGET = "target";

   public UseScriptTag(String type) {
      super();
      getModel().setExpectedResourceType(type);
   }

   @Override
   protected IResource getResource(Object value) {
      //handle inline resource
      ITagModel model = getModel();
      if (value == null && model.getContent() != null) {
         return ResourceFactory.createInlineResource(model.getExpectedResourceType(), model.getContent());
      } else if (value instanceof String) {
         if ((((String) value).toLowerCase().startsWith("http://") || ((String) value).toLowerCase().startsWith(
               "https://"))) {
            return ResourceFactory.createExternalResource(model.getExpectedResourceType(), (String) value);
         }
      }

      return super.getResource(value);
   }

   @Override
   public String render(final IResource resource) {
      final ResourceRuntimeContext ctx = ResourceRuntimeContext.ctx();
      if (resource != null) {
         ITagModel model = getModel();
         String target = (String) model.getAttributes().get(TARGET);

         //if target not found, output result directly
         if (target == null) {
            if (ctx.getModel().registerResource(resource, null)) {
               return super.render(resource);
            } else {
               //if resource is already rendered, do nothing
               return "";
            }
         }

         final ResourceAggregator aggregator = ctx.getResourceAggregator();
         if (aggregator.registerResource(resource, target) /*&& !aggregator.isSlotActive(target)*/) { //bug?
            final IResourceUrn urn = resource.getUrn();
            String key = getClass().getSimpleName() + ":" + urn.hashCode();

            ctx.registerDeferRenderable(key, new IDeferRenderable() {
               @Override
               public String deferRender() {
                  IResource r = aggregator.getResourceOutput(resource);

                  if (r != null) {
                     return UseScriptTag.super.render(r);
                  }

                  return "";
               }
            });

            // use js marker
            return Markers.forDefer().build(key);
         }
      }

      return "";
   }

}
