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

package com.ebayopensource.webrex.resource.cache;

import java.util.Locale;

import junit.framework.Assert;

import org.junit.Test;

import com.ebayopensource.webrex.resource.ResourceLocale;
import com.ebayopensource.webrex.resource.ResourceUrn;
import com.ebayopensource.webrex.resource.api.IResourceLocale;
import com.ebayopensource.webrex.resource.api.IResourceUrn;

public class ResourceCacheKeyTest {

   
   @Test
   public void testConstructor() {
      IResourceUrn urn = new ResourceUrn("js", "local", "/js/sample/sample.js");
      IResourceLocale locale = new ResourceLocale(new Locale("en", "US"));
      ResourceCacheKey cacheKey = new ResourceCacheKey(urn, locale);
      Assert.assertTrue(cacheKey.hashCode() != 31);
   }
   
   @Test
   public void testEquals() {
      IResourceUrn urn = new ResourceUrn("js", "local", "/js/sample/sample.js");
      IResourceUrn urn1 = new ResourceUrn("js", "local", "/js/sample/sample1.js");
      IResourceLocale locale = new ResourceLocale(new Locale("en", "US"));
      IResourceLocale locale1 = new ResourceLocale(new Locale("de", "DE"));
      ResourceCacheKey cacheKey = new ResourceCacheKey(urn, locale);
      Assert.assertTrue(cacheKey.equals(cacheKey));
      Assert.assertFalse(cacheKey.equals(null));
      Assert.assertFalse(cacheKey.equals(new Object()));
      ResourceCacheKey cacheKey1 = new ResourceCacheKey(urn, null);
      Assert.assertFalse(cacheKey1.equals(cacheKey));
      ResourceCacheKey cacheKey2 = new ResourceCacheKey(urn, locale1);
      Assert.assertFalse(cacheKey2.equals(cacheKey));
      ResourceCacheKey cacheKey3 = new ResourceCacheKey(null, locale);
      Assert.assertFalse(cacheKey3.equals(cacheKey));
      ResourceCacheKey cacheKey4 = new ResourceCacheKey(urn1, locale);
      Assert.assertFalse(cacheKey4.equals(cacheKey));
      ResourceCacheKey cacheKey5 = new ResourceCacheKey(urn, locale);
      Assert.assertTrue(cacheKey5.equals(cacheKey));
   }
}
