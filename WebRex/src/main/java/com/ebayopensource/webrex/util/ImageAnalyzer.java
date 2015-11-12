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

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;

/**
 * Helper class to process image file to extract its meta data.
 * This class is ported from open source.
 */
public class ImageAnalyzer {

   /**
    * Recognized image formats that can be handled.
    */
   public static final int FORMAT_JPEG = 0;

   public static final int FORMAT_GIF = 1;

   public static final int FORMAT_PNG = 2;

   public static final int FORMAT_BMP = 3;

   public static final int FORMAT_PCX = 4;

   public static final int FORMAT_IFF = 5;

   public static final int FORMAT_RAS = 6;

   public static final int FORMAT_PBM = 7;

   public static final int FORMAT_PGM = 8;

   public static final int FORMAT_PPM = 9;

   public static final int FORMAT_PSD = 10;

   public static final int FORMAT_ICO = 11;

   private static final String[] FORMAT_NAMES = { "JPEG", "GIF", "PNG", "BMP", "PCX", "IFF", "RAS", "PBM", "PGM",
         "PPM", "PSD", "ICO" };

   private static final String[] MIME_TYPE_STRINGS = { "image/jpeg", "image/gif", "image/png", "image/bmp",
         "image/pcx", "image/iff", "image/ras", "image/x-portable-bitmap", "image/x-portable-graymap",
         "image/x-portable-pixmap", "image/psd", "image/vnd.microsoft.icon" };

   private int m_width;

   private int m_height;

   private int m_bitsPerPixel;

   private boolean m_progressive;

   private int m_format;

   private InputStream m_in;

   private DataInput m_din;

   private boolean m_determineNumberOfImages = false;;

   private int m_numberOfImages;

   private int m_physicalHeightDpi;

   private int m_physicalWidthDpi;

   //Only applicable for png image at present
   private int m_alphaBits = 0;

   /**
    * Analyzes the alpha bits of the given input and set the field accordingly.<br>
    * We will use ImageIO API to do the analyze thus we can use the <code>m_in</code> field, 'coz the read pointer of
    * that stread has already been moved in method <code>check()</code>
    * 
    * @param input
    */
   public void analyzeAlphaBits(Object input) {
      if (m_format == FORMAT_PNG) {
         m_alphaBits = PngImageUtils.getTransparencyChannelBitDepth(input);
      } else {
         m_alphaBits = 0;
      }
   }

   /**
    * Call this method after you have provided an input stream or file
    * using {@link #setInput(InputStream)} or {@link #setInput(DataInput)}.
    * If <tt>true</tt> is returned, the file format was known and information
    * on the file's content can be retrieved using the various getXyz methods.
    * 
    * @return <tt.true</tt. if information could be retrieved from input
    */
   public boolean check() {
      m_format = -1;
      m_width = -1;
      m_height = -1;
      m_bitsPerPixel = -1;
      m_numberOfImages = 1;
      m_physicalHeightDpi = -1;
      m_physicalWidthDpi = -1;

      try {
         m_in.mark(1024);
         int b1 = read() & 0xff;
         int b2 = read() & 0xff;
         if (b1 == 0x47 && b2 == 0x49) {
            return checkGif();
         } else if (b1 == 0x89 && b2 == 0x50) {
            return checkPng();
         } else if (b1 == 0xff && b2 == 0xd8) {
            return checkJpeg();
         } else if (b1 == 0x42 && b2 == 0x4d) {
            return checkBmp();
         } else if (b1 == 0x0a && b2 < 0x06) {
            return checkPcx();
         } else if (b1 == 0x46 && b2 == 0x4f) {
            return checkIff();
         } else if (b1 == 0x59 && b2 == 0xa6) {
            return checkRas();
         } else if (b1 == 0x50 && b2 >= 0x31 && b2 <= 0x36) {
            return checkPnm(b2 - '0');
         } else if (b1 == 0x38 && b2 == 0x42) {
            return checkPsd();
         } else if (b1 == 0x00 && b2 == 0x00) {
            return checkIco();
         } else {
            return false;
         }
      } catch (IOException ioe) {
         return false;
      }
   }

