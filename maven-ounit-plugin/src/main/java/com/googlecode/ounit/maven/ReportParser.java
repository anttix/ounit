/*
 * ounit - an OPAQUE compliant framework for Computer Aided Testing
 *
 * Copyright (C) 2010-2011  Antti Andreimann
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.googlecode.ounit.maven;

import java.io.File;
import java.util.List;

public interface ReportParser {
	/**
	 * Find report files in specified directories and parse them.
	 * 
	 * @param dirs directories to look for report files
	 * @return aggregated results of the report files
	 */
	public abstract TestResults parseReportFiles(List<File> dirs) throws Exception;
}