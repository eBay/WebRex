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

import com.ebayopensource.webrex.util.Reflects;

public final class LoggerFactory {
   private static ILoggerFactory s_factory = ConsoleLoggerFactory.INSTANCE;

   public static ILogger getLogger(Class<?> source) {
      return new LoggerWrapper(source);
   }

   public static ILoggerFactory getLoggerFactory() {
      return s_factory;
   }

   public static void setLoggerFactory(ILoggerFactory loggerFactory) {
      s_factory = loggerFactory;
   }

   public static void setLoggerFactoryClass(String loggerClass) {
      Class<?> factoryClass = Reflects.forClass().getClass(loggerClass);
      if (factoryClass == null) {
         throw new IllegalArgumentException("Can't loading the ILoggerFactory:" + loggerClass);
      }
      Object factory = null;
      try {
         factory = factoryClass.newInstance();
      } catch (IllegalAccessException e) {
         throw new IllegalArgumentException("Can't create the ILoggerFactory:" + loggerClass, e);
      } catch (InstantiationException e) {
         throw new IllegalArgumentException("Can't create the ILoggerFactory:" + loggerClass, e);
      }

      if (factory instanceof ILoggerFactory) {
         setLoggerFactory((ILoggerFactory) factory);
      } else {
         throw new IllegalArgumentException("The class is not an instance of ILoggerFactory:" + factory);
      }
   }

   static class LoggerWrapper extends BaseLogger {

      private Class<?> m_source;

      ILogger m_logger;

      public LoggerWrapper(Class<?> source) {
         super(source.getSimpleName());
         m_source = source;
      }

      @Override
      protected void doLog(LogLevel level, String message, Throwable throwable) {
         //lazy init logger in case log factory is registered later
         if (m_logger == null) {
            m_logger = LoggerFactory.getLoggerFactory().getLogger(m_source);
         }
         m_logger.log(level, message, throwable);
      }
   }

}
