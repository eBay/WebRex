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

package com.ebayopensource.webrex.logging;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LoggerFactoryTest {
   private ILoggerFactory m_loggerFactory;
   
   @Before
   public void setup() {
      m_loggerFactory = LoggerFactory.getLoggerFactory();
   }
   
   
   @After
   public void tearDown() {
      LoggerFactory.setLoggerFactory(m_loggerFactory);
   }

   
   @Test
   public void testLogFactory() {
      LoggerFactory.setLoggerFactory(new MockLoggerFactory());
      try {
         LoggerFactory.setLoggerFactoryClass("com.ebayopensource.webrex.logging.MockkLoggerFactory"); //wrong class name for throwing exception
         Assert.fail();
      } catch (Exception e) {
         Assert.assertTrue(e instanceof IllegalArgumentException);
         Assert.assertTrue(e.getMessage().contains("Can't loading the ILoggerFactory:"));
      }
      LoggerFactory.setLoggerFactoryClass("com.ebayopensource.webrex.logging.MockLoggerFactory");
      try {
         LoggerFactory.setLoggerFactoryClass("com.ebayopensource.webrex.logging.LoggerFactoryTest$MockLoggerFactory1");
      } catch (Exception e) {
         Assert.assertTrue(e instanceof IllegalArgumentException);
         Assert.assertTrue(e.getMessage().contains("The class is not an instance of ILoggerFactory:"));
      }
      
   }
   
   public static class MockLoggerFactory1 {
      
   }
}
