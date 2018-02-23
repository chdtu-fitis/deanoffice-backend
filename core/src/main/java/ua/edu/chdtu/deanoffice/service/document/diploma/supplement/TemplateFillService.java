package ua.edu.chdtu.deanoffice.service.document.diploma.supplement;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.*;
import static ua.edu.chdtu.deanoffice.util.GradeUtil.getNationalGradeEng;
import static ua.edu.chdtu.deanoffice.util.GradeUtil.getNationalGradeUkr;

@Service
public class TemplateFillService {

    private static final int FIRST_SECTION_ROW_INDEX = 2;
    private static final int TEMPLATE_ROW_INDEX = 1;
    private static final Logger log = LoggerFactory.getLogger(TemplateFillService.class);
    private DocumentIOService documentIOService;

    public TemplateFillService(DocumentIOService documentIOService) {
        this.documentIOService = documentIOService;
    }

    private static Map<String, String> getGradeDictionary(Grade grade) {
        Map<String, String> result = new HashMap<>();
        result.put("Credits", formatCredits(grade.getCourse().getCredits()));
        result.put("Hours", formatHours(grade.getCourse().getHours()));
        result.put("LocalGrade", String.format("%d", grade.getPoints()));
        result.put("NationalGradeUkr", getNationalGradeUkr(grade));
        result.put("NationalGradeEng", getNationalGradeEng(grade));
        result.put("ECTSGrade", grade.getEcts().toString());
        result.put("CourseNameUkr", grade.getCourse().getCourseName().getName());
        result.put("CourseNameEng", grade.getCourse().getCourseName().getNameEng());
        return result;
    }

    private static Map<String, String> getTotalDictionary(StudentSummary studentSummary) {
        Map<String, String> result = new HashMap<>();
        result.put("TotalHours", String.format("%4d", studentSummary.getTotalHours()));
        result.put("TotalCredits", formatCredits(studentSummary.getTotalCredits()));
        result.put("TotalGrade", String.format("%2d", Math.round(studentSummary.getTotalGrade())));
        result.put("TotalECTS", studentSummary.getTotalEcts().toString());
        result.put("TotalNGradeUkr", studentSummary.getTotalNationalGradeUkr());
        result.put("TotalNGradeEng", studentSummary.getTotalNationalGradeEng());
        return result;
    }

