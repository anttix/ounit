package com.googlecode.ounit.test.moodle19;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage {
    private final WebDriver driver;
    
    private WebElement username;
    private WebElement password;
    @FindBy(xpath = "//form[@id='login']//input[@type='submit']")
    private WebElement submitButton;

    public LoginPage(WebDriver driver) {
        this.driver = driver;

        if (!driver.getTitle().contains("Login")) {
            throw new IllegalStateException("This is not the login page");
        }
    }

    public HomePage loginAs(String username, String password) {
    	String oldUrl = driver.getCurrentUrl();
    	
    	this.username.sendKeys(username);
    	this.password.sendKeys(password);
    	submitButton.submit();
    	if(oldUrl.equals(driver.getCurrentUrl())) // Still on same page? Login failed!
    		throw new IllegalStateException("Login FAILED");
    	
    	return PageFactory.initElements(driver, HomePage.class);
    }

}
