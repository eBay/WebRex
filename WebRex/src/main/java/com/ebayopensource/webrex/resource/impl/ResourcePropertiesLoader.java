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

package com.ebayopensource.webrex.resource.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.ebayopensource.webrex.logging.ILogger;
import com.ebayopensource.webrex.logging.LogLevel;
import com.ebayopensource.webrex.logging.LoggerFactory;

public class ResourcePropertiesLoader {
   private static ILogger s_logger = LoggerFactory.getLogger(ResourcePropertiesLoader.class);

   private static final String RESOURCE_PROPERTIES = "/WEB-INF/webrex/resource.properties";

   public Map<String, String> load(File warRoot) {
      File file = new File(warRoot, RESOURCE_PROPERTIES);
      if (!file.exists()) {
         // changed logger to INFO, since property is not required to be
         // there
         s_logger.log(LogLevel.INFO, "No resource.properties configured under " + warRoot);
         return null;
      }

      InputStream in = null;
      try {
         in = new FileInputStream(file);
         return load(in);
      } catch (Exception e) {
         s_logger.log(LogLevel.ERROR, "Failed to load resource.properties, exception:" + e.toString());
         return null;
      } finally {
         if (in != null) {
            try {
               in.close();
            } catch (IOException e) {
               // ignore it
            }
         }
      }
   }

   public Map<String, String> load(InputStream in) throws IOException {
      Map<String, String> result = new HashMap<String, String>();
      Properties props = new Properties();
      props.load(in);

      for (Entry<Object, Object> entry : props.entrySet()) {
         String key = (String) entry.getKey();
         String value = (String) entry.getValue();

         if (value != null) {
            value = value.length() == 0 ? null : value.trim();
         }
         if (key != null) {
            key = key.length() == 0 ? "" : key.trim();
         }

         result.put(key, value);
      }

      return result;
   }
}
