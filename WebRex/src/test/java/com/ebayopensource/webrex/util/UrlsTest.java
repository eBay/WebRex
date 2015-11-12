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

package com.ebayopensource.webrex.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;

public class UrlsTest {

   @Test
   public void testUrlsFunctions() throws IOException {
      
      //just invoke
      URL url = (new File("warRoot/WEB-INF/lib/SharedResourceTest-0.0.1-SNAPSHOT.jar")).toURI().toURL();
      Assert.assertTrue(Urls.getLength(url) != 0);
      
      //for Exception
      URL exceptionUrl = new URL("jar", null, url.toExternalForm());
      Assert.assertTrue(Urls.getLastModified(exceptionUrl) == 0);
      Assert.assertTrue(Urls.getLength(exceptionUrl) == 0);
   }
}