    private static Map<String, String> getStudentInfoDictionary(StudentSummary studentSummary) {
        StudentGroup group = studentSummary.getStudent().getStudentGroup();
        Map<String, String> result = new HashMap<>();

        result.put("SurnameUkr", getValueSafely(studentSummary.getStudent().getSurname().toUpperCase(), "Ім'я"));
        result.put("SurnameEng", getValueSafely(studentSummary.getStudent().getSurnameEng(), "Surname").toUpperCase());
        result.put("NameUkr", getValueSafely(studentSummary.getStudent().getName().toUpperCase(), "Прізвище"));
        result.put("NameEng", getValueSafely(studentSummary.getStudent().getNameEng(), "Name").toUpperCase());
        result.put("PatronimicUkr", getValueSafely(studentSummary.getStudent().getPatronimic().toUpperCase(), "По-батькові"));

        DateFormat dateOfBirthFormat = new SimpleDateFormat("dd.MM.yyyy");
        result.put("BirthDate", studentSummary.getStudent().getBirthDate() != null
                ? dateOfBirthFormat.format(studentSummary.getStudent().getBirthDate())
                : "BirthDate");

        TuitionForm tuitionForm = studentSummary.getStudent().getStudentGroup().getTuitionForm();
        String modeOfStudyUkr = "";
        String modeOfStudyEng = "";
        String modeOfStudyUkrAblativeCase = "";
        switch (tuitionForm) {
            case FULL_TIME:
                modeOfStudyUkr = "Денна";
                modeOfStudyUkrAblativeCase = "денною";
                modeOfStudyEng = "Full-time";
                break;
            case EXTRAMURAL:
                modeOfStudyUkr = "Заочна";
                modeOfStudyUkrAblativeCase = "заочною";
                modeOfStudyEng = "Part-time";
                break;
        }

        result.put("ModeOfStudyUkr", modeOfStudyUkr);
        result.put("ModeOfStudyEng", modeOfStudyEng);
        result.put("ModeOfStudyUkrAblativeCase", modeOfStudyUkrAblativeCase);
        result.put("ModeOfStudyEngAblativeCase", modeOfStudyEng.toLowerCase());

        Specialization specialization = studentSummary.getStudent().getStudentGroup().getSpecialization();
        Speciality speciality = specialization.getSpeciality();
        Degree degree = specialization.getDegree();
        result.put("SpecializationUkr", specialization.getName());
        result.put("SpecializationEng", getValueSafely(specialization.getNameEng()));
        result.put("SpecialityUkr", speciality.getName());
        result.put("SpecialityEng", getValueSafely(speciality.getNameEng()));
        result.put("DegreeUkr", degree.getName());
        result.put("DegreeEng", getValueSafely(degree.getNameEng()));
        result.put("DEGREEUKR", degree.getName().toUpperCase());
        result.put("DEGREEENG", getValueSafely(degree.getNameEng()).toUpperCase());
        result.put("TheoreticalTrainingCredits", formatCredits(countCreditsSum(studentSummary.getGrades().get(0))));
        result.put("PracticalTrainingCredits", formatCredits(countCreditsSum(studentSummary.getGrades().get(2))
                .add(countCreditsSum(studentSummary.getGrades().get(1)))));
        result.put("ThesisDevelopmentCredits", formatCredits(countCreditsSum(studentSummary.getGrades().get(3))));
        result.put("RequiredCredits", formatCredits(specialization.getRequiredCredits()));
        result.put("DegreeRequiredCredits", formatCredits(studentSummary.getTotalCredits()));
        result.put("QualificationUkr", getValueSafely(specialization.getQualification()));
        result.put("QualificationEng", getValueSafely(specialization.getQualificationEng()));
        result.put("TrainingDurationYears", String.format("%1d", group.getStudyYears().intValue()));
        switch ((int) Math.round(group.getStudyYears().doubleValue())) {
            case 0:
                result.put("TrainingDurationYears", " ");
                result.put("DurationLabelYears", " ");
                result.put("DurationLabelYearsEng", " ");
                break;
            case 1:
                result.put("DurationLabelYears", "рік");
                result.put("DurationLabelYearsEng", "year");
                break;
            case 2:
                result.put("DurationLabelYears", "роки");
                result.put("DurationLabelYearsEng", "years");
                break;
            case 3:
                result.put("DurationLabelYears", "роки");
                result.put("DurationLabelYearsEng", "years");
                break;
            case 4:
                result.put("DurationLabelYears", "роки");
                result.put("DurationLabelYearsEng", "years");
                break;
            default:
                result.put("DurationLabelYears", "років");
                result.put("DurationLabelYearsEng", "years");
                break;
        }

        result.put("TrainingDurationMonths", getMonthsFromYears(group.getStudyYears()));
        switch (Math.round(Integer.parseInt(result.get("TrainingDurationMonths")))) {
            case 0:
                result.put("TrainingDurationMonths", " ");
                result.put("DurationLabelMonths", " ");
                result.put("DurationLabelMonthsEng", " ");
                break;
            case 1:
                result.put("DurationLabelMoths", "місяць");
                result.put("DurationLabelMonthsEng", "month");
                break;
            case 2:
                result.put("DurationLabelMoths", "місяці");
                result.put("DurationLabelMonthsEng", "months");
                break;
            case 3:
                result.put("DurationLabelMoths", "місяці");
                result.put("DurationLabelMonthsEng", "months");
                break;
            case 4:
                result.put("DurationLabelMoths", "місяці");
                result.put("DurationLabelMonthsEng", "months");
                break;
            default:
                result.put("DurationLabelMonths", "місяців");
                result.put("DurationLabelMonthsEng", "months");
                break;
        }
        result.put("FieldOfStudy", getValueSafely(speciality.getFieldOfStudy()));
        result.put("FieldOfStudyEng", getValueSafely(speciality.getFieldOfStudyEng()));
        result.put("QualificationLevel", getValueSafely(degree.getQualificationLevelDescription()));
        result.put("QualificationLevelEng", getValueSafely(degree.getQualificationLevelDescriptionEng()));
        result.put("AdmissionRequirements", getValueSafely(degree.getAdmissionRequirements()));
        result.put("AdmissionRequirementsEng", getValueSafely(degree.getAdmissionRequirementsEng()));
        result.put("FurtherStudyAccess", getValueSafely(degree.getFurtherStudyAccess()));
        result.put("FurtherStudyAccessEng", getValueSafely(degree.getFurtherStudyAccessEng()));
        result.put("ProfessionalStatus", getValueSafely(degree.getProfessionalStatus()));
        result.put("ProfessionalStatusEng", getValueSafely(degree.getProfessionalStatusEng()));

        result.put("KnowledgeAndUnderstanding", getValueSafely(specialization.getKnowledgeAndUnderstandingOutcomes()));
        result.put("KnowledgeAndUnderstandingEng", getValueSafely(specialization.getKnowledgeAndUnderstandingOutcomesEng()));
        result.put("ApplyingKnowledgeAndUnderstanding", getValueSafely(specialization.getApplyingKnowledgeAndUnderstandingOutcomes()));
        result.put("ApplyingKnowledgeAndUnderstandingEng", getValueSafely(specialization.getApplyingKnowledgeAndUnderstandingOutcomesEng()));
        result.put("MakingJudgements", getValueSafely(specialization.getMakingJudgementsOutcomes()));
        result.put("MakingJudgementsEng", getValueSafely(specialization.getMakingJudgementsOutcomesEng()));
        result.put("ProgramHeadName", getValueSafely(specialization.getEducationalProgramHeadName()));
        result.put("ProgramHeadNameEng", getValueSafely(specialization.getEducationalProgramHeadNameEng()));
        result.put("ProgramHeadInfo", getValueSafely(specialization.getEducationalProgramHeadInfo()));
        result.put("ProgramHeadInfoEng", getValueSafely(specialization.getEducationalProgramHeadInfoEng()));

        DateFormat diplomaDateFormat = dateOfBirthFormat;
        StudentDegree studentDegree;
        try {
            studentDegree = studentSummary.getStudent().getDegrees().stream().filter(
                    sd -> sd.getDegree().getName()
                            .equals(studentSummary.getStudent().getStudentGroup().getSpecialization().getDegree().getName()))
                    .findFirst().get();
        } catch (NoSuchElementException e) {
            log.warn("There is no suitable StudentDegree for " + studentSummary.getStudent().getInitialsUkr());
            return result;
        }
        result.put("ThesisNameUkr", getValueSafely(studentDegree.getThesisName()));
        result.put("ThesisNameEng", getValueSafely(studentDegree.getThesisNameEng()));
        result.put("ProtocolNumber", getValueSafely(studentDegree.getProtocolNumber()));
        result.put("PreviousDiplomaNumber", getValueSafely(studentDegree.getPreviousDiplomaNumber()));

        int dateStyle = DateFormat.LONG;
        DateFormat protocolDateFormatUkr = DateFormat.getDateInstance(dateStyle, new Locale("uk", "UA"));
        result.put("ProtocolDateUkr", studentDegree.getProtocolDate() == null ? ""
                : protocolDateFormatUkr.format(studentDegree.getProtocolDate()));
        DateFormat protocolDateFormatEng = DateFormat.getDateInstance(dateStyle, Locale.ENGLISH);
        result.put("ProtocolDateEng", studentDegree.getProtocolDate() == null ? ""
                : protocolDateFormatEng.format(studentDegree.getProtocolDate()));

        result.put("SupplNumber", getValueSafely(studentDegree.getSupplementNumber(), "СС № НОМЕРДОД"));
        result.put("SupplDate", studentDegree.getSupplementDate() == null ? "ДАТА ДОД"
                : diplomaDateFormat.format(studentDegree.getSupplementDate()));
        result.put("DiplNumber", getValueSafely(studentDegree.getDiplomaNumber(), "МСС № НОМЕРДИП"));
        result.put("DiplDate", studentDegree.getDiplomaDate() == null ? "ДАТА ДИПЛ"
                : diplomaDateFormat.format(studentDegree.getDiplomaDate()));
        return result;
    }

