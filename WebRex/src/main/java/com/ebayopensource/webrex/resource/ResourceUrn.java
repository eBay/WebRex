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

package com.ebayopensource.webrex.resource;

import com.ebayopensource.webrex.resource.api.IResourceUrn;

public class ResourceUrn implements IResourceUrn {

   private String m_path;

   private String m_namespace;

   private String m_type;

   private int m_hashCode;

   private String m_urnStr;

   public ResourceUrn(String type, String namespace, String resourcePath) {
      m_type = type;
      m_namespace = namespace;
      m_path = resourcePath;

      if (resourcePath.charAt(0) != '/') {
         throw new IllegalArgumentException("Invalid resource path, the path should start with '/'!");
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
//      if (getClass() != obj.getClass())
//         return false;
      if(!(obj instanceof ResourceUrn)) {
         return false;
      } 
      ResourceUrn other = (ResourceUrn) obj;
      
      if (m_path == null) {
         if (other.m_path != null)
            return false;
      } else if (!m_path.equals(other.m_path))
         return false;
      if (m_type == null) {
         if (other.m_type != null)
            return false;
      } else if (!m_type.equals(other.m_type))
         return false;
      
      if (m_namespace == null) {
          if (other.m_namespace != null)
             return false;
       } else if (!m_namespace.equals(other.m_namespace))
          return false;
       
      return true;
   }

   @Override
   public String getNamespace() {
      return m_namespace;
   }

   @Override
   public String getPath() {
      return m_path;
   }

   @Override
   public String getType() {
      return m_type;
   }

   @Override
   public int hashCode() {
      if (m_hashCode == 0) {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((m_namespace == null) ? 0 : m_namespace.hashCode());
         result = prime * result + ((m_path == null) ? 0 : m_path.hashCode());
         result = prime * result + ((m_type == null) ? 0 : m_type.hashCode());
         m_hashCode = result;
      }
      return m_hashCode;
   }

   @Override
   public String toString() {
      //type.ns:/path 
      if (m_urnStr == null) {
         StringBuilder sb = new StringBuilder(128);
         sb.append(m_type).append('.').append(m_namespace).append(':').append(m_path);
         m_urnStr = sb.toString();
      }
      return m_urnStr;
   }

}
