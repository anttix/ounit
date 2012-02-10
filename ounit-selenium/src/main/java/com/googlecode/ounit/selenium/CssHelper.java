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

package com.googlecode.ounit.selenium;

import org.openqa.selenium.WebElement;

public class CssHelper {
	public static String getFirstCssProperty(WebElement e, String[] props) {
		String rv = null;

		for (String prop : props) {
			// getCssValue is very browser specific
			rv = e.getCssValue(prop);
			if (rv != null && !rv.isEmpty() && !rv.equals("null"))
				break;
		}

		return rv;
	}

	public static boolean hasStyleAttribute(WebElement e) {
		String st = e.getAttribute("style");
		if (st == null || "".equals(st))
			return false;
		else
			return true;
	}

	// getCssValue is very browser specific
	public static String getBackgroundColor(WebElement e) {
		String[] props = { "backgroundColor", "background-color" };

		return getFirstCssProperty(e, props);
	}

	public static String getTextColor(WebElement e) {
		String[] props = { "color" };

		return getFirstCssProperty(e, props);
	}

	public static String getBorderStyle(WebElement e) {
		String[] props = { "borderStyle", "border-left-style" };

		return getFirstCssProperty(e, props);
	}
}
