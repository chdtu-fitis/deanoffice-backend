package ua.edu.chdtu.deanoffice.service.course.selective;

import ua.edu.chdtu.deanoffice.entity.TypeCycle;

import java.util.HashMap;
import java.util.Map;

public class SelectiveCourseConstants {
    //Map structure: degree id -> Map(student study year -> array of selective courses number:
    // element 0 - 1st semester, element 1 - 2nd semester
    public static final Map<Integer, Map<String, Integer[]>[]> SELECTIVE_COURSES_NUMBER = new HashMap<>();

    private static final int bachelorId = 1;
    private static final int masterId = 3;
    private static final int phdId = 4;

    static {
        Map<String, Integer[]>[] bachelor = new Map[4];

        Map<String, Integer[]> bachelor2 = new HashMap<>();
        bachelor2.put(TypeCycle.PROFESSIONAL.toString(), new Integer[]{0, 0});
        bachelor2.put(TypeCycle.GENERAL.toString(), new Integer[]{3, 2});
        bachelor[1] = bachelor2;

        Map<String, Integer[]> bachelor3 = new HashMap<>();
        bachelor3.put(TypeCycle.PROFESSIONAL.toString(), new Integer[]{3, 2});
        bachelor3.put(TypeCycle.GENERAL.toString(), new Integer[]{0, 0});
        bachelor[2] = bachelor3;

        Map<String, Integer[]> bachelor4 = new HashMap<>();
        bachelor4.put(TypeCycle.PROFESSIONAL.toString(), new Integer[]{3, 2});
        bachelor4.put(TypeCycle.GENERAL.toString(), new Integer[]{0, 0});
        bachelor[3] = bachelor4;
        SELECTIVE_COURSES_NUMBER.put(bachelorId, bachelor);

        Map<String, Integer[]>[] master = new Map[1];
        Map<String, Integer[]> master1 = new HashMap<>();
        master1.put(TypeCycle.PROFESSIONAL.toString(), new Integer[]{2, 2});
        master1.put(TypeCycle.GENERAL.toString(), new Integer[]{1, 1});
        master[0] = master1;
        SELECTIVE_COURSES_NUMBER.put(masterId, master);

        Map<String, Integer[]>[] phd = new Map[4];
        Map<String, Integer[]> phd1 = new HashMap<>();
        phd1.put(TypeCycle.PROFESSIONAL.toString(), new Integer[]{0, 0});
        phd1.put(TypeCycle.GENERAL.toString(), new Integer[]{0, 0});
        phd[0] = phd1;
        SELECTIVE_COURSES_NUMBER.put(phdId, phd);
    }
}
