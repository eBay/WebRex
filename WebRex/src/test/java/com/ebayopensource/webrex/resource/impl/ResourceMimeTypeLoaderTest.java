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

import junit.framework.Assert;

import org.junit.Test;

import com.ebayopensource.webrex.resource.BaseResourceTest;
import com.ebayopensource.webrex.resource.ResourceRuntimeContext;
import com.ebayopensource.webrex.resource.spi.IResourceRegistry;

public class ResourceMimeTypeLoaderTest extends BaseResourceTest {

   @Test
   public void testMimeType() {
      IResourceRegistry registry = ResourceRuntimeContext.ctx().getConfig().getRegistry();
      String mimeType = registry.getResourceMimeType("js");
      Assert.assertEquals("application/x-javascript", mimeType);

      mimeType = registry.getResourceMimeType("rhtml");
      Assert.assertEquals("application/x-javascript", mimeType);

      mimeType = registry.getResourceMimeType("dust");
      Assert.assertEquals("application/x-javascript", mimeType);

      mimeType = registry.getResourceMimeType("jpg");
      Assert.assertEquals("image/jpeg", mimeType);

      mimeType = registry.getResourceMimeType("css");
      Assert.assertEquals("text/css", mimeType);

      mimeType = registry.getResourceMimeType("png");
      Assert.assertEquals("image/png", mimeType);

      Assert.assertEquals(null, registry.getResourceMimeType("#Extension"));
   }
}
