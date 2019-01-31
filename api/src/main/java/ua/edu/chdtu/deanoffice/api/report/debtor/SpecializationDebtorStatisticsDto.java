package ua.edu.chdtu.deanoffice.api.report.debtor;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.service.report.debtor.SpecializationDebtorsYearBean;

import java.util.Map;
import java.util.TreeMap;

@Getter
@Setter
public class SpecializationDebtorStatisticsDto {
    private Map<Integer, SpecializationDebtorsYearStatisticsDto> specializationDebtorsYearBeanMap = new TreeMap<>();
}
