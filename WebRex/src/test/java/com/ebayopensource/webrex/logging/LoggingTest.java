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



import junit.framework.Assert;

import org.junit.Test;

import com.ebayopensource.webrex.logging.LogLevel;


public class LoggingTest {

	
   @Test
   public void testConsoleLogger() {
	   ConsoleLoggerFactory factory = ConsoleLoggerFactory.INSTANCE;
	   ConsoleLogger logger = (ConsoleLogger)factory.getLogger(this.getClass());
	   LogLevel level = LogLevel.INFO;
	   logger.setLevel(level);
	   Assert.assertEquals(true, logger.isInfoEnabled());
	   Assert.assertEquals(LogLevel.INFO, logger.getLevel());
	   logger.info("this is an info test log");
	   
	   level = LogLevel.WARN;
	   logger.setLevel(level);
	   Assert.assertEquals(true, logger.isWarnEnabled());
	   Assert.assertEquals(LogLevel.WARN, logger.getLevel());
	   logger.warn("this is a warn test log");
	   
	   level = LogLevel.ERROR;
	   logger.setLevel(level);
	   Assert.assertEquals(true, logger.isErrorEnabled());
	   Assert.assertEquals(LogLevel.ERROR, logger.getLevel());
	   logger.error("this is an error test log");
 
	   level = LogLevel.NONE;
	   logger.setLevel(level);
	   Assert.assertEquals(LogLevel.NONE, logger.getLevel());
	   
	   ConsoleLogger consoleLogger = new ConsoleLogger("loggerName");
	   Assert.assertEquals("loggerName", consoleLogger.getName());
	   factory.setLogger(consoleLogger);
   }
   
   
}
