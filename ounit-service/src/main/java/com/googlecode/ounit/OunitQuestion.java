package com.googlecode.ounit;

import java.io.File;

public class OunitQuestion {
	String id;
	String version;
	String baseURL;
	String revision;
	File srcDir;
	
	public OunitQuestion(String id, String version, String baseURL) {
		this.id = id;
		this.version = version;
		this.baseURL = baseURL;
		this.revision = findHeadRevision();
	}
	
	public String getId() {
		return id;
	}
	
	public String getVersion() {
		return version;
	}
	
	public String getBaseURL() {
		return baseURL;
	}
		
	public String getRevision() {
		return revision;
	}
	
	public void setRevision(String revision) {
		if (revision != null && !revision.equals(this.revision)) {
			this.revision = revision;
			srcDir = null;
		}
	}
	
	public File getSrcDir() {
		if(srcDir == null)
			fetchQuestion();

		return srcDir;
	}
	
	/**
	 * Find latest revision for question in Database.
	 * 
	 * @return
	 */
	protected String findHeadRevision() {
		return "kala";
	}
	
	/**
	 * Fetch question from Question Database.
	 */
	protected void fetchQuestion() {
		// TODO: Make it configurable etc.
		//return new File("/srv/ounit/questions/" + questionID);
		srcDir = new File("/home/anttix/products/ounit/questions/" + id);
	}
}
