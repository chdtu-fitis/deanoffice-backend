package ua.edu.chdtu.deanoffice.util;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class PersonUtil {

    public static String toCapitalizedCase(String string) {
        if (string.isEmpty()) {
            return "";
        }

        List<String> stringList = asList(string.split("\\s+"));
        ArrayList<String> stringArrayList = new ArrayList<>(stringList);

        stringArrayList.removeIf(s -> s.isEmpty());
        stringArrayList.forEach(s -> stringArrayList.set(stringArrayList.indexOf(s), wordToCapitalizedCase(s)));

        return String.join(" ", stringArrayList);
    }

    private static String wordToCapitalizedCase(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }
}
