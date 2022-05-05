package ua.edu.chdtu.deanoffice.api.specialization.dto;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.validation.ExistingIdDTO;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class SpecializationWriteDTO {
    @NotNull(message = "Обов'язково повинна бути назва")
    @Size(min = 5, message = "Назва занадто коротка")
    private String name;
    private String nameEng;
    private boolean active;
    @NotNull(message = "Обов'язково повинна бути спеціальність")
    private ExistingIdDTO speciality;
    @NotNull(message = "Обов'язково повинен бути освітній ступінь")
    private ExistingIdDTO degree;
    @NotNull(message = "Обов'язково повинна бути випускаюча кафедра")
    private ExistingIdDTO department;
    private String code;
    private String specializationName;
    private String specializationNameEng;
    private ExistingIdDTO programHead;
    private String certificateNumber;
    private Date certificateDate;
    private String certificateIssuedBy;
    private String certificateIssuedByEng;
    private int normativeCreditsNumber;
    private BigDecimal normativeTermOfStudy;
}
