package com.googlecode.ounit.test.moodle;

import static org.junit.Assume.*;

public class SetupHelper {
	
	protected IHomePage homePage;
	protected String serviceAddress;
	protected String moodleUrl, moodleUser, moodlePass;
	
	public SetupHelper(String propPrefix) {		
		assumeNotNull(propPrefix);

		
		moodleUrl = System.getProperty(propPrefix + "url");
		moodleUser = System.getProperty(propPrefix + "user", "admin");
		moodlePass = System.getProperty(propPrefix + "pass");		

		if(moodleUrl == null)
			System.out.println(propPrefix + "url property not set, skipping tests");
		assumeNotNull(moodleUrl);

		if(moodlePass == null)
			System.out.println(propPrefix + "pass property not set, skipping tests");
		assumeNotNull(moodlePass);
	}

	public void setupMoodle(IHomePage homePage, String serviceAddress) {
		assumeNotNull(homePage, serviceAddress, moodleUrl, moodleUser, moodlePass);

		this.homePage = homePage;
		this.serviceAddress = serviceAddress;
		homePage.setBaseUrl(moodleUrl);

		ILoginPage loginPage = homePage.gotoLoginPage();
		loginPage.loginAs(moodleUser, moodlePass);
		IEnginePage enginePage = homePage.gotoEnginePage();
		enginePage.setupEngineUrl(serviceAddress);
	}
	
	public IQuizPage setupQuiz() {
		assumeNotNull(homePage);
		
		ICoursePage coursePage = homePage.gotoTestCourse();
		IQuizPage quizPage = coursePage.gotoTestQuiz();
		quizPage.doPreview();
		
		return quizPage;
	}

	public IHomePage getHomePage() {
		return homePage;
	}
}
