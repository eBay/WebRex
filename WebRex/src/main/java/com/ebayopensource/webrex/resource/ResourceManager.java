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

import com.ebayopensource.webrex.resource.api.IAggregatedResource;
import com.ebayopensource.webrex.resource.api.IExternalResource;
import com.ebayopensource.webrex.resource.api.IInlineResource;
import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.api.IResourceLibrary;
import com.ebayopensource.webrex.resource.api.IResourceLibrary.LibraryType;
import com.ebayopensource.webrex.resource.api.IResourceLocale;
import com.ebayopensource.webrex.resource.api.IResourceUrn;
import com.ebayopensource.webrex.resource.cache.ResourceCacheManager;
import com.ebayopensource.webrex.resource.spi.IExternalResourceResolver;
import com.ebayopensource.webrex.resource.spi.IResourceFactory;
import com.ebayopensource.webrex.resource.spi.IResourceRegistry;
import com.ebayopensource.webrex.resource.spi.IResourceResolver;
import com.ebayopensource.webrex.util.FileUtil;

enum ResourceManager {
   INSTANCE;

   public static final String SHARED = "shared";

   public static final String LOCAL = "local";

   public IResource create(IResourceUrn urn, URL url, IResourceLocale locale, IResourceLibrary library) {
      IResourceRegistry registry = ResourceRuntimeContext.ctx().getConfig().getRegistry();
      String extension = FileUtil.getExtension(urn.getPath());
      IResourceFactory<? extends IResource> factory = registry.getResourceFactory(urn.getType(), extension);

      IResource resource = factory.createResource(urn, url, locale, library);

      //set the resource handler
      if (resource instanceof Resource) {
         ((Resource) resource).setHandler(registry.getResourceHandler(urn.getType()));
      }

      return resource;
   }

   public IResource create(URL url, String resourcePathWithLocale, IResourceLibrary libraryInfo) {
      //check with locale prefix
      String resourcePath;
      String localePath = getLocaleFromPath(resourcePathWithLocale);
      if (localePath != null) {
         resourcePath = resourcePathWithLocale.substring(localePath.length() + 1);
      } else {
         resourcePath = resourcePathWithLocale;
      }

      IResourceLocale locale = null;
      if (localePath != null) {
         locale = ResourceLocaleFactory.getInstance().createLocale(localePath);
      }

      IResourceRegistry registry = ResourceRuntimeContext.ctx().getConfig().getRegistry();
      String extension = FileUtil.getExtension(resourcePath);
      String type = registry.getResourceType(extension.toLowerCase());

      ResourceUrn urn = new ResourceUrn(type, libraryInfo.getType() == LibraryType.JAR ? SHARED : LOCAL, resourcePath);
      return create(urn, url, locale, libraryInfo);
   }

   private String getLocaleFromPath(String path) {
      if (path != null && !path.isEmpty()) {
         int pos = path.indexOf('/', 1);
         if (pos != -1) {
            String firstPart = path.substring(1, pos);
            if (firstPart.indexOf('_') != -1) {
               return firstPart;
            }
         }
      }

      return null;
   }

   public IResource resolve(IResourceUrn urn) {
      return resolveInternal(urn, true, true);
   }

   public IResource resolve(String resourcePath) {
      ResourceRuntimeContext ctx = ResourceRuntimeContext.ctx();
      ResourceCacheManager cacheManager = ctx.getConfig().getCacheManager();
      IResourceLocale locale = ctx.getResourceContext().getLocale();
      boolean isSecure = ctx.getResourceContext().isSecure();

      //lookup from local & shared cache first
      //Fix: local, shared always support cache
      String type = ctx.getConfig().getRegistry().getResourceType(FileUtil.getExtension(resourcePath));
      IResourceUrn localUrn = new ResourceUrn(type, LOCAL, resourcePath);
      

      IResource resource = null;
      resource = (IResource) cacheManager.getResourceCache(localUrn, locale, isSecure);
      if (resource != null) {
         return resource;
      }

      IResourceUrn sharedUrn = new ResourceUrn(type, SHARED, resourcePath);
      resource = (IResource) cacheManager.getResourceCache(sharedUrn, locale, isSecure);
      if (resource != null) {
         return resource;
      }

      // Firstly, try resolve resource from local war, if fails ignore the
      // Exception and try from shared jars, if fails again, log "can't resolve"
      // error. All exceptions from resolveInternal will be ignored.
      try {
         resource = resolveInternal(localUrn, false, false);
      } catch (RuntimeException e) {
      }
      if (resource == null) {
         try {
            resource = resolveInternal(sharedUrn, false, false);
         } catch (RuntimeException e) {
         }
      }

      return resource;
   }

   public IAggregatedResource resolveAggregatedResource(List<IResource> resources) {
      return new AggregatedResource(resources);
   }

   public IInlineResource resolveInline(String type, String content) {
      return new InlineResource(content, type);
   }

   private IResource resolveInternal(IResourceUrn urn, boolean lookupFromCache, boolean handleAllException) {
      ResourceRuntimeContext ctx = ResourceRuntimeContext.ctx();
      IResourceRegistry registry = ctx.getConfig().getRegistry();
      try {
         IResourceLocale locale = ctx.getResourceContext().getLocale();
         boolean isSecure = ctx.getResourceContext().isSecure();
         ResourceCacheManager cacheManager = ctx.getConfig().getCacheManager();
         IResourceResolver resolver = registry.getResolver(urn.getType(), urn.getNamespace());

         //lookup from cache first
         IResource resource = null;
         boolean cachable = lookupFromCache && resolver.isCachable();
         if (cachable) {
            resource = (IResource) cacheManager.getResourceCache(urn, locale, isSecure);
         }

         if (resource == null) {
            resource = resolver.resolve(urn, ctx.createResourceContext());

            //set the resource handler
            if (resource instanceof Resource) {
               ((Resource) resource).setHandler(registry.getResourceHandler(urn.getType()));
            }
         }
         else { //Return the resource
        	 return resource;
         }

         //FIX: in case the resolve doesn't throw exception
         if (resource == null) {
            throw new RuntimeException(String.format("Can't reslove resource(%s).", urn));
         } else if (cachable) {
            //save resource in cache if the resource is cacheable
            cacheManager.putResourceCache(locale, resource, isSecure);
         }

         return resource;
      } catch (RuntimeException e) {
         if (handleAllException) {           
            return null;
         } else {
            throw e;
         }
      }
   }

   public IExternalResource resolveExternal(String type, String url) {
      IResourceRegistry registry = ResourceRuntimeContext.ctx().getConfig().getRegistry();
      IExternalResourceResolver resolver = registry.getExternalResourceResolver(type);
      if(resolver != null){
         return resolver.resolve(url, type);
      }
      return null;
   }

}
