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

package com.ebayopensource.webrex.resource.tag.handler;

import java.io.IOException;

import javax.servlet.jsp.JspContext;

import com.ebayopensource.webrex.resource.tag.BaseTagEnv;

public class JspTagEnv extends BaseTagEnv {
   private JspContext m_pageContext;

   public Object findAttribute(String name) {
      return m_pageContext.findAttribute(name);
   }

   @Override
   public void flush() throws IOException {
      m_pageContext.getOut().flush();
   }

   @Override
   public Object getPageAttribute(String name) {
      return m_pageContext.getAttribute(name);
   }

   public JspContext getPageContext() {
      return m_pageContext;
   }

   @Override
   public Object getRequestAttribute(String name) {
      if (m_pageContext == null) {
         return null;
      }

      return m_pageContext.getAttribute(name, 2);
   }

   @Override
   public void removePageAttribute(String name) {
      m_pageContext.removeAttribute(name);
   }

   @Override
   public void setPageAttribute(String name, Object value) {
      m_pageContext.setAttribute(name, value);
   }

   public void setPageContext(JspContext pageContext) {
      m_pageContext = pageContext;
   }

   @Override
   public void setRequestAttribute(String name, Object value) {
      m_pageContext.setAttribute(name, value, 2);
   }
}
