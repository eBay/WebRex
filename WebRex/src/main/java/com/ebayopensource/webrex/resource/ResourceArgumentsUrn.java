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

import java.util.Map;

import com.ebayopensource.webrex.resource.api.IResourceArgumentsUrn;

public class ResourceArgumentsUrn extends ResourceUrn implements IResourceArgumentsUrn {

   private Map<String, Object> m_argument;

   public ResourceArgumentsUrn(String type, String namespace, String resourcePath, Map<String, Object> arguments) {
      super(type, namespace, resourcePath);
      m_argument = arguments;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      ResourceArgumentsUrn other = (ResourceArgumentsUrn) obj;
      if (m_argument == null) {
         if (other.m_argument != null)
            return false;
      } else if (!m_argument.equals(other.m_argument))
         return false;
      return true;
   }

   @Override
   public Map<String, Object> getArgument() {
      return m_argument;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((m_argument == null) ? 0 : m_argument.hashCode());
      return result;
   }

}
