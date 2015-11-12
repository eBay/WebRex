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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.api.IResourceContext;
import com.ebayopensource.webrex.resource.api.IResourceLibrary;
import com.ebayopensource.webrex.resource.api.IResourceLocale;
import com.ebayopensource.webrex.resource.api.IResourceUrn;
import com.ebayopensource.webrex.resource.api.ITemplateContext;
import com.ebayopensource.webrex.resource.spi.IResourceHandler;
import com.ebayopensource.webrex.resource.spi.IResourceRegistry;
import com.ebayopensource.webrex.resource.spi.ITemplateProcessor;
import com.ebayopensource.webrex.util.Urls;

public class Resource implements IResource {
   private IResourceUrn m_urn;

   private URL m_url;

   private IResourceLocale m_locale;

   private IResourceLibrary m_libInfo;

   private IResourceHandler m_handler;

   private List<IResource> m_dependencies;

   private int m_hashCode;

   public Resource(IResourceUrn urn, URL physicalUrl, IResourceLocale locale, IResourceLibrary libraryInfo) {
      m_urn = urn;
      m_url = physicalUrl;
      m_locale = locale;
      m_libInfo = libraryInfo;
   }

   public Resource(Resource resource) {
      this(resource.m_urn, resource.m_url, resource.m_locale, resource.m_libInfo);
      this.m_handler = resource.m_handler;
      this.m_dependencies = resource.m_dependencies;
      this.m_hashCode = resource.m_hashCode;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Resource other = (Resource) obj;
      if (m_libInfo == null) {
         if (other.m_libInfo != null)
            return false;
      } else if (!m_libInfo.equals(other.m_libInfo))
         return false;
      if (m_urn == null) {
         if (other.m_urn != null)
            return false;
      } else if (!m_urn.equals(other.m_urn))
         return false;
      return true;
   }

   @Override
   public byte[] getBinaryContent(IResourceContext context) {
      Object getHandlerContent = context.getAttribute("get_handler_content");
      Boolean goThroughHandler = Boolean.FALSE;
      if (getHandlerContent != null && getHandlerContent instanceof Boolean) {
         goThroughHandler = (Boolean) getHandlerContent;
      }

      if (goThroughHandler) {
         try {
            if (m_handler != null) {
               return m_handler.getBinaryContent(this, context);
            }
         } catch (Throwable e) {
            ResourceRuntimeContext
                  .ctx()
                  .getConfig()
                  .getRegistry()
                  .getErrorHandler()
                  .handle(
                        new ResourceException(ResourceErrConstants.HANDLER_CONTENT_ERR, String.format(
                              "Error get content on resource(%s).", this.getUrn()), e), e);
         }
      } else {
         return getOriginalBinaryContent();
      }

      return null;
   }

   @Override
   public String getContent(IResourceContext context) {
      Object getHandlerContent = context.getAttribute("get_handler_content");
      Boolean goThroughHandler = Boolean.FALSE;
      if (getHandlerContent != null && getHandlerContent instanceof Boolean) {
         goThroughHandler = (Boolean) getHandlerContent;
      }

      if (goThroughHandler) {
         try {
            if (m_handler != null) {
               return m_handler.getContent(this, context);
            }
         } catch (Throwable e) {
            ResourceRuntimeContext
                  .ctx()
                  .getConfig()
                  .getRegistry()
                  .getErrorHandler()
                  .handle(
                        new ResourceException(ResourceErrConstants.HANDLER_CONTENT_ERR, String.format(
                              "Error get content on resource(%s).", this.getUrn()), e), e);
         }
      } else {
         return getOriginalContent();
      }
      return null;
   }

   @Override
   public List<IResource> getDependencies() {
      if (m_dependencies == null) {
         return Collections.emptyList();
      } else {
         return m_dependencies;
      }
   }

   private String getExtFromPath(String path) {
      int index = path.lastIndexOf('.');
      if (index >= 0) {
         return path.substring(index + 1);
      }
      return null;
   }

   @Override
   public long getLastModified() {
      long maxLastModified = -1;
      if (m_dependencies != null) {
         for (IResource resource : m_dependencies) {
            long lastModified = resource.getLastModified();
            if (lastModified > maxLastModified) {
               maxLastModified = lastModified;
            }
         }
      }
      try {
         long lastModified = m_url.openConnection().getLastModified();
         if (lastModified > maxLastModified) {
            maxLastModified = lastModified;
         }
         return maxLastModified;
      } catch (IOException e) {
         return -1;
      }
   }

