package ua.edu.chdtu.deanoffice.util.comparators;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EntityUtil {

    public static boolean isValuesOfFieldsReturnedByGettersMatch(Object object1, Object object2, String fields[]) {
        if (object1.getClass().equals(object2.getClass())) {
            for (String field : fields) {
                try {
                    Method method1 = object1.getClass().getMethod("get" + field.substring(0, 1).toUpperCase() + field.substring(1));
                    Method method2 = object2.getClass().getMethod("get" + field.substring(0, 1).toUpperCase() + field.substring(1));
                    if (!(method1.invoke(object1).equals(method2.invoke(object2))))
                        return false;
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            return true;
        } else
            return false;
    }
}
