package ua.edu.chdtu.deanoffice.api.general;

import org.junit.Before;
import org.junit.Test;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentDTO;
import ua.edu.chdtu.deanoffice.entity.Student;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ParseTest {
    private StudentDTO sourceObject = new StudentDTO();
    private Student expectedObject = new Student();

    private List<StudentDTO> sourceList = new ArrayList<>();
    private List<Student> expectedList = new ArrayList<>();

    @Before
    public void setUpList() {
        sourceObject.setEmail("example@com");
        expectedObject.setEmail("example@com");
    }

    @Test
    public void object() {
        Student actualObject = (Student) Parse.toObject(sourceObject, Student.class);
        assertStudentParser(actualObject, expectedObject);
    }

    private void assertStudentParser(Student actualObject, Student expectedObject) {
        assertEquals(actualObject.getEmail(), expectedObject.getEmail());
    }

    @Before
    public void setList() {
        sourceList.add(sourceObject);
        expectedList.add(expectedObject);
    }

    @Test
    public void list() {
        List<Student> actualList = Parse.toList(sourceList, Student.class);
        assertStudentParser(actualList);
    }

    private void assertStudentParser(List<Student> actualList) {
        assertStudentParser(actualList.get(0), expectedList.get(0));
    }
}