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

import com.ebayopensource.webrex.resource.ResourceAggregator.INSERTION;
import com.ebayopensource.webrex.resource.api.IResource;

public class ResourceAggregatorTest extends BaseResourceTest{

   @Test
   public void testRegisterResource() {
      ResourceModel model = ResourceRuntimeContext.ctx().getModel();
      Assert.assertTrue(model.registerSlot("js-slot", "js"));
      
      IResource resource = ResourceFactory.createResource("/js/sample/sample.js");
      ResourceAggregator aggregator = ResourceRuntimeContext.ctx().getResourceAggregator();
      
      Assert.assertFalse(aggregator.isRegisteredResource(resource));
      Assert.assertNull(aggregator.getSlotResources("js-slot", "js"));
      Assert.assertTrue(aggregator.registerResource("js-slot", resource));
      Assert.assertTrue(aggregator.isRegisteredResource(resource));
      Assert.assertTrue(aggregator.getActiveSlotIds("js").size() == 1);
   }
   
   @Test
   public void testSetDeferJsSlots() {
      ResourceModel model = ResourceRuntimeContext.ctx().getModel();
      String slot1 = "js-slot1";
      String slot2 = "js-slot2";
      Assert.assertTrue(model.registerSlot(slot1, "js"));
      Assert.assertTrue(model.registerSlot(slot2, "js"));
 
      List<String> slots = new ArrayList<String>();
      slots.add(slot1);
      slots.add(slot2);
      
      ResourceAggregator aggregator = ResourceRuntimeContext.ctx().getResourceAggregator();
      aggregator.setDeferJsSlots(slots);
   }
   
   @Test
   public void testBeforeAndAfterSlots() {
      ResourceModel model = ResourceRuntimeContext.ctx().getModel();
      String slotId = "css-slot";
      Assert.assertTrue(model.registerSlot(slotId, "css"));
      IResource resource = ResourceFactory.createResource("/css/sample/sample.css");
      IResource resource1 = ResourceFactory.createResource("/css/sample/sample1.css");
      IResource resource2 = ResourceFactory.createResource("/css/sample/sample2.css");
      
      ResourceAggregator aggregator = ResourceRuntimeContext.ctx().getResourceAggregator();
      Assert.assertTrue(aggregator.registerResource(slotId, resource));
      aggregator.registerResource(slotId, resource1, INSERTION.BEFORE);
      aggregator.registerResource(slotId, resource2, INSERTION.AFTER);
      Assert.assertTrue(aggregator.getBeforeSlotResources(slotId, "css").size() == 1);
      Assert.assertTrue(aggregator.getAfterSlotResources(slotId, "css").size() == 1);
   }
}
