package ua.edu.chdtu.deanoffice.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PersonUtilTest {

    private void assertCapitalizedCase(String initial, String expected) {
        assertEquals(expected, PersonUtil.toCapitalizedCase(initial));
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

    @Test
    public void nullToCapitalizedCase() {
        assertCapitalizedCase(null, null);
    }
}
