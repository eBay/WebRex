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

public interface IResourceContext {
   public IResourceRuntimeConfig getConfig();

   public IResourceLocale getLocale();

   public boolean isSecure();

   public void setLocale(IResourceLocale locale);

   public void setSecure(boolean secure);

   public String getOptimizationCommand(String type);

   public boolean isOptimizationEnabled();

   public String getOriginalRequestUri();

   public void setOriginalRequestUri(String uri);

   public Object getAttribute(String name);
   
   public void setAttribute(String name, Object value);
}
