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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ebayopensource.webrex.resource.ResourceRuntimeContext.ResourceContext;
import com.ebayopensource.webrex.resource.spi.IResourceTokenStorage;
import com.ebayopensource.webrex.util.Checksum;
import com.ebayopensource.webrex.util.Joiners;
import com.ebayopensource.webrex.util.Splitters;

public class ResourceRuntimeContextTest {
   
   @AfterClass
   public static void destroy() {
      ResourceRuntime.INSTANCE.reset();
   }

   @BeforeClass
   public static void init() {
      ResourceInitializer.initialize("/ResourceRuntimeContextTest", new File("warRoot"), BaseResourceTest.class.getClassLoader());
   }
   
   @Before
   public void setup() {
      ResourceRuntimeContext.setup();
   }

   @After
   public void tearDown() {
      ResourceRuntimeContext.reset();
   }

   @Test
   public void testUninitializedException() {
      ResourceRuntimeContext.reset();
      try {
         ResourceRuntimeContext.ctx();
      } catch (Exception e) {
         Assert.assertTrue(e instanceof IllegalStateException);
         Assert.assertEquals("ResourceRuntimeContext must be setup by ResourceRuntimeContext.setup!", e.getMessage());
      }
   }
   
   @Test
   public void testSetupWithOutSlash() {
      ResourceRuntimeContext.setup();
   }
   
   @Test
   public void testSetAndGetAttrubite() {
      ResourceRuntimeContext ctx = ResourceRuntimeContext.ctx();
      ctx.setAttribute("key", "value");
      Object value = ctx.getAttribute("key");
      if(value instanceof String) {
         Assert.assertEquals("value", (String)value);
      }
   }
   
   @Test
   public void testSupportDeferRendering() {
      ResourceRuntimeContext.ctx().supportDeferRendering("");
   }
   
   @Test
   public void testSetDedupToken() {
      ResourceRuntimeContext ctx = ResourceRuntimeContext.ctx();
      MockTokenStorage storage = new MockTokenStorage();
      ctx.getConfig().getRegistry().setTokenStorage(storage);
      List<String> urns = new ArrayList<String>();
      urns.add("js.local.sample.sample1_js");
      urns.add("js.local.sample.sample2_js");
      String dedupToken = storage.storeResourceUrns(urns);
      ctx.setDeDupToken(dedupToken);
   }
   
   @Test
   public void testGetDedupToken() {
      ResourceRuntimeContext ctx = ResourceRuntimeContext.ctx();
      MockTokenStorage storage = new MockTokenStorage();
      ctx.getConfig().getRegistry().setTokenStorage(storage);

      
      ResourceModel model = ResourceRuntimeContext.ctx().getResourceAggregator().getModel();
      model.enableCollectResources("js", true);
      model.processModel();
      Set<String> set = new HashSet<String>();
      set.add("js.local:/tests/ajaxDedup/sample4.js");
      set.add("js.local:/tests/ajaxDedup/sample1.js");
      set.add("js.inline:/dffoyfpm0yzvdbg1p025pkmixin");
      set.add("js.local:/tests/table.js");
      set.add("js.local:/tests/ajaxDedup/sample3.js");
      model.getCollectedResource("js").addAll(set);
      String token = ctx.getDeDupToken("js");
      
      Assert.assertEquals("p5ezkcqygu0qfam5grb2phojo23", token);
   }
   
   @Test
   public void testRuntimeContext() {
      ResourceRuntimeContext ctx = ResourceRuntimeContext.ctx();
      ResourceContext context = (ResourceContext)ctx.getResourceContext();
      context.setAttribute("key", "value");
      Object value = context.getAttribute("key");
      if(value instanceof String) {
         Assert.assertEquals("value", (String)value);
      }
      
      Assert.assertNull(context.getOptimizationCommand("testType"));
      Assert.assertFalse(context.isOptimizationEnabled());
      context.setOriginalRequestUri("test original request uri");
      Assert.assertEquals("test original request uri", context.getOriginalRequestUri());
   }
   
   @SuppressWarnings("deprecation")
   @Test
   public void testDeprecatedSetup() {
      ResourceRuntimeContext.reset();
      ResourceRuntimeContext.setup("/ResourceRuntimeContextTest");
      testRuntimeContext();
   }
   
   public class MockTokenStorage implements IResourceTokenStorage {
      
      private Map<String, String> m_tokenMap = new HashMap<String, String>();
      private String getStrFromUrnList(List<String> urns) {
         return Joiners.by(',').join(urns);
      }

      private List<String> getUrnsFromStr(String urnStr) {
         if (urnStr != null) {
            return Splitters.by(',').split(urnStr);
         }

         return null;
      }

      @Override
      public List<String> loadResourceUrns(String token) {
         String urnStr = m_tokenMap.get(token);
         return getUrnsFromStr(urnStr);
      }

      @Override
      public String storeResourceUrns(List<String> urns) {
         String urnStr = getStrFromUrnList(urns);
         String token = Checksum.checksum(urnStr);
         m_tokenMap.put(token, urnStr);
         return token;
      }
   }
}
