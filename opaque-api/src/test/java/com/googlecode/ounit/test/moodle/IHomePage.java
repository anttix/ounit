package com.googlecode.ounit.test.moodle;

public interface IHomePage {

	public abstract ILoginPage gotoLoginPage();

	public abstract ICoursePage gotoTestCourse();

	public abstract ICoursePage createTestCourse();

	public abstract IEnginePage gotoEnginePage();
	
	public abstract String getBaseUrl();
	
	public abstract void setBaseUrl(String baseUrl);

}