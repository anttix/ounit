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

package com.googlecode.ounit.opaque;

import org.junit.*;
import org.openqa.selenium.support.PageFactory;

import com.googlecode.ounit.test.moodle.*;
import com.googlecode.ounit.test.moodle19.HomePage;

/**
 * Test {@link MockOpaqueService} with Moodle 1.9.
 * <p>
 * To run these tests, a <a href="http://www.moodle.org">Moodle</a> 1.9.x server with
 * <a href="http://docs.moodle.org/en/Opaque_question_type">Opaque</a> question type
 * installed must be configured at localhost.
 * </p>
 * <p>
 * A number of properties have to be set to communicate the server URL and admin
 * credentials. This can be done from the command line:
 * </p>
 * <pre>
 * mvn -Dmoodle19.url=http://localhost/moodle -Dmoodle19.user=admin -Dmoodle19.pass=password test
 * </pre>
 * Or by adding the following block into maven settings file (normally ~/.m2/settings.xml):
 * <pre> 
 *   &lt;profile&gt;
 *     &lt;id&gt;moodle&lt;/id&gt;
 *     &lt;activation&gt;
 *       &lt;activeByDefault&gt;true&lt;/activeByDefault&gt;
 *     &lt;/activation&gt;
 *     &lt;properties&gt;
 *       &lt;moodle19.url&gt;http://localhost/moodle/1.9.12&lt;/moodle19.url&gt;
 *       &lt;moodle19.user&gt;admin&lt;/moodle19.user&gt;
 *       &lt;moodle19.pass&gt;moodlepass&lt;/moodle19.pass&gt;
 *     &lt;/properties&gt;
 *   &lt;/profile&gt;
 * </pre>
 * See 
 *  <a href="http://maven.apache.org/settings.html">Maven settings reference</a> for details.
 *  
 * @author anttix
 *
 */
public class Moodle19IntegrationTest extends MoodleIntegrationTests {
	protected static SetupHelper helper;
	protected static HomePage homePage;
	
	@BeforeClass
	public static void setupTestEnvironment() {
		helper = new SetupHelper("moodle19.");

		startServer();
		openBrowser();

		homePage = PageFactory.initElements(driver, HomePage.class); 
		helper.setupMoodle(homePage, serviceAddress);
	}
	
	@Override
	public IQuizPage setupQuiz() {
		return helper.setupQuiz();
	}
	
	@Override
	public IHomePage getHomePage() {
		return homePage;
	}
}
