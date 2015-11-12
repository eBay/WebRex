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

public class JavaHighResTimer {

   /** Used to convert from nanoseconds to millisecons */
   private static final long MS_PER_NANOSECOND = (long) 1.0e06;

   /**
    * Returns a long value, that when latter passed to 
    * {@link #endStr(long)} or {@link #endVal(long)} can
    * be used to calculate elapsed time.  No attempt should be
    * made to interpret the value returned by this method directly.
    * It is only useful as a relative time value.
    * @return a long value used to implement an accurate 
    * millisecond timer.
    */
   public static long[] begin() {
      return new long[] { System.nanoTime(), System.currentTimeMillis() };
   }

   /**
    * Returns the number of nano seconds elapsed since
    * (@links #begin()} was called.  The value is rounded to
    * the nearest millisecond. 
    * @param nStart a value previously returned by {@link #begin()}.
    * @return the number of whole milliseconds elapsed since 
    * (@links #begin()} was called expressed as a float.
    */
   public static long endVal(long[] nStart) {
      long now = System.nanoTime();
      //fallback to use ms if now is less than begin
      if (now < nStart[0]) {
         return (System.currentTimeMillis() - nStart[1]) * MS_PER_NANOSECOND;
      }
      return now - nStart[0];
   }
}
