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

package com.ebayopensource.webrex.resource.spi;

import java.util.List;
import java.util.Set;

import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.api.IResourceRuntimeConfig;

public interface IResourceRegistry {

   IResourceRuntimeConfig getConfig();

   IResourceFactory<IResource> getDefaultResourceFactory();

   IResourceHandler getDefaultResourceHandler();

   IResourceResolver getDefaultResourceResolver(String namespace);

   IDeferProcessor getDeferProcessor();

   public IResourceErrorHandler getErrorHandler();

   IExpressionEvaluator getExpressionEvaluator(String elType);

   public ILibraryVersionProvider getLibraryVersionProvider();

   IResourceResolver getResolver(String type, String namespace);

   List<String> getResourceExtensions(String resType);

   IResourceFactory<? extends IResource> getResourceFactory(String type, String extension);

   IResourceHandler getResourceHandler(String type);

   IResourceLoader getResourceLoader(String namespace);

   String getResourceMimeType(String ext);

   IResourceOptimizer getResourceOptimizer(String type);

   String getResourceType(String extension);

   Set<String> getResourceTypes();

   public IResourceTagRenderer getTagRenderer(String type);

   ITemplateProcessor getTemplateProcessor(String extName);

   public IResourceTokenStorage getTokenStorage();

   void registerExpressionEvaluator(String elType, IExpressionEvaluator evaluator);

   void registerMimeTypes(String resourceType, String mimeType, List<String> exts);

   void registerResolver(String type, String namespace, IResourceResolver resolver);

   void registerResourceFactory(String type, String extension, IResourceFactory<? extends IResource> factory);

   void registerResourceHandler(String type, IResourceHandler handler);

   public void registerResourceLoader(String namespace, IResourceLoader loader);

   void registerResourceOptimizer(String type, IResourceOptimizer optimizer);

   void registerTagRenderer(String type, IResourceTagRenderer tagRenderer);

   public void registerTemplateProcessor(String extName, ITemplateProcessor processor);

   void setDefaultResourceFactory(IResourceFactory<IResource> defaultFactory);

   void setDefaultResourceHandler(IResourceHandler defaultResolverHandler);

   void setDefaultResourceResolver(String namespace, IResourceResolver resolver);

   public void setDeferProcessor(IDeferProcessor deferProcessor);

   public void setErrorHandler(IResourceErrorHandler handler);

   public void setLibraryVersionProvider(ILibraryVersionProvider provider);

   public void setTokenStorage(IResourceTokenStorage tokenStorage);

   IExternalResourceResolver getExternalResourceResolver(String type);
   
   public void registerExternalResourceResolver(String type, IExternalResourceResolver resolver);

   /**
    * Ignore white spaces in resource defer processor
    * @return
    */
   public boolean ignoreWhitespaces();
}
