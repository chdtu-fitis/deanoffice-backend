package ua.edu.chdtu.deanoffice.api.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.entity.TestEntity;
import ua.edu.chdtu.deanoffice.test.TestService;
import ua.edu.chdtu.deanoffice.webstarter.ErrorMessageResponse;

@RestController
@RequestMapping("/test")
public class TestController {

    private final TestService testService;

    @Autowired
    public TestController(TestService testService) {
        this.testService = testService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<String> get() {
        String var = "aaa";
        TestEntity test = testService.getTest();

        return ResponseEntity.ok("TestEntity: " + test);
    }

    @RequestMapping("{var}")
    public ResponseEntity<ErrorMessageResponse> get(@PathVariable String var) {
        if (true)
            throw new RuntimeException("aaqaaq");
        return ResponseEntity.ok(new ErrorMessageResponse("TestVariable: " + var));
    }
}