   private boolean checkBmp() throws IOException {
      byte[] a = new byte[44];
      if (read(a) != a.length) {
         return false;
      }
      m_width = getIntLittleEndian(a, 16);
      m_height = getIntLittleEndian(a, 20);
      if (m_width < 1 || m_height < 1) {
         return false;
      }
      m_bitsPerPixel = getShortLittleEndian(a, 26);
      if (m_bitsPerPixel != 1 && m_bitsPerPixel != 4 && m_bitsPerPixel != 8 && m_bitsPerPixel != 16
            && m_bitsPerPixel != 24 && m_bitsPerPixel != 32) {
         return false;
      }
      int x = (int) (getIntLittleEndian(a, 36) * 0.0254);
      if (x > 0) {
         setPhysicalWidthDpi(x);
      }
      int y = (int) (getIntLittleEndian(a, 40) * 0.0254);
      if (y > 0) {
         setPhysicalHeightDpi(y);
      }
      m_format = FORMAT_BMP;
      return true;
   }

   private boolean checkGif() throws IOException {
      final byte[] GIF_MAGIC_87A = { 0x46, 0x38, 0x37, 0x61 };
      final byte[] GIF_MAGIC_89A = { 0x46, 0x38, 0x39, 0x61 };
      byte[] a = new byte[11];
      // 4 from the GIF signature + 7 from the global header
      if (read(a) != 11) {
         return false;
      }
      if ((!equals(a, 0, GIF_MAGIC_89A, 0, 4)) && (!equals(a, 0, GIF_MAGIC_87A, 0, 4))) {
         return false;
      }
      m_format = FORMAT_GIF;
      m_width = getShortLittleEndian(a, 4);
      m_height = getShortLittleEndian(a, 6);
      int flags = a[8] & 0xff;
      m_bitsPerPixel = ((flags >> 4) & 0x07) + 1;
      //progressive = (flags & 0x02) != 0;
      if (!m_determineNumberOfImages) {
         return true;
      }
      // skip global color palette
      if ((flags & 0x80) != 0) {
         int tableSize = (1 << ((flags & 7) + 1)) * 3;
         skip(tableSize);
      }
      m_numberOfImages = 0;
      int blockType;
      do {
         blockType = read();
         switch (blockType) {
         case (0x2c): // image separator
            if (read(a, 0, 9) != 9) {
               return false;
            }
            flags = a[8] & 0xff;
            m_progressive = (flags & 0x40) != 0;
            /*int locWidth = getShortLittleEndian(a, 4);
            int locHeight = getShortLittleEndian(a, 6);
            System.out.println("LOCAL: " + locWidth + " x " + locHeight);*/
            int localBitsPerPixel = (flags & 0x07) + 1;
            if (localBitsPerPixel > m_bitsPerPixel) {
               m_bitsPerPixel = localBitsPerPixel;
            }
            if ((flags & 0x80) != 0) {
               skip((1 << localBitsPerPixel) * 3);
            }
            skip(1); // initial code length
            int n;
            do {
               n = read();
               if (n > 0) {
                  skip(n);
               } else if (n == -1) {
                  return false;
               }
            } while (n > 0);
            m_numberOfImages++;
            break;
         case (0x21): // extension
            read();
            int m;
            do {
               m = read();
               if (m > 0) {
                  skip(m);
               } else if (m == -1) {
                  return false;
               }
            } while (m > 0);
            break;
         case (0x3b): // end of file
            break;
         default:
            return false;
         }
      } while (blockType != 0x3b);
      return true;
   }

   private boolean checkIff() throws IOException {
      byte[] a = new byte[10];
      // read remaining 2 bytes of file id, 4 bytes file size 
      // and 4 bytes IFF subformat
      if (read(a, 0, 10) != 10) {
         return false;
      }
      final byte[] IFF_RM = { 0x52, 0x4d };
      if (!equals(a, 0, IFF_RM, 0, 2)) {
         return false;
      }
      int type = getIntBigEndian(a, 6);
      if (type != 0x494c424d && // type must be ILBM...
            type != 0x50424d20) { // ...or PBM
         return false;
      }
      // loop chunks to find BMHD chunk
      do {
         if (read(a, 0, 8) != 8) {
            return false;
         }
         int chunkId = getIntBigEndian(a, 0);
         int size = getIntBigEndian(a, 4);
         if ((size & 1) == 1) {
            size++;
         }
         if (chunkId == 0x424d4844) { // BMHD chunk
            if (read(a, 0, 9) != 9) {
               return false;
            }
            m_format = FORMAT_IFF;
            m_width = getShortBigEndian(a, 0);
            m_height = getShortBigEndian(a, 2);
            m_bitsPerPixel = a[8] & 0xff;
            return (m_width > 0 && m_height > 0 && m_bitsPerPixel > 0 && m_bitsPerPixel < 33);
         } else {
            skip(size);
         }
      } while (true);
   }

