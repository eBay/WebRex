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
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ebayopensource.webrex.resource.ResourceInitializer;
import com.ebayopensource.webrex.resource.ResourceRuntime;
import com.ebayopensource.webrex.resource.ResourceRuntimeContext;
import com.ebayopensource.webrex.resource.api.IResourceRuntimeConfig;

public class SharedResourceLoaderTest{
   
   @AfterClass
   public static void destroy() {
      ResourceRuntime.INSTANCE.reset();
   }

   @BeforeClass
   public static void init() {
      ResourceInitializer.initialize("/SharedResourceLoaderTest", new File("warRoot"), SharedResourceLoaderTest.class.getClassLoader());
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
   public void testLoadResources() throws IOException {
      IResourceRuntimeConfig config = ResourceRuntimeContext.ctx().getConfig();
      File file = new File("warRoot/WEB-INF/lib/SharedResourceTest-0.0.1-SNAPSHOT.jar!/META-INF/resources");
      @SuppressWarnings("deprecation")
      String urlStr = file.toURL().toString();
      URL url = new URL("jar", null, urlStr);
      config.setAppClassLoader(new MockClassLoader(url));
      Assert.assertFalse(config.loadResource("shared", false).isEmpty());

   }
   
   private static class MockClassLoader extends ClassLoader {
      
      private URL m_jarURL;
    
      public MockClassLoader(URL url) {
         super();
         m_jarURL = url;
      }
      
      @Override
      public Enumeration<URL> getResources (String resName) throws IOException {
         final List<URL> resources = new ArrayList<URL>();
         resources.add(m_jarURL);
         return new Enumeration<URL>() {
            int index = 0;

            public boolean hasMoreElements() {
               if (index < resources.size()) {
                  return true;
               } else {
                  
                  return false;
               }
            }
            public URL nextElement() {
               while (index < resources.size()) { 
                  return resources.get(index++);
               }
               throw new NoSuchElementException();
            }
         };
      }
      
      
   }
}
