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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import com.ebayopensource.webrex.logging.ILogger;
import com.ebayopensource.webrex.logging.LogLevel;
import com.ebayopensource.webrex.logging.LoggerFactory;
import com.ebayopensource.webrex.resource.spi.ILibraryVersionProvider;

/**
 * Default impl for resource id, version provider based on OSGI Manifest header
 *
 */
public class LibraryVersionProvider implements ILibraryVersionProvider {
   private static final String MANIFEST_FILE = "META-INF/MANIFEST.MF";

   private static final String BUNDLE_SYMBOLIC_NAME = "Bundle-SymbolicName:";

   private static final String BUNDLE_VERSION = "Bundle-Version:";

   private static ILogger s_logger = LoggerFactory.getLogger(LibraryVersionProvider.class);

   private String m_libPath;

   private String m_nameKey;

   private String m_versionKey;

   public LibraryVersionProvider() {
      this(null, null, null);
   }

   public LibraryVersionProvider(String libPath, String namekey, String versionKey) {
      m_libPath = libPath;
      m_nameKey = namekey;
      m_versionKey = versionKey;

      //set default value
      if (m_libPath == null) {
         m_libPath = MANIFEST_FILE;
      }

      if (m_nameKey == null) {
         m_nameKey = BUNDLE_SYMBOLIC_NAME;
      }

      if (m_versionKey == null) {
         m_versionKey = BUNDLE_VERSION;
      }
   }

   protected VersionID createVersionID(InputStream in) throws IOException {
      String id = null;
      String version = null;

      LineNumberReader reader = null;
      try {
         reader = new LineNumberReader(new InputStreamReader(in, "UTF-8"));
         String line = reader.readLine();
         while (line != null) {
            if (line.startsWith(m_nameKey)) {
               id = line.substring(m_nameKey.length());
               if (id != null) {
                  id = trimKeyword(id);
               }
            } else if (line.startsWith(m_versionKey)) {
               version = line.substring(m_versionKey.length());
               if (version != null) {
                  version = trimKeyword(version);
               }
            }

            line = reader.readLine();
         }
      } finally {
         if (reader != null) {
            reader.close();
         }

         if (in != null) {
            in.close();
         }
      }

      // id is required
      if (id != null) {
         return new VersionID(version, id);
      }

      return null;
   }

   protected String getLibEntrancePath(String urlStr, String[] endStrs) {
      int startPos = 0;
      if (urlStr.startsWith("file:")) {
         startPos = 5;
      } else if (urlStr.startsWith("jar:file:")) {
         startPos = 9;
      }
      int endPos = -1;
      for (int i = 0; i < endStrs.length && endPos < 0; i++) {
         endPos = urlStr.indexOf('/' + endStrs[i]);
      }
      if (endPos != -1) {
         // support for jar file
         if (endPos > 0 && urlStr.charAt(endPos - 1) == '!') {
            endPos = endPos - 1;
         }
      } else {
         endPos = urlStr.length();
      }

      return urlStr.substring(startPos, endPos);
   }

   @Override
   public VersionID resolveLibrary(URL url) {
      String urlStr = url.toExternalForm();

      try {
         if (urlStr.endsWith(m_libPath)) {
            return createVersionID(url.openStream());
         } else {
            return resolveLibraryFromResoureUrl(url, urlStr);
         }
      } catch (Exception e) {
         s_logger.log(LogLevel.WARN, "Failed to resolve libary info with url(" + url.toExternalForm()
               + ") , exception:" + e.toString());
         return null;
      }
   }

   protected VersionID resolveLibraryFromResoureUrl(URL url, String urlStr) throws FileNotFoundException, IOException {
      InputStream in = null;
      JarFile jarFile = null;
      try {
         boolean isJarFile = false;
         if (urlStr.startsWith("jar:file:/") || urlStr.indexOf('!') != -1) {
            isJarFile = true;
         }

         //libEntrancePath should be jar:file:/foo/bar/xx.jar
         String libEntrancePath = getLibEntrancePath(urlStr, new String[] { "META-INF", "v4contentsource/source" });
         if (isJarFile) {
            jarFile = new JarFile(libEntrancePath);
            ZipEntry entry = jarFile.getEntry(m_libPath);

            if (entry != null) {
               in = jarFile.getInputStream(entry);
               if (in != null) {
                  return createVersionID(in);
               }
            }
         } else {
            return createVersionID(new FileInputStream(new File(libEntrancePath, m_libPath)));
         }
      } finally {
         if (in != null) {
            try {
               in.close();
            } catch (IOException e) {
               // ignore
            }
         }

         if (jarFile != null) {
            try {
               jarFile.close();
            } catch (IOException e) {
               // ignore it
            }
         }
      }

      return null;
   }

   private String trimKeyword(String keyword) {
      String trim = keyword.trim();
      if (trim.startsWith(":") || trim.startsWith("=")) {
         trim = trim.substring(1);
      }
      return trim.trim();
   }

   @Override
   public VersionID resolveWeb(File warRoot) {
      if (warRoot.exists()) {
         try {
            if (warRoot.getPath().endsWith(m_libPath)) {
               return createVersionID(new FileInputStream(warRoot));
            } else {
               return createVersionID(new FileInputStream(new File(warRoot, m_libPath)));
            }
         } catch (Exception e) {
            s_logger.log(LogLevel.WARN,
                  "Failed to resolve libary info with file(" + warRoot + ") , exception:" + e.toString());
         }
      }
      return null;
   }
}
