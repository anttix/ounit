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

package com.googlecode.ounit.test.moodle;

public class MoodleParams {
	public final static String engineName       = "Opaque Testing Engine";
	public final static String courseName       = "Opaque Testing Course";
	public final static String quizName         = "Opaque Testing Quiz";
	public final static String questionIdPrefix = "opaque.selftest.";
	public final static String questionVersion  = "1.0";
	
	public static void log(String s) {
		System.err.println("*********************************");
		System.err.println("    " + s);
		System.err.println("*********************************");
	}

}
