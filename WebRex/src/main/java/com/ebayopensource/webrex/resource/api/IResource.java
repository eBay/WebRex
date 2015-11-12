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

package com.ebayopensource.webrex.resource.api;

import java.net.URL;
import java.util.List;

import com.ebayopensource.webrex.resource.spi.IResourceHandler;

public interface IResource {
   public byte[] getBinaryContent(IResourceContext context);

   public String getContent(IResourceContext context);

   public List<IResource> getDependencies();

   public long getLastModified();

   public IResourceLibrary getLibrary();

   public IResourceLocale getLocale();

   public byte[] getOriginalBinaryContent();

   public byte[] getOriginalBinaryContent(ITemplateContext context);
   /**
    * Support for content pre-processing, for example, image in CSS, dust, less
    */
   public String getOriginalContent();
   
   /**
    * for resource hotswap in local resource server, need process resource in another thread
    * @return
    */
   public String getOriginalContent(ITemplateContext context);

   public URL getOriginalUrl();

   /**
    * Convenient method to get web request url of this resource, the url is return by the IResourceHandler
    * @return the browser request url of the resource
    */
   public String getUrl(IResourceContext context);

   public IResourceUrn getUrn();

   public void setDependencies(List<IResource> resources);

   public void setHandler(IResourceHandler handler);
}
