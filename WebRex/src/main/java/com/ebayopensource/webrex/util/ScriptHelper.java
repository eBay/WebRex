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

import java.util.Collections;
import java.util.Map;

import com.ebayopensource.webrex.resource.tag.ITagEnv.TagOutputType;

/**
 * Helper class to create javascript resource and html tags
 * 
 */
public class ScriptHelper {
   public static String createInlineJsScript(String text, Map<String, Object> attributes) {
      int len = (text == null ? 0 : text.length());
      StringBuilder sb = new StringBuilder(len + 64);

      if (len > 0) {
         sb.append("<script");

         if (attributes == null) {
            attributes = Collections.emptyMap();
         }

         Object type = attributes.get("type");

         if (type == null) {
            sb.append(" type=\"text/javascript\"");
         }

         for (Map.Entry<String, Object> e : attributes.entrySet()) {
            String key = e.getKey();

            sb.append(' ').append(key).append("=\"").append(e.getValue()).append('"');
         }

         sb.append('>');
         sb.append(text);
         sb.append("</script>");
      }

      return sb.toString();
   }

   public static String createJsScript(String url, Map<String, Object> attributes) {
      StringBuilder sb = new StringBuilder(url == null ? 0 : url.length() + 64);

      if (url != null) {
         sb.append("<script");

         if (attributes == null) {
            attributes = Collections.emptyMap();
         }

         sb.append(" src=\"").append(url).append("\"");

         Object type = attributes.get("type");
         if (type == null) {
            sb.append(" type=\"text/javascript\"");
         }

         for (Map.Entry<String, Object> e : attributes.entrySet()) {
            String key = e.getKey();

            if (!"src".equalsIgnoreCase(key)) {
               sb.append(' ').append(key).append("=\"").append(e.getValue()).append('"');
            }
         }

         sb.append("></script>");
      }

      return sb.toString();
   }

   public static String createInlineCssScript(String text, Map<String, Object> attributes) {
      StringBuilder sb = new StringBuilder(text == null ? 0 : text.length() + 64);
      // Fix for <res:body> without any css aggregated to the default "body" and "bottom"
      // there will be an empty "<style>" displayed.
      if (text != null && text.length() > 0) {
         sb.append("<style");

         if (attributes == null) {
            attributes = Collections.emptyMap();
         }

         Object type = attributes.get("type");

         if (type == null) {
            sb.append(" type=\"text/css\"");
         }

         for (Map.Entry<String, Object> e : attributes.entrySet()) {
            String key = e.getKey();
            sb.append(' ').append(key).append("=\"").append(e.getValue()).append('"');
         }

         sb.append('>');
         sb.append(text);
         sb.append("</style>");
      }

      return sb.toString();
   }

   public static String createCssScript(String url, Map<String, Object> attributes, TagOutputType outputType) {
      if (outputType != TagOutputType.html && outputType != TagOutputType.xhtml) {
         throw new RuntimeException(String.format("Unsupported TagOutputType(%s) for css link tag.", outputType));
      }

      StringBuilder sb = new StringBuilder(url == null ? 0 : url.length() + 64);
      if (url != null) {
         sb.append("<link");

         if (attributes == null) {
            attributes = Collections.emptyMap();
         }

         sb.append(" href=\"").append(url).append("\"");

         Object type = attributes.get("type");
         if (type == null) {
            sb.append(" type=\"text/css\"");
         }

         Object rel = attributes.get("rel");
         if (rel == null) {
            sb.append(" rel=\"stylesheet\"");
         }

         for (Map.Entry<String, Object> e : attributes.entrySet()) {
            String key = e.getKey();

            if (!"href".equalsIgnoreCase(key)) {
               sb.append(' ').append(key).append("=\"").append(e.getValue()).append('"');
            }
         }

         if (outputType == TagOutputType.html) {
            sb.append(">");
         } else { // xhtml case
            sb.append("/>");
         }
      }

      return sb.toString();
   }
}