   private boolean checkJpeg() throws IOException {
      byte[] data = new byte[12];
      while (true) {
         if (read(data, 0, 4) != 4) {
            return false;
         }
         int marker = getShortBigEndian(data, 0);
         int size = getShortBigEndian(data, 2);
         if ((marker & 0xff00) != 0xff00) {
            return false; // not a valid marker
         }
         if (marker == 0xffe0) { // APPx 
            if (size < 14) {
               return false; // APPx header must be >= 14 bytes
            }
            if (read(data, 0, 12) != 12) {
               return false;
            }
            final byte[] APP0_ID = { 0x4a, 0x46, 0x49, 0x46, 0x00 };
            if (equals(APP0_ID, 0, data, 0, 5)) {
               //System.out.println("data 7=" + data[7]);
               if (data[7] == 1) {
                  setPhysicalWidthDpi(getShortBigEndian(data, 8));
                  setPhysicalHeightDpi(getShortBigEndian(data, 10));
               } else if (data[7] == 2) {
                  int x = getShortBigEndian(data, 8);
                  int y = getShortBigEndian(data, 10);
                  setPhysicalWidthDpi((int) (x * 2.54f));
                  setPhysicalHeightDpi((int) (y * 2.54f));
               }
            }
            skip(size - 14);
         } else if (marker >= 0xffc0 && marker <= 0xffcf && marker != 0xffc4 && marker != 0xffc8) {
            if (read(data, 0, 6) != 6) {
               return false;
            }
            m_format = FORMAT_JPEG;
            m_bitsPerPixel = (data[0] & 0xff) * (data[5] & 0xff);
            m_progressive = marker == 0xffc2 || marker == 0xffc6 || marker == 0xffca || marker == 0xffce;
            m_width = getShortBigEndian(data, 3);
            m_height = getShortBigEndian(data, 1);
            return true;
         } else {
            skip(size - 2);
         }
      }
   }

   private boolean checkPcx() throws IOException {
      byte[] a = new byte[64];
      if (read(a) != a.length) {
         return false;
      }
      if (a[0] != 1) { // encoding, 1=RLE is only valid value
         return false;
      }
      // width / height
      int x1 = getShortLittleEndian(a, 2);
      int y1 = getShortLittleEndian(a, 4);
      int x2 = getShortLittleEndian(a, 6);
      int y2 = getShortLittleEndian(a, 8);
      if (x1 < 0 || x2 < x1 || y1 < 0 || y2 < y1) {
         return false;
      }
      m_width = x2 - x1 + 1;
      m_height = y2 - y1 + 1;
      // color depth
      int bits = a[1];
      int planes = a[63];
      if (planes == 1 && (bits == 1 || bits == 2 || bits == 4 || bits == 8)) {
         // paletted
         m_bitsPerPixel = bits;
      } else if (planes == 3 && bits == 8) {
         // RGB truecolor
         m_bitsPerPixel = 24;
      } else {
         return false;
      }
      setPhysicalWidthDpi(getShortLittleEndian(a, 10));
      setPhysicalHeightDpi(getShortLittleEndian(a, 10));
      m_format = FORMAT_PCX;
      return true;
   }

   private boolean checkPng() throws IOException {
      final byte[] PNG_MAGIC = { 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a };
      byte[] a = new byte[27];
      if (read(a) != 27) {
         return false;
      }
      if (!equals(a, 0, PNG_MAGIC, 0, 6)) {
         return false;
      }
      m_format = FORMAT_PNG;
      m_width = getIntBigEndian(a, 14);
      m_height = getIntBigEndian(a, 18);
      m_bitsPerPixel = a[22] & 0xff;
      int colorType = a[23] & 0xff;
      if (colorType == 2 || colorType == 6) {
         m_bitsPerPixel *= 3;
      }
      m_progressive = (a[26] & 0xff) != 0;

      return true;
   }

