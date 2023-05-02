package ua.edu.chdtu.deanoffice.service.course.selective.statistics;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PercentStudentsRegistrationOnCourses {
    private String groupName;
    private String facultyName;
    private String specializationName;
    private int studyYear;
    private String department;
    private int count;
    private int totalCount;
    private int registeredCount;
    private int registeredPercent;
    private int choosingLessCount;
    private int choosingLessPercent;
    private int notRegisteredCount;
    private int notRegisteredPercent;

    public PercentStudentsRegistrationOnCourses(String groupName, String facultyName, long studyYear, String department, long count) {
        this.groupName = groupName;
        this.facultyName = facultyName;
        this.studyYear = (int)studyYear;
        this.department = department;
        this.count = (int)count;
    }

    public PercentStudentsRegistrationOnCourses(String facultyName, long count) {
        this.facultyName = facultyName;
        this.count = (int)count;
    }

    public PercentStudentsRegistrationOnCourses(String facultyName, String specializationName, long count) {
        this.facultyName = facultyName;
        this.specializationName = specializationName;
        this.count = (int)count;
    }

    public PercentStudentsRegistrationOnCourses(String facultyName, long studyYear, long count) {
        this.facultyName = facultyName;
        this.studyYear = (int)studyYear;
        this.count = (int)count;
    }

        public PercentStudentsRegistrationOnCourses(String facultyName, long studyYear, String specializationName, long count) {
        this.facultyName = facultyName;
        this.studyYear = (int)studyYear;
        this.specializationName = specializationName;
        this.count = (int)count;
    }

    public PercentStudentsRegistrationOnCourses(long studyYear, long count) {
        this.studyYear = (int)studyYear;
        this.count = (int)count;
    }

    public PercentStudentsRegistrationOnCourses(String groupName, String facultyName, int studyYear,
                                                String department, String specializationName, int totalCount,
                                                int registeredCount, int choosingLessCount) {
        this.groupName = groupName;
        this.facultyName = facultyName;
        this.studyYear = studyYear;
        this.department = department;
        this.specializationName = specializationName;
        this.totalCount = totalCount;
        this.registeredCount = registeredCount;
        this.choosingLessCount = choosingLessCount;
        calculateInterest();
    }

    private void calculateInterest() {
        notRegisteredCount = totalCount - registeredCount - choosingLessCount;
        registeredPercent = registeredCount * 100 / totalCount;
        choosingLessPercent = choosingLessCount * 100 / totalCount;
        notRegisteredPercent = notRegisteredCount * 100 / totalCount;
    }

    public PercentStudentsRegistrationOnCourses() {
    }
}
