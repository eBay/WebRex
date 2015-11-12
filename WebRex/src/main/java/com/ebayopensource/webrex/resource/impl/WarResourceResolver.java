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

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

import com.ebayopensource.webrex.resource.ResourceException;
import com.ebayopensource.webrex.resource.ResourceFactory;
import com.ebayopensource.webrex.resource.ResourceLibrary;
import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.api.IResourceContext;
import com.ebayopensource.webrex.resource.api.IResourceLibrary;
import com.ebayopensource.webrex.resource.api.IResourceLibrary.LibraryType;
import com.ebayopensource.webrex.resource.api.IResourceLocale;
import com.ebayopensource.webrex.resource.api.IResourceRuntimeConfig;
import com.ebayopensource.webrex.resource.api.IResourceUrn;
import com.ebayopensource.webrex.resource.spi.IResourceResolver;

public class WarResourceResolver implements IResourceResolver {
   private static final String WEB_INF_TAGS = "WEB-INF/tags";

   protected IResource createResource(IResourceUrn urn, File resourceFile, IResourceLocale locale,
         IResourceRuntimeConfig config) {
      IResourceLibrary libraryInfo = new ResourceLibrary(config.getAppId(), config.getAppVersion(), LibraryType.WAR);

      try {
         return ResourceFactory.createResource(urn, resourceFile.toURI().toURL(), locale, libraryInfo);
      } catch (MalformedURLException e) {
         throw new RuntimeException("Error when resolving resource " + urn.toString(), e);
      }
   }

   @Override
   public boolean isCachable() {
      return true;
   }

   @Override
   public IResource resolve(IResourceUrn urn, IResourceContext ctx) throws ResourceException {
      IResourceRuntimeConfig config = ctx.getConfig();
      String resourceBase = config.getResourceBase(urn.getType());

      IResource resource = resolveResolveFromResourceBase(urn, ctx, config, resourceBase);
      if (resource == null) {
         resource = resolveResolveFromResourceBase(urn, ctx, config, WEB_INF_TAGS);
      }

      if (resource == null) {
         throw new RuntimeException(String.format("Can't reslove resource(%s).", urn));
      }

      return resource;
   }

   private IResource resolveResolveFromResourceBase(IResourceUrn urn, IResourceContext ctx,
         IResourceRuntimeConfig config, String resourceBase) {
      IResourceLocale locale = ctx.getLocale();
      List<IResourceLocale> localeFallback = null;
      if (locale != null) {
         localeFallback = locale.getLocaleFallback();

         for (IResourceLocale resourceLocale : localeFallback) {
            if (resourceLocale != null) {
               //resource with locale
               File resourceFile = new File(config.getWarRoot(), resourceBase + '/' + resourceLocale.toExternal()
                     + urn.getPath());
               if (resourceFile.exists()) {
                  return createResource(urn, resourceFile, resourceLocale, config);
               }
            } else {

               //resource without locale
               File resourceFile = new File(config.getWarRoot(), resourceBase + urn.getPath());
               if (resourceFile.exists() && resourceFile.isFile()) {
                  return createResource(urn, resourceFile, null, config);
               }
            }
         }
      } else {
         File resourceFile = new File(config.getWarRoot(), resourceBase + urn.getPath());
         if (resourceFile.exists() && resourceFile.isFile()) {
            return createResource(urn, resourceFile, null, config);
         }
      }
      return null;
   }

}
