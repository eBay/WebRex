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

package com.ebayopensource.webrex.util;

import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

public class SplittersTest {
   @Test
   public void test() {
      Assert.assertEquals("[, a,  b , c, d, ]", Splitters.by(',').split(",a, b ,c,d,").toString());
      Assert.assertEquals("[a, b , c, d]", Splitters.by(',').noEmptyItem().split(",a,b ,c,d,").toString());
      Assert.assertEquals("[, a, b, c, d, ]", Splitters.by(',').trim().split(",a,b ,c,d,").toString());
      Assert.assertEquals("[a, b, c, d]", Splitters.by(',').noEmptyItem().trim().split(",a,b ,c,d,").toString());
      Assert.assertEquals("[a, b, c, d]", Splitters.by(",").noEmptyItem().trim().split(",a,b ,c,d,").toString());
      Assert.assertEquals(null, Splitters.by(",").noEmptyItem().trim().split(null));
   }
   
   
   @Test
   public void testUrl() throws Exception {
      URL url = new URL("http://28282:99/WEB-INF/classes/v4contentsource/");
      URL u = new URL(url, "../");
      System.out.println(u.toString());
   }
   
   @Test
   public void testThresholds() {
      Assert.assertTrue(Splitters.split(null, '.').length == 0);
      Assert.assertTrue(Splitters.split("", '.').length == 0);
   }
}
