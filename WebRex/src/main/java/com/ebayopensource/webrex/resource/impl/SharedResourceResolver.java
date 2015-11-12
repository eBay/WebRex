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

import java.net.URL;
import java.util.List;

import com.ebayopensource.webrex.resource.ResourceFactory;
import com.ebayopensource.webrex.resource.ResourceLibrary;
import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.api.IResourceContext;
import com.ebayopensource.webrex.resource.api.IResourceLibrary;
import com.ebayopensource.webrex.resource.api.IResourceLibrary.LibraryType;
import com.ebayopensource.webrex.resource.api.IResourceLocale;
import com.ebayopensource.webrex.resource.api.IResourceRuntimeConfig;
import com.ebayopensource.webrex.resource.api.IResourceUrn;
import com.ebayopensource.webrex.resource.spi.ILibraryVersionProvider.VersionID;
import com.ebayopensource.webrex.resource.spi.IResourceResolver;

public class SharedResourceResolver implements IResourceResolver {

   private static final String META_INF_RESOURCES = "META-INF/resources";

   private static final String META_INF_TAGS = "META-INF/tags";

   protected IResource createResource(IResourceUrn urn, URL url, IResourceLocale locale, IResourceRuntimeConfig config) {
      IResourceLibrary libraryInfo = null;
      VersionID versionId = config.getRegistry().getLibraryVersionProvider().resolveLibrary(url);

      if (versionId != null) {
         libraryInfo = new ResourceLibrary(versionId.getId(), versionId.getVersion(), LibraryType.JAR);
      } else {
         libraryInfo = new ResourceLibrary(null, null, LibraryType.JAR);
      }

      return ResourceFactory.createResource(urn, url, locale, libraryInfo);
   }

   @Override
   public boolean isCachable() {
      return true;
   }

   @Override
   public IResource resolve(IResourceUrn urn, IResourceContext ctx) {
      IResource resource = resolveResourceFromResourceBase(urn, ctx, META_INF_RESOURCES);

      if (resource == null) {
         resource = resolveResourceFromResourceBase(urn, ctx, META_INF_TAGS);
      }

      if (resource == null) {
         throw new RuntimeException(String.format("Can't reslove resource(%s).", urn));
      }

      return resource;

   }

   private IResource resolveResourceFromResourceBase(IResourceUrn urn, IResourceContext ctx, String resourceBase) {
      IResourceLocale locale = ctx.getLocale();
      List<IResourceLocale> localeFallback = null;
      if (locale != null) {
         localeFallback = locale.getLocaleFallback();

         for (IResourceLocale resourceLocale : localeFallback) {
            String path = urn.getPath();
            if (resourceLocale != null) {
               //resource with locale
               path = resourceBase + "/" + resourceLocale.toExternal() + path;
            } else {
               path = resourceBase + path;
            }

            URL url = ctx.getConfig().getAppClassLoader().getResource(path);
            if (url != null) {
               return createResource(urn, url, resourceLocale, ctx.getConfig());
            }

         }
      } else {
         String path = resourceBase + urn.getPath();
         URL url = ctx.getConfig().getAppClassLoader().getResource(path);
         if (url != null) {
            return createResource(urn, url, null, ctx.getConfig());
         }
      }

      return null;
   }

}
