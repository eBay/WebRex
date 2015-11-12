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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.api.IResourceContext;
import com.ebayopensource.webrex.resource.api.IResourceLibrary;
import com.ebayopensource.webrex.resource.api.IResourceLocale;
import com.ebayopensource.webrex.resource.api.IResourcePackage;
import com.ebayopensource.webrex.resource.api.IResourceUrn;
import com.ebayopensource.webrex.resource.api.ITemplateContext;
import com.ebayopensource.webrex.resource.spi.IResourceHandler;
import com.ebayopensource.webrex.resource.spi.IResourceRegistry;
import com.ebayopensource.webrex.resource.spi.IResourceResolver;

public class ResourceBundleTest{
   
   @AfterClass
   public static void destroy() {
      ResourceRuntime.INSTANCE.reset();
   }

   @BeforeClass
   public static void init() {
      ResourceInitializer.initialize("/ResourceBundleTest", new File("warRoot"), BaseResourceTest.class.getClassLoader());
      
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
   public void testResourceBundle() {

      ResourceRuntimeContext ctx = ResourceRuntimeContext.ctx();
      IResourceRegistry registry = ctx.getConfig().getRegistry();
      registry.registerResolver("resourcePackage", "module", new MockResourceResolver());
      ResourceInitializer.initializeResourceBundles(ResourceRuntime.INSTANCE.getConfig(),
            ResourceBundleTest.class.getClassLoader());
      ResourceRuntimeContext.setup();

      ResourceBundleConfig config = ResourceRuntimeContext.ctx().getConfig().getResourceBundleConfig();
      String expected = "{shared2=[Resource [m_urn=js.shared:/js/sample/shared1.js, m_libInfo=ResourceLibrary [m_id=sharedlib, m_version=1.0.0, m_type=JAR]], " +
      		"Resource [m_urn=js.shared:/js/sample/shared2.js, m_libInfo=ResourceLibrary [m_id=sharedlib, m_version=1.0.0, m_type=JAR]]], " +
      		"sample=[Resource [m_urn=js.local:/js/sample/sample1.js, m_libInfo=ResourceLibrary [m_id=webressdk, m_version=1.0.8, m_type=WAR]], " +
      		"Resource [m_urn=js.local:/js/sample/sample.js, m_libInfo=ResourceLibrary [m_id=webressdk, m_version=1.0.8, m_type=WAR]]], " +
      		"sample2=[Resource [m_urn=js.local:/js/sample/sample1.js, m_libInfo=ResourceLibrary [m_id=webressdk, m_version=1.0.8, m_type=WAR]], " +
      		"Resource [m_urn=js.local:/js/sample/sample2.js, m_libInfo=ResourceLibrary [m_id=webressdk, m_version=1.0.8, m_type=WAR]]], "
      		+ "packagebundle=[Resource [m_urn=js.local:/js/sample/resourcemodule.js, m_libInfo=ResourceLibrary [m_id=webressdk, m_version=1.0.8, m_type=WAR]], "
      		+ "Resource [m_urn=js.local:/js/sample/resourcepackage.js, m_libInfo=ResourceLibrary [m_id=webressdk, m_version=1.0.8, m_type=WAR]]]}";
      Assert.assertEquals(expected, config.getAppBundles().toString());

      Assert.assertEquals(
            "{shared=[Resource [m_urn=js.shared:/js/sample/shared1.js, m_libInfo=ResourceLibrary [m_id=sharedlib, m_version=1.0.0, m_type=JAR]], " +
            "Resource [m_urn=js.shared:/js/sample/shared2.js, m_libInfo=ResourceLibrary [m_id=sharedlib, m_version=1.0.0, m_type=JAR]]], " +
            "common=[Resource [m_urn=js.local:/js/sample/sample_sys1.js, m_libInfo=ResourceLibrary [m_id=webressdk, m_version=1.0.8, m_type=WAR]], " +
            "Resource [m_urn=js.local:/js/sample/sample_sys2.js, m_libInfo=ResourceLibrary [m_id=webressdk, m_version=1.0.8, m_type=WAR]]]}",
            config.getSysBundles("js").toString());
   }
   
   private class MockResourceResolver implements IResourceResolver {

      @Override
      public IResource resolve(IResourceUrn urn, IResourceContext ctx) {
         // TODO Auto-generated method stub
         String urnStr = urn.toString();
         if(urnStr.contains("resourcepackage")) {
            return new MockResourcePackage();
         } else {
            return ResourceFactory.createResource("/js/sample/resourcemodule.js");
         }
         
      }

      @Override
      public boolean isCachable() {
         // TODO Auto-generated method stub
         return false;
      }

   }
   
   public class MockResourcePackage implements IResource, IResourcePackage {

      private List<IResource> m_resources;
      
      public MockResourcePackage() {
         m_resources = new ArrayList<IResource>();
         m_resources.add(ResourceFactory.createResource("/js/sample/resourcepackage.js"));
      }
      
      @Override
      public List<IResource> getResources(String type) {
         // TODO Auto-generated method stub
         return m_resources;
      }

      @Override
      public byte[] getBinaryContent(IResourceContext context) {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public String getContent(IResourceContext context) {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public List<IResource> getDependencies() {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public long getLastModified() {
         // TODO Auto-generated method stub
         return 0;
      }

      @Override
      public IResourceLibrary getLibrary() {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public IResourceLocale getLocale() {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public byte[] getOriginalBinaryContent() {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public byte[] getOriginalBinaryContent(ITemplateContext context) {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public String getOriginalContent() {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public String getOriginalContent(ITemplateContext context) {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public URL getOriginalUrl() {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public String getUrl(IResourceContext context) {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public IResourceUrn getUrn() {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public void setDependencies(List<IResource> resources) {
         // TODO Auto-generated method stub
         
      }

      @Override
      public void setHandler(IResourceHandler handler) {
         // TODO Auto-generated method stub
         
      }
      
   }
}
