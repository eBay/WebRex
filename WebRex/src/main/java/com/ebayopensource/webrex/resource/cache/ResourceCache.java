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

package com.ebayopensource.webrex.resource.cache;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.api.IResourceLocale;
import com.ebayopensource.webrex.resource.api.IResourceUrn;

public class ResourceCache {
   private Map<CacheKey, IResource> m_cache = new ConcurrentHashMap<CacheKey, IResource>();

   public IResource getCache(IResourceUrn urn, IResourceLocale locale, boolean isSecure) {
      return m_cache.get(new CacheKey(locale, urn, isSecure));
   }

   public void putCache(IResourceUrn urn, IResourceLocale locale, boolean isSecure, IResource resource) {
      m_cache.put(new CacheKey(locale, urn, isSecure), resource);
   }

   public void putCaches(String namespace, Map<String, List<IResource>> map, boolean isSecure) {
      for (Entry<String, List<IResource>> entry : map.entrySet()) {
         String locale = entry.getKey();
         List<IResource> resources = entry.getValue();
         
         for (IResource resource : resources) {
            CacheKey key = new CacheKey(locale, resource.getUrn(), isSecure);
            m_cache.put(key, resource);
         }
      }
   }

   public static class CacheKey {

      private String m_locale;

      private IResourceUrn m_urn;
      
      private boolean m_isSecure;

      private int m_hashcode = 0;

      public CacheKey(IResourceLocale locale, IResourceUrn urn, boolean isSecure) {
         this(locale == null ? null : locale.toExternal(), urn, isSecure);
      }

      public CacheKey(String locale, IResourceUrn urn, boolean isSecure) {
         m_locale = locale;
         m_urn = urn;
         m_isSecure = isSecure;
      }

      @Override
      public boolean equals(Object obj) {
    	  if (!(obj instanceof CacheKey)) {
    		  return false;
    	  }
         CacheKey other = (CacheKey) obj;
         if (m_isSecure != other.m_isSecure) {
        	 return false;
         }
         
         if (m_locale == null) {
            if (other.m_locale != null)
               return false;
         } else if (!m_locale.equals(other.m_locale))
            return false;
         if (m_urn == null) {
            if (other.m_urn != null)
               return false;
         } else if (!m_urn.equals(other.m_urn))
            return false;
         return true;
      }

      @Override
      public int hashCode() {
         if (m_hashcode == 0) {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((m_locale == null) ? 0 : m_locale.hashCode());
            result = prime * result + ((m_urn == null) ? 0 : m_urn.hashCode());
            result = prime * result + ((!m_isSecure) ? 1231 : 1237);
            m_hashcode = result;
         }

         return m_hashcode;
      }
   }

}
