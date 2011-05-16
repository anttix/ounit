package com.googlecode.ounit.opaque;

import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;

/**
 * Page object that models questions generated by
 * {@link MockOpaqueService}
 * @author anttix
 *
 */
public class MockQuestionPage {
	@SuppressWarnings("unused")
	private WebDriver driver;
	
	/* FIXME: This will validate only the first quiz on page.
	 * To do better we need to find a way to fetch all %%IDPREFIX%%-s from page.
	 * Moodle does not currently support multiple Opaque questions on
	 * one page so it's not much of an issue (yet)!
	 */
	@FindBy(css = ".quizdiv")
	private RenderedWebElement quizDiv;
	@FindBy(css = ".headdiv")
	private RenderedWebElement headDiv;
	@FindBy(css = ".cssdiv")
	private RenderedWebElement cssDiv;
	@FindBy(css = ".jsdiv")
	private RenderedWebElement jsDiv;
	@FindBy(css = ".picdiv")
	private RenderedWebElement picDiv;
	@FindBy(css = ".lastreply")
	private WebElement lastReplyText;
	
	@FindBys(value = {
		@FindBy(css = ".quizdiv"),
		@FindBy(xpath = "//input[@type = 'text' and contains(@name, 'nr')]")
	})
	private WebElement answerBox;
	
	@FindBys(value = {
			@FindBy(css = ".quizdiv"),
			@FindBy(xpath = "//input[@type = 'submit' and contains(@name, 'go')]")
	})
	private WebElement answerButton;

	public MockQuestionPage(WebDriver driver) {
		this.driver = driver;
	}
	
	/**
	 * Validate that question page is rendered correctly.
	 * 
	 */
	public void validate() {
		// Sleep for 1 second to give JavaScript on page some time to do it's job
		if (picDiv.isDisplayed())
			try {
				Thread.sleep(1100);
			} catch (InterruptedException e) { }

		assert quizDiv.isDisplayed()  : "Main question DIV is not visible";
		assert !headDiv.isDisplayed() : "Header contributions did not work";
		assert !cssDiv.isDisplayed()  : "Question CSS was not loaded";
		assert !jsDiv.isDisplayed()   : "Javascript file was not loaded from resources";
		assert !picDiv.isDisplayed()  : "Image file was not loaded from resources";
	}

	public void answer(int i) {
		answerBox.clear();
		answerBox.sendKeys(i + "");
		answerButton.click();
	}

	public String getLastAnswer() {
		return lastReplyText.getText();	
	}
}
