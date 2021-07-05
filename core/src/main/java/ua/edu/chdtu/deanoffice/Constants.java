package ua.edu.chdtu.deanoffice;

public class Constants {
    public static final int EXAM = 1;
    public static final int CREDIT = 2;
    public static final int COURSEWORK = 3;
    public static final int COURSE_PROJECT = 4;
    public static final int DIFFERENTIATED_CREDIT = 5;
    public static final int STATE_EXAM = 6;
    public static final int ATTESTATION = 7;
    public static final int INTERNSHIP = 8;
    public static final int NON_GRADED_INTERNSHIP = 9;
    //TODO cr: це схоже на якийсь список для якоїсь однієї цілі. Можливо тут краще використастити енум замість констант?

    public static final Integer[] SUCCESS_REASON_IDS = {7, 8, 16};
    public static final int EXPELLED_STUDENTS_YEARS_FOR_INITIAL_VIEW = 5;

    public static final Integer ID_SUCCESSFUL_END_BACHELOR = 7;
    public static final Integer ID_SUCCESSFUL_END_SPECIALIST = 8;
    public static final Integer ID_SUCCESSFUL_END_MASTER = 16;

    public static final int MINIMAL_SATISFACTORY_POINTS = 60;
    public static final int FOREIGN_STUDENTS_FACULTY_ID = 8;
    public static final int PHD_FACULTY_ID = 12;

    public static final String UNIVERSITY_NAME = "Черкаський державний технологічний університет";
    public static final String UNIVERSITY_NAME_ENG = "Cherkasy State Technological University";
}
