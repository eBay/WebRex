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

package com.ebayopensource.webrex.resource;

public class ResourceException extends RuntimeException {
   private static final long serialVersionUID = 3003735822975755410L;

   private int m_errCode;

   public ResourceException(int errorCode, String message) {
      super(message);
      m_errCode = errorCode;
   }

   public ResourceException(int errorCode, String message, Throwable cause) {
      super(message, cause);
      m_errCode = errorCode;
   }

   public int getErrCode() {
      return m_errCode;
   }
}
