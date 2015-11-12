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

package com.ebayopensource.webrex.resource.perf.properties;

import java.io.File;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.ebayopensource.webrex.resource.ResourceInitializer;
import com.ebayopensource.webrex.resource.ResourceRuntime;
import com.ebayopensource.webrex.resource.ResourceRuntimeContext;
import com.ebayopensource.webrex.resource.expression.IResourceExpression;
import com.ebayopensource.webrex.resource.expression.ResourceExpression;

public class BaseELBenchmarkTest {
   private ResourceExpression m_bean;

   @AfterClass
   public static void destroy() {
      ResourceRuntime.INSTANCE.reset();
   }

   @BeforeClass
   public static void initialize() {
      ResourceRuntime.INSTANCE.reset();
      ResourceInitializer.initialize(null, new File("./WebContent"));
   }

   @After
   public void after() {
      ResourceRuntimeContext.reset();
   }

   @Before
   public void before() {
      ResourceRuntimeContext.setup(null);
      m_bean = new ResourceExpression();
   }

   @SuppressWarnings( { "unchecked" })
   protected <T> T eval(String expression) {
      String[] parts = expression.split(Pattern.quote("."));
      Object result = m_bean;

      for (int i = 0; i < parts.length; i++) {
         String part = parts[i];

         if (result instanceof IResourceExpression) {
            result = ((IResourceExpression) result).get(part);

            if (result == null) {
               return null;
            }
         } else if (i == parts.length - 1) {
            return (T) result;
         }
      }

      return (T) result;
   }
}
