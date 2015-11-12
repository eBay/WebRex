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

import junit.framework.Assert;

import org.junit.Test;

import com.ebayopensource.webrex.resource.Statistics.StatItem;

public class StatisticsTest {
   
   @Test
   public void testStatistics() {
      Statistics statistics = new Statistics();
      Assert.assertNull(statistics.getItem(null));
      Assert.assertTrue(statistics.getMap().size() == 0);
     
   }
   
   @Test
   public void testStatItem() {
      Statistics statistics = new Statistics();
      statistics.addItem(getClass(), System.currentTimeMillis());
      StatItem item = statistics.getItem(getClass());
      Assert.assertNotNull(item);
      Assert.assertTrue(statistics.getMap().size() == 1);
      int count = 233;
      item.setCount(count);
      item.setAccTime(System.currentTimeMillis());
      Assert.assertEquals(count, item.getCount());
      Assert.assertEquals(getClass(), item.getType());
      Assert.assertTrue(item.toString().contains("StatisticsTest[233]"));
   }
}
