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

	/*
	@Override
	public StartReturn start(String questionID, String questionVersion,
			String questionBaseURL, String[] initialParamNames,
			String[] initialParamValues, String[] cachedResources)
			throws OpaqueException {
		
		log.debug("start({}, {}, {}, {}, {}, {})", new Object[] { questionID,
				questionVersion, questionBaseURL, initialParamNames,
				initialParamValues, cachedResources });

		// Do not allow more than one thread to mess with a single session
		SessionLock lock = null;
		assert lock != null : "Engine session was not set up properly";
		
		synchronized (lock) {
			StartReturn rv = super.start(questionID, questionVersion,
					questionBaseURL, initialParamNames, initialParamValues,
					cachedResources);
			return rv;
		}				
	}

	@Override
	public ProcessReturn process(String questionSession, String[] names,
			String[] values) throws OpaqueException {
		
		log.debug("process({}, {}, {}, {}, {}, {})", new Object[] {
				questionSession, names, values });
		
		// Do not allow more than one thread to mess with a single session
		SessionLock lock = null;
		assert lock != null : "Stale session";
		
		synchronized (lock) {

			ProcessReturn rv = super.process(questionSession, names, values);
			
			// FIXME: Ugly, ugly, ugly hack!
			String fname = context.getDownloadFileName();
			if (fname != null && !context.getCachedResources().contains(fname)) {
				try {
					FileInputStream is = new FileInputStream(context.getDownloadFile());
					int len = (int) is.getChannel().size();
					byte[] buf = new byte[len];
					is.read(buf);
					is.close();
					Resource r = new Resource(fname,
							"application/octet-stream", buf);
					Resource[] rs = rv.getResources();
					int rlen = rs == null ? 0 : rs.length;
					Resource[] newrs = new Resource[rlen + 1];
					if (rlen > 0)
						System.arraycopy(rs, 0, newrs, 0, rlen);
					newrs[rlen] = r;
					rv.setResources(newrs);
				} catch (Exception e) {
					throw new RuntimeException(
							"Error creating download resource", e);
				}
			}
			
			return rv;
		}
	}

	@Override
	public void stop(String questionSession) throws OpaqueException {
		log.debug("stop({})", questionSession);

		SessionLock lock = null;
		synchronized(lock) {
			super.stop(questionSession);
			// FIXME: This should be in some listener
			deleteDirectory(new File(OunitSession.sessDir, questionSession));
		}
	}
	*/

}
