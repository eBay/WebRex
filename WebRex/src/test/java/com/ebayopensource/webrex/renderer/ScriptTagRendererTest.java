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

package com.ebayopensource.webrex.renderer;





import junit.framework.Assert;

import org.junit.Test;

import com.ebayopensource.webrex.resource.BaseResourceTest;
import com.ebayopensource.webrex.resource.ResourceTypeConstants;
import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.impl.ResourceDeferProcessor;
import com.ebayopensource.webrex.resource.tag.ITag;
import com.ebayopensource.webrex.resource.tag.UseScriptTag;
import com.ebayopensource.webrex.resource.tag.handler.MockNoUrlResourceHandler;



public class ScriptTagRendererTest extends BaseResourceTest{
	

	@Test
	public void testNoUrlJsRenderResource() {
		//logic for resource can not find url from handler
		UseScriptTag tag = new UseScriptTag(ResourceTypeConstants.JS);
		tag.setEnv(new MockTagEnv());
		tag.getModel().setValue("/js/sample/sample_sys1.js");
		tag.getModel().setAttribute("target", "body");
		String expected = "<script type=\"text/javascript\">document.write(\"this is sample sys1.js<br>\");</script>";
		String actual = render(tag);
		Assert.assertEquals(expected, actual);
		
		

	}
	
	@Test
	public void testNoUrlCssRenderResource() {
		UseScriptTag tag = new UseScriptTag(ResourceTypeConstants.CSS);
		tag.setEnv(new MockTagEnv());
		tag.getModel().setValue("/css/sample/sample.css");
		tag.getModel().setAttribute("target", "body");
		String expected = "<style type=\"text/css\">head {" + System.getProperty("line.separator")
				+ "}" + System.getProperty("line.separator")
				+ "</style>";
		String actual = render(tag);
		Assert.assertEquals(expected, actual);
	}
	
	protected String render(ITag tag) {
		
		//render without url
		tag.start();
		IResource resource = tag.build();
		resource.setHandler(new MockNoUrlResourceHandler());
		String actual = tag.render(resource);
		
		// process marker
		if (actual != null) {
			StringBuilder sb = new StringBuilder(actual);
			new ResourceDeferProcessor().process(sb);
			actual = sb.toString();
		}
		tag.end();
		return actual;
	}
	
}
