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



import com.ebayopensource.webrex.resource.tag.ITag;
import com.ebayopensource.webrex.resource.tag.TokenTag;


public class TokenTagHandler extends ResourceTagHandler{


	private static final long serialVersionUID = 570130206789868183L;

	@Override
	protected ITag createTag() {
		TokenTag tag = new TokenTag();
		return tag;
	}
	
	 public void setType(String type) {
	      getModel().setAttribute("type", type);
	   }
}
