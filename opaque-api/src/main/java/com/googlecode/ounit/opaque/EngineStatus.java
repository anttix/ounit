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

/*
 * API CLASS: This class is used in SOAP returns and should not be altered  
 */

/**
 * Generic status information about the question engine.
 * It's currently only used by Moodle OPAQE question type to test
 * the connection to the question engine.
 * However, in the future it could be used for load balancing purposes.
 * 
 * @author anttix
 */
public class EngineStatus {
	private String name;
	private String usedmemory;
	private int activesessions;
	
	private static final String DEFAULT_NAME = "Generic OPAQUE question engine";

	public EngineStatus() {
		name = DEFAULT_NAME; // Name must Exist in all Info replies.
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsedmemory() {
		return usedmemory;
	}

	public void setUsedmemory(String usedmemory) {
		this.usedmemory = usedmemory;
	}

	public int getActivesessions() {
		return activesessions;
	}

	public void setActivesessions(int activesessions) {
		this.activesessions = activesessions;
	}
}
