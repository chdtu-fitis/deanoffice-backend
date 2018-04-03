package ua.edu.chdtu.deanoffice.api.general;

import org.junit.Before;
import org.junit.Test;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentDTO;
import ua.edu.chdtu.deanoffice.entity.Student;

import static org.junit.Assert.assertEquals;
import static ua.edu.chdtu.deanoffice.api.general.Parse.toObject;

public class ParseTest {
    private StudentDTO sourceObject = new StudentDTO();
    private Student expectedObject = new Student();

    @Before
    public void setUp() {
        setUpObjects();
    }

    private void setUpObjects() {
        sourceObject.setEmail("example@com");
        expectedObject.setEmail("example@com");
    }

    @Test
    public void object() {
        Student actualObject = (Student) toObject(sourceObject, Student.class);
        String actualEmail = actualObject.getEmail();
        assertEquals(actualEmail, expectedObject.getEmail());
    }
}