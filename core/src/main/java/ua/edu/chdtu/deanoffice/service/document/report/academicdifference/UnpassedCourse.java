package ua.edu.chdtu.deanoffice.service.document.report.academicdifference;

import java.util.HashMap;
import lombok.Getter;
@Getter
public class UnpassedCourse {
    public String name;
    public int hours;
    public String knowledgeControl;

    public UnpassedCourse(String name, int hours, String knowledgeControl) {
        this.name = name;
        this.hours = hours;
        this.knowledgeControl = knowledgeControl;
    }

    public HashMap<String,String> getDictionary(){
        HashMap<String, String> result = new HashMap();
        result.put("n",name);
        result.put("h",String.valueOf(hours));
        result.put("v",knowledgeControl);
        return result;
    }
}
