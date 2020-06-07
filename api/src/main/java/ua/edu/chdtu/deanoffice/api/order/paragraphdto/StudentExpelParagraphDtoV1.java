package ua.edu.chdtu.deanoffice.api.order.paragraphdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentExpelParagraphDtoV1 {

    private Set<String> fullOrderFieldsList = new HashSet<>(Arrays.asList("studentFullName", "studyYear",
            "studentDegree", "tuitionForm", "groupName",
            "specialization", "speciality", "applicationDate",
            "orderReason", "orderCause"));
    private Set<String> requiredFields = new HashSet<>(Arrays.asList("studentFullName", "studyYear",
            "studentDegree", "tuitionForm", "groupName",
            "specialization", "speciality", "applicationDate",
            "orderReason", "orderCause"));
    private String paragraph;

    public StudentExpelParagraphDtoV1(String paragraph) {
        this.paragraph = paragraph;
    }
}
