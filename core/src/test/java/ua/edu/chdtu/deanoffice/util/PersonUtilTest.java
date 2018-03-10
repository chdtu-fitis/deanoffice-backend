package ua.edu.chdtu.deanoffice.util;

import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PersonUtilTest {

    private void assertCapitalizedCase(String initial, String expected) {
        assertThat(PersonUtil.toCapitalizedCase(initial), is(expected));
    }

    @Test
    public void oneWordToCapitalizedCase() {
        assertCapitalizedCase("word", "Word");
    }

    @Test
    public void twoWordsToCapitalizedCase() {
        assertCapitalizedCase("hello world", "Hello World");
    }

    @Test
    public void emptyString() {
        assertCapitalizedCase("", "");
    }

    @Test
    public void stringWithSeveralSpaces() {
        assertCapitalizedCase("hello       world ", "Hello World");
    }

    @Test
    public void startedBySpaces() {
        assertCapitalizedCase("  hello", "Hello");
    }
}
