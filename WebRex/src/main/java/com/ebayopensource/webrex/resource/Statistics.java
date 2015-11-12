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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Statistics {

   private Map<Class<?>, StatItem> m_map = null;

   public void addItem(Class<?> type, long nanoTime) {
      if (m_map == null) {
         m_map = new HashMap<Class<?>, StatItem>(128);
      }

      StatItem item = m_map.get(type);

      if (item == null) {
         item = new StatItem(type);
         m_map.put(type, item);
      }

      item.touch(nanoTime);
   }

   public StatItem getItem(Class<?> type) {
      return m_map != null ? m_map.get(type) : null;
   }

   public Map<Class<?>, StatItem> getMap() {
      if (m_map == null) {
         return Collections.emptyMap();
      } else {
         return m_map;
      }
   }

   public static class StatItem {
      private Class<?> m_type;

      private long m_accTime = 0;

      private int m_count = 0;

      private static final long MS_PER_NANOSECOND = (long) 1.0e06;

      private static final long HALF_MS_IN_NANOSECONDS = (long) 5.0e05;

      public StatItem(Class<?> type) {
         m_type = type;
      }

      public long getAccTime() {
         return m_accTime;
      }

      public int getCount() {
         return m_count;
      }

      public Class<?> getType() {
         return m_type;
      }

      public void setAccTime(long accTime) {
         m_accTime = accTime;
      }

      public void setCount(int count) {
         m_count = count;
      }

      public String toString() {
         return m_type.getSimpleName() + "[" + m_count + "]"
               + ((m_accTime + HALF_MS_IN_NANOSECONDS) / MS_PER_NANOSECOND) + "ms";
      }

      void touch(long time) {
         m_accTime += time;
         m_count++;
      }
   }
}
