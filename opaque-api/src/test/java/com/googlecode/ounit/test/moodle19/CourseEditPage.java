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

import com.googlecode.ounit.test.moodle.ICourseEditPage;
import com.googlecode.ounit.test.moodle.ICoursePage;

public class CourseEditPage implements ICourseEditPage {
    private final WebDriver driver;
    
    private WebElement shortname;
    private WebElement fullname;
    private WebElement submitbutton;
    
    @FindBy(xpath="//input[@type='hidden' and @name='id']/../input[@type='submit']")
    private WebElement enterCourse;

	public CourseEditPage(WebDriver driver) {
		this.driver = driver;
	}
	
	@Override
	public ICoursePage newCourse(String shortname, String fullname) {
    	this.shortname.clear();
		this.shortname.sendKeys(shortname);
		this.fullname.clear();
		this.fullname.sendKeys(fullname);
		submitbutton.click();

    	//if(driver.getCurrentUrl().contains("edit.php")) // Still on edit page? Failed!
    	//	throw new IllegalStateException("Course creation FAILED");
    	
    	enterCourse.submit();
    	
    	return PageFactory.initElements(driver, CoursePage.class);
	}
}
