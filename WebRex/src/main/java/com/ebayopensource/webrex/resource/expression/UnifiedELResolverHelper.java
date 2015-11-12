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

import javax.el.ELResolver;
import javax.servlet.ServletContext;

import com.ebayopensource.webrex.util.Reflects;

public class UnifiedELResolverHelper {
   public static void register(ClassLoader loader, ServletContext servletContext) {
      registerJSPELResolver(loader, servletContext);
      registerJSFELResolver(loader);
   }

   private static void registerJSFELResolver(ClassLoader loader) {
      // Register JSFELResolver
      Class<?> factory = null;
      try {
         factory = loader.loadClass("javax.faces.FactoryFinder");
      } catch (ClassNotFoundException e) {
         return;
      }
      if (factory != null) {
         Object instance = Reflects.forMethod().invokeStaticMethod(factory, "getFactory",
               new Object[] { String.class, "javax.faces.application.ApplicationFactory" });
         if (instance != null) {
            Object context = Reflects.forMethod().invokeMethod(instance, "getApplication", new Object[] {});
            if (context != null) {
               Reflects.forMethod().invokeMethod(context, "addELResolver",
                     new Object[] { ELResolver.class, new UnifiedResourceELResolver() });
            }
         }
      }
   }

   private static void registerJSPELResolver(ClassLoader loader, ServletContext servletContext) {
      if (servletContext == null) {
         throw new RuntimeException("Register JSP EL Resolver failed, servletContext couldn't be null.");
      }
      // Register JSPELResolver
      Class<?> factory = null;
      try {
         factory = loader.loadClass("javax.servlet.jsp.JspFactory");
      } catch (ClassNotFoundException e) {
         return;
      }
      if (factory != null) {
         Object instance = Reflects.forMethod().invokeStaticMethod(factory, "getDefaultFactory", new Object[] {});
         if (instance != null) {
            Object context = Reflects.forMethod().invokeMethod(instance, "getJspApplicationContext",
                  new Object[] { ServletContext.class, servletContext });
            if (context != null) {
               Reflects.forMethod().invokeMethod(context, "addELResolver",
                     new Object[] { ELResolver.class, new UnifiedResourceELResolver() });
            }
         }
      }
   }

}
