package ua.edu.chdtu.deanoffice;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.StudentSummary;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.TemplateFillFactory;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.webstarter.Application;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class TestFeatureDiplomaAddition {

    private static Logger log = LoggerFactory.getLogger(Application.class);

    private static final String TEMPLATE = "DiplomaSupplementTemplate.docx";

    private static DateFormat dateOfBirthFormat = new SimpleDateFormat("dd.MM.yyyy");

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
        studentGroup.setStudySemesters(8);
        studentGroup.setStudyYears(new BigDecimal(4));
        studentGroup.setBeginYears(1);
        studentGroup.setTuitionForm('f');
        studentGroup.setTuitionTerm('f');
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
        student.setStudentGroup(studentGroup);

        Specialization specialization = createSpecialization();
        studentGroup.setSpecialization(specialization);

        Speciality speciality = createSpeciality();
        specialization.setSpeciality(speciality);

        Degree degree = new Degree("Ступінь", "Degree");
        specialization.setDegree(degree);

        return student;
    }

    @Test
    public void testFillWithStudentInformation() {
        Student student = createStudent();
        StudentSummary studentSummary = new StudentSummary(student, new ArrayList<>());
        Assert.assertEquals(true, TemplateFillFactory.fillWithStudentInformation(TEMPLATE, studentSummary).getAbsolutePath().endsWith(".docx"));
    }

    @Test
    public void testAdjustAverage() {
        double[] expectedResult1 = {5, 90};
        double[] actualResult1 = StudentSummary.adjustAverageGradeAndPoints(5, 89);
        Assert.assertArrayEquals(expectedResult1, actualResult1, 0);
    }

}
