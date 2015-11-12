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

package com.ebayopensource.webrex.resource.expression;

import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;


public class ResourceELEvaluatorTest {

   @Test
   public void testBuildResourceURLFromEL1L() {
      ResourceELEvaluator evaluator = new ResourceELEvaluator();
      ResourceExpression exp = new ResBeanExpression();
      exp = (ResourceExpression)exp.get("js");
      Assert.assertTrue(exp instanceof ResTypeExpression);
      exp = (ResourceExpression)exp.get("local");
      Assert.assertTrue(exp instanceof ResNamespaceExpression);
      exp = (ResourceExpression)exp.get("foo_js");
      Assert.assertTrue(!(exp instanceof ResNamespaceExpression || exp instanceof ResTypeExpression));
      StringBuilder sb = new StringBuilder();
      Map<String, Object> arguments = new LinkedHashMap<String, Object>(3);
      evaluator.buildResourcePath(sb, exp, arguments);
      Assert.assertEquals("/foo_js", sb.toString());
      
      evaluator.convertELToPath(sb, "", "");
      Assert.assertEquals("/foo.js", sb.toString());
      
      
   }
   @Test
   public void testBuildResourceURLFromEL3L() {
      ResourceELEvaluator evaluator = new ResourceELEvaluator();
      ResourceExpression exp = new ResBeanExpression();
      exp = (ResourceExpression)exp.get("js");
      exp = (ResourceExpression)exp.get("local");
      exp = (ResourceExpression)exp.get("static");
      exp = (ResourceExpression)exp.get("EN");
      exp = (ResourceExpression)exp.get("foo_js");
      StringBuilder sb = new StringBuilder();
      Map<String, Object> arguments = new LinkedHashMap<String, Object>(3);
      evaluator.buildResourcePath(sb, exp, arguments);
      Assert.assertEquals("/static/EN/foo_js", sb.toString());
      
      evaluator.convertELToPath(sb, "", "");
      Assert.assertEquals("/static/EN/foo.js", sb.toString());
      
      
   }
   
   @Test
   public void testGetExpressionNamespace() {
      ResourceELEvaluator evaluator = new ResourceELEvaluator();
      ResourceExpression exp = new ResBeanExpression();
      exp = (ResourceExpression)exp.get("js");
      exp = (ResourceExpression)exp.get("local");
      exp = (ResourceExpression)exp.get("static");
      exp = (ResourceExpression)exp.get("EN");
      exp = (ResourceExpression)exp.get("foo_js");
      Assert.assertEquals("local",evaluator.getExpressionNamespace(exp).getKey());
   }
   
}
