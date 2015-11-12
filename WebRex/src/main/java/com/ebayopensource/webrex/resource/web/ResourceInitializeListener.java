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

package com.ebayopensource.webrex.resource.web;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.ebayopensource.webrex.resource.ResourceInitializer;
import com.ebayopensource.webrex.resource.ResourceRuntime;
import com.ebayopensource.webrex.resource.api.IResourceRuntimeConfig;
import com.ebayopensource.webrex.util.Servlets;

public class ResourceInitializeListener implements ServletContextListener {

   private String m_contextPath;

   @Override
   public void contextDestroyed(ServletContextEvent arg0) {
      ResourceRuntime.INSTANCE.reset();
   }

   @Override
   public void contextInitialized(ServletContextEvent arg0) {
      ServletContext servletContext = arg0.getServletContext();
      String warRoot = servletContext.getRealPath("/");
      m_contextPath = Servlets.forContext().getContextPath(servletContext);

      ResourceInitializer.initialize(m_contextPath, new File(warRoot));

      //init resource bundle
      IResourceRuntimeConfig config = ResourceRuntime.INSTANCE.getConfig();
      ResourceInitializer.initializeResourceBundles(config, config.getAppClassLoader());
   }

}