   private boolean checkPnm(int id) throws IOException {
      if (id < 1 || id > 6) {
         return false;
      }
      final int[] PNM_FORMATS = { FORMAT_PBM, FORMAT_PGM, FORMAT_PPM };
      m_format = PNM_FORMATS[(id - 1) % 3];
      boolean hasPixelResolution = false;
      String s;
      while (true) {
         s = readLine();
         if (s != null) {
            s = s.trim();
         }
         if (s == null || s.length() < 1) {
            continue;
         }
         if (s.charAt(0) == '#') { // comment
            continue;
         }
         if (!hasPixelResolution) { // split "343 966" into width=343, height=966
            int spaceIndex = s.indexOf(' ');
            if (spaceIndex == -1) {
               return false;
            }
            String widthString = s.substring(0, spaceIndex);
            spaceIndex = s.lastIndexOf(' ');
            if (spaceIndex == -1) {
               return false;
            }
            String heightString = s.substring(spaceIndex + 1);
            try {
               m_width = Integer.parseInt(widthString);
               m_height = Integer.parseInt(heightString);
            } catch (NumberFormatException nfe) {
               return false;
            }
            if (m_width < 1 || m_height < 1) {
               return false;
            }
            if (m_format == FORMAT_PBM) {
               m_bitsPerPixel = 1;
               return true;
            }
            hasPixelResolution = true;
         } else {
            int maxSample;
            try {
               maxSample = Integer.parseInt(s);
            } catch (NumberFormatException nfe) {
               return false;
            }
            if (maxSample < 0) {
               return false;
            }
            for (int i = 0; i < 25; i++) {
               if (maxSample < (1 << (i + 1))) {
                  m_bitsPerPixel = i + 1;
                  if (m_format == FORMAT_PPM) {
                     m_bitsPerPixel *= 3;
                  }
                  return true;
               }
            }
            return false;
         }
      }
   }

   private boolean checkPsd() throws IOException {
      byte[] a = new byte[24];
      if (read(a) != a.length) {
         return false;
      }
      final byte[] PSD_MAGIC = { 0x50, 0x53 };
      if (!equals(a, 0, PSD_MAGIC, 0, 2)) {
         return false;
      }
      m_format = FORMAT_PSD;
      m_width = getIntBigEndian(a, 16);
      m_height = getIntBigEndian(a, 12);
      int channels = getShortBigEndian(a, 10);
      int depth = getShortBigEndian(a, 20);
      m_bitsPerPixel = channels * depth;
      return (m_width > 0 && m_height > 0 && m_bitsPerPixel > 0 && m_bitsPerPixel <= 64);
   }

   private boolean checkRas() throws IOException {
      byte[] a = new byte[14];
      if (read(a) != a.length) {
         return false;
      }
      final byte[] RAS_MAGIC = { 0x6a, (byte) 0x95 };
      if (!equals(a, 0, RAS_MAGIC, 0, 2)) {
         return false;
      }
      m_format = FORMAT_RAS;
      m_width = getIntBigEndian(a, 2);
      m_height = getIntBigEndian(a, 6);
      m_bitsPerPixel = getIntBigEndian(a, 10);
      return (m_width > 0 && m_height > 0 && m_bitsPerPixel > 0 && m_bitsPerPixel <= 24);
   }

   private boolean checkIco() throws IOException {
      byte[] a = new byte[4];
      if (read(a, 0, 4) != 4) {
         return false;
      }

      int idType = getShortLittleEndian(a, 0);
      if (idType != 1) {
         return false;
      }

      int idCount = getShortLittleEndian(a, 2);
      m_numberOfImages = idCount;

      byte[] b = new byte[16 * idCount];
      if (read(b, 0, 16 * idCount) != 16 * idCount) {
         return false;
      }

      for (int i = 0; i < idCount; ++i) {
         //int width, height;
         //width = b[i*16] & 0xff;
         //height = b[i*16 + 1] & 0xff;

         if ((b[i * 16 + 3] & 0xff) != 0) { //Reserved. Should be 0.
            return false;
         }

         int colorPlanes = getShortLittleEndian(b, i * 16 + 4);
         //Specifies color planes. Should be 0 or 1
         if (colorPlanes != 0 && colorPlanes != 1) {
            return false;
         }
      }

      m_format = FORMAT_ICO;
      return true;
   }

