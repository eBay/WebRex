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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.api.IResourceContext;
import com.ebayopensource.webrex.resource.api.IResourceLocale;
import com.ebayopensource.webrex.resource.api.ITemplateContext;

public class ResourceWrapperTest {
   @AfterClass
   public static void destroy() {
      ResourceRuntime.INSTANCE.reset();
   }

   @BeforeClass
   public static void init() {
      ResourceInitializer.initialize("/ResourceWrapperTest", new File("warRoot"), BaseResourceTest.class.getClassLoader());
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
   public void testResourceWrapper() {
      
      //just invoke all these methods for line coverage
      //this class is just a wrapper, so no more Assertion needed
      IResource resource = ResourceFactory.createResource("/js/sample/sample.js");
      ResourceWrapper wrapper = new ResourceWrapper(resource);
      IResourceContext context = ResourceRuntimeContext.ctx().getResourceContext();
      wrapper.getBinaryContent(context);
      wrapper.getContent(context);
      wrapper.getDependencies();
      wrapper.getLastModified();
      wrapper.getLibrary();
      wrapper.getLocale();
      wrapper.getOriginalBinaryContent();
      wrapper.getOriginalBinaryContent(new MockTemplateContext());
      wrapper.getOriginalContent();
      wrapper.getOriginalContent(new MockTemplateContext());
      wrapper.getOriginalUrl();
      wrapper.getUrl(context);
      wrapper.getUrn();
      wrapper.setDependencies(null);
      wrapper.setHandler(null);
      
   }
   
   public static class MockTemplateContext implements ITemplateContext {
      private String m_contextPath;

      private boolean m_isSecure;

      private IResourceLocale m_resourceLocale;

      @Override
      public String getContextPath() {
         return m_contextPath;
      }

      @Override
      public IResourceLocale getLocale() {
         return m_resourceLocale;
      }

      @Override
      public boolean isSecure() {
         return m_isSecure;
      }

      public void setContextPath(String contextPath) {
         m_contextPath = contextPath;
      }

      public void setResourceLocale(IResourceLocale resourceLocale) {
         m_resourceLocale = resourceLocale;
      }

      public void setSecure(boolean isSecure) {
         m_isSecure = isSecure;
      }

   }
}
