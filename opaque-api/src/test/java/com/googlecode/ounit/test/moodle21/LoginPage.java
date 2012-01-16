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

package com.googlecode.ounit.test.moodle21;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.googlecode.ounit.test.moodle.IHomePage;
import com.googlecode.ounit.test.moodle.ILoginPage;

public class LoginPage implements ILoginPage {
    private final WebDriver driver;
    
    private WebElement username;
    private WebElement password;
    @FindBy(xpath = "//form[@id='login']//input[@type='submit']")
    private WebElement submitButton;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    @Override
	public IHomePage loginAs(String username, String password) {
    	String oldUrl = driver.getCurrentUrl();
    	
    	this.username.sendKeys(username);
    	this.password.sendKeys(password);
    	submitButton.submit();
    	if(oldUrl.equals(driver.getCurrentUrl())) // Still on same page? Login failed!
    		throw new IllegalStateException("Login FAILED");
    	
    	return PageFactory.initElements(driver, HomePage.class);
    }

}
