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

package com.ebayopensource.webrex.resource.tag;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.ebayopensource.webrex.resource.BaseResourceTest;
import com.ebayopensource.webrex.resource.ExternalResource;
import com.ebayopensource.webrex.resource.ResourceAggregator;
import com.ebayopensource.webrex.resource.ResourceFactory;
import com.ebayopensource.webrex.resource.ResourceInitializer;
import com.ebayopensource.webrex.resource.ResourceModel;
import com.ebayopensource.webrex.resource.ResourceRuntime;
import com.ebayopensource.webrex.resource.ResourceRuntimeContext;
import com.ebayopensource.webrex.resource.ResourceTypeConstants;
import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.cache.ResourceCacheManager;
import com.ebayopensource.webrex.resource.impl.DefaultResourceHandler.ContentCacheValue;
import com.ebayopensource.webrex.resource.tag.ITag.State;
import com.ebayopensource.webrex.resource.tag.SlotTag.SlotTemplateModel;

public class ResourceTagTest extends BaseResourceTest {
   
   @BeforeClass
   public static void init() {
      ResourceInitializer.initialize("/BaseResourceTest", new File("warRoot"), BaseResourceTest.class.getClassLoader());
      ResourceInitializer.initializeResourceBundles(ResourceRuntime.INSTANCE.getConfig(),
            BaseResourceTest.class.getClassLoader());
   }
   
   @Test
   public void testAggInlineJs() {
      SlotTag slotTag = new SlotTag(ResourceTypeConstants.JS);
      slotTag.setSlotType("js");
      slotTag.setEnv(new MockTagEnv());
      slotTag.getModel().setAttribute("id", "exec");
      slotTag.getModel().setAttribute("renderType", "inline");
      String marker = render(slotTag, false);

      UseScriptTag tag1 = new UseScriptTag(ResourceTypeConstants.JS);
      tag1.setEnv(new MockTagEnv());
      tag1.getModel().setContent("document.write('1234');");
      tag1.getModel().setAttribute("target", "exec");
      assertRender("", tag1, true);

      UseScriptTag tag2 = new UseScriptTag(ResourceTypeConstants.JS);
      tag2.setEnv(new MockTagEnv());
      tag2.getModel().setContent("document.write('5678');");
      tag2.getModel().setAttribute("target", "exec");
      assertRender("", tag2, true);

      String expected = "<script type=\"text/javascript\">document.write('1234');document.write('5678');</script>";
      assertMarker(expected, marker);
      
      
   }

/*   @Test
   public void testAggExternCss() {
      SlotTag slotTag = new SlotTag(ResourceTypeConstants.CSS);
      slotTag.setSlotType("css");
      slotTag.setEnv(new MockTagEnv());
      slotTag.getModel().setAttribute("id", "exec");
      slotTag.getModel().setAttribute("renderType", "externalized");
      render(slotTag, false);

      UseScriptTag tag1 = new UseScriptTag(ResourceTypeConstants.CSS);
      tag1.setEnv(new MockTagEnv());
      tag1.getModel().setAttribute("target", "exec");
      tag1.start();
      ExternalResource resource = new ExternalResource("http://hostname/rs/v/ifuwbu2qxu0z1kfkhtu1l0yhsaa.css", "css");
      resource.setCanAggregate(true);
      tag1.render(resource);
      tag1.end();

      UseScriptTag tag2 = new UseScriptTag(ResourceTypeConstants.CSS);
      tag2.setEnv(new MockTagEnv());
      tag2.getModel().setAttribute("target", "exec");
      tag1.start();
      ExternalResource resource2 = new ExternalResource("http://hostname/rs/v/4sx5r4zm2a3axfmranpl4nhh3ew.css", "css");
      resource2.setCanAggregate(true);
      tag1.render(resource2);
      tag1.end();

      ResourceModel model = ResourceRuntimeContext.ctx().getModel();
      ResourceAggregator aggregator = new ResourceAggregator(model);
      List<IResource> resources = aggregator.getSlotResourcesWithGroup("exec", "css");
      Assert.assertNotNull(resources);
      Assert.assertEquals(1, resources.size());
      Assert.assertEquals("Resource [m_urn=css.agg:/http://hostname/rs/v/ifuwbu2qxu0z1kfkhtu1l0yhsaa.css;/http://hostname/rs/v/4sx5r4zm2a3axfmranpl4nhh3ew.css, m_libInfo=null]", resources.get(0).toString());
   }*/

