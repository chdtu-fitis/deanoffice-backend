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

    public static List<String> makeHyphenationByCharsLength(String string, int charsLength) {
        List<String> result = new ArrayList<String>();
        if (string.length()<=charsLength){
            result.add(string);
            result.add("");
            return result;
        }
        int num;
        for (num = charsLength;num<=charsLength;num--) {
            if(string.toCharArray()[num]==' '){
                break;
            }
        }
        result.add(string.substring(0,num));
        result.add(string.substring(num));
        return result;
    }

    public static String replaceSingleQuotes(String string) {
        string = string.replace("’", "'");
        string = string.replace("`", "'");
        return string.trim();
    }
}
