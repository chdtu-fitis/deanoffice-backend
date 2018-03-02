package ua.edu.chdtu.deanoffice.api.student.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.NamedDTO;
import ua.edu.chdtu.deanoffice.entity.EducationDocument;
import ua.edu.chdtu.deanoffice.entity.Payment;

import java.util.Date;

@Getter
@Setter
public class StudentDegreeDTO {
    @JsonView(StudentDegreeViews.Simple.class)
    private Integer id;
    @JsonView(StudentDegreeViews.Simple.class)
    private StudentDTO student;
    @JsonView(StudentDegreeViews.Simple.class)
    private NamedDTO studentGroup;
    @JsonView(StudentDegreeViews.DetailAndDegree.class)
    private String recordBookNumber;
    @JsonView(StudentDegreeViews.DetailAndDegree.class)
    private String diplomaNumber;
    @JsonView(StudentDegreeViews.DetailAndDegree.class)
    private Date diplomaDate;
    @JsonView(StudentDegreeViews.DetailAndDegree.class)
    private String supplementNumber;
    @JsonView(StudentDegreeViews.DetailAndDegree.class)
    private Date supplementDate;
    @JsonView(StudentDegreeViews.DetailAndDegree.class)
    private String thesisName;
    @JsonView(StudentDegreeViews.DetailAndDegree.class)
    private String thesisNameEng;
    @JsonView(StudentDegreeViews.DetailAndDegree.class)
    private String protocolNumber;
    @JsonView(StudentDegreeViews.DetailAndDegree.class)
    private Date protocolDate;
    @JsonView(StudentDegreeViews.DetailAndDegree.class)
    private EducationDocument previousDiplomaType;
    @JsonView(StudentDegreeViews.DetailAndDegree.class)
    private String previousDiplomaNumber;
    @JsonView(StudentDegreeViews.DetailAndDegree.class)
    private Date previousDiplomaDate;
    @JsonView(StudentDegreeViews.Simple.class)
    private Payment payment;
}
