package ua.edu.chdtu.deanoffice.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PersonUtilTest {

    private void assertCapitalizedCase(String initial, String expected) {
        assertEquals(expected, PersonUtil.toCapitalizedCase(initial));
    }

    @Test
    void oneWordToCapitalizedCase() {
        assertCapitalizedCase("word", "Word");
    }

    @Test
    void twoWordsToCapitalizedCase() {
        assertCapitalizedCase("hello world", "Hello World");
    }

    @Test
    void emptyString() {
        assertCapitalizedCase("", "");
    }

    @Test
    void stringWithSeveralSpaces() {
        assertCapitalizedCase("hello       world ", "Hello World");
    }

    @Test
    void startedBySpaces() {
        assertCapitalizedCase("  hello", "Hello");
    }

    @Test
    void nullToCapitalizedCase() {
        assertCapitalizedCase(null, null);
    }
}
