package ua.edu.chdtu.deanoffice.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ua.edu.chdtu.deanoffice.service.document.report.groupgrade.SummaryForGroupService;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration
public class TestService {

    @Autowired
    SummaryForGroupService summaryForGroupService;

    @Test
    public void testNewService() {

    }


}
