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

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.ebayopensource.webrex.resource.expression.ResourceExpression;

public class ELHelperTest {

   @Test
   public void testEL() {
      Assert.assertEquals("[res]", el("res"));
      Assert.assertEquals("[res]", el("${res}"));
      Assert.assertEquals("[res, js, local, sample_js]", el("${res.js.local.sample_js}"));
      Assert.assertEquals("[res, js, local, sample_js]", el("res.js.local.sample_js"));
      Assert.assertEquals("[res, js, local, /sample/sample.js]", el("${res.js.local[\"/sample/sample.js\"]}"));
      Assert.assertEquals("[res, js, local, /sample/sample.js]", el("${res.js.local['/sample/sample.js']}"));
      Assert.assertEquals("[res, js, local, /sam-ple/s.ample.js]", el("${res.js.local['/sam-ple/s.ample.js']}"));

      Assert.assertEquals(null, el("['res"));
      Assert.assertEquals(null, el("[\"res"));
      Assert.assertEquals("[res]", el("[\"res\"]"));
      Assert.assertEquals("[res]", el("['res']"));
      Assert.assertEquals("[d, d'test]", el("${d['d'test']}"));
      Assert.assertEquals("[res, js, local, /sample/sample.js]", el("${res.js.['local']['/sample/sample.js']}"));
      Assert.assertEquals("[res, js, local, /sample/sample.js]", el("${res.js.[\"local\"][\"/sample/sample.js\"]}"));

      Assert.assertEquals("[res, img, local, tabweb, src, img, subcategories-arrow.gif]",
            el("${res.img.local.tabweb.src.img[\"subcategories-arrow.gif\"]}"));
      
      Assert.assertEquals("[res, flash, local, flash2.swf, flash2_test_swf]", el("res.flash.local['flash2.swf'].flash2_test_swf"));
      Assert.assertEquals("[res, flash, local, flash2.swf, flash2_test_swf]", el("res.flash.local[\"flash2.swf\"].flash2_test_swf"));
      
      Assert.assertTrue(ELHelper.getExpressionFromEL("res.local.js.sample.sample_js") instanceof ResourceExpression);
   }

   private String el(String el) {
      List<String> elKeys = ELHelper.getELKeys(el);
      if (elKeys != null) {
         return elKeys.toString();
      } else {
         return null;
      }
   }
   
}
