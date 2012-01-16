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

package com.googlecode.ounit.executor;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class Util {
	/**
	 * A simple project without any tests.
	 */
	public static final String TP1 = "/test-project-1";	
	/**
	 * A simple project with compile errors.
	 */
	public static final String TP2 = "/test-project-2";
	/**
	 * A simple project with a test 
	 */
	public static final String TP3 = "/test-project-3";
	/**
	 * A simple project with test errors
	 */
	public static final String TP4 = "/test-project-4";
	
	public static File fromResources(String name) {
		File f;
		URL url = Util.class.getResource(name);
		try {
			f = new File(url.toURI());
		} catch (URISyntaxException e) {
			f = new File(url.getPath());
		}
	
		return f;
	}

	public static boolean deleteDirectory(String path) {
		return deleteDirectory( new File(path) );
	}

	public static boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}
}