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

import org.junit.Assert;
import org.junit.Test;

import com.ebayopensource.webrex.util.Joiners.StringJoiner;

public class JoinersTest {

   @Test
   public void testJoin() {
      Assert.assertNull(Joiners.by(",").join(null, null));
      Assert.assertTrue(Joiners.by(",").prefixDelimiter() instanceof StringJoiner);
      Assert.assertTrue(Joiners.by(",").noEmptyItem() instanceof StringJoiner);
      String noPrefixStr = Joiners.by(",").join("one", "two", "three");
      String prefixStr = Joiners.by(",").prefixDelimiter().join("one", "two", "three");
      Assert.assertEquals("," + noPrefixStr, prefixStr);
      String emptyStr = Joiners.by(",").join("one", "two", "three", null);
      String noEmptyStr = Joiners.by(",").noEmptyItem().join("one", "two", "three", null);
      Assert.assertEquals(emptyStr, noEmptyStr+",null");
   }
   
}
