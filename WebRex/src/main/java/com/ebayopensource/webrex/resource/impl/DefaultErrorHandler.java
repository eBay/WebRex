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

import com.ebayopensource.webrex.logging.ILogger;
import com.ebayopensource.webrex.logging.LogLevel;
import com.ebayopensource.webrex.logging.LoggerFactory;
import com.ebayopensource.webrex.resource.ResourceErrConstants;
import com.ebayopensource.webrex.resource.ResourceErrPolicy;
import com.ebayopensource.webrex.resource.ResourceException;
import com.ebayopensource.webrex.resource.ResourceRuntimeContext;
import com.ebayopensource.webrex.resource.spi.IResourceErrorHandler;

public class DefaultErrorHandler implements IResourceErrorHandler {
   private static ILogger s_logger = LoggerFactory.getLogger(DefaultErrorHandler.class);

   @Override
   public Object handle(ResourceException e, Object value) {
      if (ResourceRuntimeContext.ctx().getConfig().getResourceErrPolicy() == ResourceErrPolicy.FAIL_FAST) {
         throw e;
      } else if (e.getErrCode() == ResourceErrConstants.TAG_BUILD_ERR
            || e.getErrCode() == ResourceErrConstants.TAG_RENDER_ERR) {
         return e.getMessage();
      } else {
         s_logger.log(LogLevel.WARN, e.getMessage());
      }

      return null;
   }

}
