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

import java.io.File;

import org.apache.wicket.extensions.protocol.opaque.OpaqueQuestion;

public interface OunitQuestion extends OpaqueQuestion {

	public abstract String getRevision();

	/**
	 * Switch to another question revision.
	 * Please note that this will change the source
	 * directory.
	 * 
	 * @param revision new revision string
	 */
	public abstract void setRevision(String revision);

	/**
	 * Find a directory with question sources.
	 * It may point to a cached version.
	 *  
	 * @return an object pointing to source directory
	 */
	public abstract File getSrcDir();
}