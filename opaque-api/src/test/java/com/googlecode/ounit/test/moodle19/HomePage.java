package com.googlecode.ounit.test.moodle19;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import static com.googlecode.ounit.test.moodle19.MoodleParams.*;

public class HomePage {
    private final WebDriver driver;
    
    @FindBy(linkText=courseName)
    private WebElement testCourseLink;
    
    public HomePage(WebDriver driver) {
        this.driver = driver;
    }
    
    public LoginPage gotoLoginPage() {
    	driver.get(baseUrl + "/login/index.php");
    	return PageFactory.initElements(driver, LoginPage.class);
    }
    
    public CoursePage gotoTestCourse() {
    	driver.get(baseUrl);
    	try {
    		testCourseLink.click();
        	return PageFactory.initElements(driver, CoursePage.class);
    	} catch(NoSuchElementException e) {
    		return createTestCourse();
    	}
    }
    
    public CoursePage createTestCourse() {
    	driver.get(baseUrl + "/course/edit.php?category=1");
    	
    	CourseEditPage page = PageFactory.initElements(driver, CourseEditPage.class);
    	return page.newCourse("OTC" + (int)(Math.random() * 100), courseName);
    }

	public EnginePage gotoEnginePage() {
    	driver.get(baseUrl + "/question/type/opaque/engines.php");
    	return PageFactory.initElements(driver, EnginePage.class);
	}
}