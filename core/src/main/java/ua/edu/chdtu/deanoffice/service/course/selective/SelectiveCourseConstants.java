package ua.edu.chdtu.deanoffice.service.course.selective;

import ua.edu.chdtu.deanoffice.entity.TrainingCycle;
import ua.edu.chdtu.deanoffice.entity.DegreeEnum;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SelectiveCourseConstants {
    //Map structure: degree id -> Map(student study year -> array of selective courses number:
    // element 0 - 1st semester, element 1 - 2nd semester
    public static final Map<Integer, Map<String, Integer[]>[]> SELECTIVE_COURSES_NUMBER = new HashMap<>();
    public static final Map<Integer, int[]> SELECTIVE_COURSES_CHOOSE_YEARS = new HashMap<>();
    public static final Map<Integer, Integer> STUDY_DURATIONS = new HashMap<>();

    private static final int BACHELOR_ID = DegreeEnum.BACHELOR.getId();
    private static final int MASTER_ID = DegreeEnum.MASTER.getId();
    private static final int PHD_ID = DegreeEnum.PHD.getId();

    static {
        Map<String, Integer[]>[] bachelor = new Map[4];
        Map<String, Integer[]> bachelor2 = new HashMap<>();
        bachelor2.put(TrainingCycle.PROFESSIONAL.toString(), new Integer[]{0, 0});
        bachelor2.put(TrainingCycle.GENERAL.toString(), new Integer[]{3, 2});
        bachelor[1] = bachelor2;

        Map<String, Integer[]> bachelor3 = new HashMap<>();
        bachelor3.put(TrainingCycle.PROFESSIONAL.toString(), new Integer[]{3, 2});
        bachelor3.put(TrainingCycle.GENERAL.toString(), new Integer[]{0, 0});
        bachelor[2] = bachelor3;

        Map<String, Integer[]> bachelor4 = new HashMap<>();
        bachelor4.put(TrainingCycle.PROFESSIONAL.toString(), new Integer[]{3, 2});
        bachelor4.put(TrainingCycle.GENERAL.toString(), new Integer[]{0, 0});
        bachelor[3] = bachelor4;
        SELECTIVE_COURSES_NUMBER.put(BACHELOR_ID, bachelor);

        Map<String, Integer[]>[] master = new Map[1];
        Map<String, Integer[]> master1 = new HashMap<>();
        master1.put(TrainingCycle.PROFESSIONAL.toString(), new Integer[]{2, 2});
        master1.put(TrainingCycle.GENERAL.toString(), new Integer[]{1, 1});
        master[0] = master1;
        SELECTIVE_COURSES_NUMBER.put(MASTER_ID, master);

        Map<String, Integer[]>[] phd = new Map[4];
        Map<String, Integer[]> phd1 = new HashMap<>();
        phd1.put(TrainingCycle.PROFESSIONAL.toString(), new Integer[]{0, 0});
        phd1.put(TrainingCycle.GENERAL.toString(), new Integer[]{0, 0});
        phd[0] = phd1;

        Map<String, Integer[]> phd2 = new HashMap<>();
        phd2.put(TrainingCycle.PROFESSIONAL.toString(), new Integer[]{0, 0});
        phd2.put(TrainingCycle.GENERAL.toString(), new Integer[]{0, 0});
        phd[1] = phd2;

        Map<String, Integer[]> phd3 = new HashMap<>();
        phd3.put(TrainingCycle.PROFESSIONAL.toString(), new Integer[]{0, 0});
        phd3.put(TrainingCycle.GENERAL.toString(), new Integer[]{0, 0});
        phd[2] = phd3;

        Map<String, Integer[]> phd4 = new HashMap<>();
        phd4.put(TrainingCycle.PROFESSIONAL.toString(), new Integer[]{0, 0});
        phd4.put(TrainingCycle.GENERAL.toString(), new Integer[]{0, 0});
        phd[3] = phd4;
        SELECTIVE_COURSES_NUMBER.put(PHD_ID, phd);
        //-------------------------------------------------------
        // ініціалізація структури, яка вміщує інформацію про курси, студенти яких вибирають вибіркові дисципліни (для кожного освітнього рівня)
        SELECTIVE_COURSES_CHOOSE_YEARS.put(BACHELOR_ID, new int[]{1, 2, 3});
        SELECTIVE_COURSES_CHOOSE_YEARS.put(MASTER_ID, new int[]{0});
        SELECTIVE_COURSES_CHOOSE_YEARS.put(PHD_ID, new int[]{0});

        STUDY_DURATIONS.put(BACHELOR_ID, 4);
        STUDY_DURATIONS.put(MASTER_ID, 2);
        STUDY_DURATIONS.put(PHD_ID, 4);
    }

    private static int getSelectiveCoursesCount(int degreeId, int studentYear) throws OperationCannotBePerformedException {
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

    public static int getSelectiveCoursesCount(int degreeId, int studentYear, int currentYear, int selectiveCoursesYear) throws OperationCannotBePerformedException {
        int correction = currentYear - selectiveCoursesYear + 1;
        studentYear -= correction;
        return getSelectiveCoursesCount(degreeId, studentYear);
    }

    public static int getSelectiveCoursesCount(int degreeId) throws OperationCannotBePerformedException {
        try {
            Map<String, Integer[]>[] selectiveCoursesNumberForDegree = SELECTIVE_COURSES_NUMBER.get(degreeId);
            Set<Integer> numbers = new HashSet<>();
            for (Map<String, Integer[]> scn : selectiveCoursesNumberForDegree) {
                if (scn != null) {
                    int scNumberInTheYear = 0;
                    for (Integer[] scn2 : scn.values()) {
                        for (Integer scn3 : scn2) {
                            scNumberInTheYear += scn3;
                        }
                    }
                    numbers.add(scNumberInTheYear);
                }
            }
            if (numbers.size() == 1)
                return (Integer) numbers.toArray()[0];
            else
                throw new OperationCannotBePerformedException("Для даного освітнього ступеня немає однакової кількості вибіркових дисциплін на всіх курсах");
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new OperationCannotBePerformedException("Неправильно вказано курс");
        } catch (NullPointerException e) {
            throw new OperationCannotBePerformedException("Неправильно вказано освітній ступінь або курс");
        }
    }

    public static int[] getSelectiveCourseChooseYears(int degreeId, int currentYear, int selectiveCoursesYear) {
        int studyDuration = STUDY_DURATIONS.get(degreeId);
        int[] chooseYears = SELECTIVE_COURSES_CHOOSE_YEARS.get(degreeId);
        int correction = currentYear - selectiveCoursesYear + 1;
        int[] result = Arrays.stream(chooseYears).map(cy -> cy + correction).filter(cy -> cy <= studyDuration).toArray();
        return result;
    }
}
