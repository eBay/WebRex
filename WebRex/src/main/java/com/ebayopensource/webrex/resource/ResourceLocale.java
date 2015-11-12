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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.ebayopensource.webrex.resource.api.IResourceLocale;
import com.ebayopensource.webrex.util.Splitters;

public class ResourceLocale implements IResourceLocale {

   public static IResourceLocale fromExternal(String external) {
      if(external.indexOf('_') != -1){
         List<String> localeInfo = Splitters.by('_').split(external);
         return new ResourceLocale(new Locale(localeInfo.get(0), localeInfo.get(1)));
      }
      return new ResourceLocale(new Locale(external));
   }

   private Locale m_locale;

   public ResourceLocale(Locale locale) {
      m_locale = locale;
   }

   @Override
   public Locale getLocale() {
      return m_locale;
   }

   @Override
   public String toExternal() {
      return m_locale.toString();
   }

   @Override
   public List<IResourceLocale> getLocaleFallback() {
      return Arrays.asList((IResourceLocale) this, null);
   }

}
