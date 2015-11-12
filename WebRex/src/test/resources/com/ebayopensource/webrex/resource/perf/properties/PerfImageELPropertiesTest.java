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

package com.ebayopensource.webrex.resource.perf.properties;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.ebay.eunit.benchmark.BenchmarkClassRunner;
import com.ebay.eunit.benchmark.CpuMeta;

@RunWith(BenchmarkClassRunner.class)
public class PerfImageELPropertiesTest extends BaseELBenchmarkTest {
   @Test
   @CpuMeta(loops = 2000)
   public void testUrl() {
      String url = eval("img.local.ebayLogo_gif.$url");

      Assert.assertEquals("/img/ebayLogo.gif", url);
   }

   @Test
   @CpuMeta(loops = 2000)
   public void testObject() {
      Object object = eval("img.local.ebayLogo_gif.$object");

      Assert.assertEquals("LocalImageResource", object.getClass().getSimpleName());
   }

   @Test
   @CpuMeta(loops = 2000)
   public void testWidth() {
      Integer width = eval("img.local.ebayLogo_gif.$width");

      Assert.assertEquals(110, width.intValue());
   }

   @Test
   @CpuMeta(loops = 2000)
   public void testHeight() {
      Integer height = eval("img.local.ebayLogo_gif.$height");

      Assert.assertEquals(45, height.intValue());
   }
}
