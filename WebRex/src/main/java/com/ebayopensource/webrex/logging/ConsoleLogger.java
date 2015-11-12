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

package com.ebayopensource.webrex.logging;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*
 * "[webres_logger] WARN : 2011-06-01 06:36:24 Test Get Logger Warning"
 */
public class ConsoleLogger extends BaseLogger {

   public ConsoleLogger(String loggerName) {
      super(loggerName);
   }

   protected void doLog(LogLevel level, String message, Throwable cause) {
      StringBuilder sb = new StringBuilder(256);
      long time = System.currentTimeMillis();
      LogFormatter.format(sb, null, null, level, time, message, cause);
      System.out.println(sb.toString());
   }

   static class LogFormatter {

      private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

      static final FastDateFormat DATE_FORMAT = new FastDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
            Locale.US), 1000);

      public static StringBuilder appendLevel(StringBuilder sb, LogLevel p) {
         if (p != null) {
            String levelName = p.name();
            sb.append(levelName);
            if (levelName.length() == 4) {
               sb.append(' ');
            }
         } else {
            sb.append(LogLevel.INFO);
         }

         return sb;
      }

      public static StringBuilder format(StringBuilder sb, long time) {
         return DATE_FORMAT.format(sb, time);
      }

      /**
       * Return formatted string
       * 
       * @param entry
       * @return String
       */
      public static StringBuilder format(StringBuilder sb, String name, String threadName, LogLevel level, long time,
            String message, Throwable throwable) {

         format(sb, time).append(' ');

         // Append level name
         appendLevel(sb, level);
         sb.append(": ");

         // Append message
         if (message != null) {
            sb.append(message);
         }

         if (throwable != null) {
            sb.append(LINE_SEPARATOR);

            format(sb, throwable);
         }

         return sb;
      }

      public static StringBuilder format(StringBuilder sb, Throwable throwable) {
         if (throwable != null) {
            synchronized (sb) {
               StringBuilderWriter writer = new StringBuilderWriter(sb);
               PrintWriter pw = new PrintWriter(writer);
               try {
                  throwable.printStackTrace(pw);
               } catch (Exception e) {
                  throw new RuntimeException(e.getMessage(), e);
               }
            }
         }

         return sb;
      }

      private static class FastDateFormat {
         private volatile DateFormat m_format;

         private long m_lastTime = -1;

         private String m_lastResult = null;

         private int m_timeout = 1000;

         private Date m_date = new Date();

         public FastDateFormat(DateFormat format, int timeout) {
            this.m_format = format;
            this.m_timeout = timeout == -1 ? 1000 : timeout;
         }

         public String format(long time) {
            if (Math.abs(time - m_lastTime) > m_timeout) {
               synchronized (m_format) {
                  m_date.setTime(time);
                  format0(m_date, time);
               }
            }
            return m_lastResult;
         }

         public StringBuilder format(StringBuilder sb, long time) {
            return sb.append(format(time));
         }

         private String format0(Date date, long time) {
            m_lastResult = m_format.format(date);
            m_lastTime = time;
            return m_lastResult;
         }
      }

      protected static class StringBuilderWriter extends Writer {
         private StringBuilder m_buf;

         public StringBuilderWriter(StringBuilder sb) {
            this.m_buf = sb;
            this.lock = sb;
         }

         @Override
         public void close() throws IOException {

         }

         @Override
         public void flush() throws IOException {
         }

         /**
          * Return the buffer's current value as a string.
          */
         public String toString() {
            return m_buf.toString();
         }

         /**
          * Write a portion of an array of characters.
          * 
          * @param cbuf
          *            Array of characters
          * @param off
          *            Offset from which to start writing characters
          * @param len
          *            Number of characters to write
          */
         public void write(char cbuf[], int off, int len) {
            if ((off < 0) || (off > cbuf.length) || (len < 0) || ((off + len) > cbuf.length) || ((off + len) < 0)) {
               throw new IndexOutOfBoundsException();
            } else if (len == 0) {
               return;
            }
            m_buf.append(cbuf, off, len);
         }

         /**
          * Write a single character.
          */
         public void write(int c) {
            m_buf.append((char) c);
         }

         /**
          * Write a string.
          */
         public void write(String str) {
            m_buf.append(str);
         }

         /**
          * Write a portion of a string.
          * 
          * @param str
          *            String to be written
          * @param off
          *            Offset from which to start writing characters
          * @param len
          *            Number of characters to write
          */
         public void write(String str, int off, int len) {
            m_buf.append(str.substring(off, off + len));
         }
      }
   }

}
