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

import com.ebayopensource.webrex.resource.api.IResourceLibrary;
import com.ebayopensource.webrex.resource.api.IResourceLocale;
import com.ebayopensource.webrex.resource.api.IResourceUrn;

public class ImageResource extends Resource {

   private int m_width;

   private int m_height;

   public ImageResource(IResourceUrn urn, URL physicalUrl, IResourceLocale locale, IResourceLibrary libraryInfo) {
      super(urn, physicalUrl, locale, libraryInfo);
   }
   
   public ImageResource(ImageResource resource) {
      super(resource);
      this.m_height = resource.m_height;
      this.m_width = resource.m_width;
   }

   public int getHeight() {
      return m_height;
   }

   public int getWidth() {
      return m_width;
   }

   public void setHeight(int height) {
      m_height = height;
   }

   public void setWidth(int width) {
      m_width = width;
   }

}
