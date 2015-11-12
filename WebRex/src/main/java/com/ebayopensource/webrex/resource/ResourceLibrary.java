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

import com.ebayopensource.webrex.resource.api.IResourceLibrary;

public class ResourceLibrary implements IResourceLibrary {
   private String m_id;

   private String m_version;

   private LibraryType m_type;

   private int m_hashcode;

   public ResourceLibrary(String id, String version, LibraryType type) {
      m_id = id;
      m_version = version;
      m_type = type;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      ResourceLibrary other = (ResourceLibrary) obj;
      if (m_id == null) {
         if (other.m_id != null)
            return false;
      } else if (!m_id.equals(other.m_id))
         return false;
      if (m_type != other.m_type)
         return false;
      if (m_version == null) {
         if (other.m_version != null)
            return false;
      } else if (!m_version.equals(other.m_version))
         return false;
      return true;
   }

   @Override
   public String getId() {
      return m_id;
   }

   @Override
   public LibraryType getType() {
      return m_type;
   }

   @Override
   public String getVersion() {
      return m_version;
   }

   @Override
   public int hashCode() {
      if (m_hashcode == 0) {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((m_id == null) ? 0 : m_id.hashCode());
         result = prime * result + ((m_type == null) ? 0 : m_type.hashCode());
         result = prime * result + ((m_version == null) ? 0 : m_version.hashCode());
         m_hashcode = result;
      }

      return m_hashcode;
   }

   @Override
   public String toString() {
      return "ResourceLibrary [m_id=" + m_id + ", m_version=" + m_version + ", m_type=" + m_type + "]";
   }
}
