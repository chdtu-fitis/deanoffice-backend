package ua.edu.chdtu.deanoffice.service.course.selective;

import ua.edu.chdtu.deanoffice.entity.PeriodCaseEnum;
import ua.edu.chdtu.deanoffice.entity.TuitionTerm;
import ua.edu.chdtu.deanoffice.entity.TypeCycle;

import java.util.HashMap;
import java.util.Map;

public class SelectiveCourseConstants {
    //Map structure: degree id -> Map(student study year -> array of selective courses number:
    // element 0 - 1st semester, element 1 - 2nd semester
    public static final Map<Integer, Map<String, Integer[]>[]> SELECTIVE_COURSES_NUMBER = new HashMap<>();
    public static final PeriodCase[] PERIOD_CASES = new PeriodCase[8];

    private static final int BACHELOR_ID = 1;
    private static final int MASTER_ID = 3;
    private static final int PHD_ID = 4;

    static {
        for (int i = 0; i < 3; i++) {
            PERIOD_CASES[i] = new PeriodCase(BACHELOR_ID, i + 1, TuitionTerm.REGULAR, PeriodCaseEnum.EARLY);
        }

        PERIOD_CASES[3] = new PeriodCase(BACHELOR_ID, BACHELOR_ID, TuitionTerm.SHORTENED, PeriodCaseEnum.LATE);
        PERIOD_CASES[4] = new PeriodCase(MASTER_ID, 1, TuitionTerm.REGULAR, PeriodCaseEnum.LATE);
        PERIOD_CASES[5] = new PeriodCase(PHD_ID, 1, TuitionTerm.REGULAR, PeriodCaseEnum.LATE);
        PERIOD_CASES[6] = new PeriodCase(PHD_ID, 2, TuitionTerm.REGULAR, PeriodCaseEnum.EARLY);
        PERIOD_CASES[7] = new PeriodCase(PHD_ID, 3, TuitionTerm.REGULAR, PeriodCaseEnum.EARLY);

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
        SELECTIVE_COURSES_NUMBER.put(BACHELOR_ID, bachelor);

        Map<String, Integer[]>[] master = new Map[1];
        Map<String, Integer[]> master1 = new HashMap<>();
        master1.put(TypeCycle.PROFESSIONAL.toString(), new Integer[]{2, 2});
        master1.put(TypeCycle.GENERAL.toString(), new Integer[]{1, 1});
        master[0] = master1;
        SELECTIVE_COURSES_NUMBER.put(MASTER_ID, master);

        Map<String, Integer[]>[] phd = new Map[4];
        Map<String, Integer[]> phd1 = new HashMap<>();
        phd1.put(TypeCycle.PROFESSIONAL.toString(), new Integer[]{0, 0});
        phd1.put(TypeCycle.GENERAL.toString(), new Integer[]{0, 0});
        phd[0] = phd1;

        Map<String, Integer[]> phd2 = new HashMap<>();
        phd2.put(TypeCycle.PROFESSIONAL.toString(), new Integer[]{0, 0});
        phd2.put(TypeCycle.GENERAL.toString(), new Integer[]{0, 0});
        phd[1] = phd2;

        Map<String, Integer[]> phd3 = new HashMap<>();
        phd3.put(TypeCycle.PROFESSIONAL.toString(), new Integer[]{0, 0});
        phd3.put(TypeCycle.GENERAL.toString(), new Integer[]{0, 0});
        phd[2] = phd3;

        Map<String, Integer[]> phd4 = new HashMap<>();
        phd4.put(TypeCycle.PROFESSIONAL.toString(), new Integer[]{0, 0});
        phd4.put(TypeCycle.GENERAL.toString(), new Integer[]{0, 0});
        phd[3] = phd4;

        SELECTIVE_COURSES_NUMBER.put(PHD_ID, phd);
    }
}
