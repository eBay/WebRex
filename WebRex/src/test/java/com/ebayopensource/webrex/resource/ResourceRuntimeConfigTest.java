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
import java.util.Map;

import junit.framework.Assert;
import org.junit.Test;



public class ResourceRuntimeConfigTest {
	

	@Test
	public void testResourceRuntimeConfig() {
		ResourceRuntimeConfig cfg = new ResourceRuntimeConfig("/ResourceRuntimeConfigTest", new File("warRoot"));
		cfg.setOptimizationCommand("test", "testCommand");
		Assert.assertEquals("testCommand", cfg.getOptimizationCommand("test"));
		cfg.setProperty("testProperties", "123");
		Map<String, String> properties = cfg.getProperties();
		Assert.assertEquals("/resources", properties.get("resource.base"));
		Assert.assertEquals("123", properties.get("testProperties"));
		Assert.assertTrue(cfg.loadResource("local", false).isEmpty());
		Assert.assertTrue(cfg.loadResource("shared", false).isEmpty());
		cfg.setDev(false);
		cfg.setOptimizationEnabled(false);
		cfg.setStatisticsEnabled(false);
		Assert.assertNull(cfg.gettVariationResource(null));
	}
	
	@Test
	public void testConstructorException() {
	   try {
	      new ResourceRuntimeConfig("/ResourceRuntimeConfigTest", new File("warRoot"));
	   } catch(Exception e) {
	      Assert.assertTrue(e instanceof IllegalArgumentException);
	      Assert.assertTrue(e.getMessage().contains("Unable to resolve WarRoot(%s)!"));
	   }
	}
}
