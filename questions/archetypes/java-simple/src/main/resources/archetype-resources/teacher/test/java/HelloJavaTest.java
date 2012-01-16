#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
import org.junit.*;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Teacher tests to verify {@link HelloJava} class behavior
 */
public class HelloJavaTest {

    /**
     * Verify that {@link HelloJava${symbol_pound}sayHello()} returns "Hello Java"
     */
    @Test(timeout = 1000)
    public void testSayHello() {
        assertThat("Invalid return value",
                HelloJava.sayHello(), is("Hello Java"));
    }

}
