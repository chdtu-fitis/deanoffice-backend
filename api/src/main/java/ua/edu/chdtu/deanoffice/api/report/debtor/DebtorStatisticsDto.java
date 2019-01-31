package ua.edu.chdtu.deanoffice.api.report.debtor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DebtorStatisticsDto {
    private String studentNumber;
    private String debtorNumber;

    public DebtorStatisticsDto() {
    }

    public DebtorStatisticsDto(String studentNumber, String debtorNumber) {
        this.studentNumber = studentNumber;
        this.debtorNumber = debtorNumber;
    }
}
