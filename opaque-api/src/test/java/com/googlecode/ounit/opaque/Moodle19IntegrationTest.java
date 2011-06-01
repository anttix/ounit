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

package com.googlecode.ounit.opaque;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import static org.hamcrest.text.StringContains.containsString;
import static org.hamcrest.CoreMatchers.*;

import org.openqa.selenium.support.PageFactory;

import com.googlecode.ounit.test.moodle19.*;

/**
 * Test {@link MockOpaqueService} with Moodle 1.9.
 * <p>
 * To run these tests, a <a href="http://www.moodle.org">Moodle</a> 1.9.x server with
 * <a href="http://docs.moodle.org/en/Opaque_question_type">Opaque</a> question type
 * installed must be configured at localhost.
 * </p>
 * <p>
 * A number of properties have to be set to communicate the server URL and admin
 * credentials. This can be done from the command line:
 * </p>
 * <pre>
 * mvn -Dmoodle19.url=http://localhost/moodle -Dmoodle19.user=admin -Dmoodle19.pass=password test
 * </pre>
 * Or by adding the following block into maven settings file (normally ~/.m2/settings.xml):
 * <pre> 
 *   &lt;profile&gt;
 *     &lt;id&gt;moodle&lt;/id&gt;
 *     &lt;activation&gt;
 *       &lt;activeByDefault&gt;true&lt;/activeByDefault&gt;
 *     &lt;/activation&gt;
 *     &lt;properties&gt;
 *       &lt;moodle19.url&gt;http://localhost/moodle/1.9.12&lt;/moodle19.url&gt;
 *       &lt;moodle19.user&gt;admin&lt;/moodle19.user&gt;
 *       &lt;moodle19.pass&gt;moodlepass&lt;/moodle19.pass&gt;
 *     &lt;/properties&gt;
 *   &lt;/profile&gt;
 * </pre>
 * See 
 *  <a href="http://maven.apache.org/settings.html">Maven settings reference</a> for details.
 *  
 * @author anttix
 *
 */
public class Moodle19IntegrationTest extends TestBase {
	public final static String propPrefix = "moodle19.";
	
	protected static String moodleUrl  = System.getProperty(propPrefix + "url");
	protected static String moodleUser = System.getProperty(propPrefix + "user"); 
	protected static String moodlePass = System.getProperty(propPrefix + "pass"); 
	
	static {
		// Fill defaults
		if(moodleUser == null) moodleUser = "admin";
		MoodleParams.baseUrl = moodleUrl;
	}

	private MockQuestionPage mockPage = PageFactory.initElements(driver, MockQuestionPage.class);
	private static HomePage homePage;
	
	public static HomePage loginToMoodle() {
		assumeNotNull(moodleUrl, moodleUser, moodlePass);
		
		HomePage homePage = PageFactory.initElements(driver, HomePage.class);
		LoginPage loginPage = homePage.gotoLoginPage();
    	loginPage.loginAs(moodleUser, moodlePass);
 
    	return homePage;
	}
	
	public static void setupMoodle() {
    	//driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		homePage = loginToMoodle();
		EnginePage enginePage = homePage.gotoEnginePage();
		enginePage.setupEngineUrl(serviceAddress);
    }
	
	public static QuizPage setupQuiz() {
		assumeNotNull(homePage);
		
    	CoursePage coursePage = homePage.gotoTestCourse();
    	QuizPage quizPage = coursePage.gotoTestQuiz();
    	quizPage.doPreview();
    	
    	return quizPage;
	}
    
    @BeforeClass
    public static void setupTestEnvironment() {
		if(moodleUrl == null)
			System.out.println(propPrefix + "url property not set, skipping tests");
		assumeNotNull(moodleUrl);
		if(moodlePass == null)
			System.out.println(propPrefix + "pass property not set, skipping tests");
		assumeNotNull(moodlePass);

    	startServer();
    	openBrowser();
    	setupMoodle();
    }
	
	@Test
	public void shouldPassConnectionTest() {
		// Given I am on engine test page,
		EnginePage page = homePage.gotoEnginePage();

		// when I click on connection test button
		page.testConnection();

		// then I should see the question engine information block
		String html = driver.getPageSource();
		assertThat(html, containsString("<dt>name</dt>"));
		assertThat(html, containsString("<dt>usedmemory</dt>"));
		assertThat(html, containsString("<dt>activesessions</dt>"));
	}
	
	@Test
	public void shouldDisplayEngineQuestionsProperly() {
		// Given I am on a quiz that uses OPAQE questions
		QuizPage quizPage = setupQuiz();

		// then I should see a correctly rendered mock question on all three pages
		mockPage.validate();
		quizPage.navigate(2);
		mockPage.validate();
		quizPage.navigate(3);
		mockPage.validate();
	}
	
	@Test
	public void shouldGradeProperly() {
		// Given I am on a quiz that uses OPAQUE questions
		QuizPage quizPage = setupQuiz();
		
		// when I enter an answer that the engine refuses to grade
		mockPage.answer(6);
		
		// then my last answer should be displayed
		assertThat(mockPage.getLastAnswer(), is("6"));
		
		// when I navigate away from the page and come back later
		quizPage.navigate(2);
		quizPage.navigate(1);
		
		// then last answer should equal to what was typed into the answer box (empty string)
		assertThat(mockPage.getLastAnswer(), is(""));

		// when I enter a sequence of answers that results in a grade
		mockPage.answer(5);
		mockPage.answer(1);
		
		// then moodle should record that grade
		assertThat(quizPage.getGrade(), is(1));
		
		// when I navigate to second page and answer a question
		quizPage.navigate(2);
		mockPage.answer(7);
		mockPage.answer(2);
		
		// then moodle should record that grade
		assertThat(quizPage.getGrade(), is(2));
		
		// when I navigate to third page and answer a question
		quizPage.navigate(3);
		mockPage.answer(3);
		
		// then moodle should record that grade
		assertThat(quizPage.getGrade(), is(3));
		
		// when I navigate back to first page
		quizPage.navigate(1);
		
		// then the grade should still be there
		assertThat(quizPage.getGrade(), is(1));
	}
	
	//@Test
	// TODO: shouldDisplayAnswerSummaryInReport (this requires another "student" user)
	public static void main(String [] args) {
    	startServer();
	}
}
