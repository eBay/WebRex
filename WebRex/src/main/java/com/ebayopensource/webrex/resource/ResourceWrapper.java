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
import java.util.List;

import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.api.IResourceContext;
import com.ebayopensource.webrex.resource.api.IResourceLibrary;
import com.ebayopensource.webrex.resource.api.IResourceLocale;
import com.ebayopensource.webrex.resource.api.IResourceUrn;
import com.ebayopensource.webrex.resource.api.ITemplateContext;
import com.ebayopensource.webrex.resource.spi.IResourceHandler;

public class ResourceWrapper implements IResource {
   protected IResource m_resource;
   
   public ResourceWrapper(IResource resource) {
      m_resource = resource;
   }

   @Override
   public byte[] getBinaryContent(IResourceContext context) {
      return m_resource.getBinaryContent(context);
   }

   @Override
   public String getContent(IResourceContext context) {
      return m_resource.getContent(context);
   }

   @Override
   public List<IResource> getDependencies() {
      return m_resource.getDependencies();
   }

   @Override
   public long getLastModified() {
      return m_resource.getLastModified();
   }

   @Override
   public IResourceLibrary getLibrary() {
      return m_resource.getLibrary();
   }

   @Override
   public IResourceLocale getLocale() {
      return m_resource.getLocale();
   }

   @Override
   public byte[] getOriginalBinaryContent() {
      return m_resource.getOriginalBinaryContent();
   }

   @Override
   public byte[] getOriginalBinaryContent(ITemplateContext context) {
      return m_resource.getOriginalBinaryContent(context);
   }

   @Override
   public String getOriginalContent() {
      return m_resource.getOriginalContent();
   }

   @Override
   public String getOriginalContent(ITemplateContext context) {
      return m_resource.getOriginalContent(context);
   }

   @Override
   public URL getOriginalUrl() {
      return m_resource.getOriginalUrl();
   }

   @Override
   public String getUrl(IResourceContext context) {
      return m_resource.getUrl(context);
   }

   @Override
   public IResourceUrn getUrn() {
      return m_resource.getUrn();
   }

   @Override
   public void setDependencies(List<IResource> resources) {
      m_resource.setDependencies(resources);
   }

   @Override
   public void setHandler(IResourceHandler handler) {
      m_resource.setHandler(handler);
   }
   
   
}
