package ua.edu.chdtu.deanoffice.service.course.selective;

import ua.edu.chdtu.deanoffice.entity.TypeCycle;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;

import java.util.HashMap;
import java.util.Map;

public class SelectiveCourseConstants {
    //Map structure: degree id -> Map(student study year -> array of selective courses number:
    // element 0 - 1st semester, element 1 - 2nd semester
    public static final Map<Integer, Map<String, Integer[]>[]> SELECTIVE_COURSES_NUMBER = new HashMap<>();

    private static final int BACHELOR_ID = 1;
    private static final int MASTER_ID = 3;
    private static final int PHD_ID = 4;

    static {
        Map<String, Integer[]>[] bachelor = new Map[4];
        Map<String, Integer[]> bachelor2 = new HashMap<>();
        bachelor2.put(TypeCycle.PROFESSIONAL.toString(), new Integer[]{0, 0});
        bachelor2.put(TypeCycle.GENERAL.toString(), new Integer[]{2, 3});
        bachelor[1] = bachelor2;

        Map<String, Integer[]> bachelor3 = new HashMap<>();
        bachelor3.put(TypeCycle.PROFESSIONAL.toString(), new Integer[]{2, 3});
        bachelor3.put(TypeCycle.GENERAL.toString(), new Integer[]{0, 0});
        bachelor[2] = bachelor3;

        Map<String, Integer[]> bachelor4 = new HashMap<>();
        bachelor4.put(TypeCycle.PROFESSIONAL.toString(), new Integer[]{2, 3});
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

    public static int getSelectiveCoursesCount(int degreeId, int studentYear) throws OperationCannotBePerformedException {
        try {
            Map<String, Integer[]>[] selectiveCoursesNumberForDegree = SELECTIVE_COURSES_NUMBER.get(degreeId);
            Map<String, Integer[]> scn = selectiveCoursesNumberForDegree[studentYear];
            int scNumberInTheYear = 0;
            for (Integer[] scn2 : scn.values()) {
                for (Integer scn3 : scn2) {
                    scNumberInTheYear += scn3;
                }
            }
            return scNumberInTheYear;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new OperationCannotBePerformedException("Не вірно вказано курс");
        } catch (NullPointerException e) {
            throw new OperationCannotBePerformedException("Не вірно вказано освітній ступінь або курс");
        }

    }
}
