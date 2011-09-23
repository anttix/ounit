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

public class Pre extends Tag {
	private static final long serialVersionUID = 9172203971058435589L;
	
	public Pre() {
		super("pre");
	}
	
	@Override
	public String render(String prefix) {
    	String rv = "";
    	String attr = "";
    	String inner = "";

    	if(getId() != null)
    		attr += " id=\"" + getId() + "\"";
    	if(getClasses() != null)
    		attr += " class=\"" + getClasses() + "\"";
    	if(getAttributes() != null)
    		attr += " " + getAttributes();
    	
    	
    	for(Object i: this) {
    		if(i instanceof Tag) {
    			inner += ((Tag)i).render("");
    		} else {
    			inner += escape(i);
    		}
    	}
    	
    	rv += prefix + "<" + getName() + attr + ">\n";
    	rv += inner;
    	rv += prefix + "</" + getName() + ">\n";
    	
    	return rv;
	}
}