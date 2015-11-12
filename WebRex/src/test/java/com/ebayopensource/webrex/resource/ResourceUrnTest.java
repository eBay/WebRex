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

import junit.framework.Assert;

import org.junit.Test;

public class ResourceUrnTest {
	
	@Test
	public void testUrnEquals() {
		ResourceUrn urn = new ResourceUrn("js", "local", "/js/sample/sample1.js");
		ResourceUrn urn1 = new ResourceUrn(null, "local", "/js/sample/sample1.js");
		ResourceUrn urn2 = new ResourceUrn("js", null, "/js/sample/sample1.js");
		ResourceUrn urn4 = null;
		ResourceUrn urn5 = new ResourceUrn("css", "local", "/js/sample/sample1.js");
		ResourceUrn urn6 = new ResourceUrn("js", "global", "/js/sample/sample1.js");
		Assert.assertFalse(urn.equals(urn4));
		Assert.assertFalse(urn1.equals(urn));
		Assert.assertFalse(urn.equals(urn5));
		Assert.assertFalse(urn2.equals(urn));
		Assert.assertFalse(urn.equals(urn6));
	}
}
