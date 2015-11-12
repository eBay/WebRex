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

import java.net.URL;
import java.util.List;
import java.util.Map;

import com.ebayopensource.webrex.resource.api.IAggregatedResource;
import com.ebayopensource.webrex.resource.api.IExternalResource;
import com.ebayopensource.webrex.resource.api.IInlineResource;
import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.api.IResourceLibrary;
import com.ebayopensource.webrex.resource.api.IResourceLocale;
import com.ebayopensource.webrex.resource.api.IResourceUrn;
import com.ebayopensource.webrex.resource.expression.ResourceExpression;
import com.ebayopensource.webrex.util.ELHelper;
import com.ebayopensource.webrex.util.FileUtil;

public class ResourceFactory {
   /**
    * Create aggregation resource
    * @param resources
    * @return created aggregation resource
    */
   public static IAggregatedResource createAggregatedResource(List<IResource> resources) {
      return ResourceManager.INSTANCE.resolveAggregatedResource(resources);
   }

   /**
    * Create inline resource
    * @param type resource type
    * @param content inline context
    * @return created inline resource
    */
   public static IInlineResource createInlineResource(String type, String content) {
      return ResourceManager.INSTANCE.resolveInline(type, content);
   }

   public static IExternalResource createExternalResource(String type, String url) {
      return ResourceManager.INSTANCE.resolveExternal(type, url);
   }

   /**
    * Create resource from shared library
    * @param resourcePath
    * @return
    */
   public static IResource createLibraryResource(String resourcePath) {
      ResourceRuntimeContext ctx = ResourceRuntimeContext.ctx();
      String type = ctx.getConfig().getRegistry().getResourceType(FileUtil.getExtension(resourcePath));
      IResourceUrn urn = new ResourceUrn(type, ResourceManager.SHARED, resourcePath);
      return ResourceManager.INSTANCE.resolve(urn);
   }

   /**
    * Create resource with resource urn, URL, locale and library info
    * @param urn
    * @param url
    * @param locale
    * @param library
    * @return created resource
    */
   public static IResource createResource(IResourceUrn urn, URL url, IResourceLocale locale, IResourceLibrary library) {
      return ResourceManager.INSTANCE.create(urn, url, locale, library);
   }

   /**
    * 
    * @param resourcePath the path starts from the resource root, the path should start with '/'; 
    * the default resource path is "/resources"
    * @return Resource object
    */
   public static IResource createResource(String resourcePath) {
      return ResourceManager.INSTANCE.resolve(resourcePath);
   }

   public static IResource createResourceByEl(String el) {
      ResourceExpression expression = ELHelper.getExpressionFromEL(el);
      if (expression != null) {
         Object result = expression.evaluate();
         if (result instanceof IResource) {
            return (IResource) result;
         }
      }
      return null;
   }

   /**
    * Create resource with specified resource type and namespace
    * @param resourceType
    * @param namespace
    * @param resourcePath
    * @return created resource
    */
   public static IResource createResource(String resourceType, String namespace, String resourcePath) {
      IResourceUrn urn = new ResourceUrn(resourceType, namespace, resourcePath);
      return ResourceManager.INSTANCE.resolve(urn);
   }

   /**
    * Create resource with specified resource type, namespace and arguments
    * @param resourceType
    * @param namespace
    * @param resourcePath
    * @param arguments
    * @return created resource
    */
   public static IResource createResource(String resourceType, String namespace, String resourcePath,
         Map<String, Object> arguments) {
      IResourceUrn urn = new ResourceArgumentsUrn(resourceType, namespace, resourcePath, arguments);
      return ResourceManager.INSTANCE.resolve(urn);
   }

   /**
    * Create resource with resource url and path
    * @param url
    * @param resourcePathWithLocale
    * @param libraryInfo
    * @return
    */
   public static IResource createResource(URL url, String locale, IResourceLibrary libraryInfo) {
      return ResourceManager.INSTANCE.create(url, locale, libraryInfo);
   }

   /**
    * Create resource from local war project
    * @param resourcePath
    * @return
    */
   public static IResource createWarResource(String resourcePath) {
      ResourceRuntimeContext ctx = ResourceRuntimeContext.ctx();
      String type = ctx.getConfig().getRegistry().getResourceType(FileUtil.getExtension(resourcePath));
      IResourceUrn urn = new ResourceUrn(type, ResourceManager.LOCAL, resourcePath);
      return ResourceManager.INSTANCE.resolve(urn);
   }

}
