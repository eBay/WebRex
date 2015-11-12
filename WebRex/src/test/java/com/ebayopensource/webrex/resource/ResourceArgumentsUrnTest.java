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

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.ebayopensource.webrex.resource.api.IResourceArgumentsUrn;

public class ResourceArgumentsUrnTest {

   @Test
   public void testCreateResourceArgumentsUrn() {
      Map<String, Object> arguments = new HashMap<String, Object>();
      arguments.put("key1", "value1");
      arguments.put("key2", "value2");
      ResourceArgumentsUrn urn = new ResourceArgumentsUrn("js", "local", "/js/sample/sample.js", arguments);
      urn.hashCode();
      Assert.assertTrue(urn.getArgument().size() == 2);
   }
   
   @Test
   public void testEquals() {
      Map<String, Object> arguments = new HashMap<String, Object>();
      arguments.put("key1", "value1");
      arguments.put("key2", "value2");
      ResourceArgumentsUrn urn = new ResourceArgumentsUrn("js", "local", "/js/sample/sample.js", arguments);
      Assert.assertTrue(urn.equals(urn));
      ResourceArgumentsUrn cloneUrn = new ResourceArgumentsUrn("js", "local", "/js/sample/sample.js", arguments);
      Assert.assertTrue(urn.equals(cloneUrn));
      ResourceArgumentsUrn urn1 = new ResourceArgumentsUrn("js", "local", "/js/sample/sample.js", null);
      Assert.assertFalse(urn1.equals(urn));
      Assert.assertFalse(urn.equals(urn1));
      MockResourceArgumentsUrn mockUrn = new MockResourceArgumentsUrn("js", "local", "/js/sample/sample.js", arguments);
      Assert.assertFalse(urn.equals(mockUrn));
   }
   
   private class MockResourceArgumentsUrn extends ResourceUrn implements IResourceArgumentsUrn {
      private Map<String, Object> m_argument;
      
      public MockResourceArgumentsUrn(String type, String namespace, String resourcePath) {
         super(type, namespace, resourcePath);
         // TODO Auto-generated constructor stub
      }
      
      public MockResourceArgumentsUrn(String type, String namespace, String resourcePath, Map<String, Object> arguments) {
         super(type, namespace, resourcePath);
         m_argument = arguments;
      }
      
      
      @Override
      public Map<String, Object> getArgument() {
         // TODO Auto-generated method stub
         return m_argument;
      }
   }

  
}
