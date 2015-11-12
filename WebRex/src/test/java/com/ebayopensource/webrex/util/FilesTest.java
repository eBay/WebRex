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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

import junit.framework.Assert;

import org.junit.Test;

import com.ebayopensource.webrex.util.Files.AutoClose;
import com.ebayopensource.webrex.util.Files.Dir;
import com.ebayopensource.webrex.util.Files.IO;

public class FilesTest {
	
	@Test
	public void testFiles() throws IOException {
		IO io = Files.forIO();
		Dir dir = Files.forDir();
		File file = new File("warRoot/resources/text/filetext.txt");
		File file1 = new File("warRoot/resources/123.txt");
		File dir1 = new File("warRoot/resources/text");;
		File dir2 = new File("warRoot/resources/text1");;
		dir.copyDir(dir1, dir2);
		ZipFile zip = new ZipFile(new File("warRoot/resources/zipFileTest.zip"));
		Files.close(zip);
		dir.createDir(file1);
		boolean deleteFlag = dir.delete(file1);
		Assert.assertTrue(deleteFlag);
		InputStream is = new FileInputStream(file);
		ByteArrayOutputStream os = new ByteArrayOutputStream();  
		io.copy(is, os);
		InputStream is1 = new FileInputStream(file);
		io.readFrom(file);
		io.readFrom(is1);
		InputStream is2 = new FileInputStream(file);
		io.readFrom(is2, "utf-8");
		InputStream is3 = new FileInputStream(file);
		io.readFrom(is3, AutoClose.INPUT);
		File writeFile = new File("warRoot/resources/text/fileWriteTest.txt");
		io.writeTo(writeFile, "test String");
		
	}
}
