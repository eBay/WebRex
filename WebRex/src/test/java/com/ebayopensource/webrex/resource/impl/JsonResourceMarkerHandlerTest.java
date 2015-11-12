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

package com.ebayopensource.webrex.resource.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.ebayopensource.webrex.resource.impl.ResourceDeferProcessor.MarkerParserV3;
import com.ebayopensource.webrex.resource.impl.ResourceDeferProcessor.MarkerParserV4;
import com.ebayopensource.webrex.resource.impl.ResourceMarkerProcessor.JsonResourceMarkerHandler;

public class JsonResourceMarkerHandlerTest extends JsonResourceMarkerHandler {

   @Test
   public void testTranslateMarker() {
      JsonResourceMarkerHandler handler = new JsonResourceMarkerHandler();
      
      String result = handler.translateMarker("${quantity}");
      assertNull(result);
      
   }

   @Test
   public void testMarkerParserV3(){
      JsonResourceMarkerHandler handler = new JsonResourceMarkerHandler();
      
      StringBuilder content = new StringBuilder("abcdefg${quantity}hijkl");
      MarkerParserV3.INSTANCE.parse(content, handler);
      assertEquals("abcdefg${quantity}hijkl", content.toString());
   }
   
   @Test
   public void testMarkerParserV4(){
      JsonResourceMarkerHandler handler = new JsonResourceMarkerHandler();
      
      StringBuilder content = new StringBuilder("abcdefg${quantity}hijkl");
      MarkerParserV4.INSTANCE.parse(content, handler);
      assertEquals("abcdefg${quantity}hijkl", content.toString());
   }
   
}
