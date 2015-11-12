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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.ebayopensource.webrex.resource.api.IInlineResource;
import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.api.IResourceContext;
import com.ebayopensource.webrex.resource.cache.ResourceCacheManager;
import com.ebayopensource.webrex.resource.impl.DefaultResourceHandler.ContentCacheValue;
import com.ebayopensource.webrex.resource.spi.IResourceHandler;

public class ResourceFactoryTest extends BaseResourceTest {
   @Test
   public void testIdVersion() {
      IResourceContext ctx = ResourceRuntimeContext.ctx().getResourceContext();
      Assert.assertEquals("webressdk", ctx.getConfig().getAppId());
      Assert.assertEquals("1.0.8", ctx.getConfig().getAppVersion());
   }

   @Test
   public void testCreateResource() {
      IResource sampleResource = ResourceFactory.createResource("/js/sample/sample.js");
      Assert.assertNotNull(sampleResource);
      Assert.assertEquals("document.write(\"this is sample.js<br>\");", new String(sampleResource.getOriginalContent()));

      IResource sampleResource2 = ResourceFactory.createWarResource("/js/sample/sample.js");
      Assert.assertNotNull(sampleResource2);
      Assert.assertEquals("document.write(\"this is sample.js<br>\");",
            new String(sampleResource2.getOriginalContent()));

      IResource img = ResourceFactory.createWarResource("/img/ebayLogo.gif");
      Assert.assertNotNull(img);
      Assert.assertTrue(img.getOriginalUrl().toExternalForm().contains("ebayLogo.gif"));

      //IResource sampleResource3 = ResourceFactory.createResourceFromSharedLib("/sample/sample.js");
      //Assert.assertNotNull(sampleResource3);
      //Assert.assertEquals("", sampleResource3.getContent());
   }

   @Test
   public void testResourceHandler() {
      IResourceContext context = ResourceRuntimeContext.ctx().getResourceContext();

      IResource sampleResource = ResourceFactory.createResource("/js/sample/sample.js");
      Assert.assertEquals("/BaseResourceTest/lrssvr/gggaszfqbq2lbniuqi2xlyvxk2b.js", sampleResource.getUrl(context));
      Assert.assertEquals(
            "document.write(\"this is sample.js<br>\");",
            new String(
                  (byte[]) ((ContentCacheValue) ResourceCacheManager.getGlobalCache("gggaszfqbq2lbniuqi2xlyvxk2b"))
                        .getContent()));

      IResource img = ResourceFactory.createWarResource("/img/ebayLogo.gif");
      Assert.assertEquals("/BaseResourceTest/lrssvr/ry4sqfvidizw3iszva2fca1haul.gif", img.getUrl(context));

      Assert.assertEquals(1242, ((byte[]) ((ContentCacheValue) ResourceCacheManager
            .getGlobalCache("ry4sqfvidizw3iszva2fca1haul")).getContent()).length);
      
   }
   
   @Test
   public void testGetContent() {
      IResourceContext context = ResourceRuntimeContext.ctx().getResourceContext();

      IResource sampleResource1 = ResourceFactory.createResource("/js/sample/sample1.js");
      IResource sampleResource2 = ResourceFactory.createResource("/js/sample/sample2.js");
      
      Assert.assertEquals("document.write(\"this is sample1.js<br>\");", sampleResource1.getContent(context));
      
      Assert.assertEquals("document.write(\"this is sample2.js<br>\");", sampleResource2.getContent(context));

      List<IResource> resources = new ArrayList<IResource>();
      resources.add(sampleResource1);
      resources.add(sampleResource2);
      IResource aggregatedResource = ResourceFactory.createAggregatedResource(resources);
      Assert.assertEquals("document.write(\"this is sample1.js<br>\");document.write(\"this is sample2.js<br>\");", aggregatedResource.getContent(context));
      
      
      
   }
   
   
   @Test
   public void testGetObfuscatedContent() {
      IResourceContext context = ResourceRuntimeContext.ctx().getResourceContext();
      IResourceHandler handler = context.getConfig().getRegistry().getDefaultResourceHandler();
      //set handler
      context.getConfig().getRegistry().setDefaultResourceHandler(new MockConfuscateResourceHandler());
      IResource sampleResource1 = ResourceFactory.createResource("/js/sample/sample1.js");
      IResource sampleResource2 = ResourceFactory.createResource("/js/sample/sample2.js");

      List<IResource> resources = new ArrayList<IResource>();
      resources.add(sampleResource1);
      resources.add(sampleResource2);
      IResource aggregatedResource = ResourceFactory.createAggregatedResource(resources);

      String content = aggregatedResource.getContent(context);
      context.setAttribute(AggregatedResource.OBFUSCATE_INLINE_AGG_RES, true);
      String obfuseContent = aggregatedResource.getContent(context);
      Assert.assertNotSame(content, obfuseContent);
      //set back the handler
      context.getConfig().getRegistry().setDefaultResourceHandler(handler);
   }
   
