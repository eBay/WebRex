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

package com.ebayopensource.webrex.resource.tag;

import java.util.LinkedHashMap;
import java.util.Map;

import com.ebayopensource.webrex.util.Xmls;

public class ResourceTagModel implements ITagModel {
   public String m_exptectedResourceType;

   private String m_bodyContent;

   private Map<String, Object> m_dynamicAttributes;

   private Object m_value;

   @Override
   public Map<String, Object> getAttributes() {
      if (m_dynamicAttributes == null) {
         m_dynamicAttributes = new LinkedHashMap<String, Object>(8, 0.75f, true);
         return m_dynamicAttributes;
      } else {
         return m_dynamicAttributes;
      }
   }

   @Override
   public String getContent() {
      return m_bodyContent;
   }

   public String getExpectedResourceType() {
      return m_exptectedResourceType;
   }

   @Override
   public Object getValue() {
      return m_value;
   }

   @Override
   public void setAttribute(String name, Object value) {
      if (m_dynamicAttributes == null) {
         m_dynamicAttributes = new LinkedHashMap<String, Object>(8, 0.75f, true);
      }

      m_dynamicAttributes.put(name, value);
   }

   public void setContent(String bodyContent) {
      m_bodyContent = Xmls.forData().trimCData(bodyContent);
   }

   public void setExpectedResourceType(String expectedType) {
      m_exptectedResourceType = expectedType;
   }

   @Override
   public void setValue(Object value) {
      m_value = value;
   }

   @Override
   public String toString() {
      return "ResourceTagModel [m_bodyContent=" + m_bodyContent + ", m_dynamicAttributes=" + m_dynamicAttributes
            + ", m_value=" + m_value + "]";
   }
}
