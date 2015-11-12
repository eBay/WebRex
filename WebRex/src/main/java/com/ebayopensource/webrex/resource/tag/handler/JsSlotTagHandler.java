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
import com.ebayopensource.webrex.resource.tag.SlotTag;

public class JsSlotTagHandler extends ResourceTagHandler {
   private static final long serialVersionUID = -8092219398608717125L;

   protected ITag createTag() {
      SlotTag tag = new SlotTag(ResourceTypeConstants.JS);
      tag.setSlotType(ResourceTypeConstants.JS);
      return tag;
   }

   public void setId(String id) {
      getModel().setAttribute("id", id);
   }
}
