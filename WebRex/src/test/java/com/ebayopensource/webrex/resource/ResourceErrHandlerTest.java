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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.api.IResourceContext;
import com.ebayopensource.webrex.resource.expression.ResBeanExpression;
import com.ebayopensource.webrex.resource.spi.IResourceErrorHandler;
import com.ebayopensource.webrex.resource.spi.IResourceHandler;
import com.ebayopensource.webrex.resource.tag.ResourceTag;
import com.ebayopensource.webrex.resource.tag.UseScriptTag;

public class ResourceErrHandlerTest extends BaseResourceTest {
   private StringBuilder m_sb = new StringBuilder(128);

   @Before
   public void setup() {
      super.setup();
      IResourceErrorHandler handler = new MockResourceErrorHandler(m_sb);
      ResourceRuntimeContext.ctx().getConfig().getRegistry().setErrorHandler(handler);
   }

   @After
   public void tearDown() {
      ResourceRuntimeContext.reset();
      m_sb.setLength(0);
   }

/*   @Test
   public void testExpression() {
      ResourceRuntimeContext.ctx().getConfig().setResourceErrPolicy(ResourceErrPolicy.FAIL_FAST);
      ResBeanExpression bean = new ResBeanExpression();
      Assert.assertNull(eval(bean, "a.b.c").evaluate());
      Assert.assertEquals(
            "e:com.ebayopensource.webrex.resource.ResourceException: No resolver registered for resource(a) in namespace(b)!;v:a.b:/c",
            m_sb.toString());
   }*/

   @Test
   public void testHandler() {
      IResource sampleResource = ResourceFactory.createResource("/js/sample/sample.js");
      Assert.assertNotNull(sampleResource);

      sampleResource.setHandler(new McokResourceHandler());

      IResourceContext resourceContext = ResourceRuntimeContext.ctx().createResourceContext();
      resourceContext.setAttribute("get_handler_content", true);
      Assert.assertEquals(null, sampleResource.getUrl(resourceContext));
      Assert.assertEquals(
            "e:com.ebayopensource.webrex.resource.ResourceException: Error get url on resource(js.local:/js/sample/sample.js).;v:java.lang.UnsupportedOperationException",
            m_sb.toString());

      m_sb.setLength(0);
      Assert.assertEquals(null, sampleResource.getContent(resourceContext));
      Assert.assertEquals(
            "e:com.ebayopensource.webrex.resource.ResourceException: Error get content on resource(js.local:/js/sample/sample.js).;v:java.lang.UnsupportedOperationException",
            m_sb.toString());

      m_sb.setLength(0);
      Assert.assertEquals(null, sampleResource.getBinaryContent(resourceContext));
      Assert.assertEquals(
            "e:com.ebayopensource.webrex.resource.ResourceException: Error get content on resource(js.local:/js/sample/sample.js).;v:java.lang.UnsupportedOperationException",
            m_sb.toString());
   }

/*   @Test
   public void testResource() {
      IResource sampleResource = ResourceFactory.createResource("/js/sample/sample.js");
      Assert.assertNotNull(sampleResource);
      Assert.assertEquals("document.write(\"this is sample.js<br>\");", new String(sampleResource.getOriginalContent()));

      IResource sampleResource2 = ResourceFactory.createWarResource("/js/sample/sample_unknown.js");
      Assert.assertNull(sampleResource2);
      Assert.assertEquals(
            "e:com.ebayopensource.webrex.resource.ResourceException: Error resloving resource(js.local:/js/sample/sample_unknown.js).;v:js.local:/js/sample/sample_unknown.js",
            m_sb.toString());
   }*/

   @Test
   public void testTagBuild() {
      ResourceTag imageTag = new ResourceTag();
      imageTag.getModel().setExpectedResourceType(ResourceTypeConstants.IMAGE);
      imageTag.setEnv(new MockTagEnv());

      String expected = null;
      assertRender(expected, imageTag);
      Assert.assertEquals(
            "e:com.ebayopensource.webrex.resource.ResourceException: Error build tag(class com.ebayopensource.webrex.resource.tag.ResourceTag) with value(null)!;v:ResourceTagModel [m_bodyContent=null, m_dynamicAttributes=null, m_value=null]",
            m_sb.toString());
   }

   @Test
   public void testTagRender() {
      UseScriptTag tag = new UseScriptTag(ResourceTypeConstants.JS);
      tag.setEnv(new MockTagEnv());
      tag.getModel().setValue("/js/sample/sample.js");
      tag.getModel().setAttribute("renderType", "unknown");

      Assert.assertEquals(null, render(tag, true));
      Assert.assertEquals(
            "e:com.ebayopensource.webrex.resource.ResourceException: Error render tag(class com.ebayopensource.webrex.resource.tag.UseScriptTag) with resource(js.local:/js/sample/sample.js)!;v:js.local:/js/sample/sample.js",
            m_sb.toString());
   }

   private static class McokResourceHandler implements IResourceHandler {

      @Override
      public byte[] getBinaryContent(IResource resource, IResourceContext context) {
         throw new UnsupportedOperationException();
      }

      @Override
      public String getContent(IResource resource, IResourceContext context) {
         throw new UnsupportedOperationException();
      }

      @Override
      public String getUrl(IResource resource, IResourceContext context) {
         throw new UnsupportedOperationException();
      }

   }

   private static class MockResourceErrorHandler implements IResourceErrorHandler {
      private StringBuilder m_sb;

      public MockResourceErrorHandler(StringBuilder sb) {
         m_sb = sb;
      }

      @Override
      public Object handle(ResourceException e, Object value) {
         m_sb.append("e:" + e).append(";v:" + value);
         return null;
      }

   }
}
