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

import java.net.URL;

import com.ebayopensource.webrex.resource.api.IExternalResource;
import com.ebayopensource.webrex.resource.api.IResourceContext;
import com.ebayopensource.webrex.resource.api.IResourceLibrary;
import com.ebayopensource.webrex.resource.api.IResourceLocale;
import com.ebayopensource.webrex.resource.api.IResourceUrn;

public class ExternalResource extends Resource implements IExternalResource {
   private static final String EXTERNAL = "external";

   private static IResourceUrn createUrn(String externalUrl, String resType) {
      return new ResourceUrn(resType, EXTERNAL, "/" + externalUrl);
   }
   
   private String m_url;

   private boolean m_canAggregate = false;

   public ExternalResource(IResourceUrn urn, URL physicalUrl, IResourceLocale locale, IResourceLibrary libraryInfo) {
      super(urn, physicalUrl, locale, libraryInfo);
   }

   public ExternalResource(String externalUrl, String resType) {
      super(createUrn(externalUrl, resType), null, null, null);
      m_url = externalUrl;
   }

   @Override
   public boolean canAggregate() {
      return m_canAggregate;
   }

   @Override
   public String getExternalUrl() {
      return m_url;
   }

   @Override
   public String getOriginalContent() {
      return null;
   }

   @Override
   public String getUrl(IResourceContext context) {
      return m_url;
   }

   public void setCanAggregate(boolean canAggregate) {
      m_canAggregate = canAggregate;
   }
}
