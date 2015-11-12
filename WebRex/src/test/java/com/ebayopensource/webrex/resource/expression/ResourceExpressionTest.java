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

import junit.framework.Assert;

import org.junit.Test;

import com.ebayopensource.webrex.resource.BaseResourceTest;
import com.ebayopensource.webrex.resource.DiagnosisModel;
import com.ebayopensource.webrex.resource.Resource;
import com.ebayopensource.webrex.resource.ResourceErrPolicy;
import com.ebayopensource.webrex.resource.ResourceRuntimeContext;

public class ResourceExpressionTest extends BaseResourceTest {
   @Test
   public void testBeanExpression() {
	   ResourceExpression expr = new ResBeanExpression();
	   String type = expr.getELType(expr);

	   Assert.assertEquals(null, type);
	   Assert.assertEquals("", expr.toString());
   }
	
   @Test
   public void testExpression() {
      ResBeanExpression bean = new ResBeanExpression();
      ResourceExpression expr = eval(bean, "a.b.c");
      String type = expr.getELType(expr);

      Assert.assertEquals("a", type);
   }

   @Test
   public void testExpressionEvaluate() {
      ResourceRuntimeContext.ctx().setShowDiag(true);

      ResBeanExpression bean = new ResBeanExpression();
      ResourceExpression expr1 = eval(bean, "a.b.c");
      Assert.assertNull(expr1.evaluate());

      ResourceExpression expr2 = eval(bean, "img.local.img.ebayLogo_gif");
      Object obj2 = expr2.evaluate();
      Assert.assertNotNull(obj2);
      Assert.assertEquals("com.ebayopensource.webrex.resource.ImageResource", obj2.getClass().getName());

      ResourceExpression expr3 = eval(bean, "js.local.js.sample.sample_js");
      Object obj3 = expr3.evaluate();
      Assert.assertNotNull(obj3);
      Assert.assertEquals("com.ebayopensource.webrex.resource.Resource", obj3.getClass().getName());
      Assert.assertEquals("document.write(\"this is sample.js<br>\");",
            new String(((Resource) obj3).getOriginalContent()));

      ResourceExpression expr4 = eval(bean, "img.local.img");
      Object obj4 = expr4.evaluate();
      Assert.assertNull(obj4);

      ResourceExpression expr5 = eval(bean, "img.local.img.ebayLogo_gif.error");
      Object obj5 = expr5.evaluate();
      Assert.assertNull(obj5);

      DiagnosisModel model = ResourceRuntimeContext.ctx().getShowDiagModel();
      Assert.assertEquals(
            "{\"EL\":[\"{\"el\":\"res.a.b.c\",\"status\":\"UNRESOLVING\"}\",\"{\"el\":\"res.img.local.img.ebayLogo_gif\",\"status\":\"OK\"}\",\"{\"el\":\"res.js.local.js.sample.sample_js\",\"status\":\"OK\"}\",\"{\"el\":\"res.img.local.img\",\"status\":\"UNRESOLVING\"}\",\"{\"el\":\"res.img.local.img.ebayLogo_gif.error\",\"status\":\"UNRESOLVING\"}\"]}",
            model.toString());
   }

/*   @Test
   public void testExpressionFailFastException() {
      try {
         ResourceRuntimeContext.ctx().getConfig().setResourceErrPolicy(ResourceErrPolicy.FAIL_FAST);
         ResBeanExpression bean = new ResBeanExpression();
         eval(bean, "a.b.c").evaluate();
         Assert.fail("exception should be thrown");
      } catch (Exception e) {
         Assert.assertTrue(e.toString().contains(
               "com.ebayopensource.webrex.resource.ResourceException: No resolver registered for resource(a) in namespace(b)"));
      }
   }*/
   
   @Test
   public void testConstructors() {
      ResourceExpression parent = new ResourceExpression("parent", null, ResourceExpressionType.NAMESPACE);
      ResourceExpression child = (ResourceExpression) parent.call("child", null);
      try {
         child.clear();
         Assert.fail();
      } catch (Exception e) {
         Assert.assertTrue(e instanceof UnsupportedOperationException);
      }
      try {
         child.containsKey("testKey");
         Assert.fail();
      } catch (Exception e) {
         Assert.assertTrue(e instanceof UnsupportedOperationException);
      }
      try {
         child.containsValue("testValue");
         Assert.fail();
      } catch (Exception e) {
         Assert.assertTrue(e instanceof UnsupportedOperationException);
      }
      try {
         child.put("testKey", "testValue");
         Assert.fail();
      } catch (Exception e) {
         Assert.assertTrue(e instanceof UnsupportedOperationException);
      }
      try {
         child.putAll(null);
         Assert.fail();
      } catch (Exception e) {
         Assert.assertTrue(e instanceof UnsupportedOperationException);
      }
      try {
         child.remove("testValue");
         Assert.fail();
      } catch (Exception e) {
         Assert.assertTrue(e instanceof UnsupportedOperationException);
      }
      try {
         child.size();
         Assert.fail();
      } catch (Exception e) {
         Assert.assertTrue(e instanceof UnsupportedOperationException);
      }
      try {
         child.values();
         Assert.fail();
      } catch (Exception e) {
         Assert.assertTrue(e instanceof UnsupportedOperationException);
      }
      Assert.assertNotNull(child.entrySet());
      ResourceRuntimeContext.ctx().setShowDiag(true);
      Assert.assertTrue(child.evaluateAsString().isEmpty());
      Assert.assertTrue(child.isEmpty());
      Assert.assertNotNull(child.keySet());
      Object[] objs = {new Object()};
      child.setArguments(objs);
      child.setType(ResourceExpressionType.RES);
      Assert.assertNotNull(child.getArguments());
      Assert.assertNotNull(child.getArguments());
      Assert.assertEquals("", (new ResBeanExpression()).evaluate().toString());
   }
   
   @Test
   public void testGetELType() {
      ResourceExpression exp = new ResBeanExpression();
      Assert.assertTrue(exp.getELType(exp) == null);
      exp = (ResourceExpression)exp.get("js");
      Assert.assertTrue(exp instanceof ResTypeExpression);
      exp = (ResourceExpression)exp.get("local");
      Assert.assertTrue(exp instanceof ResNamespaceExpression);
      exp = (ResourceExpression)exp.get("foo_js");
      Assert.assertEquals(exp.getELType(exp), "js");
      
   }

   /**
    */
   @Test
   public void testELIsEmpty() {

      ResBeanExpression bean = new ResBeanExpression();
      ResourceExpression expr1 = eval(bean, "js.local.js.sample.sample_js");
      Assert.assertFalse(expr1.isEmpty());
   }
}
