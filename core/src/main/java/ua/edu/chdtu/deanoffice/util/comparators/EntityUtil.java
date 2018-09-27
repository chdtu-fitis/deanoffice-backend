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
                    Object result1 = method1.invoke(object1);
                    Object result2 = method2.invoke(object2);
                    if (result1 == null && (result2 == null || (result2 instanceof String && result2.equals(""))))
                        continue;
                    if (result2 == null && (result1 == null || (result1 instanceof String && result1.equals(""))))
                        continue;
                    if (result1 == null || result2 == null)
                        return false;
                    if (!(result1.equals(result2)))
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
