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

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.googlecode.ounit.test.moodle.IQuestionEditPage;
import com.googlecode.ounit.test.moodle.IQuizPage;

import static com.googlecode.ounit.test.moodle.MoodleParams.*;


public class QuestionEditPage implements IQuestionEditPage {
	private WebDriver driver;

	private WebElement name;
	private WebElement remoteid;
	private WebElement remoteversion;
	private WebElement submitbutton;
	
	@FindBy(xpath = "//option[text() = '" + engineName + "']")
	private WebElement testEngineSelect;
	
	public QuestionEditPage(WebDriver driver) {
		this.driver = driver;
	}

	@Override
	public IQuizPage createQuestion(String name, String remoteid, String version) {
		this.name.sendKeys(name);
		this.remoteid.sendKeys(remoteid);
		remoteversion.sendKeys(version);
		testEngineSelect.click();
		submitbutton.click();
		if(driver.getCurrentUrl().contains("question.php")) // Still on edit page? Failed!
			throw new IllegalStateException("Question creation FAILED");
		
    	return PageFactory.initElements(driver, QuizPage.class);
	}
}
