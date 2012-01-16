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

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import org.apache.wicket.Page;

import com.googlecode.ounit.opaque.OpaqueException;
import com.googlecode.ounit.opaque.QuestionInfo;

@WebService(serviceName="MockWicketService")
@SOAPBinding(style = Style.RPC)
public class MockWicketService extends WicketOpaqueService {
	public MockWicketService() {
		super(new OpaqueApplication() {
			@Override
			public Class<? extends Page> getHomePage() {
				return MockHomePage.class;
			}

			@Override
			public OpaqueQuestion fetchQuestion(final String id,
					final String version, final String baseUrl)
					throws OpaqueException {

				// Mock engine responds to any question ID and Version.				
				return new OpaqueQuestion() {
					@Override
					public String getId() {
						return id;
					}
					@Override
					public String getVersion() {
						return version;
					}
					@Override
					public String getBaseUrl() {
						return baseUrl;
					}
					@Override
					public QuestionInfo getInfo() {
						return new QuestionInfo();
					}
				};
			}
		});
	}
}