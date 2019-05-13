package ua.edu.chdtu.deanoffice.service.document.report.journal.rating;

import org.docx4j.wml.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ua.edu.chdtu.deanoffice.Constants;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.Payment;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.report.journal.ComparatorCourses;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FormRatingBase {
    protected static final int WIDTH_NUMBER_COLUMN = 400;
    protected static final int WIDTH_NAME_COLUMN = 2800;
    protected static final int HEIGHT_FIRST_ROW = 200;
    protected static final int FONT_SIZE_14 = 28;
    protected static final String FONT_FAMILY = "Times New Roman";
    protected static final String KURS= "-kurs";
    protected static final String JOURNAL= "journal-otsinok-";
    protected ObjectFactory factory;
    @Autowired
    protected DocumentIOService documentIOService;
    @Autowired
    protected CourseForGroupService courseForGroupService;
    @Autowired
    protected StudentGroupService groupService;

    protected void getDataFromDataBase(StudentGroup studentGroup, Integer semester, List<String> namesStudents, List<String> namesCourses) {
        namesStudents.clear();
        namesCourses.clear();
        List<CourseForGroup> courseForGroups = courseForGroupService.getCoursesForGroupBySemester(studentGroup.getId(), semester);
        List<StudentDegree> students = studentGroup.getStudentDegrees();
        prepareCourses(courseForGroups, namesCourses);
        prepareNames(students, namesStudents);
    }

    private void prepareCourses(List<CourseForGroup> courseForGroups, List<String> namesCourses){
        courseForGroups.sort(new ComparatorCourses());
        Map<Integer,String> retrenchments = createRetrenchments();
        for(CourseForGroup courseForGroup:courseForGroups) {
            Integer kcId = Integer.valueOf(courseForGroup.getCourse().getKnowledgeControl().getId());
            if (retrenchments.containsKey(kcId)){
                namesCourses.add(courseForGroup.getCourse().getCourseName().getName() +"("+courseForGroup.getCourse().getHours()+")" + retrenchments.get(kcId));
            } else {
                namesCourses.add(courseForGroup.getCourse().getCourseName().getName() +"("+courseForGroup.getCourse().getHours()+")");
            }
        }
    }

    private void prepareNames(List<StudentDegree> students, List<String> namesStudents){
        for(StudentDegree studentDegree:students){
            if(studentDegree.getPayment() == Payment.CONTRACT){
                namesStudents.add(studentDegree.getStudent().getInitialsUkr()+" (к)");
            } else {
                namesStudents.add(studentDegree.getStudent().getInitialsUkr());
            }
        }
    }

    private Map<Integer,String> createRetrenchments(){
        HashMap<Integer,String> retrenchments = new HashMap<>();
        retrenchments.put(Constants.EXAM," (ісп)");
        retrenchments.put(Constants.CREDIT," (з)");
        retrenchments.put(Constants.COURSEWORK,"(КР)");
        retrenchments.put(Constants.COURSE_PROJECT,"(КП)");
        retrenchments.put(Constants.DIFFERENTIATED_CREDIT," (д/з)");
        retrenchments.put(Constants.STATE_EXAM," (д/ісп)");
        retrenchments.put(Constants.ATTESTATION," (а)");
        retrenchments.put(Constants.INTERNSHIP," (П)");
        retrenchments.put(Constants.NON_GRADED_INTERNSHIP," (пз)");
        return retrenchments;
    }
}
