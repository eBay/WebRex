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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseTagEnv implements ITagEnv {
   //Fixed for performance tuning
   protected StringBuilder m_err;

   //Fixed for performance tuning
   protected StringBuilder m_out;

   private TagOutputType m_outputType = TagOutputType.html;

   private Map<String, Object> m_properties;

   @Override
   public BaseTagEnv err(Object value) {
      if (m_err == null) {
         m_err = new StringBuilder(512);
      }
      m_err.append(value);
      return this;
   }

   @Override
   public void flush() throws IOException {
      // do nothing since we hold a buffer
   }

   public String getError() {
      if (m_err == null) {
         return "";
      }
      String value = m_err.toString();
      m_err.setLength(0);
      return value;
   }

   public String getOutput() {
      if (m_out == null) {
         return "";
      }
      String value = m_out.toString();
      m_out.setLength(0);
      return value;
   }

   @Override
   public TagOutputType getOutputType() {
      return m_outputType;
   }

   @Override
   public Object getProperty(String name) {
      if (m_properties != null) {
         return m_properties.get(name);
      } else {
         return null;
      }
   }

   @Override
   public void onError(String message, Throwable cause) {
      throw new RuntimeException(message, cause);
   }

   public BaseTagEnv out(Object value) {
      if (value != null) {
         if (m_out == null) {
            m_out = new StringBuilder(1024);
         }
         m_out.append(value);
      }
      return this;
   }

   @Override
   public void setOutputType(TagOutputType type) {
      if (type == null) {
         throw new IllegalArgumentException("TagOutputType can't be null.");
      }

      m_outputType = type;
   }

   @Override
   public void setProperty(String name, Object value) {
      if (m_properties == null) {
         m_properties = new HashMap<String, Object>();
      }

      m_properties.put(name, value);
   }
}
