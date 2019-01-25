package ua.edu.chdtu.deanoffice.service.report.debtor;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.TreeMap;

@Getter
@Setter
public class SpecializationDebtorsBean {
    private Map<Integer, SpecializationDebtorsYearBean> specializationDebtorsYearBeanMap = new TreeMap<>();
}
