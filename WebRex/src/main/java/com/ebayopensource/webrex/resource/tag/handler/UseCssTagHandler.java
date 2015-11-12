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

import com.ebayopensource.webrex.resource.ResourceTypeConstants;
import com.ebayopensource.webrex.resource.tag.ITag;
import com.ebayopensource.webrex.resource.tag.UseScriptTag;

public class UseCssTagHandler extends ResourceTagHandler {
   private static final long serialVersionUID = -2451921818419481206L;

   @Override
   protected ITag createTag() {
      UseScriptTag tag = new UseScriptTag(ResourceTypeConstants.CSS);
      return tag;
   }

   public void setTarget(String slotId) {
      getModel().setAttribute("target", slotId);
   }

}
