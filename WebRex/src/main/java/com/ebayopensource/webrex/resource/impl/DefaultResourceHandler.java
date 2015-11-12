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

import java.io.UnsupportedEncodingException;

import com.ebayopensource.webrex.resource.api.IExternalResource;
import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.api.IResourceContext;
import com.ebayopensource.webrex.resource.api.IResourceLibrary;
import com.ebayopensource.webrex.resource.api.IResourceLocale;
import com.ebayopensource.webrex.resource.api.IResourceUrn;
import com.ebayopensource.webrex.resource.cache.ResourceCacheManager;
import com.ebayopensource.webrex.resource.spi.IResourceHandler;
import com.ebayopensource.webrex.resource.spi.IResourceOptimizer;
import com.ebayopensource.webrex.util.Checksum;
import com.ebayopensource.webrex.util.FileUtil;

public class DefaultResourceHandler implements IResourceHandler {
   private byte[] asByteArray(Object value) {
      if (value instanceof byte[]) {
         return (byte[]) value;
      } else if (value instanceof String) {
         try {
            return ((String) value).getBytes("utf-8");
         } catch (UnsupportedEncodingException e) {
            return ((String) value).getBytes();
         }
      } else {
         throw new RuntimeException("Can't convert value to byte array, value:" + value);
      }
   }

   private void assertExternal(IResource resource) {
      if (resource instanceof IExternalResource) {
         String externalUrl = ((IExternalResource) resource).getExternalUrl();
         throw new RuntimeException("External url " + externalUrl
               + " is not supported in resource tags, please use it in HTML tags.");
      }
   }

   private String asString(Object value) {
      if (value instanceof String) {
         return ((String) value);
      } else if (value instanceof byte[]) {
         try {
            return new String((byte[]) value, "utf-8");
         } catch (UnsupportedEncodingException e) {
            return new String((byte[]) value);
         }
      } else {
         return String.valueOf(value);
      }
   }

   protected String buildCheckSum(Object content) {
      if (content instanceof String) {
         return Checksum.checksum((String) content);
      } else if (content instanceof byte[]) {
         return Checksum.checksum((byte[]) content);
      } else {
         throw new RuntimeException("Unsupported content type!");
      }
   }

   protected String buildUrl(IResource resource, IResourceContext context, String checkSum) {
      String ext = FileUtil.getExtension(resource.getUrn().getPath());

      StringBuilder sb = new StringBuilder(128);
      String contextPath = context.getConfig().getContextPath();
      if (contextPath != null && !contextPath.isEmpty()) {
         sb.append(contextPath);
      }

      sb.append("/lrssvr/").append(checkSum).append(".").append(ext);
      return sb.toString();
   }

   protected CacheKey createCacheKey(IResource resource, IResourceContext context) {
      CacheKey cacheKey = new CacheKey(resource.getUrn(), resource.getLibrary(), context.getLocale());

      if (context.getConfig().isOptimizationEnabled()) {
         String optimizationCmd = context.getOptimizationCommand(resource.getUrn().getType());
         cacheKey.setOptimizationCommand(optimizationCmd);
      }

      return cacheKey;
   }

   @Override
   public byte[] getBinaryContent(IResource resource, IResourceContext context) {
      assertExternal(resource);

      IResourceUrn urn = resource.getUrn();
      IResourceOptimizer optimizer = context.getConfig().getRegistry().getResourceOptimizer(urn.getType());

      if (optimizer != null) {
         Object value = optimizer.optimize(resource.getOriginalBinaryContent(),
               context.getOptimizationCommand(urn.getType()));
         return asByteArray(value);
      } else {
         return resource.getOriginalBinaryContent();
      }
   }

   protected CacheValue getCache(IResource resource, IResourceContext context) {
      CacheKey cacheKey = createCacheKey(resource, context);
      return (CacheValue) context.getConfig().getCacheManager().getCache(cacheKey);
   }

   @Override
   public String getContent(IResource resource, IResourceContext context) {
      assertExternal(resource);

      IResourceUrn urn = resource.getUrn();
      IResourceOptimizer optimizer = context.getConfig().getRegistry().getResourceOptimizer(urn.getType());

      if (optimizer != null) {
         Object value = optimizer
               .optimize(resource.getOriginalContent(), context.getOptimizationCommand(urn.getType()));
         return asString(value);
      } else {
         return resource.getOriginalContent();
      }
   }

