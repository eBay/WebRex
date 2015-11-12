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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import com.ebayopensource.webrex.resource.ResourceModel.Slot;
import com.ebayopensource.webrex.resource.api.IResource;

public class ResourceModelTest extends BaseResourceTest {
   @Test
   public void testDedup() {
      ResourceModel model = new ResourceModel("/dedup");
      model.registerSlot("head", ResourceTypeConstants.JS);
      model.registerSlot("head2", ResourceTypeConstants.JS);

      IResource sample = ResourceFactory.createResource("/js/sample/sample.js");
      Assert.assertTrue(model.registerResource(sample, "head"));
      Assert.assertFalse(model.registerResource(sample, "head"));
      Assert.assertTrue(model.registerResource(sample, "head2"));
      Assert.assertFalse(model.isResourceRendered(sample));

      List<IResource> resources = model.getSlotResources("head2", ResourceTypeConstants.JS);
      Assert.assertEquals(
            "[Resource [m_urn=js.local:/js/sample/sample.js, m_libInfo=ResourceLibrary [m_id=webressdk, m_version=1.0.8, m_type=WAR]]]",
            resources.toString());
      Assert.assertTrue(model.isResourceRendered(sample));

      resources = model.getSlotResources("head", ResourceTypeConstants.JS);
      Assert.assertEquals("[]", resources.toString());
   }

   @Test
   public void testModel() {
      ResourceModel model = new ResourceModel("/a/b");
      Assert.assertEquals("/a/b", model.getRequestUri());

      IResource sample = ResourceFactory.createResource("/js/sample/sample.js");
      Assert.assertNotNull(sample);
      Assert.assertTrue(model.registerResource(sample, "head"));
      Assert.assertFalse(model.getSlot("head", ResourceTypeConstants.JS).isActive());

      IResource sample1 = ResourceFactory.createResource("/js/sample/sample1.js");
      Assert.assertTrue(model.isResourceRenderable(sample1));

      boolean registeredSlot = model.registerSlot("head", ResourceTypeConstants.JS);
      Assert.assertTrue(registeredSlot);
      Assert.assertFalse(model.isResourceRenderable(sample));

      Assert.assertTrue(model.getSlot("head", ResourceTypeConstants.JS).isActive());
      Assert.assertFalse(model.getSlot("head", ResourceTypeConstants.JS).isDeferred());
      Assert.assertFalse(model.getSlot("head", ResourceTypeConstants.JS).isFlushed());

      String nullSlot = "no-slot";
      Assert.assertNull(model.getSlot(nullSlot, ResourceTypeConstants.JS));
      model.getSlotResources(nullSlot, ResourceTypeConstants.JS);
      Assert.assertFalse(model.registerSlot("head", "js"));
      
      List<IResource> headSlotResoures = model.getSlotResources("head", ResourceTypeConstants.JS);
      Assert.assertTrue(model.getSlot("head", ResourceTypeConstants.JS).isFlushed());
      Assert.assertEquals(
            "[Resource [m_urn=js.local:/js/sample/sample.js, m_libInfo=ResourceLibrary [m_id=webressdk, m_version=1.0.8, m_type=WAR]]]",
            headSlotResoures.toString());
      Assert.assertEquals("[Slot [m_slotId=head, m_type=js, m_active=true, m_flushed=true, m_deferred=false]]", model
            .getSlots(ResourceTypeConstants.JS).toString());

      Assert.assertTrue(model.getSlotResources("head", ResourceTypeConstants.JS).isEmpty());
      int slotHashCode = model.getSlot("head", ResourceTypeConstants.JS).hashCode();
      Assert.assertEquals(99155754, slotHashCode);
      model.setRenderedResource("js.local:/js/sample/sample.js");
      String requestUri = "testRequestUri";
      model.setRequestUri(requestUri);
      
      List<IResource> list1 = new ArrayList<IResource>();
      list1.add(sample1);
      model.getSlot("head", ResourceTypeConstants.JS).setResources(list1);
      model.getSlot("head", ResourceTypeConstants.JS).setDeferred(false);
      model.toString();
      Assert.assertEquals(requestUri, model.getRequestUri());
      model.reset();
   }

