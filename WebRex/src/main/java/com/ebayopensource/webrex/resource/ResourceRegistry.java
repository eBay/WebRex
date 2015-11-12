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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.api.IResourceRuntimeConfig;
import com.ebayopensource.webrex.resource.spi.IDeferProcessor;
import com.ebayopensource.webrex.resource.spi.IExpressionEvaluator;
import com.ebayopensource.webrex.resource.spi.IExternalResourceResolver;
import com.ebayopensource.webrex.resource.spi.ILibraryVersionProvider;
import com.ebayopensource.webrex.resource.spi.IResourceErrorHandler;
import com.ebayopensource.webrex.resource.spi.IResourceFactory;
import com.ebayopensource.webrex.resource.spi.IResourceHandler;
import com.ebayopensource.webrex.resource.spi.IResourceLoader;
import com.ebayopensource.webrex.resource.spi.IResourceOptimizer;
import com.ebayopensource.webrex.resource.spi.IResourceRegistry;
import com.ebayopensource.webrex.resource.spi.IResourceResolver;
import com.ebayopensource.webrex.resource.spi.IResourceTagRenderer;
import com.ebayopensource.webrex.resource.spi.IResourceTokenStorage;
import com.ebayopensource.webrex.resource.spi.ITemplateProcessor;

public class ResourceRegistry implements IResourceRegistry {

   // Resource type => Namespace => Resolver
   private Map<String, Map<String, IResourceResolver>> m_resolvers = new HashMap<String, Map<String, IResourceResolver>>();

   // Resource type => File Extension => Resource Factory 
   private Map<String, Map<String, IResourceFactory<? extends IResource>>> m_factories = new HashMap<String, Map<String, IResourceFactory<? extends IResource>>>();

   // Resource type => resource handler
   private Map<String, IResourceHandler> m_resourceHandlers = new HashMap<String, IResourceHandler>();

   // ext name => template processor
   private Map<String, ITemplateProcessor> m_templateProcessors = new HashMap<String, ITemplateProcessor>();

   private IResourceHandler m_defaultResourceHandler;

   // Namespace => Default Resolver
   private Map<String, IResourceResolver> m_defaultResolvers = new HashMap<String, IResourceResolver>(3);

   private IResourceFactory<IResource> m_defaultResourceFactory;

   // Resource type => resource render
   private Map<String, IResourceTagRenderer> m_tagRenders = new HashMap<String, IResourceTagRenderer>();

   private Map<String, IExpressionEvaluator> m_expressionEvaluators = new HashMap<String, IExpressionEvaluator>();

   private IDeferProcessor m_deferProcessor;

   private IResourceRuntimeConfig m_config;

   // Resource mime types
   // Extension => Resource type
   private Map<String, String> m_extResType = new HashMap<String, String>();

   // Resource Type => List of Extensions
   private Map<String, List<String>> m_resTypeExt = new HashMap<String, List<String>>();

   // Extension => MimeType
   private Map<String, String> m_extMimeType = new HashMap<String, String>();

   // Namespace => Resource Loader
   private Map<String, IResourceLoader> m_namespaceLoaders = new HashMap<String, IResourceLoader>();

   private ILibraryVersionProvider m_libraryVersionProvider;

   private IResourceErrorHandler m_errHandler;

   private IResourceTokenStorage m_tokenStorage;

   // resource type => external resource resolver
   private Map<String, IExternalResourceResolver> m_externalResolvers = new HashMap<String, IExternalResourceResolver>();

   public ResourceRegistry(IResourceRuntimeConfig config) {
      m_config = config;
   }

   @Override
   public IResourceRuntimeConfig getConfig() {
      return m_config;
   }

   @Override
   public IResourceFactory<IResource> getDefaultResourceFactory() {
      return m_defaultResourceFactory;
   }

   @Override
   public IResourceHandler getDefaultResourceHandler() {
      return m_defaultResourceHandler;
   }

   @Override
   public IResourceResolver getDefaultResourceResolver(String namespace) {
      return m_defaultResolvers.get(namespace);
   }

   @Override
   public IDeferProcessor getDeferProcessor() {
      return m_deferProcessor;
   }

   @Override
   public IResourceErrorHandler getErrorHandler() {
      return m_errHandler;
   }

   @Override
   public IExpressionEvaluator getExpressionEvaluator(String elType) {
      return m_expressionEvaluators.get(elType);
   }

   @Override
   public ILibraryVersionProvider getLibraryVersionProvider() {
      return m_libraryVersionProvider;
   }

   @Override
   public IResourceResolver getResolver(String type, String namespace) {
      Map<String, IResourceResolver> map = m_resolvers.get(type);

      if (map != null) {
         IResourceResolver resolver = map.get(namespace);
         if (resolver != null) {
            return resolver;
         }
      }

      IResourceResolver defaultResolver = m_defaultResolvers.get(namespace);
      if (defaultResolver != null) {
         return defaultResolver;
      }

      throw new ResourceException(ResourceErrConstants.NO_RESOLVER, String.format(
            "No resolver registered for resource(%s) in namespace(%s)!", type, namespace));
   }

   @Override
   public List<String> getResourceExtensions(String resType) {
      return m_resTypeExt.get(resType);
   }

