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

package com.ebayopensource.webrex.resource.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.ebayopensource.webrex.resource.ResourceRuntime;
import com.ebayopensource.webrex.resource.ResourceRuntimeContext;
import com.ebayopensource.webrex.resource.expression.ResBeanExpression;
import com.ebayopensource.webrex.resource.impl.ResourceFlusher;

public class ResourceFilter implements Filter {

   @Override
   public void destroy() {
   }

   public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
         throws IOException, ServletException {
      if (ResourceRuntime.INSTANCE.getConfig() != null) {
         //Before the request, setup resource context
         ResourceRuntimeContext.setup();
         ResourceRuntimeContext.ctx().getResourceContext().setOriginalRequestUri(request.getRequestURI());
         request.setAttribute("res", new ResBeanExpression());

         ResponseWrapper wrapper = new ResponseWrapper(response);
         chain.doFilter(request, wrapper);
         wrapper.flushBuffer();
         return;
      }

      chain.doFilter(request, response);
   }

   @Override
   public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain)
         throws IOException, ServletException {
      if (req instanceof HttpServletRequest && res instanceof HttpServletResponse) {
         try {
            doFilter((HttpServletRequest) req, (HttpServletResponse) res, chain);
         } catch (RuntimeException e) {
            throw e;
         }
      } else {
         chain.doFilter(req, res);
      }
   }

   @Override
   public void init(FilterConfig arg0) throws ServletException {
   }

   static class OutputBuffer extends ByteArrayOutputStream {
      private ResponseWrapper m_res;

      private boolean m_chunkEnabled = true;

      public OutputBuffer(ResponseWrapper res, int size) {
         super(size);

         m_res = res;
      }

      @Override
      public void close() throws IOException {
         finish();
      }

      protected void disableChunk() {
         m_chunkEnabled = false;
      }

      protected void finish() throws IOException {
         flushChunk(true);
      }

      @Override
      public void flush() throws IOException {
         flushChunk(false);
      }

      protected void flushChunk(boolean lastChunk) throws IOException {
         if (m_chunkEnabled) {
            if (size() > 0) {
               ServletOutputStream out = m_res.getResponse().getOutputStream();

               if (!ResourceRuntimeContext.isInitialized()) {
                  byte[] data = super.toByteArray();

                  if (!m_res.isCommitted() && lastChunk) {
                     m_res.setContentLength(data.length);
                  }

                  super.reset();
                  out.write(data);
                  out.flush();
               } else {
                  byte[] data = getProcessedData();

                  if (!m_res.isCommitted() && lastChunk) {
                     m_res.setContentLength(data.length);
                  }

                  out.write(data);
                  out.flush();
               }
            }
         } else {
            ServletOutputStream out = m_res.getResponse().getOutputStream();
            byte[] data = super.toByteArray();
            m_res.setContentLength(data.length);

            super.reset();
            out.write(data);
            out.flush();
         }
      }

      protected byte[] getProcessedData() throws IOException {
         String charset = m_res.getCharacterEncoding();
         String content = super.toString(charset);
         super.reset();

         StringBuilder sb = new StringBuilder(content);
         ResourceFlusher.INSTANCE.process(sb, m_res.getContentType());

         return sb.toString().getBytes(charset);
      }
   }

   public static class ResponseWrapper extends HttpServletResponseWrapper {
      private int m_capacity = 8192;

      private OutputBuffer m_buffer;

      private PrintWriter m_writer;

      private ServletOutputStream m_out;

      public ResponseWrapper(HttpServletResponse res) {
         super(res);
      }

      @Override
      public void flushBuffer() throws IOException {
         super.flushBuffer();

         if (m_writer != null) {
            m_writer.flush();
         } else if (m_out != null) {
            m_out.flush();
         }

         if (m_buffer != null) {
            m_buffer.flush();
         }
      }

      OutputBuffer getBuffer() {
         return m_buffer;
      }

      @Override
      public int getBufferSize() {
         return m_capacity;
      }

      @Override
      public String getCharacterEncoding() {
         String charset = super.getCharacterEncoding();

         return charset == null ? "utf-8" : charset;
      }

      @Override
      public ServletOutputStream getOutputStream() throws IOException {
         if (m_writer != null) {
            throw new IOException("Can't getOutputStream() after getWriter()!");
         }

         if (m_out == null) {
            m_buffer = new OutputBuffer(this, m_capacity);
            m_out = new ServletOutputStreamWrapper(m_buffer);
         }

         return m_out;
      }

      @Override
      public PrintWriter getWriter() throws IOException {
         if (m_out != null) {
            throw new IOException("Can't getWriter() after getOutputStream()!");
         }

         if (m_writer == null) {
            m_buffer = new OutputBuffer(this, m_capacity);
            m_writer = new PrintWriter(new OutputStreamWriter(m_buffer, getCharacterEncoding()));
         }

         return m_writer;
      }

      @Override
      public void reset() {
         super.reset();

         if (m_buffer != null) {
            m_buffer.reset();
         }
      }

      @Override
      public void resetBuffer() {
         super.resetBuffer();

         if (m_buffer != null) {
            m_buffer.reset();
         }
      }

      @Override
      public void setBufferSize(int size) {
         if (size > m_capacity) {
            m_capacity = size;
         }
      }

      @Override
      public void setContentLength(int len) {
         if (m_buffer != null) {
            // 'Content-Length' header can't co-exist with 'Transfer-Encoding'
            m_buffer.disableChunk();
         }

         if (len > m_capacity) {
            m_capacity = len;
         }
      }

      @Override
      public void setHeader(String name, String value) {
         // application must set this hint explicitly
         if (m_buffer != null && "Connection".equalsIgnoreCase(name) && "close".equalsIgnoreCase(value)) {
            m_buffer.disableChunk();
         }

         super.setHeader(name, value);
      }

      @Override
      public String toString() {
         if (m_buffer != null) {
            try {
               return m_buffer.toString(getCharacterEncoding());
            } catch (UnsupportedEncodingException e) {
               return "";
            }
         } else {
            return "";
         }
      }
   }

   static class ServletOutputStreamWrapper extends ServletOutputStream {
      private OutputBuffer m_buffer;

      public ServletOutputStreamWrapper(OutputBuffer buffer) {
         m_buffer = buffer;
      }

      @Override
      public void close() throws IOException {
         m_buffer.finish();
      }

      @Override
      public void flush() throws IOException {
         m_buffer.flush();
      }

      @Override
      public void write(int b) throws IOException {
         m_buffer.write(b);
      }
   }

}
