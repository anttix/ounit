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

import javax.servlet.ServletConfig;
import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;

/**
 * A servlet to publish the service without any of the "spring stuff"
 * http://cxf.apache.org/docs/servlet-transport.html
 * 
 * @author anttix
 *
 */
public class OunitServlet extends CXFNonSpringServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void loadBus(ServletConfig sc) {
		super.loadBus(sc);

        Bus bus = this.getBus();
        BusFactory.setDefaultBus(bus); 
        Endpoint.publish("/OunitService", new OunitService());           
    }
	
	@Override
	public void destroy() {
		// TODO: gracefully shut down OU executor thread pool
		//oe.shutdown();
		super.destroy();
	}
}