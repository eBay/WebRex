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

import junit.framework.Assert;

import org.junit.Test;

import com.ebayopensource.webrex.resource.expression.ResourceELEvaluator;
import com.ebayopensource.webrex.resource.impl.DefaultResourceHandler;
import com.ebayopensource.webrex.resource.impl.LocalResourceLoader;
import com.ebayopensource.webrex.resource.impl.SharedResourceResolver;

public class ResourceRegistryTest {

	
	@Test
	public void testResourceRegistry() {
		ResourceRuntimeConfig cfg = new ResourceRuntimeConfig("/ResourceRegistryTest", new File("warRoot"));
		ResourceRegistry registry = new ResourceRegistry(cfg);
		Assert.assertNull(registry.getDefaultResourceFactory());
		Assert.assertNull(registry.getDefaultResourceHandler());
		Assert.assertNull(registry.getDefaultResourceResolver("local"));
		Assert.assertNull(registry.getResourceExtensions("js"));
		Assert.assertTrue(registry.getResourceTypes().isEmpty());
		registry.registerResourceHandler("js", new DefaultResourceHandler());
		registry.registerResourceLoader("local", new LocalResourceLoader(cfg));
		Assert.assertTrue(registry.getResourceHandler("js") instanceof DefaultResourceHandler);
		Assert.assertTrue(registry.getResourceLoader("local") instanceof LocalResourceLoader);
		registry.registerResolver("js", "shared", new SharedResourceResolver());
		registry.registerExpressionEvaluator("test", new ResourceELEvaluator());
	}
}
