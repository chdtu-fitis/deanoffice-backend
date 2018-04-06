package ua.edu.chdtu.deanoffice.api.general.parser;

import org.junit.Before;
import org.junit.Test;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentDTO;
import ua.edu.chdtu.deanoffice.entity.Student;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.modelmapper.convention.MatchingStrategies.STRICT;
import static ua.edu.chdtu.deanoffice.api.general.parser.Parser.parse;

public class ParserTest {
    private StudentDTO sourceObject = new StudentDTO();
    private Student expectedObject = new Student();

    private List<StudentDTO> sourceList;
    private List<Student> expectedList;

    private Set<StudentDTO> sourceSet = new HashSet<>();
    private Set<Student> expectedSet = new HashSet<>();

// For objects --------------------------------------------------------------------
    @Before
    public void setUpObject() {
        sourceObject.setEmail("example@com");
        expectedObject.setEmail("example@com");
    }

    @Test
    public void forObject() {
        Student actualObject = (Student) parse(sourceObject, Student.class);
        assertParser(actualObject, expectedObject);

        actualObject = (Student) parse(sourceObject, Student.class, STRICT);
        assertParser(actualObject, expectedObject);
    }

    private void assertParser(Student actualObject, Student expectedObject) {
        assertEquals(actualObject.getEmail(), expectedObject.getEmail());
    }

// For lists ---------------------------------------------------------------------
    @Before
    public void setUpList() {
        sourceList = singletonList(sourceObject);
        expectedList = singletonList(expectedObject);
    }

    @Test
    public void forList() {
        List<Student> actualList = parse(sourceList, Student.class);
        assertParser(actualList);

        actualList = parse(sourceList, Student.class, STRICT);
        assertParser(actualList);
    }

    private void assertParser(List<Student> actualList) {
        assertParser(actualList.get(0), expectedList.get(0));
    }

// For Sets -----------------------------------------------------------------------
    @Before
    public void setUpSet() {
        sourceSet.add(sourceObject);
        expectedSet.add(expectedObject);
    }

    @Test
    public void forSet() {
        Set<Student> actualSet = parse(sourceSet, Student.class);
        assertParser(actualSet);

        actualSet = parse(sourceSet, Student.class, STRICT);
        assertParser(actualSet);
    }

    private void assertParser(Set<Student> actualMap) {
        assertParser((Student) actualMap.toArray()[0], (Student) expectedSet.toArray()[0]);
    }
}
