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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultCacheStorage implements ICacheStorage {
   private Map<Object, Object> m_cache = new ConcurrentHashMap<Object, Object>(1024);

   @Override
   public Object get(Object key) {
      return m_cache.get(key);
   }

   @Override
   public void set(Object key, Object value) {
      m_cache.put(key, value);
   }

   @Override
   public void clear() {
      m_cache.clear();
   }
}
