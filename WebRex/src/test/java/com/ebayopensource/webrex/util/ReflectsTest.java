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

package com.ebayopensource.webrex.util;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Constructor;

import org.junit.Test;

public class ReflectsTest {

   public static final String mock = "mock";

   @Test
   public void testGetClass2() {

      Class<?> classz = Reflects.forClass().getClass2("com.ebayopensource.webrex.util.ReflectsTest",
            Thread.currentThread().getContextClassLoader());
      assertEquals(this.getClass(), classz);

      Class<?> classA = Reflects.forClass().getClass2("com.ebayopensource.webrex.util.ReflectsTest$A");
      assertEquals(ReflectsTest.A.class, classA);

      Class<?> classAB = Reflects.forClass().getClass2("com.ebayopensource.webrex.util.ReflectsTest$A$B");
      assertEquals(ReflectsTest.A.B.class, classAB);

      Class<?> classABx = Reflects.forClass().getClass2("com.ebayopensource.webrex.util.ReflectsTest.A.B");
      assertEquals(ReflectsTest.A.B.class, classABx);

   }

   @Test
   public void testGetNestedClass() {

      Class<?> classz = Reflects.forClass().getNestedClass("com.ebayopensource.webrex.util.ReflectsTest", "ReflectsTest");
      assertEquals(null, classz);

      Class<?> classA = Reflects.forClass().getNestedClass("com.ebayopensource.webrex.util.ReflectsTest", "A");
      assertEquals(ReflectsTest.A.class, classA);

      Class<?> classB = Reflects.forClass().getNestedClass("com.ebayopensource.webrex.util.ReflectsTest$A", "B",
            Thread.currentThread().getContextClassLoader());
      assertEquals(ReflectsTest.A.B.class, classB);

   }

//   @Test
//   public void testConstructorReflector() {
//      Object nullO = Reflects.forConstructor().createInstance(null);
//      assertEquals(null, nullO);
//   }

//   @Test
//   public void testFieldReflector() {
//      Object nullO = Reflects.forField().getStaticFieldValue("", " ");
//      assertEquals(null, nullO);

//      Object nullOx = Reflects.forField().getStaticFieldValue(this.getClass(), "");
//      assertEquals(null, nullOx);

//      Object nullOx1 = Reflects.forField().getStaticFieldValue("com.ebayopensource.webrex.util.ReflectsTest", "");
//      assertEquals(null, nullOx1);
//
//      Object mock = Reflects.forField().getStaticFieldValue("com.ebayopensource.webrex.util.ReflectsTest", "mock");
//      assertEquals("mock", mock);
//   }

