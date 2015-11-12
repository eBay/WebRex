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

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.Test;

import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.api.IResourceContext;
import com.ebayopensource.webrex.resource.api.IResourceLibrary.LibraryType;
import com.ebayopensource.webrex.resource.api.IResourceLocale;
import com.ebayopensource.webrex.resource.api.ITemplateContext;
import com.ebayopensource.webrex.resource.spi.IResourceHandler;


public class ResourceTest  extends BaseResourceTest {

	@Test
	public void testResource() {
		Resource resource = (Resource)ResourceFactory.createResource("/js/sample/sample.js");
		resource.hashCode();
		Assert.assertTrue(resource.equals(resource));
		Assert.assertFalse(resource.equals(null));
		Assert.assertFalse(resource.equals(new Object()));
		Resource resource1 = (Resource)ResourceFactory.createResource("/js/sample/sample1.js");
		Assert.assertFalse(resource.equals(resource1));
		Assert.assertTrue(resource1.getDependencies().isEmpty());
		
	}
	
	
	@Test
	public void testArguments() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
	   Resource resource = (Resource)ResourceFactory.createResource("/js/sample/sample.js");
      Resource cloneResource = new Resource(resource);
      Assert.assertTrue(cloneResource.equals(resource));
	   Class<Resource> clazz = Resource.class;
	   Field libInfo = clazz.getDeclaredField("m_libInfo");
	   libInfo.setAccessible(true);
	   ResourceLibrary lib = new ResourceLibrary("testLib", "0.0.1-SNAPSHOT", LibraryType.JAR);
	   ResourceLibrary lib1 = new ResourceLibrary("testLib", "0.0.2-SNAPSHOT", LibraryType.JAR);
	   libInfo.set(cloneResource, lib);
	   Assert.assertFalse(resource.equals(cloneResource));
	   libInfo.set(resource, lib1);
	   Assert.assertFalse(resource.equals(cloneResource));
	   libInfo.set(resource, lib);
	   Field urn = clazz.getDeclaredField("m_urn");
	   urn.setAccessible(true);
	   urn.set(resource, null);
	   Assert.assertFalse(resource.equals(cloneResource));
	}
	
	@Test
	public void testLastModified() {
	   Resource resource = (Resource)ResourceFactory.createResource("/js/sample/sample.js");
	   IResource resource1 = (Resource)ResourceFactory.createResource("/js/sample/sample1.js");
	   List<IResource> dependency = new ArrayList<IResource>();
	   dependency.add(resource1);
	   resource.setDependencies(dependency);
	   Assert.assertTrue(resource.getDependencies().size() == 1);
	   Assert.assertTrue(resource.getLastModified() != -1);
	}
	
	@Test
	public void testGetExtFromPath() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	   Resource resource = (Resource)ResourceFactory.createResource("/js/sample/sample.js");
	   
	   Class<Resource> clazz = Resource.class;
	   Method method = clazz.getDeclaredMethod("getExtFromPath", String.class);
	   method.setAccessible(true);
	   Assert.assertNull(method.invoke(resource, "testPath"));
	   
	}
	
	@Test
	public void testGetOriginalContent() throws UnsupportedEncodingException {
		Resource resource = (Resource)ResourceFactory.createResource("/js/sample/sample.js");
		MockTemplateContext template = new MockTemplateContext();
		template.setContextPath("/BaseResourceTest");
		template.setResourceLocale(new ResourceLocale(new Locale("de_DE")));
		template.setSecure(false);
		ResourceRuntimeContext.reset();
		String expectedContent = "document.write(\"this is sample.js<br>\");";
		String actualContent = resource.getOriginalContent(template);
		Assert.assertEquals(expectedContent, actualContent);
		ResourceRuntimeContext.reset();
		byte[] actualBinaryContent = resource.getOriginalBinaryContent(template);
		Assert.assertEquals(expectedContent, new String(actualBinaryContent, "utf-8"));
	}
	
	@Test
	public void testGetContent() throws UnsupportedEncodingException {
	   Resource resource = (Resource)ResourceFactory.createResource("/js/sample/sample.js");
	   resource.setHandler(new MockHandler());
	   String binaryContent = new String(resource.getBinaryContent(ResourceRuntimeContext.ctx().getResourceContext()), "utf-8");
	   Assert.assertEquals("document.write(\"this is sample.js<br>\");", binaryContent);
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
	
	public static class MockHandler implements IResourceHandler {
	   @Override
	   public byte[] getBinaryContent(IResource resource, IResourceContext context) {
	      // TODO Auto-generated method stub
	      try {
            return "testBinaryContent".getBytes("utf-8");
         } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
         return null;
	   }

	   @Override
	   public String getContent(IResource resource, IResourceContext context) {
	      // TODO Auto-generated method stub
	      return null;
	   }

	   @Override
	   public String getUrl(IResource resource, IResourceContext context) {
	      // TODO Auto-generated method stub
	      return null;
	   }
	}

   
	
}