   /**
    * Run over String list, return false iff at least one of the arguments
    * equals <code>-c</code>.
    * 
    * @param args string list to check
    */
   /*  private static boolean determineVerbosity(String[] args) {
        if (args != null && args.length > 0) {
           for (int i = 0; i < args.length; i++) {
              if ("-c".equals(args[i])) {
                 return false;
              }
           }
        }
        return true;
     }*/

   private static boolean equals(byte[] a1, int offs1, byte[] a2, int offs2, int num) {
      while (num-- > 0) {
         if (a1[offs1++] != a2[offs2++]) {
            return false;
         }
      }
      return true;
   }

   /** 
    * If {@link #check()} was successful, returns the image's number of bits per pixel.
    * Does not include transparency information like the alpha channel.
    * 
    * @return number of bits per image pixel
    */
   public int getBitsPerPixel() {
      return m_bitsPerPixel;
   }

   /**
    * If {@link #check()} was successful, returns the image format as one
    * of the FORMAT_xyz constants from this class.
    * Use {@link #getFormatName()} to get a textual description of the file format.
    * @return file format as a FORMAT_xyz constant
    */
   public int getFormat() {
      return m_format;
   }

   /**
    * If {@link #check()} was successful, returns the image format's name.
    * Use {@link #getFormat()} to get a unique number.
    * 
    * @return file format name
    */
   public String getFormatName() {
      if (m_format >= 0 && m_format < FORMAT_NAMES.length) {
         return FORMAT_NAMES[m_format];
      } else {
         return "?";
      }
   }

   /** 
    * If {@link #check()} was successful, returns one the image's vertical
    * resolution in pixels.
    * 
    * @return image height in pixels
    */
   public int getHeight() {
      return m_height;
   }

   private static int getIntBigEndian(byte[] a, int offs) {
      return (a[offs] & 0xff) << 24 | (a[offs + 1] & 0xff) << 16 | (a[offs + 2] & 0xff) << 8 | a[offs + 3] & 0xff;
   }

   private static int getIntLittleEndian(byte[] a, int offs) {
      return (a[offs + 3] & 0xff) << 24 | (a[offs + 2] & 0xff) << 16 | (a[offs + 1] & 0xff) << 8 | a[offs] & 0xff;
   }

   /** 
    * If {@link #check()} was successful, returns a String with the
    * MIME type of the format.
    * 
    * @return MIME type, e.g. <code>image/jpeg</code>
    */
   public String getMimeType() {
      if (m_format >= 0 && m_format < MIME_TYPE_STRINGS.length) {
         if (m_format == FORMAT_JPEG && m_progressive) {
            return "image/pjpeg";
         }
         return MIME_TYPE_STRINGS[m_format];
      } else {
         return null;
      }
   }

   /**
    * Returns the number of images in the examined file.
    * Assumes that <code>setDetermineImageNumber(true);</code> was called before
    * a successful call to {@link #check()}.
    * This value can currently be only different from <code>1</code> for GIF images.
    * 
    * @return number of images in file
    */
   public int getNumberOfImages() {
      return m_numberOfImages;
   }

   /**
    * Returns the physical height of this image in dots per inch (dpi).
    * Assumes that {@link #check()} was successful.
    * Returns <code>-1</code> on failure.
    * 
    * @return physical height (in dpi)
    * @see #getPhysicalWidthDpi()
    * @see #getPhysicalHeightInch()
    */
   public int getPhysicalHeightDpi() {
      return m_physicalHeightDpi;
   }

   /**
    * If {@link #check()} was successful, returns the physical width of this image in dpi (dots per inch)
    * or -1 if no value could be found.
    * 
    * @return physical height (in dpi)
    * @see #getPhysicalHeightDpi()
    * @see #getPhysicalWidthDpi()
    * @see #getPhysicalWidthInch()
    */
   public float getPhysicalHeightInch() {
      int h = getHeight();
      int ph = getPhysicalHeightDpi();
      if (h > 0 && ph > 0) {
         return ((float) h) / ((float) ph);
      } else {
         return -1.0f;
      }
   }