   @Test
   public void testBeforeAfterResource() {
      ResourceModel model = new ResourceModel("/before");
      Assert.assertEquals("/before", model.getRequestUri());

      model.registerSlot("head", "js");
      List<IResource> list1 = new ArrayList<IResource>();
      List<IResource> list2 = new ArrayList<IResource>();
      IResource sample = ResourceFactory.createResource("/js/sample/sample.js");
      IResource sample1 = ResourceFactory.createResource("/js/sample/sample1.js");
      IResource sample2 = ResourceFactory.createResource("/js/sample/sample2.js");
      IResource sample3 = ResourceFactory.createResource("/js/sample/sample_sys1.js");
      list1.add(sample2);
      list2.add(sample3);
      model.getSlot("head", ResourceTypeConstants.JS).addAfterSlot("after", sample);
      model.getSlot("head", ResourceTypeConstants.JS).addAfterSlot("after", sample);
      model.getSlot("head", ResourceTypeConstants.JS).addBeforeSlot("before", sample1);
      Assert.assertEquals(
            "{before=[Resource [m_urn=js.local:/js/sample/sample1.js, m_libInfo=ResourceLibrary [m_id=webressdk, m_version=1.0.8, m_type=WAR]]]}",
            model.getSlotBeforeResources("head", "js").toString());
      Assert.assertEquals(
            "{after=[Resource [m_urn=js.local:/js/sample/sample.js, m_libInfo=ResourceLibrary [m_id=webressdk, m_version=1.0.8, m_type=WAR]]]}",
            model.getSlotAfterResources("head", "js").toString());
      Assert.assertFalse(model.getSlot("head", ResourceTypeConstants.JS).equals(model.getSlot("head", ResourceTypeConstants.JS).getAfterSlots().get(0)));
      model.getSlot("head", ResourceTypeConstants.JS).addBeforeSlot("before", list1);
      model.getSlot("head", ResourceTypeConstants.JS).addAfterSlot("after", list2);
      
      
   }
   
   @Test
   public void testTokenFunctions() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      ResourceModel model = ResourceRuntimeContext.ctx().getModel();
      model.enableCollectResources("js", true);
      model.enableCollectResources("js", false);
      IResource resource = ResourceFactory.createResource("/js/sample/sample.js");
      model.registerResource(resource, null);
      
      Class<ResourceModel> clazz = ResourceModel.class;
      Method method = clazz.getDeclaredMethod("collectResources", String.class);
      method.setAccessible(true);
      Object urns =  method.invoke(model, "js");
      if(urns instanceof Set<?>) {
         Set<?> urnSet = (Set<?>) urns;
         Assert.assertTrue(urnSet.size() == 1);
      }
   }
   
   @Test
   public void testHasSlot() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      Class<ResourceModel> clazz = ResourceModel.class;
      Method method = clazz.getDeclaredMethod("hasSlot", List.class, String.class);
      method.setAccessible(true);
      ResourceModel model = ResourceRuntimeContext.ctx().getModel();
      Object flag = method.invoke(model, null, "testBundle");
      if(flag instanceof Boolean) {
         boolean hasSlot = (Boolean)flag;
         Assert.assertFalse(hasSlot);
      }
      
      List<Slot> slots = new ArrayList<Slot>();
      Slot slot = new Slot("js-slot", "js");
      slots.add(slot);
      
      flag = method.invoke(model, slots, "testBundle");
      if(flag instanceof Boolean) {
         boolean hasSlot = (Boolean)flag;
         Assert.assertFalse(hasSlot);
      }
      
      flag = method.invoke(model, slots, "js-slot");
      if(flag instanceof Boolean) {
         boolean hasSlot = (Boolean)flag;
         Assert.assertTrue(hasSlot);
      }
   }
   
   @Test
   public void testSlotEquals() {
      Slot slot = new Slot("js-slot", "js");
      Assert.assertTrue(slot.equals(slot));
      Assert.assertFalse(slot.equals(null));
      Assert.assertFalse(slot.equals(new Object()));
      Slot slot1 = new Slot(null, "js");
      Assert.assertFalse(slot1.equals(slot));
      Slot slot2 = new Slot("js-slot", "css");
      Assert.assertFalse(slot.equals(slot2));
      Slot cloneSlot = new Slot("js-slot", "js");
      Assert.assertTrue(slot.equals(cloneSlot));
   }
   
}
