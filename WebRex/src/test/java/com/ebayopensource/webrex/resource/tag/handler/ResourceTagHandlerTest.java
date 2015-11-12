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

package com.ebayopensource.webrex.resource.tag.handler;


import java.io.File;





import javax.servlet.jsp.JspException;




import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ebayopensource.webrex.resource.ResourceInitializer;
import com.ebayopensource.webrex.resource.ResourceRuntime;
import com.ebayopensource.webrex.resource.ResourceRuntimeContext;
import com.ebayopensource.webrex.resource.ResourceTypeConstants;
import com.ebayopensource.webrex.resource.tag.ITag;
import com.ebayopensource.webrex.resource.tag.ITagModel;
import com.ebayopensource.webrex.resource.tag.ResourceTag;
import com.ebayopensource.webrex.resource.tag.SetTag;
import com.ebayopensource.webrex.resource.tag.SlotTag;
import com.ebayopensource.webrex.resource.tag.UseScriptTag;

public class ResourceTagHandlerTest {
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
		ResourceInitializer.initialize("/ResourceTagHandlerTest", new File(
				"warRoot"), ResourceTagHandlerTest.class.getClassLoader());
		ResourceInitializer.initializeResourceBundles(ResourceRuntime.INSTANCE.getConfig(),
				ResourceTagHandlerTest.class.getClassLoader());
	}
	
	@AfterClass
	public static void destroy() {
		ResourceRuntime.INSTANCE.reset();
	}
	
	@Test
	public void testCssSlotTagHandler() {
		CssSlotTagHandler cssSlotTagHandler = new CssSlotTagHandler();
		String expectedSlotId = "css-slot";
		ITagModel model = cssSlotTagHandler.getModel();
		SlotTag tag = (SlotTag)cssSlotTagHandler.getTag();
		Assert.assertEquals(ResourceTypeConstants.CSS, tag.getSlotType());
		cssSlotTagHandler.setId(expectedSlotId);
		cssSlotTagHandler.setMedia("testMedia");
		String actualSlotId = model.getAttributes().get("id").toString();
		Assert.assertEquals(expectedSlotId, actualSlotId);
	}
	
	@Test
	public void testJsSlotTagHandler() {
		JsSlotTagHandler jsSlotTagHandler = new JsSlotTagHandler();
		String expectedSlotId = "js-slot";
		ITagModel model = jsSlotTagHandler.getModel();
		SlotTag tag = (SlotTag)jsSlotTagHandler.getTag();
		Assert.assertEquals(ResourceTypeConstants.JS, tag.getSlotType());
		jsSlotTagHandler.setId(expectedSlotId);
		String actualSlotId = model.getAttributes().get("id").toString();
		Assert.assertEquals(expectedSlotId, actualSlotId);
	}
	
	@Test
	public void testUseCssTagHandler() {
		UseCssTagHandler useCssTagHandler = new UseCssTagHandler();
		String expectedTarget = "css-slot";
		ITagModel model = useCssTagHandler.getModel();
		UseScriptTag tag = (UseScriptTag)useCssTagHandler.getTag();
		Assert.assertEquals(ResourceTypeConstants.CSS, tag.getModel().getExpectedResourceType());
		useCssTagHandler.setTarget(expectedTarget);
		String actualTarget = model.getAttributes().get("target").toString();
		Assert.assertEquals(expectedTarget, actualTarget);
	}
	
	@Test
	public void testUseJsTagHandler() {
		UseJsTagHandler useJsTagHandler = new UseJsTagHandler();
		String expectedTarget = "js-slot";
		ITagModel model = useJsTagHandler.getModel();
		UseScriptTag tag = (UseScriptTag)useJsTagHandler.getTag();
		Assert.assertEquals(ResourceTypeConstants.JS, tag.getModel().getExpectedResourceType());
		useJsTagHandler.setTarget(expectedTarget);
		String actualTarget = model.getAttributes().get("target").toString();
		Assert.assertEquals(expectedTarget, actualTarget);
	}
	
	
	@Test
	public void testAjaxTokenTagHandler() {
		TokenTagHandler tokenTagHandler = new TokenTagHandler();
		String expectedType = "js";
		ITagModel model = tokenTagHandler.getModel();
		tokenTagHandler.setType(expectedType);
		String actualType = model.getAttributes().get("type").toString();
		Assert.assertEquals(expectedType, actualType);
	}
	
	@Test
	public void testImageTagHandler() throws JspException {
		ImageTagHandler imgTagHandler = new ImageTagHandler();
		boolean expectedSecurity = true;
		ITagModel model = imgTagHandler.getModel();
		ResourceTag tag = (ResourceTag)imgTagHandler.getTag();
		Assert.assertEquals(ResourceTypeConstants.IMAGE, tag.getModel().getExpectedResourceType());
		imgTagHandler.setSecure(expectedSecurity);
		String actualTarget = model.getAttributes().get("secure").toString();
		Assert.assertEquals(String.valueOf(expectedSecurity), actualTarget);

	}
	
	@Test
	public void testBundleTagHandler() {
		BundleTagHandler bundleTagHandler = new BundleTagHandler();
		String expectedId = "testBundle";
		ITagModel model = bundleTagHandler.getModel();
		bundleTagHandler.setId(expectedId);
		String actualId = model.getAttributes().get("id").toString();
		Assert.assertEquals(expectedId, actualId);
	}
	
	@Test
	public void testBeanTagHandler() {
		BeanTagHandler beanTagHandler = new BeanTagHandler();
		String expectedId = "testBundle";
		ITagModel model = beanTagHandler.getModel();
		beanTagHandler.setId(expectedId);
		String actualId = model.getAttributes().get("id").toString();
		Assert.assertEquals(expectedId, actualId);
	}
	
	@Test
	public void testResourceTagHandler() throws JspException {
		UseJsTagHandler useJsTagHandler = new UseJsTagHandler();
		String expectedTarget = "js-slot";
		useJsTagHandler.setTarget(expectedTarget);
		useJsTagHandler.setValue("/js/sample/sample1.js");
		int actualStartResults = useJsTagHandler.doStartTag();
		Assert.assertEquals(2, actualStartResults);
		try{
			useJsTagHandler.doAfterBody();
		}catch(Exception e){
			Assert.assertTrue(e instanceof NullPointerException);
		}
		try{
			useJsTagHandler.doEndTag();
		}catch(Exception e){
			Assert.assertTrue(e instanceof NullPointerException);
		}
	}
	
	@Test
	public void testSetTagHandlerTest() {
	   SetTagHandler setTagHandler = new SetTagHandler();
	   ITag tag = setTagHandler.createTag();
	   Assert.assertTrue(tag instanceof SetTag);
	   setTagHandler.setId("testId");
	}
	
}
