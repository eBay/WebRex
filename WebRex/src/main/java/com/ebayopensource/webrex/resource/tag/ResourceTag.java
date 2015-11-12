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

package com.ebayopensource.webrex.resource.tag;



import com.ebayopensource.webrex.resource.ResourceErrConstants;
import com.ebayopensource.webrex.resource.ResourceException;
import com.ebayopensource.webrex.resource.ResourceFactory;
import com.ebayopensource.webrex.resource.ResourceModel;
import com.ebayopensource.webrex.resource.ResourceRuntimeContext;
import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.expression.ResourceExpression;
import com.ebayopensource.webrex.resource.spi.IResourceRegistry;
import com.ebayopensource.webrex.resource.spi.IResourceTagRenderer;

public class ResourceTag implements ITag {
   private State m_state;

   private ITagModel m_model;

   private ITagEnv m_env;

   public ResourceTag() {
      this(new ResourceTagModel());
   }

   public ResourceTag(ITagModel model) {
      m_model = model;
      m_state = State.CREATED;
   }

   @Override
   public IResource build() {
      try {
         return getResource(m_model.getValue());
      } catch (Exception e) {
         ResourceRuntimeContext ctx = ResourceRuntimeContext.ctx();
         ResourceException re = new ResourceException(ResourceErrConstants.TAG_BUILD_ERR, String.format(
               "Error build tag(%s) with value(%s)!", getClass(), m_model.getValue()), e);
         String result = (String) ctx.getConfig().getRegistry().getErrorHandler().handle(re, m_model);
         if (result != null) {
            error(result);
         }
         return null;
      }
   }

   @Override
   public void end() {
   }

   protected void error(String message, Object... args) {
      ITagEnv env = getEnv();
      String error = getErrMsg(message, args);
      env.err(error);
   }

   @Override
   public ITagEnv getEnv() {
      return m_env;
   }

   protected String getErrMsg(String message, Object... args) {
      Object showErrorContext = m_env.getProperty("jsp.showErrorContext");

      String error;
      if (Boolean.TRUE.equals(showErrorContext)) {
         int len = args.length;
         Object[] params = new Object[len + 1];

         System.arraycopy(args, 0, params, 0, len);
         params[len] = toString();

         error = String.format("<!-- " + message + " Tag: %s -->", params);
      } else {
         error = String.format("<!-- " + message + " -->", args);
      }
      return error;
   }

   @Override
   public ITagModel getModel() {
      return m_model;
   }

   protected IResourceTagRenderer getRenderer(ResourceTag tag, IResource resource) {
      IResourceRegistry registry = ResourceRuntimeContext.ctx().getConfig().getRegistry();

      String type = tag.getModel().getExpectedResourceType();
      if (type == null) {
         type = resource.getUrn().getType();
      }
      return (IResourceTagRenderer) registry.getTagRenderer(type);
   }

   protected IResource getResource(Object value) {
      if (value == null) {
         throw new RuntimeException(String.format("The attribute(value) of %s is null.", getClass().getSimpleName()));
      }

      IResource resource = null;
      if (value instanceof ResourceExpression) {
         resource = (IResource) ((ResourceExpression) value).evaluate();
      } else if (value instanceof IResource) {
         resource = (IResource) value;
      } else if (value instanceof String) {
            //literal path support
            resource = ResourceFactory.createResource((String) value);

      } else {
         throw new RuntimeException(String.format("Unsupported value(%s) of %s. ", value, getClass().getSimpleName()));
      }

      if (resource != null) {
         String resType = resource.getUrn().getType();
         String tagResType = getModel().getExpectedResourceType();
         if (tagResType!= null && !resType.equals(tagResType)) {
            throw new RuntimeException(String.format("Resource type doesn't match, can't render %s resource in %s",
                  resType, getClass().getSimpleName()));
         }
      }
      return resource;
   }

   @Override
   public State getState() {
      return m_state;
   }

   protected void out(Object obj) {
      ITagEnv env = getEnv();

      env.out(obj);
   }

   @Override
   public String render(IResource resource) {
      if (resource != null) {
         ResourceRuntimeContext ctx = ResourceRuntimeContext.ctx();
         try {
            IResourceTagRenderer renderer = getRenderer(this, resource);
            String result = renderer.render(this, resource);

            //log the rendered resource, for dedup
            ResourceModel model = ctx.getResourceAggregator().getModel();
            model.setRenderedResource(resource);

            return result;
         } catch (Throwable e) {
            ResourceException re = new ResourceException(ResourceErrConstants.TAG_RENDER_ERR, String.format(
                  "Error render tag(%s) with resource(%s)!", getClass(), resource.getUrn()), e);
            error((String) ctx.getConfig().getRegistry().getErrorHandler().handle(re, resource.getUrn()));
         }
      }

      return null;
   }

   @Override
   public void setEnv(ITagEnv env) {
      m_env = env;
   }

   @Override
   public void setState(State newState) {
      if (m_state.canTransit(newState)) {
         m_state = newState;
      } else {
         throw new RuntimeException(String.format("Can't transit state from(%s) to (%s) in tag(%s)!", m_state,
               newState, this));
      }
   }

   @Override
   public void start() {
   }

}
