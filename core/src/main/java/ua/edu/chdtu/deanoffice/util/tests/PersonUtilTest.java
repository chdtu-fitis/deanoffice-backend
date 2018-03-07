package ua.edu.chdtu.deanoffice.util.tests;

import org.junit.Test;
import ua.edu.chdtu.deanoffice.util.PersonUtil;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class PersonUtilTest {

    private void assertCapitalizeString(String initial, String expected) {
        assertThat(PersonUtil.toCapitalizeCase(initial), is(expected));
    }

    @Test
    public void oneWordToCapitalizeCase() {
        assertCapitalizeString("word", "Word");
    }

    @Test
    public void emptyStringToCapitalizeCase() {
        assertCapitalizeString("", "");
    }

    @Test
    public void twoWordsToCapitalize() {
        assertCapitalizeString("hello world", "Hello World");
    }
}
