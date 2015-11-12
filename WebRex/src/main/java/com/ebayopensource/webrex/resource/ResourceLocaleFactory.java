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

import java.util.Locale;

import com.ebayopensource.webrex.resource.api.IResourceLocale;

public class ResourceLocaleFactory {
   public static ResourceLocaleFactory getInstance() {
      return s_instance;
   }

   private IResourceLocaleFactory m_factory;

   private static ResourceLocaleFactory s_instance = new ResourceLocaleFactory();

   private ResourceLocaleFactory() {
   }

   public IResourceLocale createLocale(String path) {
      if (m_factory != null) {
         return m_factory.create(path);
      } else {
         return new ResourceLocale(new Locale(path));
      }
   }

   public void setResourceLocaleFactory(IResourceLocaleFactory factory) {
      m_factory = factory;
   }

   public interface IResourceLocaleFactory {
      public IResourceLocale create(String path);
   }
}
