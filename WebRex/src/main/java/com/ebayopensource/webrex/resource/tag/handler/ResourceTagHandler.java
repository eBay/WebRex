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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.DynamicAttributes;

import com.ebayopensource.webrex.resource.ResourceRuntimeContext;
import com.ebayopensource.webrex.resource.Statistics;
import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.tag.ITag;
import com.ebayopensource.webrex.resource.tag.ITag.State;
import com.ebayopensource.webrex.resource.tag.ITagEnv;
import com.ebayopensource.webrex.resource.tag.ITagModel;
import com.ebayopensource.webrex.resource.tag.ResourceTag;
import com.ebayopensource.webrex.util.JavaHighResTimer;

public class ResourceTagHandler extends BodyTagSupport implements DynamicAttributes {
   private static final long serialVersionUID = 4697522509496102744L;

   private ITag m_tag;

   private boolean m_hasBody = false;

   private long[] m_startingTime;

   private ResourceRuntimeContext m_ctx;

   protected IResource buildComponent() {
      IResource resource = null;

      resource = m_tag.build();
      m_tag.setState(State.BUILT);

      return resource;
   }

   protected ITag createTag() {
      return new ResourceTag();
   }

   protected JspTagEnv createTagEnv() {
      return new JspTagEnv();
   }

   @Override
   public int doAfterBody() throws JspException {
      m_hasBody = true;
      handleBody();

      return SKIP_BODY;
   }

   @Override
   public int doEndTag() throws JspException {
      if (!m_hasBody) {
         handleBody();
      }

      m_hasBody = false;

      m_tag.end();
      m_tag.setState(State.ENDED);

      resetTag();
      int result = EVAL_PAGE;

      //Add Statistcs for JSP tags
      if (m_ctx.isStatisticsEnabled()) {
         Statistics statistcs = m_ctx.getStatistics();
         statistcs.addItem(getClass(), JavaHighResTimer.endVal(m_startingTime));
      }
      m_ctx = null;
      
      return result;
   }

   @Override
   public int doStartTag() throws JspException {
      m_startingTime = JavaHighResTimer.begin();
      m_ctx = ResourceRuntimeContext.ctx();

      JspTagEnv env = createTagEnv();

      if (m_tag == null) {
         m_tag = createTag();
      }

      initTagEnv(env, m_tag);

      m_tag.setEnv(env);

      m_tag.start();
      m_tag.setState(State.STARTED);

      return super.doStartTag();
   }

   private void ensureTag() {
      if (m_tag == null) {
         m_tag = createTag();
      }
   }

   protected void flushBuffer() {
      ITagEnv env = m_tag.getEnv();

      try {
         write(env.getError());
         write(env.getOutput());
      } catch (IOException e) {
         env.onError("Error when flushing buffer!", e);
      }
   }

   protected ITagModel getModel() {
      ensureTag();
      return m_tag.getModel();
   }

   protected ITag getTag() {
      return m_tag;
   }

   protected void handleBody() throws JspTagException {
      if (bodyContent != null) {
         String content = bodyContent.getString();

         if (content != null) {
            m_tag.getModel().setContent(content);
         }
      }

      IResource resource = buildComponent();
      if (resource != null) {
         renderComponent(resource);
      }

      flushBuffer();
   }

   protected void initTagEnv(JspTagEnv env, ITag tag) {
      env.setPageContext(pageContext);
   }

   protected void renderComponent(IResource resource) {
      String value = m_tag.render(resource);
      if (value != null && !value.isEmpty()) {
         m_tag.getEnv().out(value);
      }
      m_tag.setState(State.RENDERED);
   }

   protected void resetTag() {
      m_tag = null;
   }

   public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
      ensureTag();
      m_tag.getModel().setAttribute(localName, value);
   }

   public void setValue(Object value) {
      getModel().setValue(value);
   }

   public void setSecure(Boolean secure) {
      getModel().setAttribute("secure", secure);
   }

   protected void write(String data) throws IOException {
      if (data != null && data.length() > 0) {
         final JspWriter out = pageContext.getOut();

         if (bodyContent != null && out instanceof BodyContent) {
            ((BodyContent) out).getEnclosingWriter().write(data);
         } else {
            out.write(data);
         }
      }
   }
}
