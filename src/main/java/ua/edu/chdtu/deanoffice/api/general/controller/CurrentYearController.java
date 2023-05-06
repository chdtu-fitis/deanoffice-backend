package ua.edu.chdtu.deanoffice.api.general.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;

@RestController
@RequestMapping("/current-year")
public class CurrentYearController {
    private CurrentYearService currentYearService;

    public CurrentYearController(CurrentYearService currentYearService) {
        this.currentYearService = currentYearService;
    }

    @GetMapping
    public ResponseEntity<CurrentYearDTO> getCurrentYear() {
        int currYear = currentYearService.getYear();
        CurrentYearDTO currentYearDTO = new CurrentYearDTO(currYear);
        return ResponseEntity.ok(currentYearDTO);
    }

    private class CurrentYearDTO {
        private int currYear;

        CurrentYearDTO(int currYear) {
            this.currYear = currYear;
        }

        public int getCurrYear() {
            return currYear;
        }
    }
}


