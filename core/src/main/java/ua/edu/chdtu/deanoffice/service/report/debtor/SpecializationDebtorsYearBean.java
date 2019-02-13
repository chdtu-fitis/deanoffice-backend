package ua.edu.chdtu.deanoffice.service.report.debtor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpecializationDebtorsYearBean {
    private int budgetStudents;
    private int contractStudents;
    private int budgetDebtors;
    private int contractDebtors;
    private double debtorsPercent;
    private int lessThanThreeDebtsForBudgetDebtors;
    private int lessThanThreeDebtsForContractDebtors;
    private int threeOrMoreDebtsForBudgetDebtors;
    private int threeOrMoreDebtsForContractDebtors;

    public SpecializationDebtorsYearBean(int budgetStudents, int contractStudents, int budgetDebtors,
                                         int contractDebtors, double debtorsPercent, int lessThanThreeDebtsForBudgetDebtors,
                                         int lessThanThreeDebtsForContractDebtors, int threeOrMoreDebtsForBudgetDebtors,
                                         int threeOrMoreDebtsForContractDebtors) {
        this.budgetStudents = budgetStudents;
        this.contractStudents = contractStudents;
        this.budgetDebtors = budgetDebtors;
        this.contractDebtors = contractDebtors;
        this.debtorsPercent = debtorsPercent;
        this.lessThanThreeDebtsForBudgetDebtors = lessThanThreeDebtsForBudgetDebtors;
        this.lessThanThreeDebtsForContractDebtors = lessThanThreeDebtsForContractDebtors;
        this.threeOrMoreDebtsForBudgetDebtors = threeOrMoreDebtsForBudgetDebtors;
        this.threeOrMoreDebtsForContractDebtors = threeOrMoreDebtsForContractDebtors;
    }
}
