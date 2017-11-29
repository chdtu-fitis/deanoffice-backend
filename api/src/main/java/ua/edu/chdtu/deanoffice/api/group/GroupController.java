package ua.edu.chdtu.deanoffice.api.group;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.group.dto.GroupViews;
import ua.edu.chdtu.deanoffice.entity.TestEntity;
import ua.edu.chdtu.deanoffice.test.TestService;
import ua.edu.chdtu.deanoffice.webstarter.ErrorMessageResponse;

@RestController
@RequestMapping("/groups")
public class GroupController {
    @Autowired
    private TestService testService;

    @RequestMapping("/")
    @ResponseBody
    @JsonView(GroupViews.Name.class)
    public TestEntity getGroups() {
        TestEntity test = testService.getTest();
        return test;
    }

    @RequestMapping("{id}/courses")
    @ResponseBody
    public TestEntity getCourses(@PathVariable String id) {
        TestEntity test = testService.getTest();
        return test;
    }

}