   @Test
   public void testBundle() {
      BundleTag tag = new BundleTag();
      tag.setEnv(new MockTagEnv());
      tag.getModel().setAttribute("id", "sample");

      String expected = "<script src=\"/BaseResourceTest/lrssvr/bqx2otoe0y1ajaik4mqfed0qqau.js\" type=\"text/javascript\"></script>";
      assertRender(expected, tag);
   }

   @Test
   public void testDeDupTokenTag() {
      List<String> urnsList = new ArrayList<String>();
      urnsList.add("js.local:/js/sample/sample_sys1.js");
      urnsList.add("js.local:/js/sample/sample1.js");
      urnsList.add("js.local:/js/sample/sample2.js");
      Collections.sort(urnsList);
      String tokenStr = urnsList.toString();
      String token = createToken(tokenStr.substring(1, tokenStr.length()-1));

      SlotTag slotTag = new SlotTag(ResourceTypeConstants.JS);
      slotTag.setSlotType("js");
      slotTag.setEnv(new MockTagEnv());
      slotTag.getModel().setAttribute("id", "body");
      render(slotTag, false);

      UseScriptTag tag1 = new UseScriptTag(ResourceTypeConstants.JS);
      tag1.setEnv(new MockTagEnv());
      tag1.getModel().setValue("/js/sample/sample_sys1.js");
      tag1.getModel().setAttribute("target", "body");
      assertRender("", tag1, true);

      tag1 = new UseScriptTag(ResourceTypeConstants.JS);
      tag1.setEnv(new MockTagEnv());
      tag1.getModel().setValue("/js/sample/sample1.js");
      tag1.getModel().setAttribute("target", "body");
      assertRender("", tag1, true);

      tag1 = new UseScriptTag(ResourceTypeConstants.JS);
      tag1.setEnv(new MockTagEnv());
      tag1.getModel().setValue("/js/sample/sample2.js");
      tag1.getModel().setAttribute("target", "body");
      assertRender("", tag1, true);

      TokenTag tokenTag = new TokenTag();
      tokenTag.setEnv(new MockTagEnv());
      tokenTag.getModel().setAttribute("type", "js");
      String marker2 = render(tokenTag, false);

      ResourceModel model = ResourceRuntimeContext.ctx().getResourceAggregator().getModel();
      model.processModel();

      //token assertion
      assertMarker(token, marker2);
      
      BundleTag bundleTag = new BundleTag();
      Assert.assertNull(bundleTag.build());

   }

   @Test
   public void testExternalResource() {
      UseScriptTag tag1 = new UseScriptTag(ResourceTypeConstants.JS);
      tag1.setEnv(new MockTagEnv());
      tag1.getModel().setValue(ResourceFactory.createExternalResource("js", "http://myurl"));
      assertRender("<script src=\"http://myurl\" type=\"text/javascript\"></script>", tag1, true);
   }

   @Test
   public void testFlash() {
      ResourceTag imageTag = new ResourceTag();
      imageTag.getModel().setExpectedResourceType(ResourceTypeConstants.FLASH);
      imageTag.setEnv(new MockTagEnv());
      imageTag.getModel().setValue(ResourceFactory.createResource("/flash/local.swf"));

      String expected = "<object classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000\" codebase=\"http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=11,1,102,62\" width=\"200\" height=\"150\"><param name=\"movie\" value=\"/BaseResourceTest/lrssvr/byn400xkcy1excchtj2hkqshhqg.swf\"/><embed src=\"/BaseResourceTest/lrssvr/byn400xkcy1excchtj2hkqshhqg.swf\" width=\"200\" height=\"150\" type=\"application/x-shockwave-flash\"  pluginspage=\"http://www.macromedia.com/go/getflashplayer\"></embed></object>";
      assertRender(expected, imageTag);
   }