   @Override
   public IResourceFactory<? extends IResource> getResourceFactory(String type, String extension) {
      Map<String, IResourceFactory<? extends IResource>> map = m_factories.get(type);

      IResourceFactory<? extends IResource> factory = null;
      if (map != null) {
         factory = map.get(extension);
      }

      if (factory == null) {
         factory = m_defaultResourceFactory;
      }

      if (factory == null) {
         throw new ResourceException(ResourceErrConstants.NO_FACTORY, String.format(
               "No factory registered for type(%s) with extension(%s)!", type, extension));
      }

      return factory;
   }

   @Override
   public IResourceHandler getResourceHandler(String type) {
      IResourceHandler handler = m_resourceHandlers.get(type);

      if (handler == null) {
         return m_defaultResourceHandler;
      } else {
         return handler;
      }
   }

   @Override
   public IResourceLoader getResourceLoader(String namespace) {
      return m_namespaceLoaders.get(namespace);
   }

   @Override
   public String getResourceMimeType(String ext) {
      return m_extMimeType.get(ext);
   }

   @Override
   public IResourceOptimizer getResourceOptimizer(String type) {
      return null;
   }

   @Override
   public String getResourceType(String extension) {
      return m_extResType.get(extension);
   }

   @Override
   public Set<String> getResourceTypes() {
      return m_resTypeExt.keySet();
   }

   @Override
   public IResourceTagRenderer getTagRenderer(String resourceType) {
      IResourceTagRenderer renderer = m_tagRenders.get(resourceType);

      if (renderer == null) {
         throw new RuntimeException(String.format("No tag renderer registered for type(%s)!", resourceType));
      }

      return renderer;
   }

   @Override
   public ITemplateProcessor getTemplateProcessor(String extName) {
      return m_templateProcessors.get(extName);
   }

   @Override
   public IResourceTokenStorage getTokenStorage() {
      return m_tokenStorage;
   }

   @Override
   public void registerExpressionEvaluator(String elType, IExpressionEvaluator evaluator) {
      m_expressionEvaluators.put(elType, evaluator);
   }

   @Override
   public void registerMimeTypes(String resourceType, String mimeType, List<String> exts) {
      for (String ext : exts) {
         m_extMimeType.put(ext, mimeType);
         m_extResType.put(ext, resourceType);
      }

      List<String> exentions = m_resTypeExt.get(resourceType);
      if (exentions == null) {
         exentions = new ArrayList<String>(exts);
         m_resTypeExt.put(resourceType, exentions);
      } else {
         for (String ext : exts) {
            if (!exentions.contains(ext)) {
               exentions.add(ext);
            }
         }
      }

   }

   @Override
   public void registerResolver(String type, String namespace, IResourceResolver resolver) {
      Map<String, IResourceResolver> map = m_resolvers.get(type);

      if (map == null) {
         map = new HashMap<String, IResourceResolver>(4);
         m_resolvers.put(type, map);
      }

      map.put(namespace, resolver);
   }

   @Override
   public void registerResourceFactory(String type, String extension, IResourceFactory<? extends IResource> factory) {
      Map<String, IResourceFactory<? extends IResource>> map = m_factories.get(type);
      if (map == null) {
         map = new HashMap<String, IResourceFactory<? extends IResource>>();
         m_factories.put(type, map);
      }

      map.put(extension, factory);
   }

   @Override
   public void registerResourceHandler(String type, IResourceHandler handler) {
      m_resourceHandlers.put(type, handler);
   }

   @Override
   public void registerResourceLoader(String namespace, IResourceLoader loader) {
      m_namespaceLoaders.put(namespace, loader);
   }

   @Override
   public void registerResourceOptimizer(String type, IResourceOptimizer optimizer) {
   }

   @Override
   public synchronized void registerTagRenderer(String resourceType, IResourceTagRenderer renderer) {
      m_tagRenders.put(resourceType, renderer);
   }

   @Override
   public void registerTemplateProcessor(String extName, ITemplateProcessor processor) {
      m_templateProcessors.put(extName, processor);
   }

   @Override
   public void setDefaultResourceFactory(IResourceFactory<IResource> defaultFactory) {
      m_defaultResourceFactory = defaultFactory;
   }

   @Override
   public void setDefaultResourceHandler(IResourceHandler defaultResolverHandler) {
      m_defaultResourceHandler = defaultResolverHandler;
   }

   @Override
   public void setDefaultResourceResolver(String namespace, IResourceResolver resolver) {
      m_defaultResolvers.put(namespace, resolver);
   }

   @Override
   public void setDeferProcessor(IDeferProcessor deferProcessor) {
      m_deferProcessor = deferProcessor;
   }

   @Override
   public void setErrorHandler(IResourceErrorHandler handler) {
      m_errHandler = handler;
   }

   @Override
   public void setLibraryVersionProvider(ILibraryVersionProvider provider) {
      m_libraryVersionProvider = provider;
   }

   @Override
   public void setTokenStorage(IResourceTokenStorage tokenStorage) {
      m_tokenStorage = tokenStorage;
   }

   @Override
   public IExternalResourceResolver getExternalResourceResolver(String type) {
      return m_externalResolvers.get(type);
   }

   @Override
   public void registerExternalResourceResolver(String type, IExternalResourceResolver resolver) {
      m_externalResolvers.put(type, resolver);
   }

@Override
public boolean ignoreWhitespaces() {
	return !"false".equals(this.m_config.getProperties().get("ignore_whitespaces"));
}

}
