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

package com.ebayopensource.webrex.resource.api;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.ebayopensource.webrex.resource.ResourceBundleConfig;
import com.ebayopensource.webrex.resource.ResourceErrPolicy;
import com.ebayopensource.webrex.resource.cache.ResourceCacheManager;
import com.ebayopensource.webrex.resource.spi.IResourceRegistry;

public interface IResourceRuntimeConfig {
   
   String PROP_SECURE_SERVICE = "service.secure";
   
   public ClassLoader getAppClassLoader();

   public String getAppId();

   public String getAppVersion();

   public ResourceCacheManager getCacheManager();

   public String getContextPath();

   public String getOptimizationCommand(String type);

   public Map<String, String> getProperties();

   public String getProperty(String key);

   public IResourceRegistry getRegistry();

   public String getResourceBase(String type);

   public ResourceBundleConfig getResourceBundleConfig();

   public ResourceErrPolicy getResourceErrPolicy();

   public File getWarRoot();

   public boolean isDev();

   public boolean isOptimizationEnabled();

   public boolean isStatisticsEnabled();

   public Map<String, List<IResource>> loadResource(String namespace, boolean isSecure);

   public void setAppClassLoader(ClassLoader classloader);

   public void setAppId(String appId);

   public void setAppVersion(String appVersion);

   public void setDev(boolean isDev);

   public void setOptimizationCommand(String type, String command);

   public void setOptimizationEnabled(boolean enabled);

   public void setResourceErrPolicy(ResourceErrPolicy policy);
   
   public void setProperty(String key, String value);
   
   public void setVariationResource(IResource Resource, IResource minResource);

   public IResource gettVariationResource(IResource Resource);
   
   public boolean isResourcePreCollected();
   
   public void setResourcePreCollected(boolean collected);
   
}
