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

import com.googlecode.ounit.test.moodle.IEditEnginePage;
import com.googlecode.ounit.test.moodle.IEnginePage;

import static com.googlecode.ounit.test.moodle.MoodleParams.*;

public class EnginePage implements IEnginePage {
    private WebDriver driver;
    
    @FindBy(xpath = "//p[contains(text(), '" + engineName + "')]//a[contains(@href, 'editengine.php')]")
    private WebElement engineEditLink;
    @FindBy(xpath = "//a[contains(@href, 'editengine.php')]")
    private WebElement newEngineLink;
    
    @FindBy(xpath = "//p[contains(text(), '" + engineName + "')]//a[contains(@href, 'testengine.php')]")
    private WebElement testEngineLink;

	public EnginePage(WebDriver driver) {
        this.driver = driver;
    }
	
	@Override
	public void setupEngineUrl(String url) {
		try {
			engineEditLink.click();
		} catch(NoSuchElementException e) {
			newEngineLink.click();
		}
		IEditEnginePage page = PageFactory.initElements(driver, EngineEditPage.class);
		page.saveEngine(engineName, url);
	}

	@Override
	public void testConnection() {
		testEngineLink.click();
	}
}
