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

import com.ebayopensource.webrex.resource.ResourceBundleConfig;
import com.ebayopensource.webrex.resource.ResourceFactory;
import com.ebayopensource.webrex.resource.ResourceRuntimeContext;
import com.ebayopensource.webrex.resource.api.IAggregatedResource;
import com.ebayopensource.webrex.resource.api.IResource;

public class BundleTag extends ResourceTag {

   @Override
   public IResource build() {
      String bundleId = (String) getModel().getAttributes().get("id");

      if (bundleId != null) {
         ResourceBundleConfig config = ResourceRuntimeContext.ctx().getConfig().getResourceBundleConfig();
         List<IResource> resources = config.getAppBundles().get(bundleId);

         if (resources != null && !resources.isEmpty()) {
            IAggregatedResource aggResource = ResourceFactory.createAggregatedResource(resources);
            aggResource.setAggregationId(bundleId);
            return aggResource;
         }
      }

      return null;
   }
}
