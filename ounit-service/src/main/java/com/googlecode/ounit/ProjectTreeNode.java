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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;

/**
 * A node of a project view tree.
 * Also acts as a model to load and save file data.
 * 
 * @author anttix
 *
 */
public class ProjectTreeNode implements Serializable, Comparable<ProjectTreeNode> {
	private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;
	private String name;
	private File pathname;
	private boolean readonly;
	private List<ProjectTreeNode> children = new ArrayList<ProjectTreeNode>();
	private transient WeakReference<Component> editorRef;
	
	ProjectTreeNode(String name, File pathname) {
		this.name = name;
		this.pathname = pathname;
	}

	public void addChild(ProjectTreeNode node) {
		children.add(node);
	}
	
	public void removeChild(ProjectTreeNode node) {
		children.remove(node);
	}

	/**
	 * Sort directories first, if both are dirs or files, sort
	 * lexiographically by name
	 */
	public int compareTo(ProjectTreeNode o) {
		if(o == null) return -1;
		
		boolean dir1 = (pathname == null)   ? false : pathname.isDirectory();
		boolean dir2 = (o.pathname == null) ? false : o.pathname.isDirectory();
		
		if(dir1 == dir2)
			return name.compareTo(o.name);
		else
			return dir1 ? -1 : 1;
	}
	
	public String getName() {
		return name;
	}

	public File getPathname() {
		return pathname;
	}
	
	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public List<ProjectTreeNode> getChildren() {
		return children;
	}

	public void setChildren(List<ProjectTreeNode> children) {
		this.children = children;
	}
	
	public Component getEditor() {
		if(editorRef != null)
			return editorRef.get();
		else
			return null;
	}
	
	public void setEditor(Component editor) {
		editorRef = new WeakReference<Component>(editor);
	}
	
	public String getFileContents() throws IOException {
		log.debug("Reading file {}", pathname);
		
		FileInputStream stream = new FileInputStream(pathname);
		try {
		    FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			return Charset.forName("UTF-8").decode(bb).toString();
		}
		finally {
		  stream.close();
		}
	}
	
	public void setFileContents(String text) throws IOException {
		if(text == null) return;
		
		log.debug("Writing file {}", pathname);
		
		long mtime = pathname.lastModified();
		long currentTime = System.currentTimeMillis();

		if (currentTime < mtime) {
			// Modification time in future. Unlink the stupid file!
			log.info("{} has modification time in the future.", pathname);
			pathname.delete();
		}
				
		FileOutputStream stream = new FileOutputStream(pathname);
		try {
		    FileChannel fc = stream.getChannel();
			fc.write(Charset.forName("UTF-8").encode(text).asReadOnlyBuffer());
		}
		finally {
		  stream.close();
		}
	}
}
