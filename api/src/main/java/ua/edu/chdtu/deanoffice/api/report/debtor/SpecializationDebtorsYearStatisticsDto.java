package ua.edu.chdtu.deanoffice.api.report.debtor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpecializationDebtorsYearStatisticsDto {
    private int budgetStudents;
    private int contractStudents;
    private int budgetDebtors;
    private int contractDebtors;
    private double debtorsPercent;
    private int lessThanThreeDebtsForBudgetDebtors;
    private int lessThanThreeDebtsForContractDebtors;
    private int threeOrMoreDebtsForBudgetDebtors;
    private int threeOrMoreDebtsForContractDebtors;
}
