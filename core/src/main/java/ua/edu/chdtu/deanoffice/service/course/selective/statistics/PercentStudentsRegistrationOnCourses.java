package ua.edu.chdtu.deanoffice.service.course.selective.statistics;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PercentStudentsRegistrationOnCourses {
    private String groupName;
    private String facultyName;
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


    public PercentStudentsRegistrationOnCourses(String facultyName, long studyYear, String department, long count) {
        this.facultyName = facultyName;
        this.studyYear = (int)studyYear;
        this.department = department;
        this.count = (int)count;
    }
    public PercentStudentsRegistrationOnCourses(long count) {
        this.count = (int)count;
    }


    public PercentStudentsRegistrationOnCourses(String groupName, String facultyName, int studyYear,
                                                String department, int totalCount,
                                                int registeredCount, int choosingLessCount) {
        this.groupName = groupName;
        this.facultyName = facultyName;
        this.studyYear = studyYear;
        this.department = department;
        this.totalCount = totalCount;
        this.registeredCount = registeredCount;
        this.choosingLessCount = choosingLessCount;
        calculateInterest();
    }
    public PercentStudentsRegistrationOnCourses(String facultyName, int totalCount,
                                                int registeredCount, int choosingLessCount) {
        this.facultyName = facultyName;
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
