package ua.edu.chdtu.deanoffice;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.CourseName;
import ua.edu.chdtu.deanoffice.entity.Degree;
import ua.edu.chdtu.deanoffice.entity.EctsGrade;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.KnowledgeControl;
import ua.edu.chdtu.deanoffice.entity.Speciality;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.entity.TuitionForm;
import ua.edu.chdtu.deanoffice.entity.TuitionTerm;
import ua.edu.chdtu.deanoffice.repository.GradeRepository;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;
import ua.edu.chdtu.deanoffice.repository.StudentGroupRepository;
import ua.edu.chdtu.deanoffice.repository.StudentRepository;
import ua.edu.chdtu.deanoffice.service.GradeService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.StudentService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.TemplateUtil;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.DiplomaSupplementService;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.StudentSummary;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.SupplementTemplateFillService;
import ua.edu.chdtu.deanoffice.util.GradeUtil;
import ua.edu.chdtu.deanoffice.webstarter.Application;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static ua.edu.chdtu.deanoffice.util.GradeUtil.adjustAverageGradeAndPoints;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DiplomaSupplementService.class, StudentService.class, GradeService.class,
        StudentRepository.class, GradeRepository.class, StudentGroupRepository.class, StudentGroupService.class,
        DocumentIOService.class, SupplementTemplateFillService.class, StudentDegreeService.class, StudentDegreeRepository.class})
@EnableAutoConfiguration
public class TestFeatureDiplomaSupplement {

    private DiplomaSupplementService diplomaSupplementService;
    private DocumentIOService documentIOService;

    @Autowired
    public void setDiplomaSupplementService(DiplomaSupplementService diplomaSupplementService) {
        this.diplomaSupplementService = diplomaSupplementService;
    }

    @Autowired
    public void setDocumentIOService(DocumentIOService documentIOService) {
        this.documentIOService = documentIOService;
    }

    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private static final DateFormat dateOfBirthFormat = new SimpleDateFormat("dd.MM.yyyy");

    private static Speciality createSpeciality() {
        Speciality speciality = new Speciality();
        speciality.setName("Спеціальність");
        speciality.setNameEng("Speciality");
        speciality.setCode("123");
        return speciality;
    }

    private static Specialization createSpecialization() {
        Specialization specialization = new Specialization();
        specialization.setName("Спеціалізація");
        specialization.setNameEng("Specialization");
        specialization.setQualification("Кваліфікація 1  Кваліфікація 2");
        specialization.setQualificationEng("Qualification 1  Qualification 2");
        return specialization;
    }

    private static StudentGroup createStudentGroup() {
        StudentGroup studentGroup = new StudentGroup();
        studentGroup.setName("АБ-123");
        studentGroup.setActive(true);
        studentGroup.setStudySemesters(3);
        studentGroup.setStudyYears(new BigDecimal(1.5));
        studentGroup.setBeginYears(5);
        studentGroup.setTuitionForm(TuitionForm.FULL_TIME);
        studentGroup.setTuitionTerm(TuitionTerm.REGULAR);
        return studentGroup;
    }

    private static Student createStudent() {
        Student student = new Student();

        student.setName("Іван");
        student.setSurname("Іванов");
        student.setPatronimic("Іванович");

        student.setNameEng("Ivan");
        student.setSurnameEng("Ivanov");
        student.setPatronimicEng("Ivanovich");

        try {
            student.setBirthDate(dateOfBirthFormat.parse("01.01.2000"));
        } catch (ParseException e) {
            log.error("Wrong date. Should never happen", e);
        }

        StudentGroup studentGroup = createStudentGroup();

        Specialization specialization = createSpecialization();
        studentGroup.setSpecialization(specialization);

        Speciality speciality = createSpeciality();
        specialization.setSpeciality(speciality);

        Degree degree = new Degree("Магістр", "Master");
        specialization.setDegree(degree);

        StudentDegree studentDegree = createStudentDegree(student, studentGroup);
        student.setDegrees(new HashSet<>());
        student.getDegrees().add(studentDegree);

        return student;
    }

