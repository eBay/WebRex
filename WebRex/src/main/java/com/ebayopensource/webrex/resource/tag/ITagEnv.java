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

import java.io.IOException;

public interface ITagEnv {
   public ITagEnv err(Object obj);

   public Object findAttribute(String name);

   public void flush() throws IOException;

   public String getError();

   public String getOutput();

   public TagOutputType getOutputType();

   public Object getPageAttribute(String name);

   public Object getProperty(String name);

   public Object getRequestAttribute(String name);

   public void onError(String message, Throwable cause);

   public ITagEnv out(Object obj);

   public void removePageAttribute(String name);

   public void setOutputType(TagOutputType type);

   public void setPageAttribute(String name, Object value);

   public void setProperty(String name, Object value);

   public void setRequestAttribute(String name, Object value);

   public enum TagOutputType {
      html, xhtml
   }
}
