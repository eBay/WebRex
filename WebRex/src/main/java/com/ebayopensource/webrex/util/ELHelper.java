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

import java.util.ArrayList;
import java.util.List;

import com.ebayopensource.webrex.resource.Resource;
import com.ebayopensource.webrex.resource.expression.ResBeanExpression;
import com.ebayopensource.webrex.resource.expression.ResourceExpression;

public class ELHelper {

   public static String getRealPathFromEL(String el) {
      List<String> keys = ELHelper.getELKeys(el);
      if (keys != null) {

         ResourceExpression expr = new ResBeanExpression();
         Object value = null;
         for (int i = 1; i < keys.size(); i++) {
            String key = keys.get(i);

            value = expr.get(key);
            if (value instanceof ResourceExpression) {
               expr = (ResourceExpression) value;
            } else if (value != null) {
               break;
            }
         }

         if (value instanceof ResourceExpression) {
            Object resourceObject = ((ResourceExpression) value).evaluate();
            if (resourceObject instanceof Resource) {
               return ((Resource) resourceObject).getOriginalUrl().toString();
            }
         } else if (value != null) {
            return value.toString();
         }
      }
      return null;
   }

   public static ResourceExpression getExpressionFromEL(String el) {
      List<String> keys = ELHelper.getELKeys(el);
      if (keys != null) {

         ResourceExpression expr = new ResBeanExpression();
         Object value = null;
         for (int i = 1; i < keys.size(); i++) {
            String key = keys.get(i);

            value = expr.get(key);
            if (value instanceof ResourceExpression) {
               expr = (ResourceExpression) value;
            } else if (value != null) {
               break;
            }
         }

         if (value instanceof ResourceExpression) {
            return (ResourceExpression)value;
         }
      }
      return null;
   }

   public static List<String> getELKeys(String el) {
      int end;
      int start;

      if (el.startsWith("${")) {
         end = el.length() - 1;
         start = 2;
      } else {
         end = el.length();
         start = 0;
      }

      List<String> keys = new ArrayList<String>(32);
      StringBuilder sb = new StringBuilder(64);
      boolean singleBracet = false;
      boolean doubleBracet = false;
      int i = start;
      while (i < end) {
         //      for (int i = start; i < end; i++) {
         char c = el.charAt(i);

         //handle [', ']
         if (singleBracet) {
            if (c == '\'' && (i + 1 < end)) {
               if ((el.charAt(i + 1) == ']')) {
                  keys.add(sb.toString().trim());
                  sb.setLength(0);
                  i++;
                  singleBracet = false;
                  if ((i + 1 < end) && (el.charAt(i + 1) == '.')) {
                     i++;
                  }
               } else {
                  sb.append(c);
               }
            } else {
               sb.append(c);
            }
         } else if (doubleBracet) {
            //handle [", "]
            if (c == '\"' && (i + 1 < end)) {
               if ((el.charAt(i + 1) == ']')) {
                  keys.add(sb.toString());
                  sb.setLength(0);
                  i++;
                  doubleBracet = false;
                  if ((i + 1 < end) && (el.charAt(i + 1) == '.')) {
                     i++;
                  }
               } else {
                  sb.append(c);
               }

            } else {
               sb.append(c);
            }
         } else if (c == '.') {
            if (sb.length() > 0) {
               keys.add(sb.toString().trim());
               sb.setLength(0);
            } else {
               //not valid el
               return null;
            }
         } else if (c == '[' && (i + 1 < end)) {
            if (el.charAt(i + 1) == '\'') {
               i++;
               if (sb.length() > 0) {
                  keys.add(sb.toString().trim());
                  sb.setLength(0);
               }
               singleBracet = true;
            } else if (el.charAt(i + 1) == '\"') {
               i++;
               if (sb.length() > 0) {
                  keys.add(sb.toString().trim());
                  sb.setLength(0);
               }
               doubleBracet = true;
            } else {
               //not valid el
               return null;
            }
         } else {

            sb.append(c);
         }
         i++;
      }

      if (singleBracet || doubleBracet) {
         return null;
      }

      if (sb.length() > 0) {
         keys.add(sb.toString().trim());
      }

      return keys;
   }

}
