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
import java.lang.reflect.Constructor;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.Test;


public class ResourceInitializerTest {

   @AfterClass
   public static void destroy() {
      ResourceRuntime.INSTANCE.reset();
   }

   @Test
   public void testInitializeWithoutClassLoader() {
      ResourceInitializer.initialize("/ResourceBundleTest", new File("warRoot"), null);
   }
   
   @Test
   public void testPrivateConstructor() {
      try {
         Class<ResourceInitializer> clazz = ResourceInitializer.class;
         Constructor<ResourceInitializer> constructor = clazz.getDeclaredConstructor();
         constructor.setAccessible(true);
         Object instance = constructor.newInstance();
         Assert.assertTrue(instance instanceof ResourceInitializer);
      } catch (Exception e) {
         Assert.assertTrue(1 == 0);
      }
      
   }
}
