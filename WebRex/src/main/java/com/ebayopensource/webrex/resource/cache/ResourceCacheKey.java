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

package com.ebayopensource.webrex.resource.cache;

import com.ebayopensource.webrex.resource.api.IResourceLocale;
import com.ebayopensource.webrex.resource.api.IResourceUrn;

public class ResourceCacheKey {

   private IResourceUrn m_urn;

   private IResourceLocale m_locale;

   public ResourceCacheKey(IResourceUrn urn, IResourceLocale locale) {
      m_urn = urn;
      m_locale = locale;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      ResourceCacheKey other = (ResourceCacheKey) obj;
      if (m_locale == null) {
         if (other.m_locale != null)
            return false;
      } else if (!m_locale.equals(other.m_locale))
         return false;
      if (m_urn == null) {
         if (other.m_urn != null)
            return false;
      } else if (!m_urn.equals(other.m_urn))
         return false;
      return true;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((m_locale == null) ? 0 : m_locale.hashCode());
      result = prime * result + ((m_urn == null) ? 0 : m_urn.hashCode());
      return result;
   }

}
