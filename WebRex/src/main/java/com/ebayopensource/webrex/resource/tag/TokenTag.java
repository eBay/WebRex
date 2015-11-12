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

import com.ebayopensource.webrex.resource.ResourceRuntimeContext;
import com.ebayopensource.webrex.resource.api.IDeferRenderable;
import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.impl.DummyResource;
import com.ebayopensource.webrex.util.Markers;

public class TokenTag extends ResourceTag {
   public static final String TOKEN_TAG = "TokenTag:";

   private String m_tokenType;

   @Override
   public IResource build() {
      m_tokenType = super.getModel().getAttributes().get("type").toString();

      // get tokenResource of certain type from ResourceModel
      ResourceRuntimeContext ctx = ResourceRuntimeContext.ctx();
      ctx.getModel().enableCollectResources(m_tokenType, true);
      return DummyResource.DUMMY;
   }

   private String createKey() {
      return TOKEN_TAG + m_tokenType + ":" + hashCode();
   }

   @Override
   public String render(IResource resource) {
      ResourceRuntimeContext ctx = ResourceRuntimeContext.ctx();

      String key = createKey();
      String marker = Markers.forDefer().build(key);
      ctx.registerDeferRenderable(key, new DeferRenderable(m_tokenType));
      return marker;
   }

   private class DeferRenderable implements IDeferRenderable {
      private String m_resType;

      private DeferRenderable(String type) {
         m_resType = type;
      }

      public String deferRender() {
         return ResourceRuntimeContext.ctx().getDeDupToken(m_resType);
      }

   }

}
