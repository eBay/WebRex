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

import java.io.IOException;
import java.net.URL;

import com.ebayopensource.webrex.resource.FlashResource;
import com.ebayopensource.webrex.resource.ImageResource;
import com.ebayopensource.webrex.resource.Resource;
import com.ebayopensource.webrex.resource.ResourceErrConstants;
import com.ebayopensource.webrex.resource.ResourceException;
import com.ebayopensource.webrex.resource.ResourceTypeConstants;
import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.api.IResourceLibrary;
import com.ebayopensource.webrex.resource.api.IResourceLocale;
import com.ebayopensource.webrex.resource.api.IResourceUrn;
import com.ebayopensource.webrex.resource.spi.IResourceFactory;
import com.ebayopensource.webrex.util.FlashAnalyzer;
import com.ebayopensource.webrex.util.ImageAnalyzer;

public class DefaultResourceFactory implements IResourceFactory<IResource> {

   protected IResource createFlashResource(IResourceUrn urn, URL physicalUrl, IResourceLocale locale,
         IResourceLibrary libraryInfo) {
      FlashResource resource = new FlashResource(urn, physicalUrl, locale, libraryInfo);
      try {
         FlashAnalyzer analyzer = new FlashAnalyzer();
         analyzer.setInput(physicalUrl.openStream());
         analyzer.check();
         resource.setWidth(analyzer.getWidth());
         resource.setHeight(analyzer.getHeight());
      } catch (IOException e) {
         throw new ResourceException(ResourceErrConstants.RESOURCE_CREATION_ERR, "Failed to analyze flash resource(" + urn
               + ")", e);
      }

      return resource;
   }

   protected IResource createImageResource(IResourceUrn urn, URL physicalUrl, IResourceLocale locale,
         IResourceLibrary libraryInfo) {
      ImageResource imgResource = new ImageResource(urn, physicalUrl, locale, libraryInfo);
      try {
         ImageAnalyzer analyzer = new ImageAnalyzer();
         analyzer.setInput(physicalUrl.openStream());
         analyzer.check();
         imgResource.setWidth(analyzer.getWidth());
         imgResource.setHeight(analyzer.getHeight());
      } catch (IOException e) {
         throw new ResourceException(ResourceErrConstants.RESOURCE_CREATION_ERR, "Failed to analyze image resource(" + urn
               + ")", e);
      }

      return imgResource;
   }

   @Override
   public IResource createResource(IResourceUrn urn, URL physicalUrl, IResourceLocale locale,
         IResourceLibrary libraryInfo) {
      if (ResourceTypeConstants.IMAGE.equals(urn.getType())) {
         return createImageResource(urn, physicalUrl, locale, libraryInfo);
      } else if (ResourceTypeConstants.FLASH.equals(urn.getType())) {
         return createFlashResource(urn, physicalUrl, locale, libraryInfo);
      }

      //default to create resource instance
      return new Resource(urn, physicalUrl, locale, libraryInfo);
   }

}
