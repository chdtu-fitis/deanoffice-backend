package ua.edu.chdtu.deanoffice.util;

public class ObjectUtil {
    public static boolean isEmpty( Object object ){
        if (object instanceof String) {
            if (object == null || ((String)object).trim().length() == 0) {
                return true;
            }
            return false;
        } else {
            if (object == null) {
                return true;
            }
            return false;
        }
    }
}
