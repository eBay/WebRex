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
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.expression.ResBeanExpression;
import com.ebayopensource.webrex.resource.expression.ResourceExpression;
import com.ebayopensource.webrex.resource.impl.ResourceDeferProcessor;
import com.ebayopensource.webrex.resource.impl.ResourceMarkerProcessor;
import com.ebayopensource.webrex.resource.tag.ITagEnv;
import com.ebayopensource.webrex.resource.tag.ResourceTag;
import com.ebayopensource.webrex.util.ELHelper;
import com.ebayopensource.webrex.util.Splitters;

public abstract class BaseResourceTest {
   @AfterClass
   public static void destroy() {
      ResourceRuntime.INSTANCE.reset();
   }

   @BeforeClass
   public static void init() {
      ResourceInitializer.initialize("/BaseResourceTest", new File("warRoot"), BaseResourceTest.class.getClassLoader());
   }

   protected void assertMarker(String expected, String marker) {
      StringBuilder sb = new StringBuilder(marker);
      ResourceMarkerProcessor.INSTANCE.process(sb, null);
      String actual = sb.toString();
      Assert.assertEquals(expected, actual);
   }

   protected void assertRender(String expected, ResourceTag tag) {
      assertRender(expected, tag, false);
   }

   protected void assertRender(String expected, ResourceTag tag, boolean processMarker) {
      String actual = render(tag, processMarker);
      Assert.assertEquals(expected, actual);
   }

   protected void assertToken(String expected, String token) {
      Assert.assertEquals(
            expected,
            String.valueOf(ResourceRuntimeContext.ctx().getConfig().getRegistry().getTokenStorage()
                  .loadResourceUrns(token)));
   }

   protected String createToken(String urnStr) {
      return ResourceRuntimeContext.ctx().getConfig().getRegistry().getTokenStorage()
            .storeResourceUrns(Splitters.by(',').trim().split(urnStr));
   }

   protected ResourceExpression eval(ResBeanExpression bean, String el) {
      ResourceExpression expr = bean;
      List<String> elKeys = ELHelper.getELKeys(el);
      for (String key : elKeys) {
         if (expr == null) {
            return null;
         }
         expr = (ResourceExpression)expr.get(key);
      }
      return expr;
   }

   protected String render(ResourceTag tag, boolean processMarker) {
      tag.start();
      IResource resource = tag.build();
      String actual = tag.render(resource);

      //process marker
      if (processMarker && actual != null) {
         StringBuilder sb = new StringBuilder(actual);
         new ResourceDeferProcessor().process(sb);
         actual = sb.toString();
      }

      tag.end();
      return actual;
   }

   @Before
   public void setup() {
      ResourceRuntimeContext.setup();
   }

   @After
   public void tearDown() {
      ResourceRuntimeContext.reset();
   }

   public static class MockTagEnv implements ITagEnv {
      
      private Map<String, Object> m_properties;

      @Override
      public ITagEnv err(Object obj) {
         return null;
      }

      @Override
      public Object findAttribute(String name) {
         return null;
      }

      @Override
      public void flush() throws IOException {
      }

      @Override
      public String getError() {
         return null;
      }

      @Override
      public String getOutput() {
         return null;
      }

      @Override
      public TagOutputType getOutputType() {
         return TagOutputType.html;
      }

      @Override
      public Object getPageAttribute(String name) {
         return null;
      }

      @Override
      public Object getProperty(String name) {
         if (m_properties != null) {
            return m_properties.get(name);
         } else {
            return null;
         }
      }

      @Override
      public Object getRequestAttribute(String name) {
         return null;
      }

      @Override
      public void onError(String message, Throwable cause) {
      }

      @Override
      public ITagEnv out(Object obj) {
         return null;
      }

      @Override
      public void removePageAttribute(String name) {
      }

      @Override
      public void setOutputType(TagOutputType type) {
      }

      @Override
      public void setPageAttribute(String name, Object value) {
      }

      @Override
      public void setProperty(String name, Object value) {
         if (m_properties == null) {
            m_properties = new HashMap<String, Object>();
         }

         m_properties.put(name, value);
      }

      @Override
      public void setRequestAttribute(String name, Object value) {
      }

   }
}
