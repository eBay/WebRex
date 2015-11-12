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

import static org.junit.Assert.*;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ebayopensource.webrex.resource.api.IResourceRuntimeConfig;

/**
 *
 */
public class ResourceRuntimeTest {
   static IResourceRuntimeConfig config;
   
   @BeforeClass
   public static void initClass() {
      config = new ResourceRuntimeConfig("/ResourceRuntimeTest", new File(""));
   }
   
   @Before
   public void init() {
      ResourceRuntime.INSTANCE.reset();
   }

   @AfterClass
   public static void afterClass() {
      ResourceRuntime.INSTANCE.reset();
   }

   /**
    * Test method for {@link com.ebayopensource.webrex.resource.ResourceRuntime#getAllConfig()}.
    */
   @SuppressWarnings("deprecation")
   @Test
   public void testGetAllConfig() {
      assertEquals(0, ResourceRuntime.INSTANCE.getAllConfig().size());
      ResourceRuntime.INSTANCE.setConfig(config);
      assertEquals(1, ResourceRuntime.INSTANCE.getAllConfig().size());
      assertEquals(config, ResourceRuntime.INSTANCE.getAllConfig().toArray()[0]);
   }

   /**
    * Test method for {@link com.ebayopensource.webrex.resource.ResourceRuntime#getConfig(java.lang.String)}.
    */
   @SuppressWarnings("deprecation")
   @Test
   public void testGetConfigString() {
      ResourceRuntime.INSTANCE.setConfig(config);
      assertEquals(config, ResourceRuntime.INSTANCE.getConfig("/ResourceRuntimeTest"));
   }

   /**
    * Test method for {@link com.ebayopensource.webrex.resource.ResourceRuntime#hasConfig(java.lang.String)}.
    */
   @SuppressWarnings("deprecation")
   @Test
   public void testHasConfig() {
      assertFalse(ResourceRuntime.INSTANCE.hasConfig("/ResourceRuntimeTest"));
      ResourceRuntime.INSTANCE.setConfig(config);
      assertTrue(ResourceRuntime.INSTANCE.hasConfig("/ResourceRuntimeTest"));
   }

   /**
    * Test method for {@link com.ebayopensource.webrex.resource.ResourceRuntime#removeConfig(java.lang.String)}.
    */
   @SuppressWarnings("deprecation")
   @Test
   public void testRemoveConfig() {
      ResourceRuntime.INSTANCE.setConfig(config);
      ResourceRuntime.INSTANCE.removeConfig("/ResourceRuntimeTest");
      try {
         ResourceRuntime.INSTANCE.getConfig();
      } catch (ResourceException e) {
         return;
      }
      fail("Should throw exception");
   }

   /**
    * Test method for {@link com.ebayopensource.webrex.resource.ResourceRuntime#getConfig()}.
    */
   @Test
   public void testGetConfig() {
      ResourceRuntime.INSTANCE.setConfig(config);
      assertEquals(config, ResourceRuntime.INSTANCE.getConfig());
   }

   /**
    * Test method for {@link com.ebayopensource.webrex.resource.ResourceRuntime#reset()}.
    */
   @Test
   public void testReset() {
      ResourceRuntime.INSTANCE.setConfig(config);
      ResourceRuntime.INSTANCE.reset();
      try {
         ResourceRuntime.INSTANCE.getConfig();
      } catch (ResourceException e) {
         return;
      }
      fail("Should throw exception");
   }

   /**
    * Test method for {@link com.ebayopensource.webrex.resource.ResourceRuntime#setConfig(IResourceRuntimeConfig)}.
    */
   @Test
   public void testSetConfig() {
      ResourceRuntime.INSTANCE.setConfig(config);
      try {
         ResourceRuntime.INSTANCE.setConfig(config);
      } catch (RuntimeException e) {
         return;
      }
      fail("Should throw exception");
   }
}
