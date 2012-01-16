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

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.googlecode.ounit.test.moodle.ICoursePage;
import com.googlecode.ounit.test.moodle.IQuizEditPage;
import com.googlecode.ounit.test.moodle.IQuizPage;

import static com.googlecode.ounit.test.moodle.MoodleParams.*;

public class CoursePage implements ICoursePage {
    private final WebDriver driver;
    
    @FindBy(linkText=quizName)
    private WebElement testQuizLink;
    
    @FindBy(xpath="//input[@type='hidden' and @name='edit']/../input[@type='submit']")
    private WebElement toggleEditButton;
    
    @FindBy(xpath="//option[contains(@value, 'add=quiz')]")
    private WebElement newQuiz;

	public CoursePage(WebDriver driver) {
		this.driver = driver;
	}
	
	@Override
	public void toggleEditing() {
		toggleEditButton.click();
	}

	@Override
	public IQuizPage gotoTestQuiz() {
    	try {
    		testQuizLink.click();
        	return PageFactory.initElements(driver, QuizPage.class);
    	} catch(NoSuchElementException e) {
    		return createTestQuiz();
    	}
	}

	private IQuizPage createTestQuiz() {
		toggleEditing();
		newQuiz.click();
		
    	IQuizEditPage page = PageFactory.initElements(driver, QuizEditPage.class);
    	return page.newQuiz(quizName);
	}
}