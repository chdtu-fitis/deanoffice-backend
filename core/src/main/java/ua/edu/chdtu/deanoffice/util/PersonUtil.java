package ua.edu.chdtu.deanoffice.util;

import java.util.List;

import static java.util.Arrays.asList;

public class PersonUtil {

    public static String toCapitalizedCase(String string) {
        if (string.isEmpty()) {
            return "";
        }

        List<String> strings = asList(string.split("\\s+"));
        strings.forEach(s -> strings.set(strings.indexOf(s), capitalizeWord(s)));

        return String.join(" ", strings);
    }

    private static String capitalizeWord(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }
}
