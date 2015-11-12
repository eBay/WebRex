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

import com.ebayopensource.webrex.resource.api.IResource;

public class ResourceLocaleTest extends BaseResourceTest {

   @Test
   public void testWarResourceLocale() {
      IResource resource = ResourceFactory.createResource("/js/sample/sample.js");
      Assert.assertEquals("document.write(\"this is sample.js<br>\");", new String(resource.getOriginalContent()));

      ResourceRuntimeContext.ctx().setLocale(ResourceLocale.fromExternal("de_DE"));

      resource = ResourceFactory.createResource("/js/sample/sample.js");
      Assert.assertEquals("document.write(\"this is de_DE sample.js<br>\");", new String(resource.getOriginalContent()));
   }

   @Test
   public void testSharedResourceLocale() {
      IResource resource = ResourceFactory.createLibraryResource("/js/sample/sample1.js");
      Assert.assertEquals("document.write('this is shared sample1.js<br>');", new String(resource.getOriginalContent()));

      ResourceRuntimeContext.ctx().setLocale(ResourceLocale.fromExternal("de_DE"));

      resource = ResourceFactory.createLibraryResource("/js/sample/sample1.js");
      Assert.assertEquals("document.write('this is shared sample1.js<br>');", new String(resource.getOriginalContent()));
   }
}
