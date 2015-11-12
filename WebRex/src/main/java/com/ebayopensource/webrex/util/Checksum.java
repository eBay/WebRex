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

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

/**
 * checksum routines to compute checksum for binary data.
 * 
 * the checksum is based on UUID variant 2, version 3. the strength of the check sum
 * is very close to md5 hash. (6 bits less). additional logic are added to the 
 * UUID algorithm to 
 *   - convert to a custom base 32 string to allow safe embedding checksum into an URL
 *   - 5 bits of additional integrity information based on Java string hashCode()
 * 
 * The last two chars of the checksum only has 256 variations. This allows it to be 
 * used as a directory name.
 * 
 */
public class Checksum {

   /**
    * compute the checksum of byte array
    * 
    * @param data
    * @return
    */
   public static String checksum(byte[] data) {
      UUID uuid = UUID.nameUUIDFromBytes(data);

      ByteBuffer buffer = ByteBuffer.allocate(16);
      buffer.putLong(uuid.getMostSignificantBits());
      buffer.putLong(uuid.getLeastSignificantBits());
      String encoded = encode(buffer.array());
      return encoded + hash(encoded);
   }

   /**
    * internal method to compute 5 bits hash code from a string
    * 
    * @param enc
    * @return
    */
   private static char hash(String enc) {
      int index = enc.hashCode() % 32;
      if (index < 0) {
         return alphabet[-index];
      } else {
         return alphabet[index];
      }
   }

   /**
    * compute the checksum of an input string
    * 
    * @param in
    * @return
    */
   public static String checksum(String in) {
      try {
         return checksum(in.getBytes("UTF-8"));
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException(e);
      }
   }

   /**
    * custom base 32 encoder. the encoder is different from standard 
    * base 32 encoder such that
    *   - all letters are in lower case
    *   - use number 0-6, as oppose to 2-8
    * 
    * @param data
    * @return
    */
   protected static String encode(byte[] data) {
      int index = 0;
      int digit = 0;
      int currByte;
      int nextByte;

      StringBuilder base32 = new StringBuilder((data.length * 8) / 5 + 1);
      int count = data.length;

      int i = 0;
      while (i < count) {
         currByte = (data[i] >= 0) ? data[i] : (data[i] + 256);

         if (index > 3) {
            if ((i + 1) < data.length) {
               nextByte = (data[i + 1] >= 0) ? data[i + 1] : (data[i + 1] + 256);
            } else {
               nextByte = 0;
            }

            digit = currByte & (0xFF >> index);
            index = (index + 5) % 8;
            digit <<= index;
            digit |= nextByte >> (8 - index);
            i++;
         } else {
            digit = (currByte >> (8 - (index + 5))) & 0x1F;
            index = (index + 5) % 8;
            if (index == 0) {
               i++;
            }
         }
         base32.append(alphabet[digit]);
      }

      return base32.toString();
   }

   /**
    * custom base 32 decoder
    * 
    * @param base32
    * @return
    */
   protected static byte[] decode(String base32) {
      if (base32 == null) {
         return new byte[] {};
      }

      int i;
      int index;
      int offset;
      int digit;
      byte[] bytes = new byte[base32.length() * 5 / 8];

      for (i = 0, index = 0, offset = 0; i < base32.length(); i++) {
         digit = reverseMap[base32.charAt(i)];

         /* If this digit is not in the table, ignore it */
         if ((digit & 0xff) == 0xFF) {
            continue;
         }

         if (index <= 3) {
            index = (index + 5) % 8;
            if (index == 0) {
               bytes[offset] |= digit;
               offset++;
               if (offset >= bytes.length) {
                  break;
               }
            } else {
               bytes[offset] |= digit << (8 - index);
            }
         } else {
            index = (index + 5) % 8;
            bytes[offset] |= (digit >>> index);
            offset++;

            if (offset >= bytes.length) {
               break;
            }

            bytes[offset] |= digit << (8 - index);
         }
      }

      return bytes;
   }

   /**
    * check if a checksum is valid 
    * 
    * @param in
    * @return
    */
   public static boolean validateChecksum(String encoded) {
      if (encoded == null) {
         return false;
      }

      if (encoded.length() != 27) {
         return false;
      }

      for (int i = 0; i < encoded.length(); ++i) {
         char c = encoded.charAt(i);
         if (c < '0' || c > 'z') {
            return false;
         }

         if ((reverseMap[c] & 0xff) == 0xFF) {
            return false;
         }
      }

      char lastChar = encoded.charAt(26);
      String checksum = encoded.substring(0, 26);

      byte[] b = decode(checksum);
      if (b.length != 16) {
         return false;
      }

      ByteBuffer bf = ByteBuffer.allocate(16);
      bf.put(b);
      bf.rewind();
      UUID uuid = new UUID(bf.getLong(), bf.getLong());
      if (uuid.variant() != 2 || uuid.version() != 3) {
         return false;
      }

      if (lastChar != hash(checksum)) {
         return false;
      }

      return true;
   }

   //
   // coding characters
   //
   final private static char[] alphabet = new char[32];

   final private static byte[] reverseMap = new byte[128];
   static {
      Arrays.fill(reverseMap, (byte) 0xFF);
      for (int i = 'a'; i <= 'z'; i++) {
         alphabet[i - 'a'] = (char) i;
         reverseMap[i] = (byte) (i - 'a');
      }

      for (int i = 0; i < 6; ++i) {
         alphabet[26 + i] = (char) (i + '0');
         reverseMap[i + '0'] = (byte) (26 + i);
      }
   }

   //   public static String md5checksum(byte[] bin) {
   //      MessageDigest digest;
   //      try {
   //         digest = MessageDigest.getInstance("MD5");
   //         digest.update(bin);
   //         byte[] md5 = digest.digest();
   //         StringBuilder builder = new StringBuilder();
   //         for (int i = 0; i < md5.length; i++) {
   //            builder.append(Integer.toHexString(0xFF & md5[i]));
   //         }
   //         return builder.toString();
   //      } catch (NoSuchAlgorithmException e) {
   //         return null;
   //      }
   //
   //   }

   /*
   private static final String[] TESTS = {
   	"DDasdfa",
   	"DDasdfa123124",
   	"\u0000\u0001"
   };


   public static void main(String[] args) {
   	for(String test: TESTS){
   		byte[] src = test.getBytes();
   		String encoded = encode(src);
   		byte[] decoded = decode(encoded);
   		System.out.println("testing... " + test + " -> " + (Arrays.equals(src, decoded)?"pass":"fail"));
   	}
   	
   	for(String test: TESTS){
   		byte[] src = test.getBytes();
   		String ck = checksum(src);
   		System.out.println("testing validation for " + Arrays.toString(src) + " "+ validateChecksum(ck));
   	}
   	
   	Set<String> lastTwo = new HashSet<String>();
   	for(int i =0; i<10000; ++i){
   		int size = (int)(Math.random() * 1000);
   		byte[] buf = new byte[size];
   		for(int j=0; j<size; ++j){
   			buf[j] = (byte)(Math.random() * 256);
   		}
   		String ck = checksum(buf);
   		
   		String a  = ck.substring(25);
   		lastTwo.add(a);
   		
   		System.out.println(a);
   		System.out.println("testing validation " + ck + " "+ validateChecksum(ck));
   	}
   	
   	System.out.println(lastTwo.size());
   	
   }
   */
}