   @Test
   public void testHtmlTagAttrs() {
      UseScriptTag tag2 = new UseScriptTag(ResourceTypeConstants.JS);
      tag2.setEnv(new MockTagEnv());
      tag2.getModel().setValue("/js/sample/sample2.js");
      tag2.getModel().setAttribute("target", "body");
      tag2.getModel().setAttribute("id", "id1");
      tag2.getModel().setAttribute("htmlid", "htmlid");

      String expected = "<script src=\"/BaseResourceTest/lrssvr/kz1uuubssy1nfnr3swwhv3ipl2p.js\" type=\"text/javascript\" id=\"htmlid\"></script>";
      assertRender(expected, tag2, true);
   }

   @Test
   public void testImage() {
      ResourceTag imageTag = new ResourceTag();
      imageTag.getModel().setExpectedResourceType(ResourceTypeConstants.IMAGE);
      imageTag.setEnv(new MockTagEnv());
      imageTag.getModel().setValue(ResourceFactory.createResource("/img/ebayLogo.gif"));

      String expected = "<img src=\"/BaseResourceTest/lrssvr/ry4sqfvidizw3iszva2fca1haul.gif\" width=\"110\" height=\"45\">";
      assertRender(expected, imageTag);
   }

   @Test
   public void testInlineJs() {
      UseScriptTag tag = new UseScriptTag(ResourceTypeConstants.JS);
      tag.setEnv(new MockTagEnv());
      tag.getModel().setContent("document.write('1234');");

      String expected = "<script type=\"text/javascript\">document.write('1234');</script>";
      assertRender(expected, tag);
   }

   @Test
   public void testInlineSlot() {
      SlotTag slotTag = new SlotTag(ResourceTypeConstants.JS);
      slotTag.setSlotType("js");
      slotTag.setEnv(new MockTagEnv());
      slotTag.getModel().setAttribute("id", "body");
      String marker = render(slotTag, false);

      UseScriptTag tag1 = new UseScriptTag(ResourceTypeConstants.JS);
      tag1.setEnv(new MockTagEnv());
      tag1.getModel().setContent("inline content");
      tag1.getModel().setAttribute("target", "body");
      render(tag1, false);

      assertMarker("<script type=\"text/javascript\">inline content</script>", marker);
   }

   @Test
   public void testMixedSlots() {
      SlotTag slotTag = new SlotTag(ResourceTypeConstants.JS);
      slotTag.setSlotType("js");
      slotTag.setEnv(new MockTagEnv());
      slotTag.getModel().setAttribute("id", "body");
      String marker = render(slotTag, false);

      UseScriptTag tag1 = new UseScriptTag(ResourceTypeConstants.JS);
      tag1.setEnv(new MockTagEnv());
      tag1.getModel().setValue("/js/sample/sample_sys1.js");
      tag1.getModel().setAttribute("target", "body");
      assertRender("", tag1, true);

      tag1 = new UseScriptTag(ResourceTypeConstants.JS);
      tag1.setEnv(new MockTagEnv());
      tag1.getModel().setContent("inline1");
      tag1.getModel().setAttribute("target", "body");
      assertRender("", tag1, true);

      tag1 = new UseScriptTag(ResourceTypeConstants.JS);
      tag1.setEnv(new MockTagEnv());
      tag1.getModel().setContent("inline2");
      tag1.getModel().setAttribute("target", "body");
      assertRender("", tag1, true);

      UseScriptTag tag2 = new UseScriptTag(ResourceTypeConstants.JS);
      tag2.setEnv(new MockTagEnv());
      tag2.getModel().setValue("/js/sample/sample_sys2.js");
      tag2.getModel().setAttribute("target", "body");
      assertRender("", tag2, true);

      tag2 = new UseScriptTag(ResourceTypeConstants.JS);
      tag2.setEnv(new MockTagEnv());
      tag2.getModel().setValue(ResourceFactory.createExternalResource("js", "http://external"));
      tag2.getModel().setAttribute("target", "body");
      assertRender("", tag2, true);

      String expected = "<script src=\"/BaseResourceTest/lrssvr/3yg1ueqs3mycfh0pu2r0mnndaqk.js\" type=\"text/javascript\"></script><script type=\"text/javascript\">inline1inline2</script><script src=\"/BaseResourceTest/lrssvr/l0cnh31342ymfcrmehx3ehqewex.js\" type=\"text/javascript\"></script><script src=\"http://external\" type=\"text/javascript\"></script>";
      assertMarker(expected, marker);
   }

