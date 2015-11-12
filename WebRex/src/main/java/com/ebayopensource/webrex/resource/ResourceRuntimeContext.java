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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ebayopensource.webrex.resource.api.IDeferRenderable;
import com.ebayopensource.webrex.resource.api.IResourceContext;
import com.ebayopensource.webrex.resource.api.IResourceLocale;
import com.ebayopensource.webrex.resource.api.IResourceRuntimeConfig;

public class ResourceRuntimeContext {
   private static ThreadLocal<ResourceRuntimeContext> s_ctx = new ThreadLocal<ResourceRuntimeContext>() {
      @Override
      protected ResourceRuntimeContext initialValue() {
         return new ResourceRuntimeContext();
      }
   };

   public static ResourceRuntimeContext ctx() {
      ResourceRuntimeContext ctx = s_ctx.get();
      if (!ctx.m_initialized) {
         throw new IllegalStateException("ResourceRuntimeContext must be setup by ResourceRuntimeContext.setup!");
      }

      return ctx;
   }

   public static boolean isInitialized() {
      return s_ctx.get().m_initialized;
   }

   public static void reset() {
      ResourceRuntimeContext ctx = s_ctx.get();
      if (ctx != null) {
         ctx.m_initialized = false;
         ctx.m_config = null;
         ctx.m_resourceContext = null;
         ctx.m_resourceAggregator = null;
         ctx.m_model = null;
         ctx.m_attributes.clear();
         ctx.m_statistics = null;
         ctx.m_showDiag = false;
         ctx.m_showDiagModel = null;
         ctx.m_tokens.clear();
      }
   }

   /**
    * Resource context setup without permutation
    * 
    * @param contextPath
    *            context path of web project, context path should start with
    *            "/"
    */
   @Deprecated
   public static void setup(String contextPath) {
      setup();
   }

   /**
    * Resource context setup without permutation
    * 
    */
   public static void setup() {
      ResourceRuntimeContext.reset();
      ResourceRuntimeContext ctx = s_ctx.get();

      IResourceRuntimeConfig config = ResourceRuntime.INSTANCE.getConfig();
      ctx.m_config = config;
      ResourceContext resourceContext = new ResourceContext(config, ctx.m_attributes);
      ctx.m_resourceContext = resourceContext;
      ctx.m_initialized = true;
      ctx.m_showDiag = false;
      ctx.m_showDiagModel = new DiagnosisModel();
      ctx.m_statistics = new Statistics();
      ResourceModel model = new ResourceModel();
      ctx.m_model = model;
      ctx.m_resourceAggregator = new ResourceAggregator(model);

   }

   private ResourceModel m_model;

   private IResourceRuntimeConfig m_config;

   private boolean m_initialized;

   private ResourceContext m_resourceContext;

   private ResourceAggregator m_resourceAggregator;

   //Attributes to hold defer renderables and other attributes
   private Map<String, Object> m_attributes = new HashMap<String, Object>();

   private Statistics m_statistics;

   private boolean m_showDiag = false;

   private DiagnosisModel m_showDiagModel;

   private Map<String, String> m_tokens = new HashMap<String, String>();

   private ResourceRuntimeContext() {

   }

   public IResourceContext createResourceContext() {
      return (IResourceContext) m_resourceContext.clone();
   }

   public Object getAttribute(String name) {
      return m_attributes.get(name);
   }

   public IResourceRuntimeConfig getConfig() {
      return m_config;
   }

   public IDeferRenderable getDeferRenderable(String key) {
      return (IDeferRenderable) m_attributes.get(key);
   }

   public ResourceModel getModel() {
      return m_model;
   }

   public ResourceAggregator getResourceAggregator() {
      return m_resourceAggregator;
   }

   public IResourceContext getResourceContext() {
      return m_resourceContext;
   }

   public DiagnosisModel getShowDiagModel() {
      return m_showDiagModel;
   }

   public Statistics getStatistics() {
      return m_statistics;
   }

   public boolean isShowDiag() {
      return m_showDiag;
   }

   public boolean isStatisticsEnabled() {
      return m_config.isStatisticsEnabled();
   }

   public void registerDeferRenderable(String key, IDeferRenderable renderable) {
      m_attributes.put(key, renderable);
   }

   public void setAttribute(String name, Object value) {
      m_attributes.put(name, value);
   }

   public String getDeDupToken(String resType) {
      String token = m_tokens.get(resType);
      if (token == null) {
         Set<String> urns = m_model.getCollectedResource(resType);
         if (urns != null && !urns.isEmpty()) {
            for (Iterator<String> i = urns.iterator(); i.hasNext();) {
               String urn = i.next();
               if (urn.indexOf("inline:/") >= 0) {
                  i.remove();
               }
            }
            if(!urns.isEmpty()){
               List<String> urnsList = new ArrayList<String>(urns);
               Collections.sort(urnsList);
               token = m_config.getRegistry().getTokenStorage().storeResourceUrns(urnsList);
               m_tokens.put(resType, token);
            }
         }
      }

      return token;
   }

   public void setDeDupToken(String deDupToken) {
      List<String> urns = m_config.getRegistry().getTokenStorage().loadResourceUrns(deDupToken);

      if (urns != null) {
         for (String urn : urns) {
            if (urn.indexOf("inline:/") < 0) {
               m_model.setRenderedResource(urn);
            }
         }
      }
   }

   public void setLocale(IResourceLocale locale) {
      m_resourceContext.setLocale(locale);
   }

   public void setShowDiag(boolean showDiag) {
      m_showDiag = showDiag;
   }

   //backward compatible method
   public void supportDeferRendering(String uri) {
      //do nothing, the defer render is always supported.
   }

   static class ResourceContext implements IResourceContext, Cloneable {
      private IResourceRuntimeConfig m_config;

      private String m_originalRequestUri;

      private boolean m_secure;

      private Map<String, Object> m_attributes;

      private IResourceLocale m_locale;

      public ResourceContext(IResourceRuntimeConfig config, Map<String, Object> attributes) {
         m_config = config;
         m_attributes = attributes;
      }

      @Override
      protected Object clone() {
         ResourceContext context = new ResourceContext(m_config, m_attributes);
         context.m_originalRequestUri = this.m_originalRequestUri;
         context.m_secure = this.m_secure;
         context.m_locale = this.m_locale;
         return context;
      }

      public Object getAttribute(String name) {
         return m_attributes.get(name);
      }

      @Override
      public IResourceRuntimeConfig getConfig() {
         return m_config;
      }

      @Override
      public IResourceLocale getLocale() {
         return m_locale;
      }

      @Override
      public String getOptimizationCommand(String type) {
         //TODO: support request level override
         return m_config.getOptimizationCommand(type);
      }

      @Override
      public String getOriginalRequestUri() {
         return m_originalRequestUri;
      }

      @Override
      public boolean isOptimizationEnabled() {
         return false;
      }

      @Override
      public boolean isSecure() {
         return m_secure;
      }

      @Override
      public void setAttribute(String name, Object value) {
         m_attributes.put(name, value);
      }

      @Override
      public void setLocale(IResourceLocale locale) {
         m_locale = locale;
      }

      @Override
      public void setOriginalRequestUri(String uri) {
         m_originalRequestUri = uri;
      }

      @Override
      public void setSecure(boolean secure) {
         m_secure = secure;
      }

   }

}
