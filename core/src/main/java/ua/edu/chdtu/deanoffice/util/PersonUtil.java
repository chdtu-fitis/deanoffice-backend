package ua.edu.chdtu.deanoffice.util;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class PersonUtil {

    public static String toCapitalizedCase(String string) {
        try {
            if (string.isEmpty()) {
                return "";
            }

            String[] words = string.split("\\s+");
            return String.join(" ", wordsToCapitalizedCase(asList(words)));
        } catch (Exception exception) {
            return null;
        }
    }

    private static List<String> wordsToCapitalizedCase(List<String> words) {
        return words.stream()
                .filter(s -> !s.isEmpty())
                .map(s -> wordToCapitalizedCase(s))
                .collect(Collectors.toList());
    }

    private static String wordToCapitalizedCase(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }
}
