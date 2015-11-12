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

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ebayopensource.webrex.resource.ResourceFactory;
import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.api.IResourceLibrary;
import com.ebayopensource.webrex.resource.api.IResourceRuntimeConfig;
import com.ebayopensource.webrex.resource.spi.IResourceLoader;

public abstract class BaseResourceLoader implements IResourceLoader {
   private static final String MIN_MARK = ".min";
   private static final char DOT = '.';
   private IResourceRuntimeConfig m_config;

   public BaseResourceLoader(IResourceRuntimeConfig config) {
      m_config = config;
   }

   //check path like foo.min.suffix
   private boolean isMinVariation(String path) {
      int minMarkLen = MIN_MARK.length();
      int lastDotIndex = path.lastIndexOf(DOT);
      int minIndex = path.lastIndexOf(MIN_MARK);
      if(lastDotIndex > minMarkLen  && (minIndex + minMarkLen == lastDotIndex)) {
         return MIN_MARK.equals(path.substring(lastDotIndex - minMarkLen, lastDotIndex));
      }
      return false;
      
   }
   
   private String getPathFromMinPath(String minPath) {
      int minStart  = minPath.lastIndexOf(".min");
      if(minStart <= 0 ) {
         return null;
      }
      StringBuilder pathB = new StringBuilder(minPath.substring(0, minStart));
      pathB.append(minPath.substring(minStart + 4));
      return pathB.toString();
   }
   protected void createResources(Map<String, URL> urls, IResourceLibrary libraryInfo, Map<String, List<IResource>> map) {
      Map<String, IResource> handled = new HashMap<String, IResource>();
      for (Entry<String, URL> entry : urls.entrySet()) {
         URL url = entry.getValue();
         String resourcePathWithLocale = entry.getKey();
         if(handled.containsKey(resourcePathWithLocale)) {
            continue;
         }
         
         //create resource with url, resourcePath
         IResource resource = ResourceFactory.createResource(url, resourcePathWithLocale, libraryInfo);
         handled.put(resourcePathWithLocale, resource);
         IResource normalResource = null;
         String normalPath = null;
         if(isMinVariation(resourcePathWithLocale)) {
            normalPath = getPathFromMinPath(resourcePathWithLocale);
            if(urls.containsKey(normalPath)) {
               normalResource = handled.get(normalPath);
               if(normalResource == null) {
                  URL normalUrl = urls.get(normalPath);
                  normalResource = ResourceFactory.createResource(normalUrl, normalPath, libraryInfo);
               }
               
            }
         }
         
         //the locale of this two variation should be the same
         String locale = resource.getLocale() != null ? resource.getLocale().toExternal() : null;

         List<IResource> values = map.get(locale);
         if (values == null) {
            values = new ArrayList<IResource>();
            map.put(locale, values);
         }

         values.add(resource);
         //suppose this two resources should has the same locale
         if(normalResource != null ) {
            if(!handled.containsKey(normalPath)) {
               values.add(normalResource);
               handled.put(normalPath, normalResource);
            }
            getConfig().setVariationResource(normalResource, resource);
         }
         //TODO: apply resource hotdeploy
      }
   }

   protected IResourceRuntimeConfig getConfig() {
      return m_config;
   }

}
