package ua.edu.chdtu.deanoffice.service.document.report.studentslist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class StudentForList {
    private static Logger log = LoggerFactory.getLogger(StudentForList.class);
    private String fullName;
    private String recordBookNumber;
    private String number;
    private String contract;

    public StudentForList(String number, String fullName, String recordBookNumber,String contract) {
        this.number = number;
        this.fullName = fullName;
        this.recordBookNumber = recordBookNumber;
        this.contract = contract;
    }

    Map<String, String> getDictionary() {
        Map<String, String> result = new HashMap<>();
        result.put("n", number);
        result.put("name", fullName);
        result.put("nBook", recordBookNumber);
        result.put("c", contract);
        return result;
    }
}
