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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ebayopensource.webrex.resource.ResourceLibrary;
import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.api.IResourceLibrary;
import com.ebayopensource.webrex.resource.api.IResourceLibrary.LibraryType;
import com.ebayopensource.webrex.resource.api.IResourceRuntimeConfig;
import com.ebayopensource.webrex.resource.spi.IResourceLoader;
import com.ebayopensource.webrex.util.FileUtil;
import com.ebayopensource.webrex.util.FileUtil.IFinderFilter;

public class LocalResourceLoader extends BaseResourceLoader implements IResourceLoader {
   public LocalResourceLoader(IResourceRuntimeConfig config) {
      super(config);
   }

   protected void createResourceWithType(Map<String, List<IResource>> map, IResourceLibrary libraryInfo,
         String resourceType) {
      String base = getConfig().getResourceBase(resourceType);
      if (base != null) {
         List<String> exts = getConfig().getRegistry().getResourceExtensions(resourceType);
         Map<String, URL> files = listFiles(base, exts.toArray(new String[exts.size()]));
         createResources(files, libraryInfo, map);
      }
   }

   @Override
   public Map<String, List<IResource>> loadResources() {
      Map<String, List<IResource>> map = new HashMap<String, List<IResource>>();
      IResourceRuntimeConfig config = getConfig();
      IResourceLibrary libraryInfo = new ResourceLibrary(config.getAppId(), config.getAppVersion(), LibraryType.WAR);

      //handle different types of resource
      Set<String> m_resourceTypes = config.getRegistry().getResourceTypes();
      for (String type : m_resourceTypes) {
         createResourceWithType(map, libraryInfo, type);
      }

      //handle resources under /WEB-INF/tags
      String componentBase = "/WEB-INF/tags";
      List<String> allExts = new ArrayList<String>();
      for (String type : m_resourceTypes) {
         List<String> exts = config.getRegistry().getResourceExtensions(type);
         allExts.addAll(exts);
      }
      Map<String, URL> files = listFiles(componentBase, allExts.toArray(new String[allExts.size()]));
      createResources(files, libraryInfo, map);

      return map;
   }

   protected Map<String, URL> listFiles(String base, final String... suffixes) {
      File file = new File(getConfig().getWarRoot(), base);

      IFinderFilter filter = new LocalFinderFilter(suffixes);
      return FileUtil.listFiles(file, filter);
   }

   private static final class LocalFinderFilter implements IFinderFilter {
      private final String[] m_suffixes;

      private LocalFinderFilter(String[] suffixes) {
         m_suffixes = suffixes;
      }

      @Override
      public boolean doFilter(String relativePath) {

         for (String suffix : m_suffixes) {
            if (relativePath.toLowerCase().endsWith("." + suffix.toLowerCase())) {
               return true;
            }
         }
         return false;
      }
   }
}
