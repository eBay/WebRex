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

package com.ebayopensource.webrex.resource.expression;


import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.junit.Test;

public class UnifiedELResolverHelperTest {

   
   @Test
   public void testUnifiedELResolverHelper() {
      UnifiedELResolverHelper.register(Thread.currentThread().getContextClassLoader(), new MockServletContext());
   }
   
   public static class MockServletContext implements ServletContext {

      @Override
      public String getContextPath() {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public ServletContext getContext(String uripath) {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public int getMajorVersion() {
         // TODO Auto-generated method stub
         return 0;
      }

      @Override
      public int getMinorVersion() {
         // TODO Auto-generated method stub
         return 0;
      }

      @Override
      public String getMimeType(String file) {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public Set getResourcePaths(String path) {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public URL getResource(String path) throws MalformedURLException {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public InputStream getResourceAsStream(String path) {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public RequestDispatcher getRequestDispatcher(String path) {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public RequestDispatcher getNamedDispatcher(String name) {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public Servlet getServlet(String name) throws ServletException {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public Enumeration getServlets() {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public Enumeration getServletNames() {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public void log(String msg) {
         // TODO Auto-generated method stub
         
      }

      @Override
      public void log(Exception exception, String msg) {
         // TODO Auto-generated method stub
         
      }

      @Override
      public void log(String message, Throwable throwable) {
         // TODO Auto-generated method stub
         
      }

      @Override
      public String getRealPath(String path) {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public String getServerInfo() {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public String getInitParameter(String name) {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public Enumeration getInitParameterNames() {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public Object getAttribute(String name) {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public Enumeration getAttributeNames() {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public void setAttribute(String name, Object object) {
         // TODO Auto-generated method stub
         
      }

      @Override
      public void removeAttribute(String name) {
         // TODO Auto-generated method stub
         
      }

      @Override
      public String getServletContextName() {
         // TODO Auto-generated method stub
         return null;
      }
      
   }
}
