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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.JarFile;

public class Urls {

   public static byte[] getBinaryContent(URL url) {
      InputStream is = null;
      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream(16 * 1024);
         is = url.openStream();

         byte[] buffer = new byte[4098];
         int pos = 0;

         while ((pos = is.read(buffer)) != -1) {
            baos.write(buffer, 0, pos);
         }
         return baos.toByteArray();
      } catch (IOException e) {
         return null;
      } finally {
         if (is != null) {
            try {
               is.close();
            } catch (IOException e) {
               // ignore it
            }
         }
      }
   }

   public static String getContent(URL url) {
      InputStream is = null;
      try {
         is = url.openStream();

         StringBuilder sb = new StringBuilder(1024);
         byte[] buffer = new byte[4098];
         int pos = 0;

         while ((pos = is.read(buffer)) != -1) {
            sb.append(new String(buffer, 0, pos, "utf-8"));
         }
         return sb.toString();
      } catch (IOException e) {
         return null;
      } finally {
         if (is != null) {
            try {
               is.close();
            } catch (IOException e) {
               // ignore it
            }
         }
      }
   }

   public static long getLastModified(URL url) {
      try {
         URLConnection conn = url.openConnection();
         long lastModified = conn.getLastModified();
         if (conn instanceof HttpURLConnection) {
            ((HttpURLConnection) conn).disconnect();
         } else if (conn instanceof JarURLConnection) {
            JarFile file = ((JarURLConnection) conn).getJarFile();
            if (file != null) {
               file.close();
            }
         }
         return lastModified;
      } catch (IOException e) {
         return 0;
      }
   }

   public static long getLength(URL url) {
      try {
         URLConnection conn = url.openConnection();
         int length = conn.getContentLength();
         if (conn instanceof HttpURLConnection) {
            ((HttpURLConnection) conn).disconnect();
         } else if (conn instanceof JarURLConnection) {
            JarFile file = ((JarURLConnection) conn).getJarFile();
            if (file != null) {
               file.close();
            }
         }
         return length;
      } catch (IOException e) {
         return 0;
      }
   }
}
