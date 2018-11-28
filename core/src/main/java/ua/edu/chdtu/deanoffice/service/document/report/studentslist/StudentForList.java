package ua.edu.chdtu.deanoffice.service.document.report.studentslist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class StudentForList {
    private static Logger log = LoggerFactory.getLogger(StudentForList.class);
    private String name;
    private String numberBook;
    private String number;
    private String numberContract;


    public StudentForList(String number, String name, String numberBook,String numberContract) {
        this.number = number;
        this.name = name;
        this.numberBook = numberBook;
        this.numberContract = numberContract;
    }

    Map<String, String> getDictionary() {
        Map<String, String> result = new HashMap<>();
        result.put("n", number);
        result.put("name", name);
        result.put("nBook", numberBook);
        result.put("c", numberContract);
        return result;
    }


}
