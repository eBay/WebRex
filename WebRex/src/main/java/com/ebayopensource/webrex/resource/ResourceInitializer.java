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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.ebayopensource.webrex.logging.ILogger;
import com.ebayopensource.webrex.logging.LogLevel;
import com.ebayopensource.webrex.logging.LoggerFactory;
import com.ebayopensource.webrex.resource.api.IResourceRuntimeConfig;
import com.ebayopensource.webrex.resource.impl.ResourceBundleLoader;
import com.ebayopensource.webrex.resource.impl.ResourceMimeTypeLoader;
import com.ebayopensource.webrex.resource.spi.ILibraryVersionProvider.VersionID;
import com.ebayopensource.webrex.resource.spi.IResourceConfigurator;
import com.ebayopensource.webrex.resource.spi.IResourceRegistry;
import com.ebayopensource.webrex.util.Reflector;
import com.ebayopensource.webrex.util.Servlets;
import com.ebayopensource.webrex.util.Splitters;

public class ResourceInitializer {
   private static final String RESOURCE_CONFIGURATORS = "resource.configurators";

   private static final String RUNTIME_EXT_PROPERTIES = "/META-INF/webres/runtime_extension.properties";

   private static final String MIME_TYPE_PROPERTIES = "/com/ebayopensource/webrex/resource/mimetypes.properties";

   private static final String MIME_TYPE_EXT_PROPERTIES = "/META-INF/webres/mimetypes.properties";

   private static ILogger s_logger = LoggerFactory.getLogger(ResourceInitializer.class);

   private static void addConfiguratorForClass(List<IResourceConfigurator> list, Set<String> dummy,
         ClassLoader classloader, String classNameList) {
      List<String> classNames = Splitters.by(',').noEmptyItem().trim().split(classNameList);

      for (String className : classNames) {
         if (!dummy.contains(className)) {
            Class<?> clazz = null;
            try {
               clazz = classloader.loadClass(className);
            } catch (ClassNotFoundException e1) {
               s_logger.warn(e1.getMessage(), e1);
            }

            if (clazz != null && IResourceConfigurator.class.isAssignableFrom(clazz)) {
               try {
                  dummy.add(className);
                  IResourceConfigurator configurator = (IResourceConfigurator) clazz.newInstance();
                  list.add(configurator);
               } catch (Exception e) {
                  s_logger.warn("Create configurator exception:" + className, e);
               }
            }
         }
      }
   }

   static List<IResourceConfigurator> getAllConfigurators(ClassLoader... classloaders) {
      List<IResourceConfigurator> list = new ArrayList<IResourceConfigurator>();

      //load properties from class loaders
      if (classloaders != null) {
         Set<String> dummy = new HashSet<String>();//check for duplicated configurator class names
         for (ClassLoader classloader : classloaders) {
            List<Properties> propsList = Reflector.INSTANCE.getResourcesProperties(classloader, null,
                  RUNTIME_EXT_PROPERTIES);

            if (propsList != null) {
               for (Properties props : propsList) {
                  String classNameList = props.getProperty(RESOURCE_CONFIGURATORS);

                  if (classNameList != null && !classNameList.isEmpty()) {
                     addConfiguratorForClass(list, dummy, classloader, classNameList);
                  }
               }
            }
         }
      }

      return list;
   }

   private static ClassLoader[] getConfigurationClassLoaders(ClassLoader... classloaders) {
      if (classloaders == null || classloaders.length == 0) {
         //scan webres SDK classloaders
         ClassLoader classloader = ResourceInitializer.class.getClassLoader();
         //scan thread context classloaders to load application configurators
         ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

         if (!classloader.equals(contextClassLoader)) {
            classloaders = new ClassLoader[] { classloader, contextClassLoader };
         } else {
            classloaders = new ClassLoader[] { classloader };
         }
      }

      return classloaders;
   }

   public static void initialize(String contextPath, File warRoot, ClassLoader... classloaders) {
      //init war config
      String refinedContextPath = Servlets.forContext().getContextPath(contextPath);
      IResourceRuntimeConfig config = initWarConfig(refinedContextPath, warRoot);
      IResourceRegistry registry = config.getRegistry();
      ClassLoader[] configurationClassLoaders = getConfigurationClassLoaders(classloaders);

      //load resource configurators
      List<IResourceConfigurator> configurators = getAllConfigurators(configurationClassLoaders);
      initializeWithConfigurators(configurators, registry, refinedContextPath);

      //load appId, version
      VersionID versionId = registry.getLibraryVersionProvider().resolveWeb(warRoot);
      if (versionId != null) {
         config.setAppId(versionId.getId());
         config.setAppVersion(versionId.getVersion());
      }

      //load resource mimeTypes
      loadResourceMimeTypes(registry, configurationClassLoaders);

      //Set app class loader
      ClassLoader appClassLoader = Thread.currentThread().getContextClassLoader();
      if (appClassLoader == null) {
         appClassLoader = ResourceInitializer.class.getClassLoader();
      }

      config.setAppClassLoader(appClassLoader);
   }

   static void initializeWithConfigurators(List<IResourceConfigurator> configurators, IResourceRegistry registry,
         String contextPath) {
      for (IResourceConfigurator configurator : configurators) {
         try {
            configurator.configure(registry);

            if (s_logger.isInfoEnabled()) {
               s_logger.info("Configurator:" + configurator.getClass().getName() + " is configured with context path:"
                     + contextPath);
            }
         } catch (Exception e) {
            throw new RuntimeException(String.format(
                  "%s resource configurator configure failure on context path(%s), exception: %s", configurator
                        .getClass().getSimpleName(), contextPath, e.toString()), e);
         }
      }
   }

   public static void initializeResourceBundles(IResourceRuntimeConfig config, ClassLoader... classloaders) {
      ResourceBundleLoader loader = new ResourceBundleLoader();
      try {
         ResourceRuntimeContext.setup();
         loader.load(classloaders, config);

         //trigger resource bundle config externalization
         config.getResourceBundleConfig().init();
      } catch (Exception e) {
         s_logger.log(LogLevel.ERROR, "Failed to load resource bundle configuration, exception:" + e.toString());
      } finally {
         ResourceRuntimeContext.reset();
      }
   }

   private static IResourceRuntimeConfig initWarConfig(String contextPath, File warRoot) {
      IResourceRuntimeConfig config = new ResourceRuntimeConfig(contextPath, warRoot);
      ResourceRuntime.INSTANCE.setConfig(config);
      return config;
   }

   private static void loadResourceMimeTypes(IResourceRegistry registry, ClassLoader[] classloaders) {
      //load default resource mimeTypes
      URL defaultMimeType = ResourceInitializer.class.getResource(MIME_TYPE_PROPERTIES);
      ResourceMimeTypeLoader.INSTANCE.loadResourceMimeTypes(registry, defaultMimeType);

      try {
         //load extension resource mimeTypes
         for (ClassLoader classloader : classloaders) {
            Enumeration<URL> urls = classloader.getResources(MIME_TYPE_EXT_PROPERTIES);
            if (urls != null) {
               while (urls.hasMoreElements()) {
                  URL url = urls.nextElement();
                  ResourceMimeTypeLoader.INSTANCE.loadResourceMimeTypes(registry, url);
               }
            }
         }
      } catch (IOException e) {
         s_logger.log(LogLevel.WARN, "Failed to load extension mimetypes, exception:" + e.toString());
      }
   }

   private ResourceInitializer() {
   }
}
