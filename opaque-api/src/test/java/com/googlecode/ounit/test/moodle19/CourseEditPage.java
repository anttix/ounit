package com.googlecode.ounit.test.moodle19;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class CourseEditPage {
    private final WebDriver driver;
    
    private WebElement shortname;
    private WebElement fullname;
    private WebElement submitbutton;
    
    @FindBy(xpath="//input[@type='hidden' and @name='id']/../input[@type='submit']")
    private WebElement enterCourse;

	public CourseEditPage(WebDriver driver) {
		this.driver = driver;
	}
	
	public CoursePage newCourse(String shortname, String fullname) {
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
