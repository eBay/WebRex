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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.ebayopensource.webrex.logging.ILogger;
import com.ebayopensource.webrex.logging.LogLevel;
import com.ebayopensource.webrex.logging.LoggerFactory;
import com.ebayopensource.webrex.resource.ResourceFactory;
import com.ebayopensource.webrex.resource.ResourceRuntimeContext;
import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.expression.ResBeanExpression;
import com.ebayopensource.webrex.resource.expression.ResourceExpression;
import com.ebayopensource.webrex.resource.spi.ITemplateProcessor;
import com.ebayopensource.webrex.util.ELHelper;

public class CssTemplateProcessor implements ITemplateProcessor {
   private static ILogger s_logger = LoggerFactory.getLogger(CssTemplateProcessor.class);

   public String handleToken(StringBuilder originalToken, List<IResource> dependencies, String cssPath) {
      if (originalToken == null || originalToken.length() == 0) {
         return "";
      }

      StringBuilder token = originalToken;
      boolean isEl = originalToken.indexOf("${") == 0;
      String imageRelativePath = token.toString().trim();
      if (isEl) {
         //check with el end pos
         int endPos = imageRelativePath.lastIndexOf('}');

         String suffix = null;
         String elPath = imageRelativePath;
         if (endPos != -1 && endPos != imageRelativePath.length() - 1) {
            suffix = imageRelativePath.substring(endPos + 1);
            elPath = imageRelativePath.substring(0, endPos + 1);
         }

         List<String> elKeys = ELHelper.getELKeys(elPath);
         if (elKeys.size() > 0 && elKeys.get(0).equals("res")) {

            IResource dependentResource = null;
            //TODO: get the root expression from thread local
            ResourceExpression expression = new ResBeanExpression();
            boolean hasValue = false;
            for (int i = 1; i < elKeys.size(); i++) {
               String key = elKeys.get(i);
               Object value = expression.get(key);
               if (value instanceof ResourceExpression) {
                  expression = (ResourceExpression) value;
               } else {
                  if (value instanceof IResource) {
                     dependentResource = (IResource) value;
                     hasValue = true;
                  }
               }
            }

            if (!hasValue) {
               dependentResource = (IResource) expression.evaluate();
            }

            if (dependentResource != null) {
               if (!dependencies.contains(dependentResource)) {
                  dependencies.add(dependentResource);
               }

               String url = dependentResource.getUrl(ResourceRuntimeContext.ctx().createResourceContext());
               if (url != null) {
                  return suffix != null ? url + suffix : url;
               }
            }
         }
      } else if (!imageRelativePath.startsWith("data:") && !imageRelativePath.startsWith("http")) {
         try {
            URI imgPath = (URI.create(cssPath).resolve(imageRelativePath));
            IResource dependentResource = ResourceFactory.createResource(imgPath.getPath());
            if (dependentResource != null) {
               if (!dependencies.contains(dependentResource)) {
                  dependencies.add(dependentResource);
               }

               return dependentResource.getUrl(ResourceRuntimeContext.ctx().createResourceContext());
            }
         } catch (Exception e) {
            s_logger.log(
                  LogLevel.WARN,
                  String.format("Failed to parse EL(%s) in css(%s), exception:%s", imageRelativePath, cssPath,
                        e.toString()));
         }
      }

      return imageRelativePath;
   }

   public void parse(StringBuilder template, List<IResource> dependencies, String cssPath) {
      if (template != null) {
         StringBuilder url = new StringBuilder(20);

         boolean urlfound = false;
         boolean singleQuote = false;
         boolean doubleQuote = false;
         boolean inUrl = false;
         int urlStartPos = -1;
         int urlEndPos = -1;

         int end = template.length();
         int i = 0;
         while (i < end) {
            char c = template.charAt(i);

            switch (c) {
            case 'u':
            case 'U':
               if (inUrl) {
                  url.append(c);
               } else {
                  if (i + 3 < end) {
                     char r = template.charAt(i + 1);
                     char l = template.charAt(i + 2);
                     char leftParentheses = template.charAt(i + 3);
                     if (('r' == r || 'R' == r) && ('l' == l || 'L' == l) && ('(' == leftParentheses)) {
                        urlfound = true;
                        i = i + 3;
                        urlStartPos = i + 1;
                     }
                  }
               }
               break;
            case ')':
               if (urlfound) {
                  if (!singleQuote && !doubleQuote) {
                     if (urlEndPos > urlStartPos) {
                        String result = handleToken(url, dependencies, cssPath);
                        template.replace(urlStartPos, urlEndPos, result);
                        //revise i & end
                        int diff = result.length() - (urlEndPos - urlStartPos);
                        end = end + diff;
                        i = i + diff;
                     }
                  }

                  inUrl = false;
                  urlfound = false;
                  singleQuote = false;
                  doubleQuote = false;
                  urlStartPos = -1;
                  urlEndPos = -1;
                  url.setLength(0);
               }
               break;
            case ' ':
               if (urlfound) {
                  if (singleQuote || doubleQuote) {
                     url.append(c);
                  } else {
                     inUrl = false;
                  }
               }
               break;
            case '\'':
               if (urlfound) {
                  if (inUrl) {
                     if (singleQuote) {
                        urlEndPos = i;
                        singleQuote = false;
                        inUrl = false;
                     } else {
                        url.append(c);
                        urlEndPos = i + 1;
                     }
                  } else {
                     singleQuote = true;
                     urlStartPos = i + 1;
                     inUrl = true;
                  }
               }
               break;
            case '\"':
               if (urlfound) {
                  if (inUrl) {
                     if (doubleQuote) {
                        urlEndPos = i;
                        doubleQuote = false;
                        inUrl = false;
                     } else {
                        url.append(c);
                        urlEndPos = i + 1;
                     }
                  } else {
                     doubleQuote = true;
                     urlStartPos = i + 1;
                     inUrl = true;
                  }
               }
               break;
            default:
               if (urlfound) {
                  if (!inUrl) {
                     if (url.length() > 0) {
                        inUrl = false;
                        urlfound = false;
                        singleQuote = false;
                        doubleQuote = false;
                        urlStartPos = -1;
                        urlEndPos = -1;
                        url.setLength(0);
                     } else {
                        inUrl = true;
                        urlStartPos = i;
                        urlEndPos = i + 1;
                        url.append(c);
                     }
                  } else {
                     urlEndPos = i + 1;
                     url.append(c);
                  }
               }
            }

            i++;
         }
      }
   }

   @Override
   public void process(StringBuilder template, IResource resource) {
      List<IResource> dependencies = new ArrayList<IResource>();

      parse(template, dependencies, resource.getUrn().getPath());
      if (!dependencies.isEmpty()) {
         resource.setDependencies(dependencies);
      }
   }

}
