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

import java.io.File;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ebayopensource.webrex.resource.ResourceInitializer;
import com.ebayopensource.webrex.resource.ResourceRuntime;
import com.ebayopensource.webrex.resource.ResourceRuntimeConfig;
import com.ebayopensource.webrex.resource.ResourceRuntimeContext;
import com.ebayopensource.webrex.resource.ResourceUrn;
import com.ebayopensource.webrex.resource.api.IResource;

public class ResourceLoaderTest {
   @Before
   public void setup() {
      ResourceRuntimeContext.setup();
   }

   @After
   public void tearDown() {
      ResourceRuntimeContext.reset();
   }

   @BeforeClass
   public static void init() {
      ResourceInitializer.initialize("/RaptorResourceFactoryTest", new File(
            "warRoot"), ResourceLoaderTest.class.getClassLoader());
   }

   @AfterClass
   public static void destroy() {
      ResourceRuntime.INSTANCE.reset();
   }

   @Test
   public void testCreateResource() {
      ResourceRuntimeContext ctx = ResourceRuntimeContext.ctx();
      ResourceRuntimeConfig cfg = (ResourceRuntimeConfig) ctx.getConfig();
      cfg.setResourcePreCollected(true);
      Map<String, List<IResource>> map = cfg.loadResource("local", false);
      Assert.assertNotNull(map);
      Assert.assertTrue(!map.isEmpty());
      Map<IResource, IResource> variationMap = cfg.getResourceVariationMap();
      Assert.assertNotNull(variationMap);
      Assert.assertTrue(1 == variationMap.size());
      Map.Entry<IResource, IResource> entry = variationMap.entrySet().iterator().next();
      Assert.assertEquals(new ResourceUrn("js", "local", "/js/sample/testvariation.js"),entry.getKey().getUrn());
      Assert.assertEquals(new ResourceUrn("js", "local", "/js/sample/testvariation.min.js"),entry.getValue().getUrn());
      
   }
}

