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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.api.IResourceRuntimeConfig;
import com.ebayopensource.webrex.resource.cache.ResourceCacheManager;
import com.ebayopensource.webrex.resource.impl.ResourcePropertiesLoader;
import com.ebayopensource.webrex.resource.spi.IResourceLoader;
import com.ebayopensource.webrex.resource.spi.IResourceRegistry;

public class ResourceRuntimeConfig implements IResourceRuntimeConfig {
   private static final String RESOURCES = "/resources";

   private static final String RESOURCE_TYPE_BASE_SUFFIX = ".base";

   private static final String RESOURCE_BASE = "resource" + RESOURCE_TYPE_BASE_SUFFIX;

   private String m_contextPath = null;

   private File m_warRoot = null;

   private IResourceRegistry m_registry = null;

   private ResourceCacheManager m_cacheManager;

   private ClassLoader m_appClassLoader;

   private String m_appId;

   private String m_appVersion;

   private Boolean m_statisticsEnabled;

   //default dev mode is true
   private boolean m_isDev = true;

   private boolean m_optimizationEnabled;
   
   private boolean m_isResourcePreCollected;

   private Map<String, String> m_properties = new HashMap<String, String>();

   private Map<String, String> m_optimizationCommands = new HashMap<String, String>();
   
   private Map<IResource, IResource> m_resourceVariationMap = new HashMap<IResource, IResource>();

   private ResourceBundleConfig m_bundleConfig;

   private ResourceErrPolicy m_errPolicy;

   public ResourceRuntimeConfig(String contextPath, File warRoot) {
      validate(contextPath, "context");
      try {
         m_warRoot = warRoot.getCanonicalFile();
      } catch (IOException e) {
         throw new IllegalArgumentException(String.format("Unable to resolve WarRoot(%s)!", warRoot), e);
      }
      
      //set default resource base
      m_properties.put(RESOURCE_BASE, RESOURCES);

      //load resource property file
      ResourcePropertiesLoader loader = new ResourcePropertiesLoader();
      Map<String, String> map = loader.load(m_warRoot);
      if (map != null && !map.isEmpty()) {
         m_properties.putAll(map);
      }

      IResourceRegistry registry = new ResourceRegistry(this);
      new ResourceConfigurator().configure(registry);
      m_registry = registry;
      m_contextPath = contextPath;
      m_cacheManager = new ResourceCacheManager();
      m_bundleConfig = new ResourceBundleConfig();
   }

   @Override
   public ClassLoader getAppClassLoader() {
      return m_appClassLoader;
   }

   @Override
   public String getAppId() {
      if(m_appId == null){
         return "";
      }
      return m_appId;
   }

   @Override
   public String getAppVersion() {
      if(m_appVersion == null){
         return "";
      }
      return m_appVersion;
   }

   @Override
   public ResourceCacheManager getCacheManager() {
      return m_cacheManager;
   }

   @Override
   public String getContextPath() {
      if(m_contextPath == null){
         return "";
      }
      return m_contextPath;
   }

   @Override
   public String getOptimizationCommand(String type) {
      return m_optimizationCommands.get(type);
   }

   @Override
   public Map<String, String> getProperties() {
      return m_properties;
   }

   @Override
   public String getProperty(String key) {
      return m_properties.get(key);
   }

   @Override
   public IResourceRegistry getRegistry() {
      return m_registry;
   }

   @Override
   public String getResourceBase(String type) {
	  String base = null;
	  
	  //Fix image base folder compatibility issue 
	  if(ResourceTypeConstants.IMAGE.equals(type)) {
		  base = getProperty(type + RESOURCE_TYPE_BASE_SUFFIX);
		  if(base == null) {
			  base = getProperty("image" + RESOURCE_TYPE_BASE_SUFFIX);
		  }
	  } else {
		  base = getProperty(type + RESOURCE_TYPE_BASE_SUFFIX);
	  }
	   
      return base != null ? base : getProperty(RESOURCE_BASE);
   }

   @Override
   public ResourceBundleConfig getResourceBundleConfig() {
      return m_bundleConfig;
   }

   @Override
   public ResourceErrPolicy getResourceErrPolicy() {
      return m_errPolicy;
   }

   @Override
   public File getWarRoot() {
      return m_warRoot;
   }

   @Override
   public boolean isDev() {
      return m_isDev;
   }

   @Override
   public boolean isOptimizationEnabled() {
      return m_optimizationEnabled;
   }

   public boolean isStatisticsEnabled() {
      Boolean statisticsEnabled = m_statisticsEnabled;
      return statisticsEnabled != null ? statisticsEnabled : true;
   }

   @Override
   public Map<String, List<IResource>> loadResource(String namespace, boolean isSecure) {
      IResourceLoader loader = m_registry.getResourceLoader(namespace);
      if (loader != null) {
         Map<String, List<IResource>> map = loader.loadResources();
         m_cacheManager.putResourceCaches(namespace, map, isSecure);
         return map;
      } else {
         return Collections.emptyMap();
      }
   }

   public void setAppClassLoader(ClassLoader appClassLoader) {
      m_appClassLoader = appClassLoader;
   }

   public void setAppId(String appId) {
      m_appId = appId;
   }

   public void setAppVersion(String appVersion) {
      m_appVersion = appVersion;
   }

   @Override
   public void setDev(boolean isDev) {
      m_isDev = isDev;
   }

   @Override
   public void setOptimizationCommand(String type, String command) {
      m_optimizationCommands.put(type, command);
   }

   @Override
   public void setOptimizationEnabled(boolean enabled) {
      m_optimizationEnabled = enabled;
   }

   @Override
   public void setResourceErrPolicy(ResourceErrPolicy policy) {
      m_errPolicy = policy;
   }

   public void setStatisticsEnabled(Boolean statisticsEnabled) {
      m_statisticsEnabled = statisticsEnabled;
   }

   private void validate(String value, String type) {
      if (value != null) {
         int len = value.length();

         if (value.charAt(0) != '/' || (len > 1 && value.charAt(len - 1) == '/')) {
            throw new RuntimeException(String.format("Invalid %s path(%s), "
                  + "which should start with '/' and NOT end with '/'.", type, value));
         }
      }
   }

   @Override
   public void setProperty(String key, String value) {
      m_properties.put(key, value);
   }
   
   @Override
   public void setVariationResource(IResource resource, IResource minResource) {
      if(this.m_resourceVariationMap == null) {
         this.m_resourceVariationMap = new HashMap<IResource, IResource>();
      }
      this.m_resourceVariationMap.put(resource, minResource);
   }

   @Override
   public IResource gettVariationResource(IResource Resource) {
      if(m_isResourcePreCollected && (this.m_resourceVariationMap != null)) {
         return m_resourceVariationMap.get(Resource);
      }
      return null;
   }

   public Map<IResource, IResource> getResourceVariationMap() {
      return this.m_resourceVariationMap;
   }

   @Override
   public boolean isResourcePreCollected() {
      return this.m_isResourcePreCollected;
   }
   @Override
   public void setResourcePreCollected(boolean collected) {
      this.m_isResourcePreCollected = collected;
   }
}
