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

import com.ebayopensource.webrex.resource.ResourceRuntimeContext;
import com.ebayopensource.webrex.resource.impl.ResourceDeferProcessor.ResourceMarkerHandler;
import com.ebayopensource.webrex.resource.spi.IDeferProcessor;
import com.ebayopensource.webrex.resource.spi.IResourceRegistry;

public enum ResourceMarkerProcessor {
   INSTANCE;

   public void process(StringBuilder content, String contentType) {
      ResourceRuntimeContext ctx = ResourceRuntimeContext.ctx();

      IResourceRegistry registry = ctx.getConfig().getRegistry();
      IDeferProcessor processor = registry.getDeferProcessor();

      if (processor != null) {
         //TODO: temp fix for json issue
         if (contentType != null && contentType.indexOf("json") != -1
               && processor.getClass() == ResourceDeferProcessor.class) {
            processor = new JsonResourceDeferProcessor();
         }

         processor.process(content);
      }
   }

   private static class JsonResourceDeferProcessor extends ResourceDeferProcessor {
      @Override
      public IMarkerHandler getMarkerHandker() {
         return new JsonResourceMarkerHandler();
      }
   }

   protected static class JsonResourceMarkerHandler extends ResourceMarkerHandler {
      private String string2Json(String s) {
         StringBuilder sb = new StringBuilder(s.length() + 10);

         // escaping special characters.
         for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
            case '\"':
               sb.append("\\\"");
               break;
            case '\\':
               sb.append("\\\\");
               break;
            case '/':
               sb.append("\\/");
               break;
            default:
               sb.append(c);
            }
         }

         return sb.toString();
      }

      @Override
      public String translateMarker(String marker) {
         String result = super.translateMarker(marker);
         if(result == null){
            return null;
         }
         return string2Json(result);
      }
   }
}
