package ua.edu.chdtu.deanoffice.api.student;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentView;
import ua.edu.chdtu.deanoffice.entity.*;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
public class ExpelledOrRenewedStudentDTO {
    @JsonView(StudentView.Expel.class)
    private String operation;
    @JsonView(StudentView.Expel.class)
    private Integer expelOrRenewId;
    @JsonView(StudentView.Expel.class)
    private int expelOrRenewStudyYear;
    @JsonView(StudentView.Expel.class)
    private Payment expelOrRenewPayment;
    @JsonView(StudentView.Expel.class)
    private Date expelOrRenewDate;
    @JsonView(StudentView.Expel.class)
    private String expelOrRenewOrderNumber;
    @JsonView(StudentView.Expel.class)
    private Date expelOrRenewOrderDate;
    @JsonView(StudentView.Expel.class)
    private NamedDTO orderReason;
    @JsonView(StudentView.Expel.class)
    private Date expelOrRenewApplicationDate;
}
