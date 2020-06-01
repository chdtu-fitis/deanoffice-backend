package ua.edu.chdtu.deanoffice.service.stipend;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StipendServiceTest {
    @Mock
    StudentDegreeRepository studentDegreeRepositoryMock;
    @Mock
    StudentDegreeService studentDegreeServiceMock;
    @Mock
    CurrentYearService currentYearServiceMock;
    @InjectMocks
    StipendService stipendService;

    static List<Object[]> debtorStudentDegreesRawData;
    static List<DebtorStudentDegreesBean> debtorStudentDegreesMock;

    @BeforeClass
    public static void beforeClass() {
        debtorStudentDegreesRawData = new ArrayList<>();
        Object[][] rows = {
            {1, "Петренко", "Сергій", "Іванович", "бакалавр", "ПЗ-164", 4, "REGULAR", "121", "Інженерія програмного забезпечення", "Інженерія програмного забезпечення",
                    "ПЗАС", 50, "Безпека життєдіяльності та цивільний захист", "іспит", 7},
            {2, "Степанов", "Олександр", "Сергійович", "бакалавр", "ПЗ-164", 4, "REGULAR", "121", "Інженерія програмного забезпечення", "Інженерія програмного забезпечення",
                    "ПЗАС", null, "Безпека життєдіяльності та цивільний захист", "іспит", 7},
            {3, "Кіт", "Олександр", "Вікторович", "бакалавр", "ПЗ-164", 4, "REGULAR", "121", "Інженерія програмного забезпечення", "Інженерія програмного забезпечення",
                    "ПЗАС", null, "Інтелектуальні системи", "залік", 7}
        };
        debtorStudentDegreesRawData = Arrays.asList(rows);
        debtorStudentDegreesMock = new ArrayList<>();
        debtorStudentDegreesMock.add(new DebtorStudentDegreesBean(1, "Петренко", "Сергій", "Іванович", "бакалавр",
                "ПЗ-164", 4, "REGULAR", "121", "Інженерія програмного забезпечення", "Інженерія програмного забезпечення",
                "ПЗАС", BigDecimal.ZERO, "Безпека життєдіяльності та цивільний захист", "іспит", 0));
        debtorStudentDegreesMock.add(new DebtorStudentDegreesBean(2, "Степанов", "Олександр", "Сергійович", "бакалавр",
                "ПЗ-164", 4, "REGULAR", "121", "Інженерія програмного забезпечення", "Інженерія програмного забезпечення",
                "ПЗАС", BigDecimal.ZERO, "Безпека життєдіяльності та цивільний захист", "іспит", 0));
        debtorStudentDegreesMock.add(new DebtorStudentDegreesBean(3, "Кіт", "Олександр", "Вікторович", "бакалавр",
                "ПЗ-164", 4, "REGULAR", "121", "Інженерія програмного забезпечення", "Інженерія програмного забезпечення",
                "ПЗАС", BigDecimal.ZERO, "Інтелектуальні системи", "залік", 0));
    }

    @Test
    public void testGetDebtorStudentDegrees() {
        when(studentDegreeRepositoryMock.findDebtorStudentDegreesRaw(anyInt(), anyInt(), anyInt())).thenReturn(debtorStudentDegreesRawData);
        List<DebtorStudentDegreesBean> debtorStudentDegrees = stipendService.getDebtorStudentDegrees(1);
        for (int i = 0; i < debtorStudentDegrees.size(); i++) {
            DebtorStudentDegreesBean debtorStudentDegree = debtorStudentDegrees.get(i);
            DebtorStudentDegreesBean debtorStudentDegreesBeanMock = debtorStudentDegreesMock.get(i);
            assertEquals(debtorStudentDegreesBeanMock.getId(), debtorStudentDegree.getId());
            assertEquals(debtorStudentDegreesBeanMock.getSurname(), debtorStudentDegree.getSurname());
            assertEquals(debtorStudentDegreesBeanMock.getName(), debtorStudentDegree.getName());
            assertEquals(debtorStudentDegreesBeanMock.getPatronimic(), debtorStudentDegree.getPatronimic());
            assertEquals(debtorStudentDegreesBeanMock.getDegreeName(), debtorStudentDegree.getDegreeName());
            assertEquals(debtorStudentDegreesBeanMock.getGroupName(), debtorStudentDegree.getGroupName());
            assertEquals(debtorStudentDegreesBeanMock.getYear(), debtorStudentDegree.getYear());
            assertEquals(debtorStudentDegreesBeanMock.getTuitionTerm(), debtorStudentDegree.getTuitionTerm());
            assertEquals(debtorStudentDegreesBeanMock.getSpecialityCode(), debtorStudentDegree.getSpecialityCode());
            assertEquals(debtorStudentDegreesBeanMock.getSpecialityName(), debtorStudentDegree.getSpecialityName());
            assertEquals(debtorStudentDegreesBeanMock.getDepartmentAbbreviation(), debtorStudentDegree.getDepartmentAbbreviation());
            assertEquals(debtorStudentDegreesBeanMock.getAverageGrade(), debtorStudentDegree.getAverageGrade());
            assertEquals(debtorStudentDegreesBeanMock.getExtraPoints(), debtorStudentDegree.getExtraPoints());
        }
    }
}
