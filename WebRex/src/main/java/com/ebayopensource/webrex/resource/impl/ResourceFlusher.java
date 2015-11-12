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

import java.io.IOException;

import com.ebayopensource.webrex.resource.ResourceRuntimeContext;

public enum ResourceFlusher {
   INSTANCE;

   public void process(StringBuilder content, String contentType) throws IOException {
      if (ResourceRuntimeContext.isInitialized()) {
         //process model
         ResourceRuntimeContext.ctx().getModel().processModel();

         ResourceModelFlusher flusher = new ResourceModelFlusher();
         String before = flusher.beforeFlush();

         if (before != null && before.length() > 0) {
            content.insert(0, before);
         }

         ResourceMarkerProcessor.INSTANCE.process(content, contentType);

         String after = flusher.afterFlush();
         if (after != null && after.length() > 0) {
            content.append(after);
         }
      }
   }
}
