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

import javax.servlet.ServletContext;

public class Servlets {
   public static Context forContext() {
      return Context.INSTANCE;
   }

   public static enum Context {
      INSTANCE;

      public String getContextPath(Object context) {
         String contextPath = null;

         if (context instanceof ServletContext) {
            ServletContext servletContext = (ServletContext) context;
            // Servlet 2.5
            contextPath = Reflects.forMethod().invokeMethod(servletContext, "getContextPath");

            if (contextPath == null) {
               // Servlet 2.4
               contextPath = servletContext.getServletContextName();
            }
         } else if (context instanceof String) {
            contextPath = (String) context;
         }

         return getPath(contextPath);
      }

      private String getPath(String contextPath) {
         if (contextPath != null) {
            if ("/".equals(contextPath) || "".equals(contextPath)) {
               contextPath = null;
            } else if (!contextPath.startsWith("/")) {
               contextPath = "/" + contextPath;
            }
         }

         return contextPath;
      }
   }

}