   @Test
   public void testInline() {
      IInlineResource resource = ResourceFactory.createInlineResource(ResourceTypeConstants.JS,
            "document.write('this is sample.');");

      Assert.assertEquals(ResourceTypeConstants.JS, resource.getUrn().getType());
      Assert.assertEquals("js.inline:/i4o1ke3h124fbh5gzbfqs5usd2y", resource.getUrn().toString());
      Assert.assertEquals("document.write('this is sample.');", resource.getInlineText());
   }

   @Test
   public void testCSSBgUrl() {
      IResource resource = ResourceFactory.createResource("/css/sample/sample2.css");
      String content = resource.getOriginalContent();

      Assert.assertTrue(content.contains("ebayerror.gif"));
      Assert.assertTrue(content.contains("/BaseResourceTest/lrssvr/ry4sqfvidizw3iszva2fca1haul.gif"));
      Assert.assertTrue(!content.contains("/img/ebayLogo.gif"));
      Assert.assertTrue(!content.contains("ebayLogo.gif"));
   }

   @Test
   public void testFlashResource() {
      IResource flash = ResourceFactory.createResource("/flash/local.swf");

      Assert.assertTrue(flash instanceof FlashResource);
      Assert.assertEquals(200, ((FlashResource) flash).getWidth());
      Assert.assertEquals(150, ((FlashResource) flash).getHeight());
   }

   @Test
   public void testTxtResource() {
      IResource resource = ResourceFactory.createResource("/mytext.txt");

      Assert.assertEquals("common", resource.getUrn().getType());
      Assert.assertEquals("this is my text", resource.getOriginalContent());
   }

   @Test
   public void testSharedResource() {
      IResource resource = ResourceFactory.createLibraryResource("/js/sample/sample1.js");
      Assert.assertEquals("document.write('this is shared sample1.js<br>');", new String(resource.getOriginalContent()));

      Assert.assertEquals(
            "Resource [m_urn=js.shared:/js/sample/sample1.js, m_libInfo=ResourceLibrary [m_id=sharedlib, m_version=1.0.0, m_type=JAR]]",
            resource.toString());
   }

   @Test
   public void testTagsResources() {
      IResource resource = ResourceFactory.createWarResource("/component.js");
      Assert.assertEquals("document.write(\"this is component sys.js<br>\");", resource.getOriginalContent());
      
      resource = ResourceFactory.createLibraryResource("/component.js");
      Assert.assertEquals("document.write(\"this is shared component sys.js<br>\");", resource.getOriginalContent());

   }
   
   @Test
   public void testcreateResourceByEl() {
	   /*String el = "js.sample.sample2_css";
	   IResource resource = ResourceFactory.createResourceByEl(el);
	   Assert.assertTrue(resource instanceof IResource);*/
   }

}

class MockConfuscateResourceHandler implements IResourceHandler {
   @Override
   public byte[] getBinaryContent(IResource resource, IResourceContext context) {
      return null;
   }
   @Override
   public String getContent(IResource resource, IResourceContext context) {
      return "resource";
   }
   
   @Override
   public String getUrl(IResource resource, IResourceContext context) {
      return null;
   }

}
