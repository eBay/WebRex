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

import com.ebayopensource.webrex.resource.impl.CssTemplateProcessor;
import com.ebayopensource.webrex.resource.impl.DefaultErrorHandler;
import com.ebayopensource.webrex.resource.impl.DefaultExternalResourceResolver;
import com.ebayopensource.webrex.resource.impl.DefaultResourceFactory;
import com.ebayopensource.webrex.resource.impl.DefaultResourceHandler;
import com.ebayopensource.webrex.resource.impl.DefaultTokenStorage;
import com.ebayopensource.webrex.resource.impl.LibraryVersionProvider;
import com.ebayopensource.webrex.resource.impl.LocalResourceLoader;
import com.ebayopensource.webrex.resource.impl.ResourceDeferProcessor;
import com.ebayopensource.webrex.resource.impl.SharedResourceLoader;
import com.ebayopensource.webrex.resource.impl.SharedResourceResolver;
import com.ebayopensource.webrex.resource.impl.WarResourceResolver;
import com.ebayopensource.webrex.resource.spi.IResourceConfigurator;
import com.ebayopensource.webrex.resource.spi.IResourceRegistry;
import com.ebayopensource.webrex.resource.tag.renderer.FlashTagRenderer;
import com.ebayopensource.webrex.resource.tag.renderer.ImageTagRenderer;
import com.ebayopensource.webrex.resource.tag.renderer.ScriptTagRenderer;

public class ResourceConfigurator implements IResourceConfigurator {

   @Override
   public void configure(IResourceRegistry registry) {
      //registry.registerResolver("js", "local", new WarResourceResolver());
      registry.setDefaultResourceResolver("local", new WarResourceResolver());
      registry.setDefaultResourceResolver("shared", new SharedResourceResolver());
      registry.setDefaultResourceFactory(new DefaultResourceFactory());
      registry.setDefaultResourceHandler(new DefaultResourceHandler());

      registry.registerTagRenderer(ResourceTypeConstants.IMAGE, new ImageTagRenderer());
      registry.registerTagRenderer(ResourceTypeConstants.FLASH, new FlashTagRenderer());
      registry.registerTagRenderer(ResourceTypeConstants.CSS, new ScriptTagRenderer(ResourceTypeConstants.CSS));
      registry.registerTagRenderer(ResourceTypeConstants.JS, new ScriptTagRenderer(ResourceTypeConstants.JS));

      registry.setDeferProcessor(new ResourceDeferProcessor(registry.ignoreWhitespaces()));

      //css background image el processor
      registry.registerTemplateProcessor("css", new CssTemplateProcessor());

      //set default resource loaders
      registry.registerResourceLoader("local", new LocalResourceLoader(registry.getConfig()));
      registry.registerResourceLoader("shared", new SharedResourceLoader(registry.getConfig()));

      registry.setLibraryVersionProvider(new LibraryVersionProvider());

      //set default error handler
      registry.setErrorHandler(new DefaultErrorHandler());

      //set default token storage
      registry.setTokenStorage(new DefaultTokenStorage());
      
      DefaultExternalResourceResolver resolver = new DefaultExternalResourceResolver();
      registry.registerExternalResourceResolver(ResourceTypeConstants.JS, resolver);
      registry.registerExternalResourceResolver(ResourceTypeConstants.CSS, resolver);
   }

}
