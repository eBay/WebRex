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

package com.ebayopensource.webrex.resource.tag.handler;

import junit.framework.Assert;

import org.junit.Test;

import com.ebayopensource.webrex.resource.tag.ITagEnv.TagOutputType;

public class JspTagEnvTest {

   
   @Test
   public void testJspTagEnvTest() {
      JspTagEnv env = new JspTagEnv();
      String errMessage = "test for error message";
      env.err(errMessage);
      Assert.assertEquals(errMessage, env.getError());
      Assert.assertTrue(env.getOutput().equals(""));
      Assert.assertEquals(TagOutputType.html, env.getOutputType());
      try {
         env.setOutputType(null);
         Assert.fail();
      } catch (Exception e) {
         Assert.assertTrue(e instanceof IllegalArgumentException);
         Assert.assertTrue(e.getMessage().contains("TagOutputType can't be null."));
      }
      try {
         env.onError("force test error", new Throwable("test Throwable"));
         Assert.fail();
      } catch(Exception e) {
         Assert.assertTrue(e instanceof RuntimeException);
      }
      env.setOutputType(TagOutputType.xhtml);
      Assert.assertEquals(TagOutputType.xhtml, env.getOutputType());
      Assert.assertNull(env.getProperty("testKey"));
      env.setProperty("testKey", "testValue");
      Assert.assertTrue(env.getProperty("testKey").toString().equals("testValue"));
   }
}