   @Test
   @Ignore
   public void testSysJs() {
      SlotTag slotTag = new SlotTag(ResourceTypeConstants.JS);
      slotTag.setSlotType("js");
      slotTag.setEnv(new MockTagEnv());
      slotTag.getModel().setAttribute("id", "body");
      String marker = render(slotTag, false);

      UseScriptTag tag1 = new UseScriptTag(ResourceTypeConstants.JS);
      tag1.setEnv(new MockTagEnv());
      tag1.getModel().setValue("/js/sample/sample_sys1.js");
      tag1.getModel().setAttribute("target", "body");
      assertRender("", tag1, true);

      tag1 = new UseScriptTag(ResourceTypeConstants.JS);
      tag1.setEnv(new MockTagEnv());
      tag1.getModel().setValue("/js/sample/sample1.js");
      tag1.getModel().setAttribute("target", "body");
      assertRender("", tag1, true);

      //system js processing
      ResourceModel model = ResourceRuntimeContext.ctx().getResourceAggregator().getModel();
      model.processModel();

      //slot body
      String expected = "<script src=\"/BaseResourceTest/lrssvr/zrji45bl0y5etm51dokb5qwwre5.js\" type=\"text/javascript\"></script><script src=\"/BaseResourceTest/lrssvr/fodx0py55y2ttl2oso5pwxv2tqf.js\" type=\"text/javascript\"></script>";
      assertMarker(expected, marker);

      Assert.assertEquals(
            "document.write(\"this is sample sys1.js<br>\");document.write(\"this is sample sys2.js<br>\");",
            new String(((byte[]) ((ContentCacheValue) ResourceCacheManager
                  .getGlobalCache("zrji45bl0y5etm51dokb5qwwre5")).getContent())));

      Assert.assertEquals(
            "document.write(\"this is sample1.js<br>\");",
            new String(((byte[]) ((ContentCacheValue) ResourceCacheManager
                  .getGlobalCache("fodx0py55y2ttl2oso5pwxv2tqf")).getContent())));
   }

