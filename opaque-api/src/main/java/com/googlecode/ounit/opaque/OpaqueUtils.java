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

package com.googlecode.ounit.opaque;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpaqueUtils {
	
	// Relevant constants from moodle/lib/questionlib.php
	
	/**
	 * The student has requested that the responses should be saved but not submitted or validated
	 */
	public static final int MOODLE_EVENTSAVE = 2;
	/**
	 * Moodle has graded the responses. A CLOSE event can be changed to a CLOSEANDGRADE event by Moodle.
	 */
	public static final int MOODLE_EVENTCLOSEANDGRADE = 6;
	/**
	 * The student response has been submitted but it has not yet been marked.
	 * Opaque sees this event when quiz runs in "adaptive" mode and student
	 * hits "submit this page"
	 */
	public static final int MOODLE_EVENTSUBMIT = 7;
	/**
	 * The response has been submitted and the session has been closed, either because the student
	 * requested it or because Moodle did it (e.g. because of a timelimit). The responses have not
	 * been graded.
	 */
	public static final int MOODLE_EVENTCLOSE = 8;
	
	public static String makeQuestionXML(QuestionInfo qi) {
		return
			"<questionmetadata>\n" +
			"  <scoring>\n" +
			"    <marks>" + qi.getMaxScore() + "</marks>\n"+
			"  </scoring>\n" +
			"  <plainmode>" + qi.getPlainmode() + "</plainmode>\n" +
			(qi.getTitle() != null ? "  <title>" + qi.getTitle() + "</title>\n" : "") +
			"</questionmetadata>";
	}

	public static String makeEngineXML(EngineStatus es) {
		return
			"<engineinfo>\n" +
			"  <name>" + es.getName() + "</name>" +
			"  <usedmemory>" + es.getUsedmemory() + "</usedmemory>" +
			"  <activesessions>" + es.getActivesessions() + "</activesessions>" +
			"</engineinfo>";
	}

	/**
	 * Make a map out of parameter arrays.
	 * 
	 * @param names names of the parameters
	 * @param values corresponding values of the parameters
	 * @return a map with names as keys and values as values
	 */
	public static Map<String, String> makeMap(String[] names, String[] values) {
		if (names.length != values.length)
			throw new IllegalArgumentException(
					"The lengths of both arrays must match");

		Map<String, String> rv = new HashMap<String, String>(names.length);
		for (int i = 0; i < names.length; i++) {
			rv.put(names[i], values[i]);
		}
		return rv;
	}

	/**
	 * Get the amount of memory used by JVM.
	 * 
	 * @return a String representation of used memory in MiB
	 */
	public static String getJvmMem() {
		Runtime r = Runtime.getRuntime();
		double memUsed = r.totalMemory() - r.freeMemory();
		return String.format("%.1f MiB", memUsed / 1024 / 1024);
	}
	
	/**
	 * Generate an array of resources that are not yet present on the client.
	 * 
	 * @param resources a list of resources to consider
	 * @param resourceNames an array of resource fileNames already present on client
	 * @return an array of resources that were not present in resourceNames
	 */
	public static Resource [] generateNewResources(List<Resource> resources, String [] resourceNames) {
		List <Resource> rv;
		
		if(resourceNames == null || resourceNames.length <= 0) {
			/* There are no cached resources, return everything there is */
			rv = resources;
		} else {
			rv = new ArrayList<Resource>(resources.size());
			List<String> names = Arrays.asList(resourceNames);
			for(Resource r: resources) {
				if(!names.contains(r.getFilename())) {
					rv.add(r);
				}
			}
		}
		
		return rv.toArray(new Resource[0]);
	}
	
	/**
	 * Format an array to a string representation. Eg [1, 2, 3]
	 * Useful for debugging.
	 * 
	 * @param arr
	 * @return
	 */
	public static String formatArray(Object [] arr) {
		if(arr != null) {
			return Arrays.asList(arr).toString();
		} else {
			return "null";
		}
	}
}
