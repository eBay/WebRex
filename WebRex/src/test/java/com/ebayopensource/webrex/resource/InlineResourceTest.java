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

import org.junit.Assert;
import org.junit.Test;

import com.ebayopensource.webrex.resource.api.IResourceContext;
import com.ebayopensource.webrex.resource.api.IResourceLocale;
import com.ebayopensource.webrex.resource.api.ITemplateContext;

public class InlineResourceTest extends BaseResourceTest{

   @Test
   public void testInlineResource() {
      String text = "document.write(\"this is sample.js<br>\");";
      InlineResource inline = new InlineResource(text, "js");
      Assert.assertEquals(0, inline.getLastModified());
      IResourceContext context = ResourceRuntimeContext.ctx().getResourceContext();
      Assert.assertEquals(text, inline.getContent(context));
      Assert.assertEquals(text, new String(inline.getOriginalBinaryContent(new MockTemplateContext())));
      
      InlineResource inline1 = new InlineResource(new ResourceUrn("js", "local", "/js/sample/sample.js"), null, null, null);
      Assert.assertNotNull(inline1);
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
}
