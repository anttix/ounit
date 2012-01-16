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

import com.googlecode.ounit.test.moodle.ICourseEditPage;
import com.googlecode.ounit.test.moodle.ICoursePage;
import com.googlecode.ounit.test.moodle.IEnginePage;
import com.googlecode.ounit.test.moodle.IHomePage;
import com.googlecode.ounit.test.moodle.ILoginPage;

import static com.googlecode.ounit.test.moodle.MoodleParams.*;

public class HomePage implements IHomePage {
    private final WebDriver driver;
    private String baseUrl;

	@FindBy(linkText=courseName)
    private WebElement testCourseLink;
    
    public HomePage(WebDriver driver) {
        this.driver = driver;
    }
    
    @Override
    public String getBaseUrl() {
		return baseUrl;
	}

    @Override
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
    
    @Override
	public ILoginPage gotoLoginPage() {
    	driver.get(baseUrl + "/login/index.php");
    	return PageFactory.initElements(driver, LoginPage.class);
    }

    @Override
	public ICoursePage gotoTestCourse() {
    	driver.get(baseUrl);
    	try {
    		testCourseLink.click();
        	return PageFactory.initElements(driver, CoursePage.class);
    	} catch(NoSuchElementException e) {
    		return createTestCourse();
    	}
    }

    @Override
	public ICoursePage createTestCourse() {
    	driver.get(baseUrl + "/course/edit.php?category=1");
    	
    	ICourseEditPage page = PageFactory.initElements(driver, CourseEditPage.class);
    	return page.newCourse("OTC" + (int)(Math.random() * 100), courseName);
    }

	/* (non-Javadoc)
	 * @see com.googlecode.ounit.test.moodle21.IHomePage#gotoEnginePage()
	 */
	@Override
	public IEnginePage gotoEnginePage() {
    	driver.get(baseUrl + "/question/type/opaque/engines.php");
    	return PageFactory.initElements(driver, EnginePage.class);
	}
}