   @Override
   public String getUrl(IResource resource, IResourceContext context) {
      assertExternal(resource);

      //look for check sum cache
      CacheValue cacheValue = getCache(resource, context);

      String checkSum;
      //if cache not found, process the content and build the cache
      if (cacheValue == null) {
         //process resource content, for example, JsMin
         byte[] content = getBinaryContent(resource, context);

         //calc content checksum
         checkSum = buildCheckSum(content);

         //put urn, checksum on global cache
         setCache(resource, context, new CacheValue(checkSum, resource.getLastModified()), content);
      } else {
         checkSum = cacheValue.getCheckSum();

         //in dev, check with tough-go
         if (context.getConfig().isDev()) {
            long lastModified = resource.getLastModified();
            if (cacheValue.getLastModified() < lastModified) {
               //process resource content, for example, JsMin
               byte[] content = getBinaryContent(resource, context);

               //calc content checksum
               checkSum = buildCheckSum(content);

               //put urn, checksum on global cache
               setCache(resource, context, new CacheValue(checkSum, lastModified), content);
            }
         }
      }

      return buildUrl(resource, context, checkSum);
   }

   protected byte[] processResourceContent(IResource resource, IResourceContext context) {
      IResourceUrn urn = resource.getUrn();
      IResourceOptimizer optimizer = context.getConfig().getRegistry().getResourceOptimizer(urn.getType());

      //TODO: support aggregation fragment level optimization cache
      if (optimizer != null) {
         Object value = optimizer.optimize(resource, context.getOptimizationCommand(urn.getType()));
         if (value instanceof String) {
            try {
               return ((String) value).getBytes("utf-8");
            } catch (UnsupportedEncodingException e) {
               return ((String) value).getBytes();
            }
         } else {
            return (byte[]) value;
         }
      } else {
         return resource.getOriginalBinaryContent();
      }
   }

   protected void setCache(IResource resource, IResourceContext context, CacheValue cacheValue, byte[] content) {
      CacheKey cacheKey = createCacheKey(resource, context);

      //set urn => check sum cache
      context.getConfig().getCacheManager().setCache(cacheKey, cacheValue);

      //set checksum => optimized content cache
      String mimeType = context.getConfig().getRegistry()
            .getResourceMimeType(FileUtil.getExtension(resource.getUrn().getPath()));
      ResourceCacheManager.setGlobalCache(cacheValue.getCheckSum(),
            new ContentCacheValue(content, mimeType, cacheValue.getLastModified()));
   }

   private static class CacheKey {

      private IResourceUrn m_urn;

      private IResourceLibrary m_library;

      private IResourceLocale m_locale;

      private String m_optimizationCmd;

      public CacheKey(IResourceUrn urn, IResourceLibrary library, IResourceLocale locale) {
         m_urn = urn;
         m_library = library;
         m_locale = locale;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         CacheKey other = (CacheKey) obj;
         if (m_library == null) {
            if (other.m_library != null)
               return false;
         } else if (!m_library.equals(other.m_library))
            return false;
         if (m_locale == null) {
            if (other.m_locale != null)
               return false;
         } else if (!m_locale.equals(other.m_locale))
            return false;
         if (m_optimizationCmd == null) {
            if (other.m_optimizationCmd != null)
               return false;
         } else if (!m_optimizationCmd.equals(other.m_optimizationCmd))
            return false;
         if (m_urn == null) {
            if (other.m_urn != null)
               return false;
         } else if (!m_urn.equals(other.m_urn))
            return false;
         return true;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((m_library == null) ? 0 : m_library.hashCode());
         result = prime * result + ((m_locale == null) ? 0 : m_locale.hashCode());
         result = prime * result + ((m_optimizationCmd == null) ? 0 : m_optimizationCmd.hashCode());
         result = prime * result + ((m_urn == null) ? 0 : m_urn.hashCode());
         return result;
      }

      public void setOptimizationCommand(String optimizationCmd) {
         m_optimizationCmd = optimizationCmd;
      }
   }

   private static class CacheValue {

      private String m_checkSum;

      private long m_lastModified;

      public CacheValue(String checkSum, long lastModified) {
         m_checkSum = checkSum;
         m_lastModified = lastModified;
      }

      public String getCheckSum() {
         return m_checkSum;
      }

      public long getLastModified() {
         return m_lastModified;
      }

   }

   public static class ContentCacheValue {

      private String m_contentType;

      private byte[] m_content;

      private long m_lastModified;

      public ContentCacheValue(byte[] content, String mimeType, long lastModified) {
         m_content = content;
         m_contentType = mimeType;
         m_lastModified = lastModified;
      }

      public byte[] getContent() {
         return m_content;
      }

      public String getContentType() {
         return m_contentType;
      }

      public long getLastModified() {
         return m_lastModified;
      }

      @Override
      public String toString() {
         return "ContentCacheValue [m_contentType=" + m_contentType + ", m_content=" + new String(m_content) + "]";
      }
   }
}
