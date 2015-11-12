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

import junit.framework.Assert;

import org.junit.Test;

import com.ebayopensource.webrex.logging.ConsoleLogger.LogFormatter.StringBuilderWriter;

public class ConsoleLoggerTest {
   
   @Test
   public void testFormat() {
      ConsoleLogger.LogFormatter.format(
            new StringBuilder(), "testName", "testThread", LogLevel.WARN, System.currentTimeMillis(), "test WARNING message", new Throwable("test Throwable"));
   }
   
   @Test
   public void testStringBuilderWriter() {
      StringBuilder sb = new StringBuilder();
      StringBuilderWriter writer = new StringBuilderWriter(sb);
      writer.write(97);
      char[] cbuf = {'a','b','c'};
      try {
         writer.write(cbuf, -1, 3);
      } catch (Exception e) {
         Assert.assertTrue(e instanceof IndexOutOfBoundsException);
      }
      writer.write(cbuf, 1, 0);
      writer.write(cbuf, 1, 1);
      writer.write("abc", 2, 1);
      try {
         writer.close();
      } catch (IOException e) {
         Assert.fail();
      }
      Assert.assertEquals("abc", sb.toString());
   }

}
