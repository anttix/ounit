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
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ProjectTree implements Serializable {
	public final String [] resourceDirs = {"main", "test"};
	
	private static final long serialVersionUID = 1L;
	
	private List<ProjectTreeNode> trunk;
	private List<ProjectTreeNode> rwNodes = new ArrayList<ProjectTreeNode>();
	private List<ProjectTreeNode> roNodes = new ArrayList<ProjectTreeNode>();

	private FileFilter noHiddenFilter;
	
	/**
	 * Scan directory and construct a project tree.
	 * All files are marked as editable (unless filesystem permissions decide othervise)
	 * 
	 * @param srcDir
	 */
	public ProjectTree(File srcDir) {
		this(srcDir, null, null);
	}
	
	/**
	 * Scan directory and construct a project tree using provided filters.
	 * If a file is denied by both, the editing and view filter it is not included
	 * in the tree. Null filters are considered to "allow all". 
	 * 
	 * @param srcDir
	 * @param editFilter
	 * @param viewFilter
	 */
	public ProjectTree(File srcDir, final FilenameFilter editFilter,
			final FilenameFilter viewFilter) {
		
		List<String> rdList = Arrays.asList(resourceDirs);
		final List<File> rwFiles = new LinkedList<File>();
		final List<File> roFiles = new LinkedList<File>();
		
		noHiddenFilter = new FileFilter() {
			public boolean accept(File pathname) {
				if(pathname.isHidden()) return false;
				if(pathname.isDirectory()) return true;
				File dir = pathname.getParentFile();
				String name = pathname.getName();
				
				if (pathname.canWrite()
						&& (editFilter == null || editFilter.accept(dir, name)))
					rwFiles.add(pathname);
				else if (pathname.canRead()
						&& (viewFilter == null || viewFilter.accept(dir, name)))
					roFiles.add(pathname);
				else
					return false;
				
				return true;
			}
		};
		
		final Map<String, ProjectTreeNode> trunkNodes = new HashMap<String, ProjectTreeNode>();
		
		for(String rd: rdList) {
			File dir = new File(srcDir, rd);
			if(dir.isDirectory()) {
				populateTrunkDir(dir, srcDir, trunkNodes);
			}
		}
		trunk = new ArrayList<ProjectTreeNode>(trunkNodes.values());
		
		/* Now trunk contains only nodes that should be handled in java package style */
		for(ProjectTreeNode trunkNode: trunk) {
			for(ProjectTreeNode n: trunkNode.getChildren()) {
				assert n.getChildren() == null: "Inconsistent tree: initial trunk node has children";
				if(n.getPathname().isDirectory())
					populatePackageEnabledNode(n);
			}
		}
		
		for(File f: srcDir.listFiles(noHiddenFilter)) {
			if(!rdList.contains(f.getName())) {
				ProjectTreeNode newNode = new ProjectTreeNode(f.getName(), f);
				trunk.add(newNode);
				if(f.isDirectory()) {
					populateNormalNode(newNode);
				}
			}
		}
		Collections.sort(trunk);
		
		// Free filter, initialization is complete so it's no longer needed
		noHiddenFilter = null;
		
		postProcessTree(trunk, rwFiles, roFiles);		
	}

	private void populateTrunkDir(File dir, File srcDir, Map<String, ProjectTreeNode> trunkNodes) {
		File [] files = dir.listFiles(noHiddenFilter);
		for(File f: files) {
			String parentName, name;
			if(f.isDirectory()) {
				parentName = f.getName();
				name = f.getPath().replace(srcDir.getPath() + File.separator, "");
			} else {
				parentName = dir.getName();
				name = f.getName();
			}		
			ProjectTreeNode parentNode = trunkNodes.get(parentName);
			if(parentNode == null) {
				parentNode = new ProjectTreeNode(parentName, dir);
				trunkNodes.put(parentName, parentNode);
			}
			parentNode.addChild(new ProjectTreeNode(name, f));
		}
	}
	
	private void populatePackageEnabledNode(ProjectTreeNode n) {
		Map<String, ProjectTreeNode> packageNodes = new HashMap<String, ProjectTreeNode>();
		populatePackageEnabledDir(n.getPathname(), n.getPathname(), packageNodes);
		List<ProjectTreeNode> defaultNodes = null;
		if(packageNodes.get(null) != null) {
			defaultNodes = packageNodes.get(null).getChildren();
			packageNodes.remove(null);
		}
		List<ProjectTreeNode> newNodes = new ArrayList<ProjectTreeNode>(packageNodes.values());
		Collections.sort(newNodes);
		if(defaultNodes != null)
			newNodes.addAll(defaultNodes);
		for(ProjectTreeNode i: newNodes)
			Collections.sort(i.getChildren());
		n.setChildren(newNodes);
	}

	private void populatePackageEnabledDir(File dir, File baseDir,
			Map<String, ProjectTreeNode> packageNodes) {
		
		File [] files = dir.listFiles(noHiddenFilter);
		for(File f: files) {
			if(f.isDirectory()) {
				populatePackageEnabledDir(f, baseDir, packageNodes);
			} else {
				String parentName = null;
				if (!dir.equals(baseDir)) {
					parentName = dir.getPath().replace(
							baseDir.getPath() + File.separator, "").replace(
							File.separatorChar, '.');
				}
				String name = f.getName();
				ProjectTreeNode parentNode = packageNodes.get(parentName);
				if(parentNode == null) {
					parentNode = new ProjectTreeNode(parentName, dir);
					packageNodes.put(parentName, parentNode);
				}
				parentNode.addChild(new ProjectTreeNode(name, f));
			}
		}
	}

	private void populateNormalNode(ProjectTreeNode n) {
		File dir = n.getPathname();
		File [] files = dir.listFiles(noHiddenFilter);
		for(File f: files) {
			ProjectTreeNode newNode = new ProjectTreeNode(f.getName(), f); 
			n.addChild(newNode);
			if(f.isDirectory()) {
				populateNormalNode(newNode);
			}
		}
		Collections.sort(n.getChildren());
	}
	
	/**
	 * Crawl tree, remove empty nodes, make ro and rw leaf node lists.
	 * 
	 * FIXME: This could be a lot more efficient, we shouldn't collect rw/ro nodes to
	 *        temporary lists, we could store that information in the node.
	 */
	private void postProcessTree(List<ProjectTreeNode> list,
			List<File> rwFiles, List<File> roFiles) {
		
		List<ProjectTreeNode> delNodes = new ArrayList<ProjectTreeNode>();
		
		for(ProjectTreeNode tn: list) {
			File f = tn.getPathname();
			if(tn.getChildren() != null) {
				assert !f.isDirectory(): "Inconsistent tree: node with children is not a directory";
				postProcessTree(tn.getChildren(), rwFiles, roFiles);
				if(tn.getChildren().size() == 0)
					tn.setChildren(null);
			}
			
			if(tn.getChildren() == null) {
				if(f.isDirectory()) {
					delNodes.add(tn);
				} else {
					int i = rwFiles.indexOf(tn.getPathname());
					if(i != -1) {
						rwNodes.add(tn);
						rwFiles.remove(i);
					} else {
						i = roFiles.indexOf(tn.getPathname());
						if(i != -1) {
							roNodes.add(tn);
							tn.setReadonly(true);
							roFiles.remove(i);
						} else {
							assert false: "Inconsistent tree: file not found in rw/ro lists";
						}
					}
				}
			}
		}
		for(ProjectTreeNode tn: delNodes) {
			list.remove(tn);
		}
	}
	
	public List<ProjectTreeNode> getTrunk() {
		return trunk;
	}

	public List<ProjectTreeNode> getRwNodes() {
		return rwNodes;
	}

	public List<ProjectTreeNode> getRoNodes() {
		return roNodes;
	}
	
	@Override
	public String toString() {
		return toString(trunk, "");
	}
	
	private String toString(List<ProjectTreeNode> list, String prefix) {
		String rv = "";
		for(ProjectTreeNode tn: list) {
			rv += prefix + tn.getName() + (tn.isReadonly() ? " (ro)" : "") + "\n";
			if(tn.getChildren() != null)
				rv += toString(tn.getChildren(), prefix + "\t");
		}
		return rv;
	}
}
