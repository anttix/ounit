package com.googlecode.ounit.test.moodle;

public interface ILoginPage {

	public abstract IHomePage loginAs(String username, String password);

}