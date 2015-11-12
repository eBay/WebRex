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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.ebayopensource.webrex.resource.BaseResourceTest;
import com.ebayopensource.webrex.resource.ResourceFactory;
import com.ebayopensource.webrex.resource.ResourceRuntimeContext;
import com.ebayopensource.webrex.resource.api.IResource;

public class ResourceCacheTest extends BaseResourceTest {
   @Test
   public void testDefaultCache() {
      DefaultCacheStorage cache = new DefaultCacheStorage();
      cache.set("key", "value");

      Assert.assertEquals("value", cache.get("key"));

      cache.clear();
      Assert.assertEquals(null, cache.get("key"));
   }

   @Test
   public void testGloalCacheManager() {
      ResourceCacheManager.setGlobalCache("key", "value");
      Assert.assertEquals("value", ResourceCacheManager.getGlobalCache("key"));
   }

   @Test
   public void testSetGlobalCache() {
      ICacheStorage globalCache = new TestGlobaCacheStorage();
      ResourceCacheManager.setGlobalCacheStorage(globalCache);

      ResourceCacheManager.setGlobalCache("key", "value");
      try {
         Assert.assertEquals("value-global", ResourceCacheManager.getGlobalCache("key"));
      } finally {
         ResourceCacheManager.setGlobalCacheStorage(new DefaultCacheStorage());
      }
   }

   @Test
   public void testCacheManager() {
      ResourceCacheManager cacheManager = ResourceRuntimeContext.ctx().getConfig().getCacheManager();
      cacheManager.setCache("key", "value1");

      Assert.assertEquals("value1", cacheManager.getCache("key"));

      IResource resource = ResourceFactory.createResource("/js/sample/sample.js");
      cacheManager.putResourceCache(resource.getLocale(), resource, false);

      IResource actual = cacheManager.getResourceCache(resource.getUrn(), resource.getLocale(), false);
      Assert.assertEquals(resource, actual);

      IResource resource1 = ResourceFactory.createResource("/js/sample/sample1.js");
      IResource resource2 = ResourceFactory.createResource("/js/sample/sample2.js");
      Map<String, List<IResource>> map = new HashMap<String, List<IResource>>();
      List<IResource> value = new ArrayList<IResource>();
      value.add(resource1);
      value.add(resource2);
      map.put(null, value);

      cacheManager.putResourceCaches("local", map, false);

      Assert.assertEquals(resource2, cacheManager.getResourceCache().getCache(resource2.getUrn(), null, false));
   }

   private static class TestGlobaCacheStorage implements ICacheStorage {
      private Map<Object, Object> m_map = new HashMap<Object, Object>();

      @Override
      public void clear() {
         m_map.clear();
      }

      @Override
      public Object get(Object key) {
         return m_map.get(key);
      }

      @Override
      public void set(Object key, Object value) {
         m_map.put(key, value + "-global");
      }

   }
}
