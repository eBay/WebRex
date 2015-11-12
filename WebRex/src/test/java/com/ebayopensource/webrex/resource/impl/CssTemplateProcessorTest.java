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

package com.ebayopensource.webrex.resource.impl;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.ebayopensource.webrex.resource.BaseResourceTest;
import com.ebayopensource.webrex.resource.api.IResource;

public class CssTemplateProcessorTest extends BaseResourceTest {

	@Test
	public void testBackgroundEL() {
		CssTemplateProcessor processor = new CssTemplateProcessor();
		StringBuilder originalToken = new StringBuilder(
				"${res.img.local.img.ebayLogo_gif}");
		List<IResource> dependencies = new ArrayList<IResource>();
		String actual = processor.handleToken(originalToken, dependencies,
				"/resoures");

		Assert.assertEquals(
				"/BaseResourceTest/lrssvr/ry4sqfvidizw3iszva2fca1haul.gif",
				actual);
		Assert.assertEquals(
				"[Resource [m_urn=img.local:/img/ebayLogo.gif, m_libInfo=ResourceLibrary [m_id=webressdk, m_version=1.0.8, m_type=WAR]]]",
				dependencies.toString());
	}
	
	@Test
	public void testBackgroundELWithSpace() {
		CssTemplateProcessor processor = new CssTemplateProcessor();
		StringBuilder originalToken = new StringBuilder(
				"${res.img.local.img.ebayLogo_gif }");
		List<IResource> dependencies = new ArrayList<IResource>();
		String actual = processor.handleToken(originalToken, dependencies,
				"/resoures");

		Assert.assertEquals(
				"/BaseResourceTest/lrssvr/ry4sqfvidizw3iszva2fca1haul.gif",
				actual);
		Assert.assertEquals(
				"[Resource [m_urn=img.local:/img/ebayLogo.gif, m_libInfo=ResourceLibrary [m_id=webressdk, m_version=1.0.8, m_type=WAR]]]",
				dependencies.toString());
	}

	@Test
	public void testUnknownEL() {
		CssTemplateProcessor processor = new CssTemplateProcessor();
		StringBuilder originalToken = new StringBuilder(
				"${res.img.local.img.ebayLogoxxxx_gif}");
		List<IResource> dependencies = new ArrayList<IResource>();
		String actual = processor.handleToken(originalToken, dependencies,
				"/resoures");

		Assert.assertEquals("${res.img.local.img.ebayLogoxxxx_gif}", actual);
		Assert.assertEquals("[]", dependencies.toString());
		
		originalToken.setLength(0);
		originalToken.append("${res.img.local.img.ebayLogoxxxx");
		actual = processor.handleToken(originalToken, dependencies,
				"/resoures");
		
		Assert.assertEquals("${res.img.local.img.ebayLogoxxxx", actual);
	}

	@Test
	public void testBackgroundELExclude() {
		CssTemplateProcessor processor = new CssTemplateProcessor();
		StringBuilder originalToken = new StringBuilder(
				"${res.img.local.img.ebayLogo_gif}?e");
		List<IResource> dependencies = new ArrayList<IResource>();
		String actual = processor.handleToken(originalToken, dependencies,
				"/resoures");

		Assert.assertEquals(
				"/BaseResourceTest/lrssvr/ry4sqfvidizw3iszva2fca1haul.gif?e",
				actual);
		Assert.assertEquals(
				"[Resource [m_urn=img.local:/img/ebayLogo.gif, m_libInfo=ResourceLibrary [m_id=webressdk, m_version=1.0.8, m_type=WAR]]]",
				dependencies.toString());
	}
}
