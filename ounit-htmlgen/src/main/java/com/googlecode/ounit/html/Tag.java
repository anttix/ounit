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

package com.googlecode.ounit.html;

import java.util.LinkedList;

/**
 * An HTML tag
 * 
 * Based on ideas borrowed from Java HTML Generator v0.9
 * http://artho.com/webtools/java/index.shtml
 * 
 * @author anttix
 */
public class Tag extends LinkedList<Object> {
	private static final long serialVersionUID = -5267385819039950390L;
	private String name;
    private String id = null;
    private String classes = null;
    private String attributes = null;
    
    public Tag(String name) {
    	this.name = name.toLowerCase();
    }
    
    /**
     * Render tag to HTML using prefix to indent each line
     * 
     * @param prefix
     * @return
     */
    public String render(String prefix) {
    	String rv = "";
    	String attr = "";
    	String inner = "";
    	
    	if(id != null)
    		attr += " id=\"" + id + "\"";
    	if(classes != null)
    		attr += " class=\"" + classes + "\"";
    	if(attributes != null)
    		attr += " " + attributes;
    	
    	for(Object i: this) {
    		String p = prefix + "  ";
    		if(i instanceof Tag) {
    			inner += ((Tag)i).render(p);
    		} else {
    			inner += p + escape(i) + "\n";
    		}
    	}
    	
    	rv += prefix + "<" + name + attr + ">\n";
    	rv += inner;
    	rv += prefix + "</" + name + ">\n";
    	
    	return rv;
    }
    
	protected String escape(Object o) {
		return o.toString()
				.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;");
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClasses() {
		return classes;
	}

	public void setClasses(String classes) {
		this.classes = classes;
	}

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}

	@Override
	public String toString() {
		return render("");
	}
}
