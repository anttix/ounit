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
 * An OPAQUE specific Resource interface that can be used to bypass the default
 * file versioning scheme. Useful for dynamically generated resources that may
 * change somewhat but the changes should not invalidate the cache. An example
 * of such a resource would be a ZIP archive. If it is regenerated the archive
 * header may change but it will still contain exactly the same files.
 * Calculating an MD5 digest will result in a different checksum and another
 * resource will be sent to the LMS despite the fact that files inside the
 * archive have not changed at all. By using this interface you can use the
 * contents of the archive to calculate the checksum and thus avoid polluting
 * the LMS cache with multiple copies of essentially the same file. Please note
 * that the uniqueness of each resource is determined by the versioned file name
 * returned by this interface so make sure you generate names that do not
 * collide. Use with caution!
 * 
 * @author anttix
 * 
 */
public interface IVersionedResource extends IResource {
	public String getVersionedName();
}