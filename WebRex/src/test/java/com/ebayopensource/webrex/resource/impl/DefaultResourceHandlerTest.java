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
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ebayopensource.webrex.resource.ResourceFactory;
import com.ebayopensource.webrex.resource.ResourceInitializer;
import com.ebayopensource.webrex.resource.ResourceLibrary;
import com.ebayopensource.webrex.resource.ResourceLocale;
import com.ebayopensource.webrex.resource.ResourceRuntime;
import com.ebayopensource.webrex.resource.ResourceRuntimeContext;
import com.ebayopensource.webrex.resource.ResourceUrn;
import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.api.IResourceContext;
import com.ebayopensource.webrex.resource.api.IResourceLibrary;
import com.ebayopensource.webrex.resource.api.IResourceLibrary.LibraryType;
import com.ebayopensource.webrex.resource.api.IResourceLocale;
import com.ebayopensource.webrex.resource.api.IResourceUrn;
import com.ebayopensource.webrex.resource.impl.DefaultResourceHandler.ContentCacheValue;
import com.ebayopensource.webrex.util.Checksum;

public class DefaultResourceHandlerTest {
	
	private DefaultResourceHandler m_handler;
	
	@Before
	public void setup() {
		ResourceRuntimeContext.setup();
		m_handler = new DefaultResourceHandler();
	}

	@After
	public void tearDown() {
		ResourceRuntimeContext.reset();
	}
	
	@BeforeClass
	public static void init() {
		ResourceInitializer.initialize("/DefaultResourceHandlerTest", new File(
				"warRoot"), DefaultResourceHandlerTest.class.getClassLoader());
	}
	
	@AfterClass
	public static void destroy() {
		ResourceRuntime.INSTANCE.reset();
	}
	
	@Test
	public void testAsByteArray() throws NoSuchMethodException, SecurityException, ReflectiveOperationException, IllegalArgumentException, InvocationTargetException, UnsupportedEncodingException {
		Class<DefaultResourceHandler> clazz = DefaultResourceHandler.class;
		Method method = clazz.getDeclaredMethod("asByteArray", Object.class);
		method.setAccessible(true);
		String str = "test string for asByteArray";
		Assert.assertEquals(str, new String((byte[])method.invoke(m_handler, str)));
		byte[] byteArray = str.getBytes("utf-8");
		Assert.assertEquals(str, new String((byte[])method.invoke(m_handler, byteArray)));
		try {
			method.invoke(m_handler, new Object());
		} catch (Exception e) {
			Assert.assertTrue(e instanceof Exception);
		}
	}
	
	@Test
	public void testAsString()  throws NoSuchMethodException, SecurityException, ReflectiveOperationException, IllegalArgumentException, InvocationTargetException, UnsupportedEncodingException {
		Class<DefaultResourceHandler> clazz = DefaultResourceHandler.class;
		Method method = clazz.getDeclaredMethod("asString", Object.class);
		method.setAccessible(true);
		String str = "test string for asString";
		Assert.assertEquals(str, (String)method.invoke(m_handler, str));
		byte[] byteArray = str.getBytes("utf-8");
		Assert.assertEquals(str, (String)method.invoke(m_handler, byteArray));
		String result = (String)method.invoke(m_handler, new Object());
		Assert.assertTrue(result.contains("java.lang.Object"));
	}
	
	@Test
	public void testBuildCheckSum() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class<DefaultResourceHandler> clazz = DefaultResourceHandler.class;
		Method method = clazz.getDeclaredMethod("buildCheckSum", Object.class);
		method.setAccessible(true);
		String str = "test string for buildCheckSum";
		Assert.assertEquals(Checksum.checksum(str), (String)method.invoke(m_handler, str));
		try {
			method.invoke(m_handler, new Object());
		} catch (Exception e) {
			Assert.assertTrue(e instanceof Exception);
		}
	}
	
	@Test
	public void testProcessResourceContent() {
		IResource resource = ResourceFactory.createResource("/js/sample/sample.js");
		IResourceContext context = ResourceRuntimeContext.ctx().getResourceContext();
		String actualContent = new String(m_handler.processResourceContent(resource, context));
		Assert.assertEquals("document.write(\"this is sample.js<br>\");", actualContent);
	}
	
	@Test
	public void testContentCacheValue () {
		ContentCacheValue cacheValue = new ContentCacheValue(("test content").getBytes(), "mime", System.currentTimeMillis());
		cacheValue.getContent();
		cacheValue.getLastModified();
		cacheValue.toString();
	}
	
	@Test
	public <T> void testCacheKey() throws  Exception {
	   Class<DefaultResourceHandler> clazz = DefaultResourceHandler.class;
	   Class<?>[] clazzes = clazz.getDeclaredClasses();
	   for(Class clazz1: clazzes) {
	      if(clazz1.getSimpleName().contains("CacheKey")) {
	         Constructor constructor = clazz1.getConstructor(IResourceUrn.class, IResourceLibrary.class, IResourceLocale.class);
	         IResourceUrn urn = new ResourceUrn("js", "local", "/js/sample/sample.js");
	         IResourceUrn urn1 = new ResourceUrn("js", "local", "/js/sample/sample1.js");
	         IResourceLibrary library = new ResourceLibrary("testLibrary", "0.0.1", LibraryType.JAR);
	         IResourceLibrary library1 = new ResourceLibrary("testLibrary1", "0.0.1", LibraryType.JAR);
	         IResourceLocale locale = new ResourceLocale(new Locale("en", "US"));
	         IResourceLocale locale1 = new ResourceLocale(new Locale("de", "DE"));
	         Object instance1 = constructor.newInstance(urn, library, locale);
	         Assert.assertTrue(instance1.equals(instance1));
	         Assert.assertFalse(instance1.equals(null));
	         Assert.assertFalse(instance1.equals(new Object()));
	         Object instance2 = constructor.newInstance(urn, null, locale);
	         Assert.assertFalse(instance2.equals(instance1));
	         Object instance3 = constructor.newInstance(urn, library1, locale);
	         Assert.assertFalse(instance3.equals(instance1));
	         Object instance4 = constructor.newInstance(null, library, locale);
	         Assert.assertFalse(instance4.equals(instance1));
	         Object instance5 = constructor.newInstance(urn1, library, locale);
	         Assert.assertFalse(instance5.equals(instance1));
	         Object instance6 = constructor.newInstance(urn, library, locale1);
	         Assert.assertFalse(instance6.equals(instance1));
	         Object instance7 = constructor.newInstance(urn, library, locale);
	         
	         //get method
	         Method method = clazz1.getDeclaredMethod("setOptimizationCommand", String.class);
	         method.invoke(instance1, "test Cmd 1");
	         Assert.assertFalse(instance7.equals(instance1));
	         method.invoke(instance7, "test Cmd 7");
	         Assert.assertFalse(instance7.equals(instance1));
	      }
	   }
	}
	
	
}
