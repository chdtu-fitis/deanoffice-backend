package ua.edu.chdtu.deanoffice;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ua.edu.chdtu.deanoffice.repository.GradeRepository;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;
import ua.edu.chdtu.deanoffice.repository.StudentGroupRepository;
import ua.edu.chdtu.deanoffice.repository.StudentRepository;
import ua.edu.chdtu.deanoffice.service.GradeService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.StudentService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.DiplomaSupplementService;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.SupplementTemplateFillService;
import ua.edu.chdtu.deanoffice.service.document.report.groupgrade.SummaryForGroupService;

import javax.transaction.Transactional;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DiplomaSupplementService.class, StudentService.class, GradeService.class,
        StudentRepository.class, GradeRepository.class, StudentGroupRepository.class, StudentGroupService.class,
        DocumentIOService.class, SupplementTemplateFillService.class, StudentDegreeService.class, StudentDegreeRepository.class, SummaryForGroupService.class})
@EnableAutoConfiguration
@Transactional
public class TestService {

    @Autowired
    SummaryForGroupService summaryForGroupService;

    @Autowired
    StudentGroupService studentGroupService;

    @Autowired
    StudentGroupRepository studentGroupRepository;

    @Test
    public void testNewService() throws IOException, Docx4JException {
        Object o = studentGroupService.getGroups();
        o = studentGroupRepository.findAll();
        o = summaryForGroupService.formDocument(389, "docx");
        return;
    }


}
