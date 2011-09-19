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

package org.apache.wicket.extensions.protocol.opaque;

import org.apache.wicket.request.resource.IResource;

/**
 * An OPAQUE specific Resource interface that can be used to bypass default
 * file versioning scheme. Useful for dynamically generated resources that may change
 * somewhat but the changes should not invalidate the cache. An example of such
 * a resource would be a downloadable ZIP archive. If it is regenerated, the
 * order of files in the index can change but it can still contain exactly the
 * same files.
 * 
 * @author anttix
 * 
 */
public interface IVersionedResource extends IResource {
	public String getVersionedName();
}