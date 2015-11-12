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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Test;

public class ImageAnalyzerTest {

	String fileName = "16px.png";

	String gifFileName = "logoEbay_x45.gif";

	String jpegFileName = "jpegImage.jpg";

	String icoFileName = "AddedIcon.ico";

	String bmpFileName = "bee.bmp";
	
	String pcxFileName = "16px.pcx";
	
	String psdFileName = "sample.psd";
	
	String rasFileName = "rastest.ras";

	ImageAnalyzer ia = new ImageAnalyzer();

	@Test
	public void testAnalyze() {
		String dir = "warRoot/resources/img/";

		try {

			File file = new File(dir, fileName);
			ia.setInput(new FileInputStream(file));
			// ia.setCollectComments(true);
			ia.check();
			ia.analyzeAlphaBits(file);

			int trans = ia.getAlphaBits();
			assertEquals(8, trans);

			int w = ia.getWidth();
			int h = ia.getHeight();
			assertEquals(16, w);
			assertEquals(16, h);
			float hi = ia.getPhysicalHeightInch();
			float hdpi = ia.getPhysicalHeightDpi();
			assertEquals(-1, hi, 0);
			assertEquals(-1, hdpi, 0);

			File gifFile = new File(dir, gifFileName);
			ia.setInput(new FileInputStream(gifFile));
			ia.setDetermineImageNumber(true);
			ia.check();
			ia.analyzeAlphaBits(gifFile);

			int gifw = ia.getWidth();
			int gifh = ia.getHeight();
			int giftrans = ia.getAlphaBits();
			assertEquals(0, giftrans);
			assertEquals(110, gifw);
			assertEquals(45, gifh);
			assertEquals(false, ia.isProgressive());

			File jpegFile = new File(dir, jpegFileName);
			ia.setInput(new FileInputStream(jpegFile));
			ia.setDetermineImageNumber(true);
			ia.check();
			ia.analyzeAlphaBits(jpegFile);

			int jpegw = ia.getWidth();
			int jpegh = ia.getHeight();
			int jpegtrans = ia.getAlphaBits();
			assertEquals(0, jpegtrans);
			assertEquals(300, jpegw);
			assertEquals(394, jpegh);
			assertEquals(false, ia.isProgressive());

			File icoFile = new File(dir, icoFileName);
			ia.setInput(new FileInputStream(icoFile));
			ia.setDetermineImageNumber(true);
			ia.check();
			ia.analyzeAlphaBits(icoFile);

			// int icogw = ia.getWidth();
			// int icogh = ia.getHeight();
			int icotrans = ia.getAlphaBits();
			assertEquals(0, icotrans);
			// assertEquals(48, icogw);
			// assertEquals(48, icogh);
			assertEquals(false, ia.isProgressive());

			File bmpFile = new File(dir, bmpFileName);
			ia.setInput(new FileInputStream(bmpFile));
			ia.setDetermineImageNumber(true);
			ia.check();
			ia.analyzeAlphaBits(bmpFile);

			int bmpw = ia.getWidth();
			int bmph = ia.getHeight();
			int bmptrans = ia.getAlphaBits();

			int bitsPerPixel = ia.getBitsPerPixel();
			// String comments="";
			try {
				// ia.getComment(0);
			} catch (Exception e) {
				IllegalArgumentException iae = new IllegalArgumentException(
						"Not a valid comment index: " + 0);
				assertEquals(e.getMessage(), iae.getMessage());
			}
			int format = ia.getFormat();
			String formatName = ia.getFormatName();
			String mineType = ia.getMimeType();
			// int numberOfComments = ia.getNumberOfComments();
			int numberOfImages = ia.getNumberOfImages();

			assertEquals(0, bmptrans);
			assertEquals(100, bmpw);
			assertEquals(100, bmph);
			assertEquals(false, ia.isProgressive());

			assertEquals(24, bitsPerPixel);
			assertEquals(3, format);
			assertEquals("BMP", formatName);
			assertEquals("image/bmp", mineType);
			// assertEquals(0, numberOfComments);
			assertEquals(1, numberOfImages);
			
			
			File pcxFile = new File(dir, pcxFileName);
         ia.setInput(new FileInputStream(file));
         // ia.setCollectComments(true);
         ia.check();
         ia.analyzeAlphaBits(pcxFile);

         int pcxtrans = ia.getAlphaBits();
         assertEquals(0, pcxtrans);

         int pcxw = ia.getWidth();
         int pcxh = ia.getHeight();
         assertEquals(16, pcxw);
         assertEquals(16, pcxh);
         float pcxhi = ia.getPhysicalHeightInch();
         float pcxhdpi = ia.getPhysicalHeightDpi();
         assertEquals(-1, pcxhi, 0);
         assertEquals(-1, pcxhdpi, 0);
         
         File psdFile = new File(dir, psdFileName);
         ia.setInput(new FileInputStream(file));
         // ia.setCollectComments(true);
         ia.check();
         ia.analyzeAlphaBits(psdFile);

         int psdtrans = ia.getAlphaBits();
         assertEquals(0, psdtrans);

         int psdw = ia.getWidth();
         int psdh = ia.getHeight();
         assertEquals(16, psdw);
         assertEquals(16, psdh);
         float psdhi = ia.getPhysicalHeightInch();
         float psdhdpi = ia.getPhysicalHeightDpi();
         assertEquals(-1, psdhi, 0);
         assertEquals(-1, psdhdpi, 0);
         
         File rasFile = new File(dir, rasFileName);
         ia.setInput(new FileInputStream(file));
         // ia.setCollectComments(true);
         ia.check();
         ia.analyzeAlphaBits(rasFile);

         int rastrans = ia.getAlphaBits();
         assertEquals(0, rastrans);

         int rasw = ia.getWidth();
         int rash = ia.getHeight();
         assertEquals(16, rasw);
         assertEquals(16, rash);
         float rashi = ia.getPhysicalHeightInch();
         float rashdpi = ia.getPhysicalHeightDpi();
         assertEquals(-1, rashi, 0);
         assertEquals(-1, rashdpi, 0);

         

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	// @Ignore
	public void testAnalyzeAll() {
		String path = "warRoot/resources/img/";
		File imagePath = new File(path);

		File[] files = imagePath.listFiles();
		FileInputStream fis = null;
		for (File file : files) {
			try {
				fis = new FileInputStream(file);
				ia.setInput(fis);
				ia.setDetermineImageNumber(true);
				ia.check();
				ia.analyzeAlphaBits(file);

				// int bmpw = ia.getWidth();
				// int bmph = ia.getHeight();
				// assertTrue(bmpw > 0);
				// assertTrue(bmph > 0);
				// assertEquals(false, ia.isProgressive());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
