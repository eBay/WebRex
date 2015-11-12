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

package com.ebayopensource.webrex.resource.spi;

import java.io.File;
import java.net.URL;

public interface ILibraryVersionProvider {
   public VersionID resolveLibrary(URL url);

   public VersionID resolveWeb(File warRoot);

   public static class VersionID {
      private String m_version;

      private String m_id;

      public VersionID(String version, String id) {
         super();
         m_version = version;
         m_id = id;
      }

      public String getId() {
         return m_id;
      }

      public String getVersion() {
         return m_version;
      }
   }
}
