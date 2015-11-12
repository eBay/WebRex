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

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.ebayopensource.webrex.resource.BaseResourceTest;
import com.ebayopensource.webrex.resource.ResourceFactory;
import com.ebayopensource.webrex.resource.ResourceTypeConstants;
import com.ebayopensource.webrex.resource.impl.ResourceDeferProcessor.ResourceMarkerHandler;
import com.ebayopensource.webrex.resource.tag.SlotTag;
import com.ebayopensource.webrex.resource.tag.UseScriptTag;

public class ResourceDeferProcessorTest  extends BaseResourceTest{
   
   @Test
   public void testResourceMarkerHandler() throws IOException {
      SlotTag slotTag = new SlotTag(ResourceTypeConstants.JS);
      slotTag.setSlotType("js");
      slotTag.setEnv(new MockTagEnv());
      slotTag.getModel().setAttribute("id", "body");
      String marker = render(slotTag, false);

      UseScriptTag tag1 = new UseScriptTag(ResourceTypeConstants.JS);
      tag1.setEnv(new MockTagEnv());
      tag1.getModel().setValue("/js/sample/sample_sys1.js");
      tag1.getModel().setAttribute("target", "body");
      assertRender("", tag1, true);

      tag1 = new UseScriptTag(ResourceTypeConstants.JS);
      tag1.setEnv(new MockTagEnv());
      tag1.getModel().setContent("inline1");
      tag1.getModel().setAttribute("target", "body");
      assertRender("", tag1, true);

      tag1 = new UseScriptTag(ResourceTypeConstants.JS);
      tag1.setEnv(new MockTagEnv());
      tag1.getModel().setContent("inline2");
      tag1.getModel().setAttribute("target", "body");
      assertRender("", tag1, true);

      UseScriptTag tag2 = new UseScriptTag(ResourceTypeConstants.JS);
      tag2.setEnv(new MockTagEnv());
      tag2.getModel().setValue("/js/sample/sample_sys2.js");
      tag2.getModel().setAttribute("target", "body");
      assertRender("", tag2, true);

      tag2 = new UseScriptTag(ResourceTypeConstants.JS);
      tag2.setEnv(new MockTagEnv());
      tag2.getModel().setValue(ResourceFactory.createExternalResource("js", "http://external"));
      tag2.getModel().setAttribute("target", "body");
      assertRender("", tag2, true);
      
      String expected = "<script src=\"/BaseResourceTest/lrssvr/3yg1ueqs3mycfh0pu2r0mnndaqk.js\" type=\"text/javascript\"></script><script type=\"text/javascript\">inline1inline2</script><script src=\"/BaseResourceTest/lrssvr/l0cnh31342ymfcrmehx3ehqewex.js\" type=\"text/javascript\"></script><script src=\"http://external\" type=\"text/javascript\"></script>";
      StringBuilder sb = new StringBuilder();
      ResourceMarkerHandler handler = new ResourceMarkerHandler();
      handler.handle(sb, marker.substring(2, marker.length()-1));
      Assert.assertEquals(expected, sb.toString());
   }
}
