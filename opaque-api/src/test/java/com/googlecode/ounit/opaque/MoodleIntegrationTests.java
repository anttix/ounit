package com.googlecode.ounit.opaque;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.text.StringContains.*;
import static org.junit.Assert.*;
//import static org.junit.Assume.*;

import org.junit.*;
import org.openqa.selenium.support.PageFactory;

import com.googlecode.ounit.test.moodle.*;

public abstract class MoodleIntegrationTests extends TestBase {

	public abstract IQuizPage setupQuiz();
	public abstract IHomePage getHomePage();
	
	protected MockQuestionPage mockPage = PageFactory.initElements(driver, MockQuestionPage.class);
	
	@Test
	public void shouldPassConnectionTest() {
		// Given I am on engine test page,
		IEnginePage page = getHomePage().gotoEnginePage();
	
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
		IQuizPage quizPage = setupQuiz();
	
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
		IQuizPage quizPage = setupQuiz();
		
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
}