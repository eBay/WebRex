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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ebayopensource.webrex.resource.api.IAggregatedResource;
import com.ebayopensource.webrex.resource.api.IResource;

public class ResourceBundleConfig {
   //type ==> (bundle id => list of resources)
   private Map<String, Map<String, List<IResource>>> m_sysBundles = new HashMap<String, Map<String, List<IResource>>>();

   private Map<String, List<IResource>> m_appBundles = new LinkedHashMap<String, List<IResource>>();

   //urn => bundle id mapping to fasten urn/id lookup
   private Map<String, String> m_sysUrnIdMapping = new HashMap<String, String>();

   private boolean m_systemBundleEnabled = true;

   public void addBundle(String id, List<IResource> resources, boolean isSystemBundle) {
      if (isSystemBundle) {
         String resType = resources.get(0).getUrn().getType();
         Map<String, List<IResource>> map = m_sysBundles.get(resType);
         if (map == null) {
            map = new LinkedHashMap<String, List<IResource>>();
            m_sysBundles.put(resType, map);
         }
         map.put(id, resources);
      } else {
         m_appBundles.put(id, resources);
      }
   }

   public Map<String, List<IResource>> getAppBundles() {
      return m_appBundles;
   }

   public String getSysBundleId(String urn) {
      return m_sysUrnIdMapping.get(urn);
   }

   public Map<String, List<IResource>> getSysBundles(String type) {
      if (m_systemBundleEnabled) {
         return m_sysBundles.get(type);
      }
      return null;
   }

   public void init() {
      //externalize bundles
      Collection<Map<String, List<IResource>>> sysValues = m_sysBundles.values();
      for (Map<String, List<IResource>> value : sysValues) {
         for (Entry<String, List<IResource>> entry : value.entrySet()) {
            String id = entry.getKey();
            List<IResource> resources = entry.getValue();

            IAggregatedResource aggResoure = ResourceFactory.createAggregatedResource(resources);
            if (aggResoure != null) {
               aggResoure.setAggregationId(id);

               //trigger resource externalization with default env
               aggResoure.getUrl(ResourceRuntimeContext.ctx().getResourceContext());
            }
         }
      }

      for (Entry<String, List<IResource>> entry : m_appBundles.entrySet()) {
         String id = entry.getKey();
         List<IResource> resources = entry.getValue();

         IAggregatedResource aggResoure = ResourceFactory.createAggregatedResource(resources);
         if (aggResoure != null) {
            aggResoure.setAggregationId(id);

            //trigger resource externalization with default env
            aggResoure.getUrl(ResourceRuntimeContext.ctx().getResourceContext());
         }
      }

      //rebuild system urn/id cache
      m_sysUrnIdMapping.clear();
      for (Map<String, List<IResource>> value : sysValues) {
         for (Entry<String, List<IResource>> entry : value.entrySet()) {
            String id = entry.getKey();
            List<IResource> resources = entry.getValue();

            for (IResource resource : resources) {
               m_sysUrnIdMapping.put(resource.getUrn().toString(), id);
            }
         }
      }
   }

   public boolean isSystemBundleEnabled() {
      return m_systemBundleEnabled;
   }

   public void setSystemBundleEnabled(boolean systemBundleEnabled) {
      m_systemBundleEnabled = systemBundleEnabled;
   }
}
