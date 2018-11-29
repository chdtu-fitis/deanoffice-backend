package ua.edu.chdtu.deanoffice.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class PersonUtil {

    public static String toCapitalizedCase(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        }
        return wordsToCapitalizedCase(string);
    }

    private static String wordsToCapitalizedCase(String string) {
        List<String> words = asList(string.split("\\s+"));
        return words.stream()
                .filter(s -> !s.isEmpty())
                .map(PersonUtil::wordToCapitalizedCase)
                .collect(Collectors.joining(" "));
    }

    private static String wordToCapitalizedCase(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

    public static String makeInitialsSurnameLast(String fullName) {
        if (fullName == null)
            return "";
        List<String> fullNameParts = Arrays.asList(fullName.split(" "));
        if (fullNameParts.size() < 3) {
            if (fullNameParts.size() > 0)
                return fullNameParts.get(0);
            else
                return "";
        }
        return fullNameParts.get(1).substring(0, 1).toUpperCase() + "."
                + fullNameParts.get(2).substring(0, 1).toUpperCase() + ". "
                + fullNameParts.get(0);
    }
}