    private static List<List<Grade>> createGrades(StudentDegree studentDegree) {
        List<List<Grade>> grades = new ArrayList<>();
        grades.add(new ArrayList<>());
        grades.add(new ArrayList<>());
        grades.add(new ArrayList<>());
        grades.add(new ArrayList<>());

        CourseName courseName1 = new CourseName();
        courseName1.setName("Багатосеместровий курс 1");
        courseName1.setNameEng("Multiple Semester course 1");

        Course course11 = createCourse(courseName1, 60, true);
        Course course12 = createCourse(courseName1, 60, true);
        Course course13 = createCourse(courseName1, 30, false);

        Grade grade11 = createGrade(course11, studentDegree, 89);
        Grade grade12 = createGrade(course12, studentDegree, 75);
        Grade grade13 = createGrade(course13, studentDegree, 90);

        CourseName courseName12 = new CourseName();
        courseName12.setName("Курс 2");
        courseName12.setNameEng("Course 2");
        Course course121 = createCourse(courseName12, 90, true);
        Grade grade121 = createGrade(course121, 75);
        grades.get(0).addAll(Arrays.asList(grade11, grade12, grade13, grade121));

        CourseName courseName2 = new CourseName();
        courseName2.setName("Курсова робота 1");
        courseName2.setNameEng("Course work 1");
        Course course2 = createCourse(courseName2, 90, true);
        Grade grade2 = createGrade(course2, 84);
        grades.get(1).add(grade2);

        CourseName courseName3 = new CourseName();
        courseName3.setName("Практика 1");
        courseName3.setNameEng("Practice 1");
        Course course3 = createCourse(courseName3, 90, true);
        Grade grade3 = createGrade(course3, 90);
        grades.get(2).add(grade3);

        CourseName courseName4 = new CourseName();
        courseName4.setName("Дипломна робота 1");
        courseName4.setNameEng("Diploma work 1");
        Course course4 = createCourse(courseName4, 90, true);
        Grade grade4 = createGrade(course4, 90);
        grades.get(3).add(grade4);

        return grades;
    }

    @Test
    public void testStudentSummary() {
        Student student = createStudent();
        StudentDegree studentDegree = student.getDegrees().iterator().next();
        StudentSummary summary = new StudentSummary(studentDegree, createGrades(studentDegree));

        Assert.assertEquals(84.8, summary.getTotalGrade(), 0.1);
        Assert.assertEquals("Добре", summary.getTotalNationalGradeUkr());
        Assert.assertEquals("Good", summary.getTotalNationalGradeEng());
        Assert.assertEquals(510, summary.getTotalHours().intValue());
        Assert.assertEquals(17, summary.getTotalCredits().doubleValue(), 0.1);
    }

    @Test
    public void testAdjustAverage() {
        double[] expectedResult1 = {5, 90};
        int[] actualResult1 = adjustAverageGradeAndPoints(5, 89);
        Assert.assertEquals(expectedResult1[0], actualResult1[0], 0.01);
        Assert.assertEquals(expectedResult1[1], actualResult1[1], 0.01);

        double[] expectedResult2 = {4, 83};
        int[] actualResult2 = adjustAverageGradeAndPoints(4.5, 83);
        Assert.assertEquals(expectedResult2[0], actualResult2[0], 0.01);
        Assert.assertEquals(expectedResult2[1], actualResult2[1], 0.01);

        double[] expectedResult3 = {4, 81};
        int[] actualResult3 = adjustAverageGradeAndPoints(4, 80.50000000001);
        Assert.assertEquals(expectedResult3[0], actualResult3[0], 0.01);
        Assert.assertEquals(expectedResult3[1], actualResult3[1], 0.01);

        double[] expectedResult4 = {4, 81};
        int[] actualResult4 = adjustAverageGradeAndPoints(4, 80.49999999999);
        Assert.assertEquals(expectedResult4[0], actualResult4[0], 0.01);
        Assert.assertEquals(expectedResult4[1], actualResult4[1], 0.01);

        double[] expectedResult5 = {4, 81};
        int[] actualResult5 = adjustAverageGradeAndPoints(4, 80.50000000000);
        Assert.assertEquals(expectedResult5[0], actualResult5[0], 0.01);
        Assert.assertEquals(expectedResult5[1], actualResult5[1], 0.01);
    }

    private static Grade createGrade(Course course, int points) {
        Grade grade = new Grade();
        grade.setPoints(points);
        grade.setCourse(course);
        grade.setGrade(GradeUtil.getGradeFromPoints(points));
        grade.setEcts(EctsGrade.getEctsGrade(points));
        return grade;
    }

    private static Course createCourse(boolean knowledgeControlHasGrade) {
        Course course = new Course();
        KnowledgeControl kc = new KnowledgeControl();
        course.setHoursPerCredit(30);
        kc.setGraded(knowledgeControlHasGrade);
        if (knowledgeControlHasGrade) {
            kc.setName("іспит");
        } else {
            kc.setName("залік");
        }
        course.setKnowledgeControl(kc);
        return course;
    }

