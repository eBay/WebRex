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

public class Markers {
   public static DeferMarker forDefer() {
      return DeferMarker.INSTANCE;
   }

   public static enum DeferMarker {
      INSTANCE;

      public String build(String... sections) {
         StringBuilder sb = new StringBuilder(64);

         sb.append("${");
         sb.append("MARKER");

         for (String section : sections) {
            sb.append(',').append(section);
         }

         sb.append('}');

         return sb.toString();
      }

      public String[] parse(String marker) {
         String[] items = Splitters.split(marker, ',');
         int size = items.length;

         if (size > 0) {
            String[] sections = new String[size - 1];
            System.arraycopy(items, 1, sections, 0, sections.length);
            return sections;
         }

         return new String[] {};
      }

      public boolean validate(String marker) {
         return parse(marker).length > 0;
      }
   }
}
