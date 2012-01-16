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

package com.googlecode.ounit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.springframework.util.PropertyPlaceholderHelper;

public class OunitConfig {
	
	// TODO: Consider moving constants to a separate class
	//       so they can be shared with the Maven plugin.

	public static final String DESCRIPTION_FILE     = "description/index.html";
	public static final String RESULTS_FILE         = "target/ounit-reports/results.html";
	public static final String MARKS_FILE           = "target/ounit-reports/marks.properties";
	public static final String DOWNLOAD_FILE		= "download.zip";
	public static final String DEFAULT_PROPERTY     = "default";
	public static final String MARKS_PROPERTY       = "ounit.marks";
	public static final String TITLE_PROPERTY       = "ounit.title";
	public static final String RWFILES_PROPERTY     = "ounit.editfiles";
	public static final String ATTEMPTS_PROPERTY    = "ounit.attempts";
	public static final String PREPARE_LOG          = "prepare.log";
	public static final String BUILD_LOG            = "build.log";
	public static final String SRCDIR               = "src";
	public static final int    DEFAULT_ATTEMPTS		= 3;
	
	public static final String SESSION_DIR		    = "sessions";
	public static final String SRC_CACHE_DIR	    = "src-cache";
	public static final String REPO_DIR				= "questions";
	
	public static final String OUNIT_PROPERTIES		= "ounit.properties";
	public static final String WORKDIR_PROPERTY     = "ounit.workdir";
	public static final String BASEURL_PROPERTY     = "ounit.baseurl";
	public static final String SCM_TIMEOUT_PROPERTY = "ounit.timeout";
	public static final String SCM_TTL_PROPERTY 	= "ounit.ttl";
	
	public static String WORKDIR;
	public static String BASEURL;
	public static int SCM_TIMEOUT;
	public static int SCM_TTL;
	
	// TODO: VERSION

	// Populate
	static {
		InputStream in;
		Properties defaults = new Properties();
		in = OunitConfig.class.getResourceAsStream(OUNIT_PROPERTIES);
		try {
			defaults.load(in);
			in.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		Properties conf = new Properties(defaults);
		try {
			in = new FileInputStream(OUNIT_PROPERTIES);
			conf.load(new FileInputStream(OUNIT_PROPERTIES));
			in.close();
		}
		catch(Exception e) {
			// TODO: Log a debug message?
		}
		conf.putAll(System.getProperties());
		PropertyPlaceholderHelper ph = new PropertyPlaceholderHelper("${","}");
		WORKDIR = ph.replacePlaceholders(conf.getProperty(WORKDIR_PROPERTY), conf);
		BASEURL = ph.replacePlaceholders(conf.getProperty(BASEURL_PROPERTY), conf);
		SCM_TIMEOUT = Integer.parseInt(conf.getProperty(SCM_TIMEOUT_PROPERTY));
		SCM_TTL = Integer.parseInt(conf.getProperty(SCM_TTL_PROPERTY));
	}
}