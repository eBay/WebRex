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

import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.api.IResourceArgumentsUrn;
import com.ebayopensource.webrex.resource.api.IResourceLocale;
import com.ebayopensource.webrex.resource.api.IResourceUrn;

public class ResourceCacheManager {
   private static final String RESOURCE_CACHE = "ResourceCache";

   private static ICacheStorage m_globalCacheStorage = new DefaultCacheStorage();
   
   private ResourceCache m_defaultCache = new ResourceCache();

   public static void clearGlobalCache() {
      m_globalCacheStorage.clear();
   }

   public static Object getGlobalCache(Object key) {
      return m_globalCacheStorage.get(key);
   }

   public static void setGlobalCache(Object key, Object value) {
      m_globalCacheStorage.set(key, value);
   }

   public static void setGlobalCacheStorage(ICacheStorage storage) {
      m_globalCacheStorage = storage;
   }

   private ICacheStorage m_cacheStorage = new DefaultCacheStorage();
   
   {
	   m_cacheStorage.set(RESOURCE_CACHE, m_defaultCache);
   }

   public void clearCache() {
      m_cacheStorage.clear();
   }

   public Object getCache(Object key) {
      return m_cacheStorage.get(key);
   }

   public ResourceCache getResourceCache() {
      return m_defaultCache;
   }

   public IResource getResourceCache(IResourceUrn urn, IResourceLocale locale, boolean isSecure) {
	   if (urn instanceof IResourceArgumentsUrn) {
		   return null;
	   }

	   return m_defaultCache.getCache(urn, locale, isSecure);
   }

   public void putResourceCache(IResourceLocale locale, IResource resource, boolean isSecure) {
	   IResourceUrn urn = resource.getUrn();
	   if (urn instanceof IResourceArgumentsUrn) {
		   return;
	   }
	   
	   m_defaultCache.putCache(urn, locale, isSecure, resource);
   }

   public void putResourceCaches(String namespace, Map<String, List<IResource>> map, boolean isSecure) {
      m_defaultCache.putCaches(namespace, map, isSecure);
   }

   public void setCache(Object key, Object value) {
      m_cacheStorage.set(key, value);
   }

   public void setCacheStorage(ICacheStorage storage) {
      m_cacheStorage = storage;
   }
}