   @Override
   public IResourceLibrary getLibrary() {
      return m_libInfo;
   }

   @Override
   public IResourceLocale getLocale() {
      return m_locale;
   }

   @Override
   public byte[] getOriginalBinaryContent() {
      ITemplateProcessor processor = getTemplateProcessor();
      if (processor != null) {
         String content = getOriginalContent(processor, Urls.getContent(m_url));
         if (content != null) {
            try {
               return content.getBytes("utf-8");
            } catch (UnsupportedEncodingException e) {
               return content.getBytes();
            }
         }
      } else {
         return Urls.getBinaryContent(m_url);
      }

      return null;
   }

   @Override
   public byte[] getOriginalBinaryContent(ITemplateContext context) {
      boolean initialized = ResourceRuntimeContext.isInitialized();
      if (!initialized) {
         ResourceRuntimeContext.setup();
      }

      ResourceRuntimeContext ctx = ResourceRuntimeContext.ctx();
      IResourceContext resourceContext = ctx.getResourceContext();
      IResourceLocale oldLocale = resourceContext.getLocale();
      boolean oldSecure = resourceContext.isSecure();

      try {
         resourceContext.setLocale(context.getLocale());
         ;
         resourceContext.setSecure(context.isSecure());

         return getOriginalBinaryContent();
      } finally {
         if (!initialized) {
            ResourceRuntimeContext.reset();
         } else {
            resourceContext.setLocale(oldLocale);
            resourceContext.setSecure(oldSecure);
         }
      }
   }

   @Override
   public String getOriginalContent() {
      //template processor
      ITemplateProcessor processor = getTemplateProcessor();
      if (processor != null) {
         return getOriginalContent(processor, Urls.getContent(m_url));
      }

      return Urls.getContent(m_url);
   }

   @Override
   public String getOriginalContent(ITemplateContext context) {
      boolean initialized = ResourceRuntimeContext.isInitialized();
      if (!initialized) {
         ResourceRuntimeContext.setup();
      }

      ResourceRuntimeContext ctx = ResourceRuntimeContext.ctx();
      IResourceContext resourceContext = ctx.getResourceContext();
      IResourceLocale oldLocale = resourceContext.getLocale();
      boolean oldSecure = resourceContext.isSecure();

      try {
         resourceContext.setLocale(context.getLocale());
         ;
         resourceContext.setSecure(context.isSecure());

         return getOriginalContent();
      } finally {
         if (!initialized) {
            ResourceRuntimeContext.reset();
         } else {
            resourceContext.setLocale(oldLocale);
            resourceContext.setSecure(oldSecure);
         }
      }
   }

   private String getOriginalContent(ITemplateProcessor processor, String content) {
      if (content == null) {
         return null;
      }

      StringBuilder template = new StringBuilder(content);
      processor.process(template, this);
      return template.toString();
   }

   @Override
   public URL getOriginalUrl() {
      return m_url;
   }

   private ITemplateProcessor getTemplateProcessor() {
      IResourceRegistry registry = ResourceRuntimeContext.ctx().getResourceContext().getConfig().getRegistry();
      ITemplateProcessor processor = registry.getTemplateProcessor(getExtFromPath(m_urn.getPath()));
      return processor;
   }

   @Override
   public String getUrl(IResourceContext context) {
      try {
         if (m_handler != null) {
            return m_handler.getUrl(this, context);
         }
      } catch (Throwable e) {
         ResourceRuntimeContext
               .ctx()
               .getConfig()
               .getRegistry()
               .getErrorHandler()
               .handle(
                     new ResourceException(ResourceErrConstants.HANDLER_URL_ERR, String.format(
                           "Error get url on resource(%s).", this.getUrn()), e), e);
      }
      return null;
   }

   @Override
   public IResourceUrn getUrn() {
      return m_urn;
   }

   @Override
   public int hashCode() {
      if (m_hashCode == 0) {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((m_libInfo == null) ? 0 : m_libInfo.hashCode());
         result = prime * result + ((m_urn == null) ? 0 : m_urn.hashCode());
         m_hashCode = result;
      }

      return m_hashCode;
   }

   @Override
   public void setDependencies(List<IResource> dependencies) {
      m_dependencies = dependencies;
   }

   public void setHandler(IResourceHandler handler) {
      m_handler = handler;
   }

   @Override
   public String toString() {
      return "Resource [m_urn=" + m_urn + ", m_libInfo=" + m_libInfo + "]";
   }

}
