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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import com.ebayopensource.webrex.logging.ILogger;
import com.ebayopensource.webrex.logging.LogLevel;
import com.ebayopensource.webrex.logging.LoggerFactory;
import com.ebayopensource.webrex.resource.spi.IResourceRegistry;
import com.ebayopensource.webrex.util.Splitters;

public enum ResourceMimeTypeLoader {
   INSTANCE;

   private static ILogger s_logger = LoggerFactory.getLogger(ResourceMimeTypeLoader.class);

   private ResourceMimeTypeLoader() {
   }

   public void loadResourceMimeTypes(IResourceRegistry registry, URL mimeTypeUrl) {
      InputStream inputStream = null;
      BufferedReader br = null;
      try {
         inputStream = mimeTypeUrl.openStream();
         br = new BufferedReader(new InputStreamReader(inputStream));
         String line = "";
         while ((line = br.readLine()) != null) {
            if (line.startsWith("#")) {
               continue;
            }
            
            List<String> split = Splitters.by(',').split(line);
            if (split.size() == 3) {
               String resourceType = split.get(0).trim();
               String mimeType = split.get(1).trim();
               String extSet = split.get(2).trim();
               List<String> exts = Splitters.by(' ').split(extSet);

               registry.registerMimeTypes(resourceType, mimeType, exts);
            }
         }
      } catch (IOException e) {
         s_logger.log(LogLevel.WARN, "Failed to load resoure mime type file:" + mimeTypeUrl);
      } finally {
         if (br != null) {
            try {
               br.close();
            } catch (IOException e) {
               //ignore
            }
         }
         if (inputStream != null) {
            try {
               inputStream.close();
            } catch (IOException e) {
               //ignore
            }
         }
      }
   }
}
