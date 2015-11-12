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

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class FlashAnalyzer {
   static final int UNCOMP_HDR_LEN = 8; // portion of header that is never compressed

   private boolean m_compressed;

   private int m_version;

   private long m_size;

   private int m_width;

   private int m_height;

   private float m_fps;

   private int m_frameCount;

   private InputStream m_flashStream = null;

   public boolean check() throws IOException {
      if (m_flashStream == null) {
         return false;
      }

      byte[] bytes = new byte[128]; // should be enough...

      if (m_flashStream.read(bytes) < bytes.length) {
         return false; // too few bytes to be a SWF
      } else if (!(bytes[0] == 'F' || bytes[0] == 'C') || bytes[1] != 'W' || bytes[2] != 'S') {
         return false; // not a SWF
      }

      bytes = expand(bytes, UNCOMP_HDR_LEN); // compressed SWF

      if (bytes.length == 0) {
         return false;
      }

      m_compressed = bytes[0] == 'C';
      m_version = bytes[3];
      m_size = bytes[4] & 0xFF | (bytes[5] & 0xFF) << 8 | (bytes[6] & 0xFF) << 16 | bytes[7] << 24;

      BitReader rdr = new BitReader(bytes, UNCOMP_HDR_LEN);

      int[] dims = decodeRect(rdr);
      m_width = (dims[1] - dims[0]) / 20; // convert twips to pixels
      m_height = (dims[3] - dims[2]) / 20;

      m_fps = (float) rdr.uI16() / 256f; // 8.8 fixed-point format
      m_frameCount = rdr.uI16();

      return true;
   }

   /**
    * Return Stage frame rectangle as 4 <code>int</code>s: LRTB
    *
    * Note the values are in TWIPS (= 1/20th of a pixel)
    *
    * I do this to avoid a loading the <code>Rect</code> class which is an
    * <code>android.graphics</code> class, and not available if you want to
    * test this with desktop Java.
    *
    * @param rdr
    * @return
    */
   private int[] decodeRect(BitReader rdr) {
      int[] dims = new int[4];
      int nBits = rdr.uBits(5);

      dims[0] = rdr.sBits(nBits); // X min = left     always 0
      dims[1] = rdr.sBits(nBits); // X max = right
      dims[2] = rdr.sBits(nBits); // Y min = top      always 0
      dims[3] = rdr.sBits(nBits); // Y max = bottom

      return dims;
   }

   /*
    * All of the file past the initial {@link UNCOMP_HDR_LEN} bytes are compressed.
    * Decompress as much as is in the buffer already read and return them,
    * overlaying the original uncompressed data.
    *
    * Fortunately, the compression algorithm used by Flash is the ZLIB standard,
    * i.e., the same algorithms used to compress .jar files
    */
   private byte[] expand(byte[] bytes, int skip) {
      byte[] newBytes = new byte[bytes.length - skip];
      Inflater inflater = new Inflater();

      inflater.setInput(bytes, skip, newBytes.length);
      try {
         int outCount = inflater.inflate(newBytes);
         System.arraycopy(newBytes, 0, bytes, skip, outCount);
         Arrays.fill(bytes, skip + outCount, bytes.length, (byte) 0);
         return bytes;
      } catch (DataFormatException e) {
         return new byte[]{};
      }
   }

   public InputStream getFlashStream() {
      return m_flashStream;
   }

   public float getFps() {
      return m_fps;
   }

   public int getFrameCount() {
      return m_frameCount;
   }

   public int getHeight() {
      return m_height;
   }

   public long getSize() {
      return m_size;
   }

   public int getVersion() {
      return m_version;
   }

   public int getWidth() {
      return m_width;
   }

   public boolean isCompressed() {
      return m_compressed;
   }

   public void setInput(InputStream inputStream) {
      m_flashStream = inputStream;
   }

   /**
    * This can be run from a desktop command line sitting at the .../bin directory as:
    *
    * java resnbl.android.swfview.SWFInfo swf_file
    *
    * @param args path to swf_file to parse
    */
   //commented out to prevent Eclipse from thinkg this is a standard Java app when used for Android!
   // public static void main(String[] args)
   // {
   //     if (args.length == 0)
   //         throw new IllegalArgumentException("No swf_file parameter given");
   //
   //     File file = new File(args[0]);
   //     SWFInfo info = SWFInfo.getInfo(file);
   //
   //     if (info != null)
   //     {
   //         System.out.println("File: " + file);
   //         System.out.println("Flash ver: " + info.version + " FPS: " + info.fps + " Frames: " + info.frameCount);
   //         System.out.println("File size: " + file.length() + " Compressed: " + info.isCompressed + " Uncompressed size: " + info.size);
   //         System.out.println("Dimensions: " + info.width + "x" + info.height);
   //     }
   //     else
   //         System.out.println("File not a .SWF: " + file);
   // }

   /**
    * Read an arbitrary number of bits from a byte[].
    *
    * This should be turned into a full-featured independant class (someday...).
    */
   static class BitReader {
      private byte[] bytes;

      private int byteIdx;

      private int bitIdx = 0;

      /**
       * Start reading from the beginning of the supplied array.
       * @param bytes byte[] to process
       */
      public BitReader(byte[] bytes) {
         this(bytes, 0);
      }

      /**
       * Start reading from an arbitrary index into the array.
       * @param bytes         byte[] to process
       * @param startIndex    byte # to start at
       */
      public BitReader(byte[] bytes, int startIndex) {
         this.bytes = bytes;
         byteIdx = startIndex;
      }

      // Get the next bit in the array
      private int getBit() {
         int value = (bytes[byteIdx] >> (7 - bitIdx)) & 0x01;

         if (++bitIdx == 8) {
            bitIdx = 0;
            ++byteIdx;
         }

         return value;
      }

      /**
       * Fetch the next <code>bitCount</code> bits as a <em>signed</em> int.
       * @param bitCount  # bits to read
       * @return int
       */
      public int sBits(int bitCount) {
         // First bit is the "sign" bit
         int value = getBit() == 0 ? 0 : -1;
         --bitCount;

         while (--bitCount >= 0){
            value = value << 1 | getBit();
         }
         return value;
      }

      /**
       * Bump indexes to the next byte boundary.
       */
      public void sync() {
         if (bitIdx > 0) {
            ++byteIdx;
            bitIdx = 0;
         }
      }

      /**
       * Fetch the next <code>bitCount</code> bits as an unsigned int.
       * @param bitCount  # bits to read
       * @return int
       */
      public int uBits(int bitCount) {
         int value = 0;

         while (--bitCount >= 0){
            value = value << 1 | getBit();
         }
         return value;
      }

      /**
       * Fetch the next 2 "whole" bytes as an unsigned int (little-endian).
       * @return  int
       */
      public int uI16() {
         sync(); // back to "byte-aligned" mode
         return (bytes[byteIdx++] & 0xff) | (bytes[byteIdx++] & 0xff) << 8;
      }
   }
}
