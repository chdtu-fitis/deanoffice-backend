package ua.edu.chdtu.deanoffice.api.report.debtor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DebtorAnalysisDto {
    private String studentNumber;
    private String debtorNumber;

    public DebtorAnalysisDto() {
    }

    public DebtorAnalysisDto(String studentNumber, String debtorNumber) {
        this.studentNumber = studentNumber;
        this.debtorNumber = debtorNumber;
    }
}