   //	@Test
   //	public void testMethodFilter(){
   //		Reflects.MethodFilter pub = Reflects.MethodFilter.PUBLIC;
   //		Method getPubMockMethod = ConverterUtil.getGetMethod(this.getClass(), "getPubMockMethod");
   //		Boolean isPub = pub.filter(getPubMockMethod);
   //		assertEquals(true, isPub);
   //		
   //		/* null pointer exception
   //		 * Boolean isPubn = pub.filter(null);
   //		assertEquals(false, isPubn);*/
   //		
   //		Reflects.MethodFilter staticM = Reflects.MethodFilter.STATIC;
   //		Method getstaticMethod = ConverterUtil.getGetMethod(this.getClass(), "getStaticMethod");
   //		Boolean isStatic = staticM.filter(getstaticMethod);
   //		assertEquals(true, isStatic);
   //		
   //		/* null pointer exception
   //		 * Boolean isStaticn = staticM.filter(null);
   //		assertEquals(false, isStaticn);*/
   //		
   //		Reflects.MethodFilter pubstaticM = Reflects.MethodFilter.PUBLIC_STATIC;
   //		Method getpubstaticMethod = ConverterUtil.getGetMethod(this.getClass(), "getStaticMethod");
   //		Boolean isPubStatic = pubstaticM.filter(getpubstaticMethod);
   //		assertEquals(true, isPubStatic);
   //		
   //		
   //		
   //	}

//   @Test
//   public void testMethodReflector() {
      Reflects.MethodReflector mr = Reflects.forMethod();
//      List<Method> methods = mr.getDeclaredMethods(ReflectsTest.A.B.class, null);
//      assertEquals(0, methods.size());
//      ReflectsTest.A a = new ReflectsTest().new A();
//      List<Method> methodsA = mr.getDeclaredMethods(ReflectsTest.A.class, null);
//      assertEquals(a.getClass().getDeclaredMethods().length, methodsA.size());
//
//      String mtName = mr.getGetMethodName("Test");
//      assertEquals("getTest", mtName);
//
//      try {
//         mr.getGetMethodName("");
//      } catch (Exception e) {
//         IllegalArgumentException iae = new IllegalArgumentException(String.format("Invalid property name: %s!", ""));
//         assertEquals(e.getMessage(), iae.getMessage());
//      }

//      List<Method> methodx = mr.getMethods(ReflectsTest.A.B.class, null);
//      ReflectsTest.A.B b = new ReflectsTest().new A().new B();
//      assertEquals(b.getClass().getMethods().length, methodx.size());
//
//      List<Method> methodxA = mr.getMethods(ReflectsTest.A.class, null);
//      assertEquals(a.getClass().getMethods().length, methodxA.size());

//   }

//   @Test
//   public void testSetPropertyValue() {
//      Reflects.MethodReflector mr = Reflects.forMethod();
//      String propName = "mock";
//      String propMockA = "mockA";
//      try {
//
//         mr.setPropertyValue(this, propName, "");
//
//      } catch (Exception e) {
//         IllegalArgumentException iae = new IllegalArgumentException("No such method:"
//               + Reflects.forMethod().getSetMethodName(propName) + " for the value:" + "");
//         assertEquals(e.getMessage(), iae.getMessage());
//      }
//
//      try {
//         A a = new ReflectsTest().new A();
//         mr.setPropertyValue(a, propMockA, "");
//         String changed = mr.getPropertyValue(a, propMockA);
//         assertEquals("", changed);
//
//      } catch (Exception e) {
//         e.printStackTrace();
//      }
//   }

//   @Test
//   public void testGetMatchedMethod() {
//      Reflects.MethodReflector mr = Reflects.forMethod();
//      String mName = "getMock";
//      String mGetMockA = "getMockA";
//      String mSetMockA = "setMockA";
//      try {
//
//         Method nullM = mr.getMatchedMethod(this.getClass(), mName, null);
//         assertEquals(null, nullM);
//
//      } catch (Exception e) {
//
//      }
//
//      try {
//         A a = new ReflectsTest().new A();
//         //null point exception
//         //Method mGet = mr.getMatchedMethod(a.getClass(), mGetMockA, null);
//         Method mGet = mr.getMatchedMethod(a.getClass(), mGetMockA);
//         Method mSet = mr.getMatchedMethod(a.getClass(), mSetMockA, "a");
//         assertArrayEquals("getMatchedMethod equals", a.getClass().getDeclaredMethods(), new Object[] { mGet, mSet });
//
//      } catch (Exception e) {
//         e.printStackTrace();
//      }
//   }

//   @Test
//   public void testInvokeMatchedMethod() {
//      Reflects.MethodReflector mr = Reflects.forMethod();
//      String mName = "getMock";
//      String mGetMockA = "getMockA";
//      String mSetMockA = "setMockA";
//      try {
//
//         Object nullM = mr.invokeMatchedMethod(this, this.getClass(), mName, null);
//         assertEquals(null, nullM);
//
//      } catch (Exception e) {
//
//      }
//
//      try {
//         A a = new ReflectsTest().new A();
//         /* null pointer exception
//          * Object o = mr.invokeMatchedMethod(a, a.getClass(), mGetMockA, null);
//         assertEquals(null, o);*/
//         Object strGet = mr.invokeMatchedMethod(a, a.getClass(), mGetMockA);
//         assertEquals(null, strGet);
//         Object strGetx = mr.invokeMatchedMethod(a, a.getClass(), mGetMockA, "x");
//         assertEquals(null, strGetx);
//         Object set = mr.invokeMatchedMethod(a, a.getClass(), mSetMockA, "a");
//         assertEquals(null, set);
//         Object strGeta = mr.invokeMatchedMethod(a, a.getClass(), mGetMockA);
//         assertEquals("a", strGeta);
//
//         //dead loop
//         //mr.invokeMatchedMethod(this, this.getClass(), "testInvokeMatchedMethod");
//
//      } catch (Exception e) {
//         e.printStackTrace();
//      }
//   }

   //	@Test
   //	public void testInvokeDeclaredMethod(){
   //		Reflects.MethodReflector mr = Reflects.forMethod();
   //		String mName = "getMock";
   //		String mGetMockA = "getMockA";
   //		String mSetMockA = "setMockA";
   //		try {
   //			
   //			Object nullM = mr.invokeDeclaredMethod(this, mName, null);
   //			assertEquals(null, nullM);
   //			
   //		} catch (Exception e) {
   //			
   //		}
   //		
   //		try {
   //			A a = new ReflectsTest().new A();
   //			/*null pointer exception
   //			 * Object o = mr.invokeMatchedMethod(a, mGetMockA, null);
   //			assertEquals(null, o);*/
   //			Object strGet = mr.invokeDeclaredMethod(a, mGetMockA);
   //			assertEquals(null, strGet);
   //			Object strGetx = mr.invokeDeclaredMethod(a,  mGetMockA,String.class, "x");
   //			assertEquals(null, strGetx);
   //			Object set = mr.invokeDeclaredMethod(a,  mSetMockA, String.class, "a");
   //			assertEquals(null, set);
   //			Object strGeta = mr.invokeDeclaredMethod(a,  mGetMockA);
   //			assertEquals("a", strGeta);
   //			
   //			//dead loop
   //			//mr.invokeMatchedMethod(this, "testInvokeMatchedMethod");
   //			
   //		} catch (Exception e) {
   //			e.printStackTrace();
   //		}
   //	}

   @Test
   public void testReflectsConstructure() {
      Object self;
      try {
         Constructor[] ctors = Reflects.class.getDeclaredConstructors();
         ctors[0].setAccessible(true);
         self = ctors[0].newInstance();

         assertEquals(Reflects.class, self.getClass());

      } catch (Exception e) {
         e.printStackTrace();

      }

   }

   public String getPubMockMethod() {
      return "getPubMockMethod";
   }

   public static String getStaticMethod() {
      return "getStaticMethod";
   }

   class A {
      private String mockA;

      public String getMockA() {
         return mockA;
      }

      public void setMockA(String name) {
         mockA = name;
      }

      class B {

      }
   }
}
