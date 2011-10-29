package com.googlecode.ounit.test.moodle;

public interface IEnginePage {

	public abstract void setupEngineUrl(String url);

	public abstract void testConnection();

}