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

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;

import com.ebayopensource.webrex.resource.api.IAggregatedResource;
import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.api.IResourceContext;
import com.ebayopensource.webrex.resource.api.IResourceLibrary;
import com.ebayopensource.webrex.resource.api.IResourceLocale;
import com.ebayopensource.webrex.resource.api.IResourceUrn;
import com.ebayopensource.webrex.resource.spi.IResourceRegistry;

public class AggregatedResource extends Resource implements IAggregatedResource {
   public static final String OBFUSCATE_INLINE_AGG_RES = "obfuscate_inl_agg_res";
   
   private static final String AGG = "agg";

   private static final String AGGREGATION_ID = "aggId";

   private static IResourceUrn createUrn(List<IResource> resources) {
      String type = null;
      StringBuilder path = new StringBuilder(1024);

      for (IResource resource : resources) {
         if (type == null) {
            type = resource.getUrn().getType();
         }

         if (path.length() != 0) {
            path.append(';');
            path.append(resource.getUrn().getPath());
         } else {
            path.append(resource.getUrn().getPath());
         }
      }

      return new ResourceUrn(type, AGG, path.toString());
   }

   private String m_aggId;

   private List<IResource> m_resources;

   protected AggregatedResource(List<IResource> resources) {
      super(createUrn(resources), null, null, null);
      m_resources = resources;

      ResourceRuntimeContext ctx = ResourceRuntimeContext.ctx();
      IResourceRegistry registry = ctx.getConfig().getRegistry();
      setHandler(registry.getResourceHandler(getUrn().getType()));
   }
   
   protected AggregatedResource(AggregatedResource resource) {
      this(resource.getResources());
      this.m_aggId = resource.getAggregationId();
   }

   public String getAggregationId() {
      return m_aggId;
   }

   @Override
   public long getLastModified() {
      long maxLastModified = -1;
      for (IResource resource : m_resources) {
         long lastModifed = resource.getLastModified();
         if (lastModifed > maxLastModified) {
            maxLastModified = lastModifed;
         }
      }

      return maxLastModified;
   }

   @Override
   public IResourceLibrary getLibrary() {
      return null;
   }

   @Override
   public IResourceLocale getLocale() {
      return null;
   }

   @Override
   public byte[] getOriginalBinaryContent() {
      try {
         return getOriginalContent().getBytes("utf-8");
      } catch (UnsupportedEncodingException e) {
         return getOriginalContent().getBytes();
      }
   }

   @Override
   public String getOriginalContent() {
      StringBuilder sb = new StringBuilder(2048);
      for (IResource resource : m_resources) {
         sb.append(resource.getOriginalContent());
      }

      return sb.toString();
   }

   @Override
   public URL getOriginalUrl() {
      throw new UnsupportedOperationException();
   }

   @Override
   public List<IResource> getResources() {
      return m_resources;
   }

   @Override
   public String getUrl(IResourceContext context) {
      if (m_aggId != null) {
         context.setAttribute(AGGREGATION_ID, m_aggId);
      }
      return super.getUrl(context);
   }

   public void setAggregationId(String aggId) {
      m_aggId = aggId;
   }

}
