package ua.edu.chdtu.deanoffice.service.course.selective.statistics;

import java.util.List;

public class SelectiveCourseStatisticsServiceUtil {
    public static List<IPercentStudentsRegistrationOnCourses> getStudentsPercentWhoChosenSelectiveCourseByYear(
                List<IPercentStudentsRegistrationOnCourses> numberRegistered,
                List<IPercentStudentsRegistrationOnCourses> allStudents){
        int i = 0;
        for (IPercentStudentsRegistrationOnCourses as : allStudents) {
            if (i >= numberRegistered.size()) {
                i--;
                as.setPercent(0);
            }
            if (as.getStudyYear() == numberRegistered.get(i).getStudyYear()) {
                as.setPercent((int) (numberRegistered.get(i).getCount() * 100 / as.getCount()));
                i++;
            }
            else {
                as.setPercent(0);
            }
        }
        return allStudents;
    }

    public static List<IPercentStudentsRegistrationOnCourses> getStudentsPercentWhoChosenSelectiveCourseByАFaculty(
                List<IPercentStudentsRegistrationOnCourses> numberRegistered,
                List<IPercentStudentsRegistrationOnCourses> allStudents){
        int i = 0;
        for (IPercentStudentsRegistrationOnCourses as : allStudents) {
            if (i == numberRegistered.size()) {
                i--;
                as.setPercent(0);
            }
            if (as.getFacultyName().equals(numberRegistered.get(i).getFacultyName())) {
                as.setPercent((int) (numberRegistered.get(i).getCount() * 100 / as.getCount()));
                i++;
            }
            else {
                as.setPercent(0);
            }
        }
        return allStudents;
    }

    public static List<IPercentStudentsRegistrationOnCourses> getStudentsPercentWhoChosenSelectiveCourseByGroup(
                List<IPercentStudentsRegistrationOnCourses> numberRegistered,
                List<IPercentStudentsRegistrationOnCourses> allStudents){
        int i = 0;
        for (IPercentStudentsRegistrationOnCourses as : allStudents) {
            if (i == numberRegistered.size()) {
                i--;
                as.setPercent(0);
            }
            if (as.getGroupName().equals(numberRegistered.get(i).getGroupName())) {
                as.setPercent((int) (numberRegistered.get(i).getCount() * 100 / as.getCount()));
                i++;
            }
            else {
                as.setPercent(0);
            }
        }

        return allStudents;
    }

    public static List<IPercentStudentsRegistrationOnCourses> getStudentsPercentWhoChosenSelectiveCourseByFacultyAndSpecialization(
                List<IPercentStudentsRegistrationOnCourses> numberRegistered,
                List<IPercentStudentsRegistrationOnCourses> allStudents) {
        int i = 0;
        for (IPercentStudentsRegistrationOnCourses as : allStudents) {
            if (i == numberRegistered.size()) {
                i--;
                as.setPercent(0);
            }
            if (as.getSpecializationName().equals(numberRegistered.get(i).getSpecializationName()) && as.getFacultyName().equals(numberRegistered.get(i).getFacultyName()) ) {
                as.setPercent((int) (numberRegistered.get(i).getCount() * 100 / as.getCount()));
                i++;
            }
            else {
                as.setPercent(0);
            }
        }
        return allStudents;
    }

    public static List<IPercentStudentsRegistrationOnCourses> getStudentsPercentWhoChosenSelectiveCourseByFacultyAndYear(
                List<IPercentStudentsRegistrationOnCourses> numberRegistered,
                List<IPercentStudentsRegistrationOnCourses> allStudents){
        int i = 0;
        for (IPercentStudentsRegistrationOnCourses as : allStudents) {
            if (i == numberRegistered.size()) {
                i--;
                as.setPercent(0);
            }
            if (as.getStudyYear() == numberRegistered.get(i).getStudyYear()  && as.getFacultyName().equals(numberRegistered.get(i).getFacultyName())) {
                as.setPercent((int) (numberRegistered.get(i).getCount() * 100 / as.getCount()));
                i++;
            }
            else {
                as.setPercent(0);
            }
        }
        return allStudents;
    }

    public static List<IPercentStudentsRegistrationOnCourses> getStudentsPercentWhoChosenSelectiveCourseByFacultyAndYearAndSpecialization(
                List<IPercentStudentsRegistrationOnCourses> numberRegistered,
                List<IPercentStudentsRegistrationOnCourses> allStudents){
        int i = 0;
        for (IPercentStudentsRegistrationOnCourses as : allStudents) {
            if (i == numberRegistered.size()) {
                i--;
                as.setPercent(0);
            }
            if (as.getStudyYear() == numberRegistered.get(i).getStudyYear()
                    && as.getFacultyName().equals(numberRegistered.get(i).getFacultyName())
                    && as.getSpecializationName().equals(numberRegistered.get(i).getSpecializationName())) {
                as.setPercent((int) (numberRegistered.get(i).getCount() * 100 / as.getCount()));
                i++;
            }
            else {
                as.setPercent(0);
            }
        }
        return allStudents;
    }
}
//            System.out.println(" NumRName " + numberRegistered.get(i).getFacultyName() +
//                    " AllName " + as.getFacultyName() + " YearNumR " + numberRegistered.get(i).getStudyYear() + " yearAll " + as.getStudyYear() +
//                    " numberReg.getCount()=" + numberRegistered.get(i).getCount() + "  as.getCount()= " + as.getCount() + " i="+i);
