package com.googlecode.ounit.opaque;

/**
 * Generic status information about the question engine.
 * It's currently only used by Moodle OPAQE question type to test
 * the connection to the question engine.
 * However, in the future it could be used for load balancing purposes.
 * 
 * @author anttix
 */
public class EngineStatus {
	private String name;
	private String usedmemory;
	private int activesessions;
	
	private static final String DEFAULT_NAME = "Generic OPAQUE question engine";

	public EngineStatus() {
		name = DEFAULT_NAME; // Name must Exist in all Info replies.
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsedmemory() {
		return usedmemory;
	}

	public void setUsedmemory(String usedmemory) {
		this.usedmemory = usedmemory;
	}

	public int getActivesessions() {
		return activesessions;
	}

	public void setActivesessions(int activesessions) {
		this.activesessions = activesessions;
	}
}
