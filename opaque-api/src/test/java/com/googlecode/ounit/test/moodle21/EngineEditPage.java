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
import org.openqa.selenium.support.PageFactory;

import com.googlecode.ounit.test.moodle.IEditEnginePage;
import com.googlecode.ounit.test.moodle.IEnginePage;

public class EngineEditPage implements IEditEnginePage {
	private WebDriver driver;
	private WebElement enginename;
	private WebElement questionengineurls;
	private WebElement submitbutton;

	public EngineEditPage(WebDriver driver) {
        this.driver = driver;
    }

	@Override
	public IEnginePage saveEngine(String name, String url) {
		enginename.clear();
		enginename.sendKeys(name);
		questionengineurls.clear();
		questionengineurls.sendKeys(url);
		submitbutton.submit();
		
		return PageFactory.initElements(driver, EnginePage.class);
	}
}
