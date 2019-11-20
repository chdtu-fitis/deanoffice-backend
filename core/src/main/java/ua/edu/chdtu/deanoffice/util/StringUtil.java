package ua.edu.chdtu.deanoffice.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {
    public static String firstNotNullNotEmpty(String first, String second) {
        if (StringUtils.isNotBlank(first))
            return first;
        if (StringUtils.isNotBlank(second))
            return second;
        if (first == null && second == null)
            return null;
        return "";
    }

    public static List<String> makeHyphenationForRow(String string,
                                                     int rowLength) {
        List<String> result = new ArrayList<String>();
        if (string.length() <= rowLength) {
            result.add(string);
            result.add("");
            result.add("");
            return result;
        }
        int num = rowLength;
        while (string.toCharArray()[num] != ' ') {
            num--;
        }
        result.add(string.substring(0, num));
        result.add(string.substring(num));
        return result;
    }

    public static String replaceSingleQuotes(String string) {
        string = string.replace("’", "'");
        string = string.replace("`", "'");
        return string.trim();
    }
}