   @Test
   @Ignore
   public void testSysJsMultiSlots() {
      SlotTag slotTag = new SlotTag(ResourceTypeConstants.JS);
      slotTag.setSlotType("js");
      slotTag.setEnv(new MockTagEnv());
      slotTag.getModel().setAttribute("id", "body");
      String marker = render(slotTag, false);

      UseScriptTag tag1 = new UseScriptTag(ResourceTypeConstants.JS);
      tag1.setEnv(new MockTagEnv());
      tag1.getModel().setValue("/js/sample/sample_sys1.js");
      tag1.getModel().setAttribute("target", "body");
      assertRender("", tag1, true);

      tag1 = new UseScriptTag(ResourceTypeConstants.JS);
      tag1.setEnv(new MockTagEnv());
      tag1.getModel().setValue("/js/sample/sample1.js");
      tag1.getModel().setAttribute("target", "body");
      assertRender("", tag1, true);

      SlotTag slotTag2 = new SlotTag(ResourceTypeConstants.JS);
      slotTag2.setSlotType("js");
      slotTag2.setEnv(new MockTagEnv());
      slotTag2.getModel().setAttribute("id", "body2");
      String marker2 = render(slotTag2, false);

      UseScriptTag tag2 = new UseScriptTag(ResourceTypeConstants.JS);
      tag2.setEnv(new MockTagEnv());
      tag2.getModel().setValue("/js/sample/sample_sys2.js");
      tag2.getModel().setAttribute("target", "body2");
      assertRender("", tag2, true);

      tag2 = new UseScriptTag(ResourceTypeConstants.JS);
      tag2.setEnv(new MockTagEnv());
      tag2.getModel().setValue("/js/sample/sample2.js");
      tag2.getModel().setAttribute("target", "body2");
      assertRender("", tag2, true);

      //system js processing
      ResourceModel model = ResourceRuntimeContext.ctx().getResourceAggregator().getModel();
      model.processModel();

      //slot body
      String expected = "<script src=\"/BaseResourceTest/lrssvr/zrji45bl0y5etm51dokb5qwwre5.js\" type=\"text/javascript\"></script><script src=\"/BaseResourceTest/lrssvr/fodx0py55y2ttl2oso5pwxv2tqf.js\" type=\"text/javascript\"></script>";
      assertMarker(expected, marker);

      Assert.assertEquals(
            "document.write(\"this is sample sys1.js<br>\");document.write(\"this is sample sys2.js<br>\");",
            new String(((byte[]) ((ContentCacheValue) ResourceCacheManager
                  .getGlobalCache("zrji45bl0y5etm51dokb5qwwre5")).getContent())));

      Assert.assertEquals(
            "document.write(\"this is sample1.js<br>\");",
            new String(((byte[]) ((ContentCacheValue) ResourceCacheManager
                  .getGlobalCache("fodx0py55y2ttl2oso5pwxv2tqf")).getContent())));

      //slot body2
      expected = "<script src=\"/BaseResourceTest/lrssvr/kz1uuubssy1nfnr3swwhv3ipl2p.js\" type=\"text/javascript\"></script>";
      assertMarker(expected, marker2);

      Assert.assertEquals(
            "document.write(\"this is sample2.js<br>\");",
            new String(((byte[]) ((ContentCacheValue) ResourceCacheManager
                  .getGlobalCache("kz1uuubssy1nfnr3swwhv3ipl2p")).getContent())));
   }

   @Test
   public void testTokenTag() {
      SlotTag slotTag = new SlotTag(ResourceTypeConstants.JS);
      slotTag.setSlotType("js");
      slotTag.setEnv(new MockTagEnv());
      slotTag.getModel().setAttribute("id", "body");
      render(slotTag, false);

      UseScriptTag tag1 = new UseScriptTag(ResourceTypeConstants.JS);
      tag1.setEnv(new MockTagEnv());
      tag1.getModel().setValue("/js/sample/sample_sys1.js");
      tag1.getModel().setAttribute("target", "body");
      assertRender("", tag1, true);

      tag1 = new UseScriptTag(ResourceTypeConstants.JS);
      tag1.setEnv(new MockTagEnv());
      tag1.getModel().setValue("/js/sample/sample1.js");
      tag1.getModel().setAttribute("target", "body");
      assertRender("", tag1, true);

      TokenTag tokenTag = new TokenTag();
      tokenTag.setEnv(new MockTagEnv());
      tokenTag.getModel().setAttribute("type", "js");
      String marker2 = render(tokenTag, false);

      ResourceModel model = ResourceRuntimeContext.ctx().getResourceAggregator().getModel();
      model.processModel();

      //token assertion
      String expected = "i1jvbm1zl22ylf5xtwpxsgzeqiu";
      assertMarker(expected, marker2);

      assertToken("[js.local:/js/sample/sample1.js, js.local:/js/sample/sample_sys1.js]", expected);
   }
   
   @Test
   public void testSingleCss() {
      UseScriptTag tag = new UseScriptTag(ResourceTypeConstants.CSS);
      tag.setEnv(new MockTagEnv());
      tag.getModel().setValue("/css/sample/sample.css");
      tag.getModel().setAttribute("id", "hello");
      tag.getModel().setAttribute("htmlid", "htmlid");

      render(tag, true);
   }
   
