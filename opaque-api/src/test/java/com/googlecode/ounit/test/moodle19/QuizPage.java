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

package com.googlecode.ounit.test.moodle19;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import static com.googlecode.ounit.test.moodle19.MoodleParams.*;

public class QuizPage {
	private WebDriver driver;
	
	@FindBy(xpath="//option[contains(@value, 'qtype=opaque')]")
	private WebElement opaqueQuestion; 
	
	@FindBy(name="add")
	private WebElement addToQuizButton;
	
	@FindBy(xpath="//a[contains(@href, 'attempt.php')]")
	private WebElement previewLink;
	
	private WebElement forcenew;
	
	@FindBy(css = "div.gradingdetails")
	private WebElement gradeDiv;
	
	@FindBy(css = ".pagingbar")
	private WebElement pagingBar;

	public QuizPage(WebDriver driver) {
		this.driver = driver;
	}
	
	public void createQuestions() {
		QuestionEditPage editPage;
		
		editPage = newOpaqueQuestion();
		editPage.createQuestion(questionIdPrefix + "v1", questionIdPrefix + "v1", questionVersion);
		
		newOpaqueQuestion();
		editPage.createQuestion(questionIdPrefix + "v2", questionIdPrefix + "v2", questionVersion);
		
		editPage = newOpaqueQuestion();
		editPage.createQuestion(questionIdPrefix + "v3", questionIdPrefix + "v3", questionVersion);
		
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("select_all_in('TABLE',null,'categoryquestions');");
		addToQuizButton.click();
		previewLink.click();
	}
	
	public QuestionEditPage newOpaqueQuestion() {
		opaqueQuestion.setSelected();
		return PageFactory.initElements(driver, QuestionEditPage.class);
	}

	public void doPreview() {
		try {
			previewLink.click();
		} catch(NoSuchElementException e) {
			createQuestions();
		}
		forcenew.submit();
	}

	public void navigate(int i) {
		pagingBar.findElement(By.linkText(i + "")).click();
	}

	public int getGrade() {
		return Integer.parseInt(gradeDiv.getText().split(": ")[1].split("/")[0]);
	}
}
