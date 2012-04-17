/*
 * OUnit - an OPAQUE compliant framework for Computer Aided Testing
 *
 * Copyright (C) 2010, 2011  Antti Andreimann
 *
 * This file is part of OUnit.
 *
 * OUnit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OUnit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OUnit.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.googlecode.ounit.junit;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class TestUtils {
	/**
	 * NIO based implementation that reads contents of a file to a String
	 * 
	 * Loosely based on: http://www.xinotes.org/notes/note/1421/
	 * 
	 * @param f
	 * @return
	 * @throws Exception
	 */
	public static String getFileContents(File f) throws IOException {
		FileInputStream fin = new FileInputStream(f);
		FileChannel fch = fin.getChannel();
		ByteBuffer bb = fch.map(FileChannel.MapMode.READ_ONLY, 0, fch.size());
		CharBuffer chBuff = Charset.forName("UTF-8").decode(bb);
		return chBuff.toString();
	}
	
	/**
	 * Load contents of a resource file into a string
	 * 
	 * @param clazz class to use as a base for resolving the resource in classpath
	 * @param name resource name
	 * @return String the contents of a resource file
	 * @throws IOException
	 */
    public static String getResourceContents(Class<?> clazz, String name) throws IOException {
    	StringBuilder sb = new StringBuilder();
        InputStream is = clazz.getResourceAsStream(name);
        assertThat("Resource file not found", is, notNullValue());
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = br.readLine()) != null) {
            	sb.append(line);
            	sb.append("\n");
            }
        }
        finally {
            is.close();
        }
        return sb.toString();
    }

	/**
	 * Try to find current reports directory from properties
	 * 
	 * Defaults to target/surefire-reports
	 * 
	 * @return
	 */
	public static File getReportsDirectory() {
		String fs = File.separator;
		String reportsDirectory = System.getProperty("reportsDirectory");
		if(reportsDirectory == null) {
			String basedir = System.getProperty("basedir");
			if(basedir != null) {
				basedir += fs + "target";
			} else {
				basedir = "target";
			}
			reportsDirectory = basedir + fs + "surefire-reports";
		}
		return new File(reportsDirectory);
	}

	/**
	 * Saves data into test "output" file
	 * 
	 * Test output files will be picked up by OUnit report generator
	 * and displayed to the user if possible.
	 * 
	 * @param prefix
	 * @param suffix
	 * @param data
	 * @return
	 */
	public static void saveOutputFile(String prefix, String suffix, String data)
			throws IOException {
		saveOutputFile(prefix, suffix, data.getBytes());
	}

	/**
	 * Saves data into test "output" file.
	 * 
	 * Test output files will be picked up by OUnit report generator
	 * and displayed to the user if possible.
	 * 
	 * @param prefix
	 * @param suffix
	 * @param data
	 * @return
	 */
	public static void saveOutputFile(String prefix, String suffix, byte[] data)
			throws IOException {
		saveFile(createOutputFile(prefix, suffix), data);
	}

	/**
	 * Creates a file object that points to a test "output" file
	 * 
	 * Test output files will be picked up by OUnit report generator
	 * and displayed to the user if possible.
	 * 
	 * @param prefix
	 * @param suffix
	 * @return
	 */
	public static File createOutputFile(String prefix, String suffix) {
		File dir = getReportsDirectory();
		String fname = prefix + "-output." + suffix;
		return new File(dir, fname);
	}

	/**
	 * Save data to a file. Creates parent directories if needed
	 * 
	 * @param name
	 * @param data
	 * @throws IOException
	 */
	public static void saveFile(String name, byte[] data) throws IOException {
		saveFile(new File(name), data);
	}

	/**
	 * Save data to a file. Creates parent directories if needed
	 * 
	 * @param dir
	 * @param name
	 * @param data
	 * @throws IOException
	 */
	public static void saveFile(File dir, String name, byte[] data) throws IOException {
		saveFile(new File(dir, name), data);
	}

	/**
	 * Save data to a file. Creates parent directories if needed
	 * 
	 * @param file
	 * @param data
	 * @throws IOException
	 */
	public static void saveFile(File file, byte[] data) throws IOException {
		if (file.getParentFile() != null)
			file.getParentFile().mkdirs();

		FileOutputStream fs = new FileOutputStream(file);
		fs.write(data);
		fs.close();
	}
}