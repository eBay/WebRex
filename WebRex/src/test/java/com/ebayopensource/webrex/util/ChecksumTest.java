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

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

public class ChecksumTest {
   private static final String[] SAMPLE_DATA = { "DDasdfa", "DDasdfa123124", "\u0000\u0001" };

   @Test
   public void testChecksum() {
      for (String test : SAMPLE_DATA) {
         byte[] src = test.getBytes();
         String encoded = Checksum.encode(src);
         byte[] decoded = Checksum.decode(encoded);

         Assert.assertTrue(String.format("String(%s) decoded result not equals!", src), Arrays.equals(src, decoded));
      }

      for (String test : SAMPLE_DATA) {
         byte[] src = test.getBytes();
         String ck = Checksum.checksum(src);

         Assert.assertTrue(String.format("String(%s) checksum validation falied!", ck), Checksum.validateChecksum(ck));
      }

      for (int i = 0; i < 10000; ++i) {
         int size = (int) (Math.random() * 1000);
         byte[] buf = new byte[size];
         for (int j = 0; j < size; ++j) {
            buf[j] = (byte) (Math.random() * 256);
         }

         String ck = Checksum.checksum(buf);

         Assert.assertTrue(String.format("String(%s) checksum validation falied!", ck), Checksum.validateChecksum(ck));
      }
      Assert.assertTrue(Checksum.decode(null).length == 0);
      Assert.assertFalse(Checksum.validateChecksum(null));
      Assert.assertFalse(Checksum.validateChecksum("abc"));
      Assert.assertFalse(Checksum.validateChecksum("abcabcabcabcabcabcabcabcab!"));
      Assert.assertFalse(Checksum.validateChecksum("abcabcabcabcabcabcabcabcabc"));
   }
}
