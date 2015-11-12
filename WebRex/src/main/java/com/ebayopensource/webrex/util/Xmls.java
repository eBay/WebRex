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

public class Xmls {
   public static Data forData() {
      return new Data();
   }

   public static class Data {
      private static final String PREFIX = "<![CDATA[";

      private static final String SUFFIX = "]]>";

      private boolean m_trim;

      public boolean isEnclosedByCData(String xmlStr) {
         if (xmlStr != null) {
            if (m_trim) {
               xmlStr = xmlStr.trim();
            }
            return xmlStr.startsWith(PREFIX) && xmlStr.endsWith(SUFFIX);
         } else {
            return false;
         }
      }

      public String trimCData(String xmlStr) {
         if (isEnclosedByCData(xmlStr)) {
            if (m_trim) {
               xmlStr = xmlStr.trim();
            }

            return xmlStr.substring(PREFIX.length(), xmlStr.length() - SUFFIX.length());
         } else {
            return xmlStr;
         }
      }

      public Data trim() {
         m_trim = true;
         return this;
      }
   }
}