   /**
    * If {@link #check()} was successful, returns the physical width of this image in dpi (dots per inch)
    * or -1 if no value could be found.
    * 
    * @return physical width (in dpi)
    * @see #getPhysicalHeightDpi()
    * @see #getPhysicalWidthInch()
    * @see #getPhysicalHeightInch()
    */
   public int getPhysicalWidthDpi() {
      return m_physicalWidthDpi;
   }

   /**
    * Returns the physical width of an image in inches, or
    * <code>-1.0f</code> if width information is not available.
    * Assumes that {@link #check} has been called successfully.
    * 
    * @return physical width in inches or <code>-1.0f</code> on failure
    * @see #getPhysicalWidthDpi
    * @see #getPhysicalHeightInch
    */
   public float getPhysicalWidthInch() {
      int w = getWidth();
      int pw = getPhysicalWidthDpi();
      if (w > 0 && pw > 0) {
         return ((float) w) / ((float) pw);
      } else {
         return -1.0f;
      }
   }

   private static int getShortBigEndian(byte[] a, int offs) {
      return (a[offs] & 0xff) << 8 | (a[offs + 1] & 0xff);
   }

   private static int getShortLittleEndian(byte[] a, int offs) {
      return (a[offs] & 0xff) | (a[offs + 1] & 0xff) << 8;
   }

   /** 
    * If {@link #check()} was successful, returns one the image's horizontal
    * resolution in pixels.
    * 
    * @return image width in pixels
    */
   public int getWidth() {
      return m_width;
   }

   public int getAlphaBits() {
      return m_alphaBits;
   }

   /**
    * Returns whether the image is stored in a progressive (also called: interlaced) way.
    * 
    * @return true for progressive/interlaced, false otherwise
    */
   public boolean isProgressive() {
      return m_progressive;
   }

   private int read() throws IOException {
      if (m_in != null) {
         return m_in.read();
      } else {
         return m_din.readByte();
      }
   }

   private int read(byte[] a) throws IOException {
      if (m_in != null) {
         return m_in.read(a);
      } else {
         m_din.readFully(a);
         return a.length;
      }
   }

   private int read(byte[] a, int offset, int num) throws IOException {
      if (m_in != null) {
         return m_in.read(a, offset, num);
      } else {
         m_din.readFully(a, offset, num);
         return num;
      }
   }

   private String readLine() throws IOException {
      return readLine(new StringBuffer());
   }

   private String readLine(StringBuffer sb) throws IOException {
      boolean finished;
      do {
         int value = read();
         finished = (value == -1 || value == 10);
         if (!finished) {
            sb.append((char) value);
         }
      } while (!finished);
      return sb.toString();
   }

   /**
    * Specify whether the number of images in a file is to be
    * determined - default is <code>false</code>.
    * This is a special option because some file formats require running over
    * the entire file to find out the number of images, a rather time-consuming
    * task.
    * Not all file formats support more than one image.
    * If this method is called with <code>true</code> as argument,
    * the actual number of images can be queried via 
    * {@link #getNumberOfImages()} after a successful call to
    * {@link #check()}.
    * 
    * @param newValue will the number of images be determined?
    * @see #getNumberOfImages
    */
   public void setDetermineImageNumber(boolean newValue) {
      m_determineNumberOfImages = newValue;
   }

   /**
    * Set the input stream to the argument stream (or file). 
    * Note that {@link java.io.RandomAccessFile} implements
    * {@link java.io.DataInput}.
    * 
    * @param dataInput the input stream to read from
    */
   public void setInput(DataInput dataInput) {
      m_din = dataInput;
      m_in = null;
   }

   /**
    * Set the input stream to the argument stream (or file).
    * 
    * @param inputStream the input stream to read from
    */
   public void setInput(InputStream inputStream) {
      m_in = inputStream;
      m_din = null;
   }

   private void setPhysicalHeightDpi(int newValue) {
      m_physicalWidthDpi = newValue;
   }

   private void setPhysicalWidthDpi(int newValue) {
      m_physicalHeightDpi = newValue;
   }

   private void skip(int num) throws IOException {
      while (num > 0) {
         long result;
         if (m_in != null) {
            result = m_in.skip(num);
         } else {
            result = m_din.skipBytes(num);
         }
         if (result > 0) {
            num -= result;
         }
      }
   }
}
