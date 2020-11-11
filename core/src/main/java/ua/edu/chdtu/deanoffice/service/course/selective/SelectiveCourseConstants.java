package ua.edu.chdtu.deanoffice.service.course.selective;

import ua.edu.chdtu.deanoffice.entity.TypeCycle;

import java.util.HashMap;
import java.util.Map;

public class SelectiveCourseConstants {
    //Map structure: degree id -> Map(student study year -> array of selective courses number:
    // element 0 - 1st semester, element 1 - 2nd semester
    public static final Map<Integer, Map<String, Integer[]>[]> SELECTIVE_COURSES_NUMBER = new HashMap<>();
    public static final int SELECTIVE_COURSES_REGISTRATION_YEAR = 2020;

    static {
        Map<String, Integer[]>[] bachelor = new Map[4];
        Map<String, Integer[]> bachelor2 = new HashMap<>();
        bachelor2.put(TypeCycle.PROFESSIONAL.toString(), new Integer[]{0, 0});
        bachelor2.put(TypeCycle.GENERAL.toString(), new Integer[]{3, 2});
        bachelor[1] = bachelor2;
        SELECTIVE_COURSES_NUMBER.put(1, bachelor);
        Map<String, Integer[]>[] master = new Map[1];
        Map<String, Integer[]> master1 = new HashMap<>();
        master1.put(TypeCycle.PROFESSIONAL.toString(), new Integer[]{2, 2});
        master1.put(TypeCycle.GENERAL.toString(), new Integer[]{1, 1});
        master[0] = master1;
        SELECTIVE_COURSES_NUMBER.put(3, master);
    }
}
