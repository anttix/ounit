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

import com.googlecode.ounit.test.moodle.IQuizEditPage;
import com.googlecode.ounit.test.moodle.IQuizPage;


public class QuizEditPage implements IQuizEditPage {
	private WebDriver driver;
	
	@FindBy(id="id_name")
	private WebElement name;
	@FindBy(xpath="//select[@name='questionsperpage']//option[@value='1']")
	private WebElement oneQuestionPerPage;
	@FindBy(id="id_submitbutton")
	private WebElement saveAndDisplay;
	
	public QuizEditPage(WebDriver driver) {
		this.driver = driver;
	}

	@Override
	public IQuizPage newQuiz(String name) {
		this.name.clear();
		this.name.sendKeys(name);
		oneQuestionPerPage.click();
		saveAndDisplay.click();
		
		return PageFactory.initElements(driver, QuizPage.class);
	}
}
