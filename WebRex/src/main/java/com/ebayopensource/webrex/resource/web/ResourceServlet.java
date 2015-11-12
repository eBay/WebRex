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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ebayopensource.webrex.resource.cache.ResourceCacheManager;
import com.ebayopensource.webrex.resource.impl.DefaultResourceHandler.ContentCacheValue;

public class ResourceServlet extends HttpServlet {
	private static final long serialVersionUID = 2183896746339469780L;

	private static final int DEFAULT_MAX_AGE = 3600;
	
	private static final String MAX_AGE = "Max-Age";
	
	private int maxAge = DEFAULT_MAX_AGE;
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		String paramMaxAge = config.getInitParameter("max_age");
		if (paramMaxAge != null) {
			maxAge = Integer.parseInt(paramMaxAge);
		}
	}
	
	protected long getLastModified(HttpServletRequest req) {
		ContentCacheValue cacheValue = getCacheValue(req);
		return cacheValue != null ? cacheValue.getLastModified() : -1;
	}

	protected ContentCacheValue getCacheValue(HttpServletRequest req) {
		ContentCacheValue cacheValue = (ContentCacheValue) req
				.getAttribute("_resource_content_cache_value_");
		if (cacheValue == null) {
			String requestURI = req.getRequestURI();
			String checkSum = parseCheckSum(requestURI);
			if (checkSum != null) {
				cacheValue = (ContentCacheValue) ResourceCacheManager
						.getGlobalCache(checkSum);
				if (cacheValue != null) {
					req.setAttribute("_resource_content_cache_value_",
							cacheValue);
				}
			}
		}
		return cacheValue;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// System.out.println("Resource Servlet serving: " +
		// req.getRequestURI());
		ContentCacheValue cache = getCacheValue(req);
		if (cache != null) {
			String mimeType = cache.getContentType();
			resp.setContentType(mimeType);
			Object content = cache.getContent();

			if (maxAge > 0) {
				resp.setHeader("Cache-Control", "max-age=" + maxAge); 
			}
			
			if (content instanceof String) {
				PrintWriter writer = resp.getWriter();
				writer.write((String) content);
				resp.flushBuffer();
			} else if (content instanceof byte[]) {
			   OutputStream out = resp.getOutputStream();
				byte[] byteContent = (byte[]) content;
				resp.setContentLength(byteContent.length);	
				out.write(byteContent);
				out.flush();
				resp.flushBuffer();	
			}
			
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

	private String parseCheckSum(String requestURI) {
		int index = requestURI.lastIndexOf('/');
		int indexPoint = requestURI.lastIndexOf('.');
		if (index >= 0 && indexPoint > index) {
			return requestURI.substring(index + 1, indexPoint);
		}
		return null;
	}

}
