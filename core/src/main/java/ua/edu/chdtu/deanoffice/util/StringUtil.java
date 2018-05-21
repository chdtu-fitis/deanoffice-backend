package ua.edu.chdtu.deanoffice.util;

import org.apache.commons.lang3.StringUtils;

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
}
