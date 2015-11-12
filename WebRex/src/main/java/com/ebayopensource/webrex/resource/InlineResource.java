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

import java.io.UnsupportedEncodingException;
import java.net.URL;

import com.ebayopensource.webrex.resource.api.IInlineResource;
import com.ebayopensource.webrex.resource.api.IResourceContext;
import com.ebayopensource.webrex.resource.api.IResourceLibrary;
import com.ebayopensource.webrex.resource.api.IResourceLocale;
import com.ebayopensource.webrex.resource.api.IResourceUrn;
import com.ebayopensource.webrex.resource.api.ITemplateContext;
import com.ebayopensource.webrex.util.Checksum;

public class InlineResource extends Resource implements IInlineResource {

   private static final String INLINE = "inline";

   private static IResourceUrn createUrn(String inlineText, String resType) {
      String resourcePath = '/' + Checksum.checksum(inlineText);

      return new ResourceUrn(resType, INLINE, resourcePath);
   }

   private String m_inlineText;

   protected InlineResource(IResourceUrn urn, URL physicalUrl, IResourceLocale locale, IResourceLibrary libraryInfo) {
      super(urn, physicalUrl, locale, libraryInfo);
   }

   public InlineResource(String inlineText, String resType) {
      super(createUrn(inlineText, resType), null, null, null);
      m_inlineText = inlineText;
   }

   @Override
   public String getContent(IResourceContext context) {
      return m_inlineText;
   }

   @Override
   public String getInlineText() {
      return m_inlineText;
   }

   @Override
   public long getLastModified() {
      return 0;
   }

   @Override
   public byte[] getOriginalBinaryContent() {
      String content = getOriginalContent();
      if (content != null) {
         try {
            return content.getBytes("utf-8");
         } catch (UnsupportedEncodingException e) {
            return content.getBytes();
         }
      }

      return null;
   }

   @Override
   public byte[] getOriginalBinaryContent(ITemplateContext context) {
      return getOriginalBinaryContent();
   }

   @Override
   public String getOriginalContent() {
      return m_inlineText;
   }

   @Override
   public String getOriginalContent(ITemplateContext context) {
      return getOriginalContent();
   }

}
