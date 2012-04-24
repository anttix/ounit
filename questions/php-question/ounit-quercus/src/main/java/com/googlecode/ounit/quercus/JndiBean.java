package com.googlecode.ounit.quercus;

import org.eclipse.jetty.plus.jndi.Resource;
import javax.naming.NamingException;
import org.eclipse.jetty.util.component.LifeCycle;

public class JndiBean implements LifeCycle {
	String name;
	Object object;
	Resource jndiResource;
	
	boolean started = false;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	@Override
	public void start() {
		try {
			jndiResource = new Resource(name, object);
			started = true;
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void stop() {
		//jndiResource.release();		
		started = false;
	}

	@Override
	public void addLifeCycleListener(Listener arg0) {
	}

	@Override
	public boolean isFailed() {
		return false;
	}

	@Override
	public boolean isRunning() {
		return false;
	}

	@Override
	public boolean isStarted() {
		return started;
	}

	@Override
	public boolean isStarting() {
		return false;
	}

	@Override
	public boolean isStopped() {
		return !started;
	}

	@Override
	public boolean isStopping() {
		return false;
	}

	@Override
	public void removeLifeCycleListener(Listener arg0) {
	}
}
// public class JndiDS extends org.eclipse.jetty.plus.jndi.Resource implements
// LifeCycle {
// public JndiDS() throws javax.naming.NamingException {
// //super("java:comp/env/jdbc/ounitDS", new org.hsqldb.jdbc.JDBCDataSource());
// super("java:comp/env/jdbc/ounitDS", new org.hsqldb.jdbc.JDBCDataSource());
// System.out.println("WTF!?");
// }
// }
