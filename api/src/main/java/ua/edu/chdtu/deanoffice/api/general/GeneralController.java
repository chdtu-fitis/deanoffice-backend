package ua.edu.chdtu.deanoffice.api.general;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.service.GeneralService;

import java.util.List;

@RestController
public class GeneralController {
    private GeneralService generalService;

    @RequestMapping(method = RequestMethod.GET, path = "/degrees")
    public ResponseEntity<List<NamedDTO>> getDegrees() {

        return null;
    }
}
