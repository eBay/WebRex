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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public class Reflects {
   public static ClassReflector forClass() {
      return ClassReflector.INSTANCE;
   }

   //   public static ConstructorReflector forConstructor() {
   //      return ConstructorReflector.INSTANCE;
   //   }

//   public static FieldReflector forField() {
//      return FieldReflector.INSTANCE;
//   }

   public static MethodReflector forMethod() {
      return new MethodReflector();
   }

   private Reflects() {
   }

   //   public static ModifierReflector forModifier() {
   //      return ModifierReflector.INSTANCE;
   //   }

   public static enum ClassReflector {
      INSTANCE;

      /**
       * for class name like "a.b.C" or "a.b.C$D$E"
       * @param className
       * @return class from current context class loader
       */
      public Class<?> getClass(String className) {
         return getClass(className, null);
      }

      /**
       * for class name like "a.b.C" or "a.b.C$D$E"
       * @param className
       * @param classloader
       * @return class from current context class loader
       */
      public Class<?> getClass(String className, ClassLoader classloader) {
         Class<?> clazz = null;

         ClassLoader loader = classloader == null ? Thread.currentThread().getContextClassLoader() : classloader;
         if (loader != null) {
            try {
               clazz = loader.loadClass(className);
            } catch (ClassNotFoundException e) {
               //ignore it
               return clazz;
            }
         }
         return clazz;
      }

      /**
       * for class name like "a.b.C" or "a.b.C.D.E"
       * @param className
       * @return class from current context class loader
       */
      public Class<?> getClass2(String className) {
         return getClass2(className, null);
      }

      /**
       * for class name like "a.b.C" or "a.b.C.D.E"
       * @param className
       * @return class from current context class loader
       */
      public Class<?> getClass2(String className, ClassLoader classloader) {
         Class<?> clazz = null;
         String name = className;

         while (true) {
            clazz = getClass(name, classloader);

            if (clazz != null) {
               break;
            }

            //try with inner class name
            int pos = name.lastIndexOf('.');
            if (pos < 0) {
               break;
            }
            name = name.substring(0, pos) + '$' + name.substring(pos + 1);
         }

         return clazz;
      }

      public Class<?> getNestedClass(Class<?> clazz, String simpleName) {
         if (clazz != null) {
            Class<?>[] subClasses = clazz.getDeclaredClasses();

            if (subClasses != null) {
               for (Class<?> subClass : subClasses) {
                  if (subClass.getSimpleName().equals(simpleName)) {
                     return subClass;
                  }
               }
            }
         }

         return null;
      }

      public Class<?> getNestedClass(String className, String simpleName) {
         return getNestedClass(getClass(className), simpleName);
      }

      public Class<?> getNestedClass(String className, String simpleName, ClassLoader classloader) {
         return getNestedClass(getClass(className, classloader), simpleName);
      }
   }

//   public static enum FieldReflector {
//      INSTANCE;

//      @SuppressWarnings("unchecked")
//      public <T> T getStaticFieldValue(Class<?> clazz, String fieldName) {
//         if (clazz != null) {
//            try {
//               Field field = clazz.getField(fieldName);
//
//               return (T) field.get(null);
//            } catch (Exception e) {
//               // ignore it
//               return null;
//            }
//         }
//         return null;
//      }

      //      @SuppressWarnings("unchecked")
      //      public <T> T getStaticFieldValue(String className, String fieldName) {
      //         Class<?> clazz = forClass().getClass(className);
      //
      //         if (clazz != null) {
      //            return (T) getStaticFieldValue(clazz, fieldName);
      //         }
      //
      //         return null;
      //      }
//   }

   public static interface IMemberFilter<T extends Member> {
      public boolean filter(T member);
   }

   public static class MethodReflector {
      //      public Method getDeclaredMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
      //         try {
      //            return clazz.getDeclaredMethod(methodName, parameterTypes);
      //         } catch (SecurityException e) {
      //            // ignore it
      //            return null;
      //         } catch (NoSuchMethodException e) {
      //            // ignore it
      //            return null;
      //         }
      //      }

//      public List<Method> getDeclaredMethods(Class<?> clazz, IMemberFilter<Method> filter) {
//         List<Method> list = new ArrayList<Method>();
//
//         Method[] methods = clazz.getDeclaredMethods();
//         for (Method method : methods) {
//            if (filter == null || filter.filter(method)) {
//               list.add(method);
//            }
//
//         }
//
//         return list;
//      }
//
//      public String getGetMethodName(String propertyName) {
//         int len = propertyName == null ? 0 : propertyName.length();
//
//         if (len == 0) {
//            throw new IllegalArgumentException(String.format("Invalid property name: %s!", propertyName));
//         }
//
//         StringBuilder sb = new StringBuilder(len + 3);
//
//         sb.append("get");
//         sb.append(Character.toUpperCase(propertyName.charAt(0)));
//         sb.append(propertyName.substring(1));
//
//         return sb.toString();
//      }

      //      public Method getMatchedMethod(Class<?> clazz, String methodName, Object... paramValues) {
      //         Method[] methods = clazz.getMethods();
      //         Method method;
      //         Class<?>[] classes;
      //         int len = methods.length;
      //         for (int i = 0; i < len; i++) {
      //            method = methods[i];
      //            if (methodName.equals(method.getName())) {
      //               classes = method.getParameterTypes();
      //               if (classes.length < paramValues.length) {
      //                  continue;
      //               }
      //               if (isParamMatch(classes, paramValues, false)) {
      //                  return method;
      //               }
      //            }
      //         }
      //         return null;
      //      }

      public Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
         try {
            return clazz.getMethod(methodName, parameterTypes);
         } catch (SecurityException e) {
            // ignore it
            return null;
         } catch (NoSuchMethodException e) {
            // ignore it
            return null;
         }

      }

      @SuppressWarnings("unchecked")
      public <T> T getPropertyValue(Object instance, String propertyName) {
         if (propertyName.length() > 0) {
            String suffix = Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
            Method method = getMethod(instance.getClass(), "get" + suffix);

            // try isXXX() method for boolean return type
            if (method == null) {
               method = getMethod(instance.getClass(), "is" + suffix);

               if (method != null && method.getReturnType() != Boolean.TYPE) {
                  method = null;
               }
            }
            
            return (T)invokeMethod0(instance, method, (Object[])null);
         }

         return null;
      }

      //      public Object invokeMatchedMethod(Object obj, Class<?> clazz, String methodName, Object... paramValues)
      //            throws InvocationTargetException, IllegalAccessException {
      //         Method[] methods = clazz.getMethods();
      //         Method method;
      //         Class<?>[] classes;
      //         int len = methods.length;
      //         for (int i = 0; i < len; i++) {
      //            method = methods[i];
      //            if (Modifier.isPublic(method.getModifiers()) && methodName.equals(method.getName())) {
      //               classes = method.getParameterTypes();
      //               if (classes.length < paramValues.length) {
      //                  continue;
      //               }
      //               if (isParamMatch(classes, paramValues, true)) {
      //                  return method.invoke(obj, paramValues);
      //               }
      //            }
      //         }
      //         return null;
      //      }

      @SuppressWarnings("unchecked")
      public <T> T invokeMethod(Object instance, String methodName, Object... typesAndParameters) {
         if (instance == null) {
            return null;
         }

         TypeArguments typeArgs = new TypeArguments(typesAndParameters);
         Method method = getMethod(instance.getClass(), methodName, typeArgs.getTypes());
         return (T)invokeMethod0(instance, method, typeArgs.getArguments());
      }


      private static Object invokeMethod0(Object instance, Method method, Object[] args) {
         if (method != null) {
               try {
                  return method.invoke(instance, args);
               } catch (IllegalArgumentException e) {
                  return null;
               } catch (IllegalAccessException e) {
                  return null;
               } catch (InvocationTargetException e) {
                  return null;
               }
         }
         return null;
      }
      @SuppressWarnings("unchecked")
      public <T> T invokeStaticMethod(Class<?> clazz, String methodName, Object... typesAndParameters) {
         if (clazz == null) {
            return null;
         }

         TypeArguments typeArgs = new TypeArguments(typesAndParameters);
         Method method = getMethod(clazz, methodName, typeArgs.getTypes());
         return (T)invokeMethod0(null, method, typeArgs.getArguments());
      }

   }

   //   public static enum ModifierReflector {
   //      INSTANCE;
   //
   //      public boolean isPublic(Member member) {
   //         return Modifier.isPublic(member.getModifiers());
   //      }
   //
   //      public boolean isStatic(Class<?> clazz) {
   //         return Modifier.isStatic(clazz.getModifiers());
   //      }
   //
   //      public boolean isStatic(Member member) {
   //         return Modifier.isStatic(member.getModifiers());
   //      }
   //   }

   static class TypeArguments {
      private Class<?>[] m_types;

      private Object[] m_arguments;

      public TypeArguments(Object... typesAndParameters) {
         int length = typesAndParameters.length;

         if (length % 2 != 0) {
            throw new IllegalArgumentException(String.format("Constrcutor argument types and data should be even"
                  + ", but was odd: %s.", length));
         }

         int half = length / 2;
         Class<?>[] types = new Class<?>[half];
         Object[] arguments = new Object[half];

         for (int i = 0; i < half; i++) {
            types[i] = (Class<?>) typesAndParameters[2 * i];
            arguments[i] = typesAndParameters[2 * i + 1];
         }

         m_types = types;
         m_arguments = arguments;
      }

      public Object[] getArguments() {
         return m_arguments;
      }

      public Class<?>[] getTypes() {
         return m_types;
      }
   }
}
