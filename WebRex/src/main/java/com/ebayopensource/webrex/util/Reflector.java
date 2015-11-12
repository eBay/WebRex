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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public enum Reflector {
   INSTANCE;

   private String getResourceName(Class<?> clazz, String resName) {
      // Turn package name into a directory path
      if (resName.length() > 0 && resName.charAt(0) == '/') {
         return resName.substring(1);
      }

      String qualifiedClassName = clazz != null ? clazz.getName() : getClass().getName();
      int classIndex = qualifiedClassName.lastIndexOf('.');
      if (classIndex == -1) {
         return resName; // from a default package
      }
      return qualifiedClassName.substring(0, classIndex + 1).replace('.', '/') + resName;
   }

   public List<Properties> getResourcesProperties(ClassLoader classloader, Class<?> anchorClass, String resName) {
      List<Properties> properties = null;
      Enumeration<URL> urls;
      try {
         resName = getResourceName(anchorClass, resName);
         urls = classloader.getResources(resName);

         if (urls != null) {
            properties = new ArrayList<Properties>();

            while (urls.hasMoreElements()) {
               URL url = urls.nextElement();
               try {
                  Properties prop = new Properties();
                  prop.load(url.openStream());
                  properties.add(prop);
               } catch (IOException e) {
                  //ignore it
                  continue;
               }
            }
         }
      } catch (IOException e1) {
         //ignore it
         return null;
      }

      return properties;
   }

}