   @Test
   public void testSlotTemplateGetContent() {

		SlotTag slotTag = new SlotTag(ResourceTypeConstants.JS);
		slotTag.setSlotType("js");
		slotTag.setEnv(new MockTagEnv());
		slotTag.getModel().setAttribute("id", "exec");

		SlotTemplateModel templateModel = new SlotTemplateModel(slotTag);
		String contentMarker = templateModel.getContent();
		slotTag.getModel().setContent(
				"slot_template_content_test:" + contentMarker);
		render(slotTag, false);

		UseScriptTag tag1 = new UseScriptTag(ResourceTypeConstants.JS);
		tag1.setEnv(new MockTagEnv());
		tag1.getModel().setContent("document.write('1234');");
		tag1.getModel().setAttribute("target", "exec");
		assertRender("", tag1, true);

		UseScriptTag tag2 = new UseScriptTag(ResourceTypeConstants.JS);
		tag2.setEnv(new MockTagEnv());
		tag2.getModel().setContent("document.write('5678');");
		tag2.getModel().setAttribute("target", "exec");
		assertRender("", tag2, true);

		String expectedContent = "<script type=\"text/javascript\">slot_template_content_test:document.write('1234');document.write('5678');</script>";
		String actualContent = ResourceRuntimeContext.ctx()
				.getDeferRenderable("SlotTag:js:exec").deferRender();
		Assert.assertEquals(expectedContent, actualContent);
   }
   
   @Test
   public void testSlotTemplateGetUrl() {

		SlotTag slotTag = new SlotTag(ResourceTypeConstants.JS);
		slotTag.setSlotType("js");
		slotTag.setEnv(new MockTagEnv());
		slotTag.getModel().setAttribute("id", "exec");

		SlotTemplateModel templateModel = new SlotTemplateModel(slotTag);
		String urlMarker = templateModel.getUrl();
		slotTag.getModel().setContent("slot_template_url_test:" + urlMarker);
		render(slotTag, false);

		UseScriptTag tag1 = new UseScriptTag(ResourceTypeConstants.JS);
		tag1.setEnv(new MockTagEnv());
		tag1.getModel().setValue("/js/sample/sample1.js");
		tag1.getModel().setAttribute("target", "exec");
		assertRender("", tag1, true);

		UseScriptTag tag2 = new UseScriptTag(ResourceTypeConstants.JS);
		tag2.setEnv(new MockTagEnv());
		tag2.getModel().setValue("/js/sample/sample2.js");
		tag2.getModel().setAttribute("target", "exec");
		assertRender("", tag2, true);

		String expectedUrl = "<script type=\"text/javascript\">slot_template_url_test:/BaseResourceTest/lrssvr/yktym4yhdi35pnucyae2bygepyn.js</script>";
		String actualUrl = ResourceRuntimeContext.ctx()
				.getDeferRenderable("SlotTag:js:exec").deferRender();
		Assert.assertEquals(expectedUrl, actualUrl);

   }
   
   @Test
   public void testBeanTag() {
      BeanTag beanTag = new BeanTag();
      beanTag.setEnv(new MockTagEnv());
      beanTag.start();
   }
   
   @Test
   public void testSetTag() {
      SetTag setTag = new SetTag();
      setTag.setEnv(new MockTagEnv());
      setTag.getModel().setAttribute("id", "testId");
      Assert.assertNull(render(setTag, true));
   }
   
   @Test
   public void testState() {
      Assert.assertTrue(State.BUILT.isBuilt());
      Assert.assertTrue(State.CREATED.isCreated());
      Assert.assertTrue(State.ENDED.isEnded());
      Assert.assertTrue(State.RENDERED.isRendered());
      Assert.assertTrue(State.STARTED.isStarted());
   }
   
   @Test
   public void testFunctions() {
      //test getErrMsg
      ResourceTag tag = new ResourceTag();
      ITagEnv env = new MockTagEnv();
      env.setProperty("jsp.showErrorContext", true);
      tag.setEnv(env);
      Assert.assertEquals("<!-- test error message Tag: error message1 -->", tag.getErrMsg("test error message", "error message1"));
      
      //invoke only
      tag.getState();
      tag.out(null);
   }
   

}