    private static Grade createGrade(Course course, StudentDegree studentDegree, int points) {
        Grade grade = createGrade(course, points);
        grade.setStudentDegree(studentDegree);
        return grade;
    }

    private static StudentDegree createStudentDegree(Student student, StudentGroup studentGroup) {
        StudentDegree studentDegree = new StudentDegree();
        studentDegree.setStudent(student);
        studentDegree.setStudentGroup(studentGroup);
        studentDegree.setSpecialization(studentGroup.getSpecialization());
        studentDegree.setStudent(student);
        studentDegree.setStudentGroup(studentGroup);
        studentDegree.setThesisName("Тема роботи");
        studentDegree.setThesisNameEng("Thesis");
        studentDegree.setPreviousDiplomaDate(new Date());
        studentDegree.setPreviousDiplomaNumber("123456");
        studentDegree.setDiplomaDate(new Date());
        studentDegree.setDiplomaNumber("654321");
        studentDegree.setActive(true);
        studentDegree.setProtocolDate(new Date());
        studentDegree.setProtocolNumber("112233");
        studentDegree.setSupplementDate(new Date());
        studentDegree.setSupplementNumber("234567");
        return studentDegree;
    }

    @Test
    public void testGetGradeFromPoints() {
        Assert.assertEquals(EctsGrade.A, EctsGrade.getEctsGrade(95));
        Assert.assertEquals(EctsGrade.B, EctsGrade.getEctsGrade(82));
        Assert.assertEquals(EctsGrade.D, EctsGrade.getEctsGrade(67));
        Assert.assertEquals(EctsGrade.C, EctsGrade.getEctsGrade(78));
    }

    private static Course createCourse(CourseName courseName, int hours, boolean knowledgeControlHasGrade) {
        Course c = createCourse(knowledgeControlHasGrade);
        c.setHours(hours);
        c.setCredits(new BigDecimal(hours/c.getHoursPerCredit()));
        c.setCourseName(courseName);
        return c;
    }

    @Test
    public void testGradeSetting() {
        Grade grade1 = createGrade(createCourse(true), 90);
        Assert.assertEquals("Відмінно", grade1.getNationalGradeUkr());
        Assert.assertEquals("Excellent", grade1.getNationalGradeEng());
        Assert.assertEquals(EctsGrade.A, grade1.getEcts());
        Assert.assertEquals(Integer.valueOf(5), grade1.getGrade());

        Grade grade2 = createGrade(createCourse(true), 76);
        Assert.assertEquals("Добре", grade2.getNationalGradeUkr());
        Assert.assertEquals("Good", grade2.getNationalGradeEng());
        Assert.assertEquals(EctsGrade.C, grade2.getEcts());
        Assert.assertEquals(Integer.valueOf(4), grade2.getGrade());

        Grade grade3 = createGrade(createCourse(true), 65);
        Assert.assertEquals("Задовільно", grade3.getNationalGradeUkr());
        Assert.assertEquals("Satisfactory", grade3.getNationalGradeEng());
        Assert.assertEquals(EctsGrade.D, grade3.getEcts());
        Assert.assertEquals(Integer.valueOf(3), grade3.getGrade());

        Grade grade4 = createGrade(createCourse(false), 90);
        Assert.assertEquals("Зараховано", grade4.getNationalGradeUkr());
        Assert.assertEquals("Passed", grade4.getNationalGradeEng());
        Assert.assertEquals(EctsGrade.A, grade4.getEcts());
        Assert.assertEquals(Integer.valueOf(5), grade4.getGrade());

        Grade grade5 = createGrade(createCourse(false), 59);
        Assert.assertEquals("Не зараховано", grade5.getNationalGradeUkr());
        Assert.assertEquals("Fail", grade5.getNationalGradeEng());
        Assert.assertEquals(EctsGrade.FX, grade5.getEcts());
    }

    @Test
    public void testCleanFileName() {
        Assert.assertEquals("fileName", documentIOService.cleanFileName("../file Name"));
        Assert.assertEquals("fileName123exe", documentIOService.cleanFileName("file Name123.exe"));
    }

    @Test
    public void testGetValueSafely() {
        Assert.assertEquals("", TemplateUtil.getValueSafely(null));
        Assert.assertEquals("42", TemplateUtil.getValueSafely(null, "42"));
        Assert.assertEquals("42", TemplateUtil.getValueSafely("", "42"));
        Assert.assertEquals("Normal string", TemplateUtil.getValueSafely("Normal string"));

    }
}
