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

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import org.apache.wicket.extensions.protocol.opaque.WicketOpaqueService;
//import com.googlecode.ounit.opaque.OpaqueException;

@WebService(serviceName="Ounit")
@SOAPBinding(style = Style.RPC)
public class OunitService extends WicketOpaqueService {	
	private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());
	
	public OunitService() {
		super(new OunitApplication());
		// TODO: rv.setName("OUnit question engine");

		log.debug("OunitService()");
	}
}