    private static String formatCredits(BigDecimal credits) {
        if (credits == null || credits.equals(new BigDecimal(0))) {
            return "-";
        } else {
            String formattedCredits = String.format("%.1f", credits);
            if (formattedCredits.split(",")[1].equals("0")) {
                return String.format("%.0f", credits);
            } else {
                return formattedCredits;
            }
        }
    }

    private static String formatHours(int hours) {
        if (hours == 0) {
            return "-";
        } else {
            return String.format("%d", hours);
        }
    }

    private static BigDecimal countCreditsSum(List<Grade> grades) {
        BigDecimal result = new BigDecimal(0);
        for (Grade g :
                grades) {
            result = result.add(g.getCourse().getCredits());
        }
        return result;
    }

    private static String getMonthsFromYears(BigDecimal years) {
        int intPart = years.intValue();
        int monthsPerYear = 12;
        return String.format("%d", Math.round((years.doubleValue() - intPart) * monthsPerYear));
    }

    private Map<String, String> getReplacementsDictionary(StudentSummary studentSummary) {
        Map<String, String> result = new HashMap<>();
        result.putAll(getStudentInfoDictionary(studentSummary));
        result.putAll(getTotalDictionary(studentSummary));
        return result;
    }

    public WordprocessingMLPackage fill(String templateFilepath, StudentSummary studentSummary)
            throws IOException, Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(templateFilepath);
        fillTableWithGrades(template, studentSummary);
        Map<String, String> commonDict = getReplacementsDictionary(studentSummary);
        replaceTextPlaceholdersInTemplate(template, commonDict);
        replacePlaceholdersInFooter(template, commonDict);
        return template;
    }

    private void fillTableWithGrades(WordprocessingMLPackage template, StudentSummary studentSummary) {
        List<Object> tables = getAllElementsFromObject(template.getMainDocumentPart(), Tbl.class);
        String tableWithGradesKey = "#CourseNum";
        Tbl tempTable = findTable(tables, tableWithGradesKey);
        if (tempTable == null) {
            log.warn("Couldn't find table that contains: " + tableWithGradesKey);
            return;
        }
        List<Object> gradeTableRows = getAllElementsFromObject(tempTable, Tr.class);
        if (gradeTableRows.size() < TEMPLATE_ROW_INDEX + 1) {
            log.warn("Incorrect table with grades is ignored.");
            return;
        }
        Tr templateRow = (Tr) gradeTableRows.get(1);
        int rowToAddIndex = FIRST_SECTION_ROW_INDEX;
        int gradeNumber = 1;

        for (List<Grade> gradesSection : studentSummary.getGrades()) {
            for (Grade grade : gradesSection) {
                Map<String, String> replacements = TemplateFillService.getGradeDictionary(grade);
                replacements.put("CourseNum", String.format("%2d", gradeNumber++));
                addRowToTable(tempTable, templateRow, rowToAddIndex, replacements);
                rowToAddIndex++;
            }
            //Need to skip header of the next section
            rowToAddIndex++;
        }
        tempTable.getContent().remove(templateRow);
    }

}
