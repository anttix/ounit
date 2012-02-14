#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import static com.googlecode.ounit.selenium.CssHelper.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import com.googlecode.ounit.selenium.*;

public class PageTest extends OunitSeleniumTest {
	@Before
	public void openPage() {
		gotoPage("index.html");
	}

	@Test
	public void checkHeadingText() {
		assertThat("Wrong heading text",
				driver.findElement(By.tagName("h1")).getText(),
				is("My Memories"));
	}
	
	@Test
	public void checkParagraphText() {
		assertThat("Wrong paragraph text",
				driver.findElement(By.tagName("p")).getText(),
				is("Once upon a time ..."));
	}
	
	@Test
	public void checkListText() {
		final String [] liTexts = {"one", "two", "three", "eat that"};

		WebElement list = driver.findElement(By.tagName("ul"));
		List<WebElement> li = list.findElements(By.tagName("li"));
		assertThat("Wrong number of list elements", li.size(), is(liTexts.length));
		for(int i = 0; i < li.size(); i++) {
			assertThat("Wrong list item " + (i + 1) + " text",
					li.get(i).getText(), is(liTexts[i]));
		}
	}
	
	@Test
	public void checkHeadingStyle() {
		WebElement h1 = driver.findElement(By.tagName("h1"));
		assertFalse("Heading can not have a style attribute",
				hasStyleAttribute(h1));
		assertThat("Heading color is wrong", 
				getTextColor(h1), is("#92b901"));
		assertTrue("Heading text is not centered with CSS",
				getTextAlign(h1).equals("center"));
	}
}
