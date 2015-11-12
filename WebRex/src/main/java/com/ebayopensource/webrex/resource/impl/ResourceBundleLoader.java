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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ebayopensource.webrex.logging.ILogger;
import com.ebayopensource.webrex.logging.LoggerFactory;
import com.ebayopensource.webrex.resource.ResourceBundleConfig;
import com.ebayopensource.webrex.resource.ResourceFactory;
import com.ebayopensource.webrex.resource.ResourceTypeConstants;
import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.api.IResourcePackage;
import com.ebayopensource.webrex.resource.api.IResourceRuntimeConfig;

public class ResourceBundleLoader {
   private static final String RESOURCE_BUNDLES = "META-INF/webres/resource_bundles.xml";

   private static final String RESOURCE_WEB_BUNDLES = "/WEB-INF/webres/resource_bundles.xml";

   private static final String BUNDLE_RESOURCE = "resource";

   private static final String BUNDLE_PACKAGE_RESOURCE = "resourcePackage";

   private static final String BUNDLE_ID = "id";

   private static final String ELEMENT_BUNDLE = "bundle";

   private static final String BUNDLE_TYPE = "type";

   private static ILogger s_logger = LoggerFactory.getLogger(ResourceBundleLoader.class);

   public void load(ClassLoader[] classloaders, IResourceRuntimeConfig config) throws IOException {
      DocumentBuilder builder = null;
      try {
         builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      } catch (ParserConfigurationException e1) {
         s_logger.error("Error init document builder, exception:" + e1.toString());
         return;
      }

      //load properties from class loaders
      //load system resource bundle first, currently only allow system level bundle in the shared resource bundle configuration
      if (classloaders != null) {
         for (ClassLoader classloader : classloaders) {
            Enumeration<URL> urls = classloader.getResources(RESOURCE_BUNDLES);

            while (urls.hasMoreElements()) {
               URL url = null;
               try {
                  url = urls.nextElement();

                  //only load system slots on the shared resource bundle configuration
                  loadBundle(builder, url, config.getResourceBundleConfig());
               } catch (Exception e) {
                  s_logger.error(String.format("Error when reading resource bundle(%s), exception:(%s)",
                        url == null ? "" : url.toExternalForm(), e.toString()));
               }
            }
         }
      }

      //load properties from local war root
      if (config.getWarRoot() != null) {
         File file = new File(config.getWarRoot(), RESOURCE_WEB_BUNDLES);

         if (file.isFile()) {
            try {
               loadBundle(builder, new FileInputStream(file), config.getResourceBundleConfig());
            } catch (Exception e) {
               s_logger.error(String.format("Error when reading resource bundle(%s), exception:(%s)", file.getPath(),
                     e.toString()));
            }
         }
      }
   }

   protected void loadBundle(DocumentBuilder builder, URL url, ResourceBundleConfig resourceBundleConfig)
         throws IOException, ParserConfigurationException, SAXException {
      loadBundle(builder, url.openStream(), resourceBundleConfig);
   }

   protected void loadBundle(DocumentBuilder builder, InputStream is, ResourceBundleConfig config)
         throws ParserConfigurationException, SAXException, IOException {
      try {
         Document doc = builder.parse(is);

         Element root = doc.getDocumentElement();
         NodeList childNodes = root.getChildNodes();
         for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
               String name = node.getNodeName();

               if (ELEMENT_BUNDLE.equals(name)) {
                  parseBundle(node, config);
               } else {
                  s_logger.error(String.format("Unsupported xml node(%s), only bundle supported.", name));
               }
            }
         }
      } finally {
         if (is != null) {
            is.close();
         }
      }
   }

   protected void parseBundle(Node node, ResourceBundleConfig config) {
      String id = node.getAttributes().getNamedItem(BUNDLE_ID).getNodeValue();
      if (id == null || id.isEmpty()) {
         s_logger.error("Bundle id can't be null.");
         return;
      }

      Node nodeType = node.getAttributes().getNamedItem(BUNDLE_TYPE);
      boolean isSystem = nodeType != null && "system".equalsIgnoreCase(nodeType.getNodeValue());

      loadResourceBundle(config, node, id, isSystem);
   }

   private void loadResourceBundle(ResourceBundleConfig config, Node resourceNode, String id, boolean isSystem) {
      NodeList childNodes = resourceNode.getChildNodes();
      int length = childNodes.getLength();
      if (length > 0) {
         List<IResource> resources = new ArrayList<IResource>();
         for (int i = 0; i < length; i++) {
            Node node = childNodes.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
               String name = node.getNodeName();

               if (BUNDLE_RESOURCE.equals(name)) {
                  Node pathNode = node.getAttributes().getNamedItem("path");
                  if (pathNode == null || pathNode.getNodeValue().isEmpty()) {
                     s_logger.error("Path is null, invalid resource bundle definition:" + id);
                     continue;
                  }

                  String path = pathNode.getNodeValue();
                  IResource resource = ResourceFactory.createResource(path);
                  if (resource != null) {
                     resources.add(resource);
                  }
               } else if (BUNDLE_PACKAGE_RESOURCE.equals(name)) {
                  //support common type
                  Node ns = node.getAttributes().getNamedItem("type");
                  String namespace = ns == null ? null : ns.getNodeValue().trim();
                  if (namespace == null || namespace.isEmpty()) {
                     s_logger.error(String.format(
                           "Unsupported xml node(%s) in bundle, only resource node or resourcePackage node supported.",
                           name));
                     continue;
                  }

                  Node pathNode = node.getAttributes().getNamedItem("id");
                  String resourcePath = pathNode == null ? null : pathNode.getNodeValue().trim();
                  if (pathNode == null || pathNode.getNodeValue().isEmpty()) {
                     s_logger.error("Id is null, invalid resource bundle definition:" + id);
                     continue;
                  }

                  Map<String, Object> params = new HashMap<String, Object>();
                  NamedNodeMap attrs = node.getAttributes();
                  for (int j = 0; j < attrs.getLength(); j++) {
                     Node attr = attrs.item(j);
                     String nodeName = attr.getNodeName();
                     if (nodeName.equalsIgnoreCase("id") || nodeName.equalsIgnoreCase("type")) {
                        continue;
                     }
                     params.put(nodeName, attr.getNodeValue().trim());
                  }

                  if (!resourcePath.startsWith("/")) {
                     resourcePath = "/" + resourcePath;
                  }

                  IResource resolvedResource = ResourceFactory.createResource(name, namespace, resourcePath, params);

                  //handle module resource
                  //TODO: only support js currently
                  if (resolvedResource instanceof IResourcePackage) {
                     List<IResource> moduleResources = ((IResourcePackage) resolvedResource)
                           .getResources(ResourceTypeConstants.JS);
                     if (moduleResources != null) {
                        for (IResource resource : moduleResources) {
                           resources.add(resource);
                        }
                     }
                  } else {
                     resources.add(resolvedResource);
                  }
               } else {
                  s_logger.error(String.format(
                        "Unsupported xml node(%s) in bundle, only resource node or resourcePackage node supported.",
                        name));
               }
            }
         }

         if (!resources.isEmpty()) {
            config.addBundle(id, resources, isSystem);
         }
      }
   }
}
