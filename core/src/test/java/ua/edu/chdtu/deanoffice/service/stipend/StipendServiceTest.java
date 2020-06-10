package ua.edu.chdtu.deanoffice.service.stipend;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.document.report.journal.GradesJournalService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class StipendServiceTest {

    GradesJournalService gradesJournalServiceMock = mock(GradesJournalService.class);
    CourseForGroupService courseForGroupServiceMock = mock(CourseForGroupService.class);

    @Test
    public void testCoursesNumberEquality() {
        List<CourseForGroup> courseForGroups = courseForGroupServiceMock.getCoursesForGroupBySemester(1, 1);
        Mockito.when(gradesJournalServiceMock.getCoursesNumber(courseForGroups)).thenReturn(10);
        assertEquals(10,gradesJournalServiceMock.getCoursesNumber(courseForGroups));
    }

    @Test
    public void checkStudentsNumber() {
        //student group ID = 479
        List<Integer> studentsList = new ArrayList<>();
        studentsList.add(1);
        studentsList.add(2);
        studentsList.add(3);
        studentsList.add(4);
        studentsList.add(5);
        studentsList.add(6);
        studentsList.add(7);

        int studentsNumber = 7;
        Mockito.when(gradesJournalServiceMock.getStudentsIdsByGroupId(479)).thenReturn(studentsList);
        assertEquals(gradesJournalServiceMock.getStudentsIdsByGroupId(479).size(),studentsNumber);
    }

    @Test
    public void checkExceptionThrowTest(){
        Mockito.when(gradesJournalServiceMock.getStudentsIdsByGroupId(anyInt())).thenThrow( new IllegalArgumentException());
    }
}
