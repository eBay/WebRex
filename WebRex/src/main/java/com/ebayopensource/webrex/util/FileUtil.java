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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FileUtil {
   public static String getExtension(String path) {
      int pos = path.lastIndexOf('.');
      if (pos != -1 && pos != (path.length() - 1)) {
         return path.substring(pos + 1);
      }

      return null;
   }

   private static String getRelativePath(Stack<String> pathStack) {
      StringBuilder sb = new StringBuilder();
      sb.append('/');

      int size = pathStack.size();
      for (int i = 0; i < size; i++) {
         sb.append(pathStack.get(i)).append('/');
      }

      return sb.toString();
   }

   public static Map<String, URL> listFiles(File file) {
      return listFiles(file, null);
   }

   public static Map<String, URL> listFiles(File file, IFinderFilter filter) {
      if (file.isDirectory()) {
         Map<String, URL> map = new HashMap<String, URL>();
         listFiles(file, filter, new Stack<String>(), map);
         return map;
      } else {
         return Collections.emptyMap();
      }
   }

   private static void listFiles(File parent, IFinderFilter filter, Stack<String> pathStack, Map<String, URL> map) {
      File[] files = parent.listFiles();
      for (File file : files) {
         String name = file.getName();

         String relativePath = getRelativePath(pathStack);
         String path = relativePath + name;
         if (file.isFile() && (filter == null || filter.doFilter(path))) {
            try {
               // System.out.println(relativePath + name + " " +
               // file.toURI());
               map.put(path, file.toURI().toURL());
            } catch (MalformedURLException e) {
               // ignore it
               continue;
            }
         } else if (file.isDirectory()) {
            pathStack.push(name);
            listFiles(file, filter, pathStack, map);
            pathStack.pop();
         }
      }
   }

   public static Map<String, URL> listJarFiles(File file, String prefix, IFinderFilter filter) throws IOException {
      Map<String, URL> map = new HashMap<String, URL>();

      Enumeration<JarEntry> entries;
      JarFile jarFile = null;
      try {
         jarFile = new JarFile(file);
         entries = jarFile.entries();
         while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();

            if (!entry.isDirectory() && name.startsWith(prefix) && filter.doFilter(name)) {
               String path = file.getPath();
               String newPath;
               if (path.startsWith("/")) {
                  newPath = "jar:file:" + path + "!/" + name;
               } else {
                  newPath = "jar:file:/" + path + "!/" + name;
               }
               map.put(name.substring(prefix.length()), new URL(newPath));
            }
         }

         return map;
      } finally {
         if (jarFile != null) {
            jarFile.close();
         }
      }
   }

   public static interface IFinderFilter {
      public boolean doFilter(String relativePath);
   }
}
