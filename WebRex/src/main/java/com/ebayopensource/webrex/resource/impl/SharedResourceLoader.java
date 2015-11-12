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
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ebayopensource.webrex.logging.ILogger;
import com.ebayopensource.webrex.logging.LoggerFactory;
import com.ebayopensource.webrex.resource.ResourceLibrary;
import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.api.IResourceLibrary;
import com.ebayopensource.webrex.resource.api.IResourceLibrary.LibraryType;
import com.ebayopensource.webrex.resource.api.IResourceRuntimeConfig;
import com.ebayopensource.webrex.resource.spi.ILibraryVersionProvider.VersionID;
import com.ebayopensource.webrex.resource.spi.IResourceLoader;
import com.ebayopensource.webrex.util.FileUtil;
import com.ebayopensource.webrex.util.FileUtil.IFinderFilter;

public class SharedResourceLoader extends BaseResourceLoader implements IResourceLoader {
   private static ILogger s_logger = LoggerFactory.getLogger(SharedResourceLoader.class);

   public SharedResourceLoader(IResourceRuntimeConfig config) {
      super(config);
   }

   private IResourceLibrary createLibraryInfo(URL url) {
      VersionID versionId = getConfig().getRegistry().getLibraryVersionProvider().resolveLibrary(url);
      if (versionId != null) {
         return new ResourceLibrary(versionId.getId(), versionId.getVersion(), LibraryType.JAR);
      } else {
         return new ResourceLibrary(null, null, LibraryType.JAR);
      }
   }

   protected String getLibEntrancePath(String urlStr, String[] endStrs) {
      int startPos = 0;
      if (urlStr.startsWith("file:")) {
         startPos = 5;
      } else if (urlStr.startsWith("jar:file:")) {
         startPos = 9;
      }
      
      int endPos = -1;
      for(int i = 0; i < endStrs.length && endPos < 0; i++) {
         endPos = urlStr.indexOf('/' + endStrs[i]);
      }
      
      if (endPos != -1) {
         // support for jar file
         if (endPos > 0 && urlStr.charAt(endPos - 1) == '!') {
            endPos = endPos - 1;
         }
      } else {
         endPos = urlStr.length();
      }

      return urlStr.substring(startPos, endPos);
   }

   @SuppressWarnings("unchecked")
   private Map<String, URL> listSharedResourceFiles(URL url, String[] suffixes, String basePath) throws IOException {
      //Skip WEB-INF/classes
      int indexOf = url.toString().indexOf("WEB-INF/classes");
      if(indexOf > 0){
         return Collections.EMPTY_MAP;
      }
      IFinderFilter filter = new SharedLibFinderFilter(suffixes);
      String urlStr = url.toExternalForm();
      String libEntrancePath = getLibEntrancePath(urlStr,  new String[] {"META-INF", "v4contentsource/source"});
      if(url.getProtocol().equals("jar")) {
         return FileUtil.listJarFiles(new File(libEntrancePath), basePath, filter);
      } else {
         return FileUtil.listFiles(new File(libEntrancePath, basePath), filter);
      }
      
   }

   protected List<SharedResource> listSharedResources(String[] basePath, List<String> suffixes) {
      List<SharedResource> list = new ArrayList<SharedResource>();
      try {
         ClassLoader classloader = getConfig().getAppClassLoader();
         if (classloader != null) {
            for (int i = 0; i < basePath.length; i++) {
               Enumeration<URL> urls = classloader.getResources(basePath[i]);
               while (urls.hasMoreElements()) {
                  URL url = urls.nextElement();

                  IResourceLibrary library = createLibraryInfo(url);
                  SharedResource sharedResource = new SharedResource(library);
                  Map<String, URL> resourceFiles = listSharedResourceFiles(url,
                        suffixes.toArray(new String[suffixes.size()]), basePath[i]);
                  sharedResource.setResourceFiles(resourceFiles);

                  list.add(sharedResource);
               }
            }
         }
      } catch (IOException e) {
         s_logger.error("Listing shared resources exception", e);
      }

      return list;
   }

   @Override
   public Map<String, List<IResource>> loadResources() {
      Map<String, List<IResource>> map = new HashMap<String, List<IResource>>();

      // share shared resources
      String[] resourceBases = new String[] { "META-INF/resources", "META-INF/tags" };
      Set<String> m_resourceTypes = getConfig().getRegistry().getResourceTypes();
      List<String> allExts = new ArrayList<String>();
      for (String type : m_resourceTypes) {
         List<String> exts = getConfig().getRegistry().getResourceExtensions(type);
         allExts.addAll(exts);
      }

      //collect shared resources
      List<SharedResource> sharedResources = listSharedResources(resourceBases, allExts);

      //create shared resources
      for (SharedResource resource : sharedResources) {
         Map<String, URL> urls = resource.getFiles();
         IResourceLibrary library = resource.getLibrary();
         createResources(urls, library, map);
      }

      return map;
   }

   private static final class SharedLibFinderFilter implements IFinderFilter {
      private final String[] m_suffixes;

      private SharedLibFinderFilter(String[] suffixes) {
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

   protected static class SharedResource {
      private Map<String, URL> m_files;;

      private IResourceLibrary m_library;

      public SharedResource(IResourceLibrary library) {
         m_library = library;
      }

      public Map<String, URL> getFiles() {
         return m_files;
      }

      public IResourceLibrary getLibrary() {
         return m_library;
      }

      public void setResourceFiles(Map<String, URL> resourceFiles) {
         m_files = resourceFiles;
      }
   }
}
