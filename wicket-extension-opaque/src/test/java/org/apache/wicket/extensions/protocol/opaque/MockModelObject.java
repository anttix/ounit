package org.apache.wicket.extensions.protocol.opaque;

import java.io.Serializable;

public class MockModelObject implements Serializable {
	private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;
	private int nr = -1;

	public MockModelObject() {
		log.debug("MockModelObject()");
	}
	
	public int getNr() {
		log.debug("getNr()");
		return nr;
	}

	public void setNr(int nr) {
		log.debug("setNr({})", nr);
		this.nr = nr;
	}
}
