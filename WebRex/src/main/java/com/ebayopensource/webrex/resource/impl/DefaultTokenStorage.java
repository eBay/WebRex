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

package com.ebayopensource.webrex.resource.impl;

import java.util.List;

import com.ebayopensource.webrex.resource.cache.ResourceCacheManager;
import com.ebayopensource.webrex.resource.spi.IResourceTokenStorage;
import com.ebayopensource.webrex.util.Checksum;
import com.ebayopensource.webrex.util.Joiners;
import com.ebayopensource.webrex.util.Splitters;

/**
 * Default token storage will only be used for the case which will use the token in the same JVM. 
 */
public class DefaultTokenStorage implements IResourceTokenStorage {
   private String getStrFromUrnList(List<String> urns) {
      return Joiners.by(',').join(urns);
   }

   private List<String> getUrnsFromStr(String urnStr) {
      if (urnStr != null) {
         return Splitters.by(',').split(urnStr);
      }

      return null;
   }

   @Override
   public List<String> loadResourceUrns(String token) {
      String urnStr = (String) ResourceCacheManager.getGlobalCache(token);
      return getUrnsFromStr(urnStr);
   }

   @Override
   public String storeResourceUrns(List<String> urns) {
      String urnStr = getStrFromUrnList(urns);
      String token = Checksum.checksum(urnStr);
      ResourceCacheManager.setGlobalCache(token, urnStr);

      return token;
   }
}
