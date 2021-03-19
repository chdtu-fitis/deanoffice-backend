package ua.edu.chdtu.deanoffice.service.document.diploma.supplement;

import com.google.common.base.Strings;
import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Br;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.Constants;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.service.*;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.TemplateUtil;
import ua.edu.chdtu.deanoffice.util.DocumentUtil;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.getTextsPlaceholdersFromContentAccessor;

@Service
public class SupplementTemplateFillService {
    private static final Logger log = LoggerFactory.getLogger(SupplementTemplateFillService.class);

    private final DocumentIOService documentIOService;
    private QualificationForSpecializationService qualificationForSpecializationService;
    private AcquiredCompetenciesService acquiredCompetenciesService;
    private StudentAcademicVacationService studentAcademicVacationService;
    //    private RenewedAcademicVacationStudentRepository
    private StudentExpelService studentExpelService;
    private RenewedExpelledStudentService renewedExpelledStudentService;

    public SupplementTemplateFillService(DocumentIOService documentIOService,
                                         QualificationForSpecializationService qualificationForSpecializationService,
                                         AcquiredCompetenciesService acquiredCompetenciesService,
                                         StudentAcademicVacationService studentAcademicVacationService,
                                         StudentExpelService studentExpelService,
                                         RenewedExpelledStudentService renewedExpelledStudentService) {
        this.documentIOService = documentIOService;
        this.qualificationForSpecializationService = qualificationForSpecializationService;
        this.acquiredCompetenciesService = acquiredCompetenciesService;
        this.studentAcademicVacationService = studentAcademicVacationService;
        this.studentExpelService = studentExpelService;
        this.renewedExpelledStudentService = renewedExpelledStudentService;
    }

    WordprocessingMLPackage fill(String templateFilepath, StudentSummary studentSummary)
            throws Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(templateFilepath);

        fillAcquiredCompetencies(template, studentSummary);
        fillTableWithGrades(template, studentSummary);
//        fillProfessionalQualificationsTable(template, studentSummary);

        Map<String, String> commonDict = getReplacementsDictionary(studentSummary);
        TemplateUtil.replaceTextPlaceholdersInTemplate(template, commonDict);
        TemplateUtil.replacePlaceholdersInFooter(template, commonDict);

        return template;
    }

    private void fillAcquiredCompetencies(WordprocessingMLPackage template, StudentSummary studentSummary) {
        AcquiredCompetencies competencies = acquiredCompetenciesService.getLastAcquiredCompetencies(
                studentSummary.getStudentGroup().getSpecialization().getId());
        if (competencies != null) {
            fillCompetenciesTable(template, competencies, "#AcquiredCompetencies");
            fillCompetenciesTable(template, competencies, "#AcquiredCompetenciesEng");
        }
    }

    private void fillTableWithGrades(WordprocessingMLPackage template, StudentSummary studentSummary) {
        int firstSectionRowIndex = 2;
        int templateRowIndex = 1;
        Tbl tableWithGrades = TemplateUtil.findTable(template, "#Number");
        if (tableWithGrades == null) {
            return;
        }
        List<Tr> gradeTableRows = TemplateUtil.getAllRowsFromTable(tableWithGrades);
        if (gradeTableRows.size() < templateRowIndex + 1) {
            log.warn("Incorrect table with grades is ignored.");
            return;
        }
        Tr templateRow = gradeTableRows.get(templateRowIndex);
        int rowToAddIndex = firstSectionRowIndex;
        int gradeNumber = 1;
        int sectionNumber = 0;

        for (List<Grade> gradesSection : studentSummary.getGrades()) {
            for (Grade grade : gradesSection) {
                Map<String, String> replacements = SupplementTemplateFillService.getGradeDictionary(grade);
                replacements.put("Number", String.format("%2d", gradeNumber++));
                TemplateUtil.addRowToTable(tableWithGrades, templateRow, rowToAddIndex, replacements);
                rowToAddIndex++;
            }
            sectionNumber++;
            //Need to skip header of the next section
            rowToAddIndex++;
        }
        tableWithGrades.getContent().remove(templateRow);

        if (tableWithGrades.getContent().size() < 50) {
            R run = TemplateUtil.createR();
            Br pageBreak = TemplateUtil.createPageBreak();
            run.getContent().add(pageBreak);

//            Text pageBreakPlaceholder = TemplateUtil.getTextsPlaceholdersFromContentAccessor(template.getMainDocumentPart())
//                    .stream().filter(text -> "#PossiblePageBreak".equals(text.getValue().trim())).findFirst().get();
//            P parentParagraph = (P) TemplateUtil.findParentNode(pageBreakPlaceholder, P.class);
//            parentParagraph.getContent().add(run);
//            template.getMainDocumentPart().getContent().remove(pageBreakPlaceholder);
        }
    }

    private Map<String, String> getReplacementsDictionary(StudentSummary studentSummary) {
        Map<String, String> result = new HashMap<>();
        result.putAll(getStudentInfoDictionary(studentSummary));
        result.putAll(getTotalDictionary(studentSummary));
        return result;
    }

    private static Map<String, String> getGradeDictionary(Grade grade) {
        Map<String, String> result = new HashMap<>();
        result.put("Credits", formatCredits(grade.getCourse().getCredits()));
        result.put("LocalGrade", grade.getPoints() == null ? "" : String.format("%d", grade.getPoints()));
        result.put("NationalGradeUkr", grade.getNationalGradeUkr());
        result.put("NationalGradeEng", grade.getNationalGradeEng());
        result.put("CourseNameUkr", grade.getCourse().getCourseName().getName());
        result.put("CourseNameEng", grade.getCourse().getCourseName().getNameEng());
        return result;
    }

    private static Map<String, String> getTotalDictionary(StudentSummary studentSummary) {
        Map<String, String> result = new HashMap<>();
        result.put("TotalHours", String.format("%4d", studentSummary.getTotalHours()));
        result.put("TotalCredits", formatCredits(studentSummary.getTotalCredits()));
        result.put("TotalGrade", String.format("%2d", Math.round(studentSummary.getTotalGrade())));
        result.put("TotalNGradeUkr", studentSummary.getTotalNationalGradeUkr());
        result.put("TotalNGradeEng", studentSummary.getTotalNationalGradeEng());
        return result;
    }

    private Map<String, String> getStudentInfoDictionary(StudentSummary studentSummary) {
        Map<String, String> result = new HashMap<>();

        StudentDegree studentDegree = studentSummary.getStudentDegree();
        Specialization specialization = studentSummary.getStudentGroup().getSpecialization();
        Speciality speciality = specialization.getSpeciality();
        Degree degree = specialization.getDegree();
        StudentGroup group = studentSummary.getStudentGroup();

        result.put("SurnameUkr", TemplateUtil.getValueSafely(studentSummary.getStudent().getSurname(), "Ім'я"));
        result.put("SurnameEng", TemplateUtil.getValueSafely(studentSummary.getStudent().getSurnameEng(), "Surname"));
        result.put("NameUkr", TemplateUtil.getValueSafely(studentSummary.getStudent().getName(), "Прізвище"));
        result.put("NameEng", TemplateUtil.getValueSafely(studentSummary.getStudent().getNameEng(), "Name"));

//        result.put("PatronimicUkr", TemplateUtil.getValueSafely(studentSummary.getStudent().getPatronimic(), "").toUpperCase());
//        result.put("PatronimicEng", TemplateUtil.getValueSafely(studentSummary.getStudent().getPatronimicEng(), "").toUpperCase());

        DateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        DateFormat yearDateFormat = new SimpleDateFormat("yyyy");
        result.put("BirthDate", studentSummary.getStudent().getBirthDate() != null
                ? simpleDateFormat.format(studentSummary.getStudent().getBirthDate())
                : "BirthDate");

        result.put("DegreeUkr", TemplateUtil.getValueSafely(degree.getName()));
        result.put("DegreeEng", TemplateUtil.getValueSafely(degree.getNameEng()));
        result.put("QualUkr", TemplateUtil.getValueSafely(degree.getName()) + " з " + speciality.getNameGenitive());
        result.put("QualEng", TemplateUtil.getValueSafely(degree.getNameEng()) + " of " + speciality.getNameEng());

        DocumentUtil.ModeOfStudyUkrEngNames mode = DocumentUtil.getModeOfStudyUkrEngNames(studentSummary.getStudentGroup().getTuitionForm());
        result.put("ModeOfStudyUkr", mode.getModeOfStudyUkr());
        result.put("ModeOfStudyEng", mode.getModeOfStudyEng());

        result.put("SpecialityUkr", speciality.getCode() + " " + speciality.getName());
        result.put("SpecialityEng", speciality.getCode() + " " + speciality.getNameEng());
        result.put("OPUkr", specialization.getName());
        result.put("OPEng", specialization.getNameEng());
        String specializationName, specializationNameEng;
        if (specialization.getSpecializationName() == null || specialization.getSpecializationName().equals("")) {
            specializationName = "Не передбачено";
            specializationNameEng = "Not applicable";
        } else {
            specializationName = specialization.getCode() + " " + specialization.getSpecializationName();
            specializationNameEng = specialization.getCode() + " " + specialization.getSpecializationNameEng();
        }
        result.put("SpecializationUkr", specializationName);
        result.put("SpecializationEng", specializationNameEng);
        result.put("FieldOfStudy", speciality.getFieldOfKnowledge().getCode() + " " + speciality.getFieldOfKnowledge().getName());
        result.put("FieldOfStudyEng", speciality.getFieldOfKnowledge().getCode() + " " + speciality.getFieldOfKnowledge().getNameEng());
        result.put("MCKOStudyEng", "(ISCE - " + speciality.getNameInternational()+")");

        result.put("TheoreticalTrainingCredits", formatCredits(countCreditsSum(studentSummary.getGrades().get(0))
                .add(countCreditsSum(studentSummary.getGrades().get(1)))));
        result.put("PracticalTrainingCredits", formatCredits(countCreditsSum(studentSummary.getGrades().get(2))));
        result.put("ThesisDevelopmentCredits", formatCredits(countCreditsSum(studentSummary.getGrades().get(3))));
        result.put("DegreeRequiredCredits", formatCredits(studentSummary.getTotalCredits()));

        result.put("CertificateNum", specialization.getCertificateNumber());
        result.put("CertificateDate", specialization.getCertificateDate() != null
                ? simpleDateFormat.format(specialization.getCertificateDate())
                : "CertificateDate");
        result.put("CertificateIssuedBy", specialization.getCertificateIssuedBy());
        result.put("CertificateIssuedByEng", specialization.getCertificateIssuedByEng());

        result.put("QualificationLevel", TemplateUtil.getValueSafely(degree.getQualificationLevelDescription()));
        result.put("QualificationLevelEng", TemplateUtil.getValueSafely(degree.getQualificationLevelDescriptionEng()));

        String admissionRequirementsPlaceholder = "AdmissionRequirements";
        String admissionRequirementsPlaceholderEng = "AdmissionRequirementsEng";
        if (studentDegree.getStudentGroup().getSpecialization().getFaculty().getId()
                == Constants.FOREIGN_STUDENTS_FACULTY_ID) {
            result.put(admissionRequirementsPlaceholder, TemplateUtil.getValueSafely(degree.getAdmissionForeignRequirements()));
            result.put(admissionRequirementsPlaceholderEng, TemplateUtil.getValueSafely(degree.getAdmissionForeignRequirementsEng()));
        } else if (studentSummary.getStudentGroup().getTuitionTerm().equals(TuitionTerm.SHORTENED)) {
            result.put(admissionRequirementsPlaceholder, TemplateUtil.getValueSafely(degree.getAdmissionShortenedRequirements()));
            result.put(admissionRequirementsPlaceholderEng, TemplateUtil.getValueSafely(degree.getAdmissionShortenedRequirementsEng()));
        } else {
            result.put(admissionRequirementsPlaceholder, TemplateUtil.getValueSafely(degree.getAdmissionRequirements()));
            result.put(admissionRequirementsPlaceholderEng, TemplateUtil.getValueSafely(degree.getAdmissionRequirementsEng()));
        }

        result.put("FurtherStudyAccess", TemplateUtil.getValueSafely(degree.getFurtherStudyAccess()));
        result.put("FurtherStudyAccessEng", TemplateUtil.getValueSafely(degree.getFurtherStudyAccessEng()));
        result.put("RegulatedProfessionAccess", TemplateUtil.getValueSafely(speciality.getRegulatedProfessionAccess()));
        result.put("RegulatedProfessionAccessEng", TemplateUtil.getValueSafely(speciality.getRegulatedProfessionAccessEng()));

        result.put("TrainingDuration", getTrainingDuration(group));
        result.put("TrainingDurationEng", getTrainingDurationEng(group));

        Map<String, String> allPreviousUniversities = getAllPreviousUniversities(studentDegree);
        result.put("AllTrainingDurationsAndUniversitiesUkr", TemplateUtil.getValueSafely(allPreviousUniversities.get("ukr")));
        result.put("AllTrainingDurationsAndUniversitiesEng", TemplateUtil.getValueSafely(allPreviousUniversities.get("eng")));

        String allTrainingDurationsFromUniversity = getAllTrainingDurationsFromUniversity(studentDegree);
        result.put("TrainingDurations", TemplateUtil.getValueSafely(allTrainingDurationsFromUniversity));

        result.put("SupplNumber", TemplateUtil.getValueSafely(studentDegree.getSupplementNumber(), "СС № НОМЕРДОД"));
        result.put("SupplDate", studentDegree.getSupplementDate() == null ? "ДАТА ДОД"
                : simpleDateFormat.format(studentDegree.getSupplementDate()));
        result.put("DiplNumber", TemplateUtil.getValueSafely(studentDegree.getDiplomaNumber(), "МСС № НОМЕРДИП"));
        result.put("DiplDate", studentDegree.getDiplomaDate() == null ? "ДАТА ДИПЛ"
                : simpleDateFormat.format(studentDegree.getDiplomaDate()));

        if (studentDegree.isDiplomaWithHonours()) {
            result.put("DiplomaHonours", "ДИПЛОМ З ВІДЗНАКОЮ");
            result.put("DiplomaHonoursEng", "DIPLOMA WITH HONOURS");
        } else {
            result.put("DiplomaHonours", "Не передбачено");
            result.put("DiplomaHonoursEng", "Not applicable");
        }

        result.put("CurrentYear", studentDegree.getSupplementDate() == null ? "ДАТА ДОД"
                : yearDateFormat.format(studentDegree.getSupplementDate()));

        result.put("PreviousDiplomaName", studentDegree.getPreviousDiplomaType().getNameUkr());
        result.put("PreviousDiplomaNameEng", studentDegree.getPreviousDiplomaType().getNameEng());
        result.put("PreviousDiplomaOrigin", studentDegree.getPreviousDiplomaIssuedBy());
        result.put("PreviousDiplomaOriginEng", studentDegree.getPreviousDiplomaIssuedByEng());
        result.put("PreviousDiplomaNumber", TemplateUtil.getValueSafely(studentDegree.getPreviousDiplomaNumber()));
        result.put("PreviousDiplomaIssuedBy", studentDegree.getPreviousDiplomaIssuedBy());
        result.put("PreviousDiplomaIssuedByEng", studentDegree.getPreviousDiplomaIssuedByEng());
        if (studentDegree.getPreviousDiplomaDate() != null) {
            result.put("PreviousDiplomaDate", simpleDateFormat.format(studentDegree.getPreviousDiplomaDate()) + " р.");
            DateFormat englishDateFormat = SimpleDateFormat.getDateInstance(DateFormat.LONG, Locale.ENGLISH);
            result.put("PreviousDiplomaDateEng", englishDateFormat.format(studentDegree.getPreviousDiplomaDate()));
        }
        result.put("Department", studentDegree.getSpecialization().getDepartment().getName());
        result.put("DepartmentEng", studentDegree.getSpecialization().getDepartment().getNameEng());
        result.put("DepartmentURL", studentDegree.getSpecialization().getDepartment().getWebSite());

        return result;
    }

    private Map<String, String> getAcademicBackground(StudentDegree studentDegree) {
        Map<String, String> academicBackground = new HashMap<>();

        Set<StudentPreviousUniversity> studentPreviousUniversities = studentDegree.getStudentPreviousUniversities();
        if (studentPreviousUniversities.isEmpty()) {
            return null;
        }

        DateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        DateFormat monthDayYearFormat = new SimpleDateFormat("MMMM dd, yyyy", new Locale("en"));

        StringBuilder academicBackgroundUkr = new StringBuilder();
        StringBuilder academicBackgroundEng = new StringBuilder();

        for (StudentPreviousUniversity university : studentPreviousUniversities) {
            academicBackgroundUkr.append("Академічна довідка №")
                    .append(TemplateUtil.getValueSafely(university.getAcademicCertificateNumber()))
                    .append(" від ")
                    .append(formatDateSafely(simpleDateFormat, university.getAcademicCertificateDate()))
                    .append(", ")
                    .append(TemplateUtil.getValueSafely(university.getUniversityName()));
        }

        for (StudentPreviousUniversity university : studentPreviousUniversities) {
            academicBackgroundEng.append("Trascript of records №")
                    .append(TemplateUtil.getValueSafely(university.getAcademicCertificateNumber()))
                    .append(" issued on ")
                    .append(formatDateSafely(monthDayYearFormat, university.getAcademicCertificateDate()))
                    .append(", ")
                    .append(TemplateUtil.getValueSafely(university.getUniversityNameEng()));
        }

        academicBackground.put("ukr", academicBackgroundUkr.toString());
        academicBackground.put("eng", academicBackgroundEng.toString());

        return academicBackground;
    }

    private Map<String, String> getAllPreviousUniversities(StudentDegree studentDegree) {
        Map<String, String> durationOfTraining = new HashMap<>();
        StringBuilder ukr = new StringBuilder();
        StringBuilder eng = new StringBuilder();

        DateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

        Set<StudentPreviousUniversity> studentPreviousUniversities = studentDegree.getStudentPreviousUniversities();
        if (!studentPreviousUniversities.isEmpty()) {
            for (StudentPreviousUniversity university : studentPreviousUniversities) {
                ukr.append(TemplateUtil.getValueSafely(university.getUniversityName()))
                        .append(". ")
                        .append("Строк навчання - ")
                        .append(formatDateSafely(simpleDateFormat, university.getStudyStartDate()))
                        .append("-")
                        .append(formatDateSafely(simpleDateFormat, university.getStudyEndDate()))
                        .append(". ");
            }

            for (StudentPreviousUniversity university : studentPreviousUniversities) {
                eng.append(" ")
                        .append(TemplateUtil.getValueSafely(university.getUniversityNameEng()))
                        .append(". ")
                        .append("Duration of training - ")
                        .append(formatDateSafely(simpleDateFormat, university.getStudyStartDate()))
                        .append("-")
                        .append(formatDateSafely(simpleDateFormat, university.getStudyEndDate()))
                        .append(". ");
            }
        }

        durationOfTraining.put("ukr", ukr.toString());
        durationOfTraining.put("eng", eng.toString());

        return durationOfTraining;
    }

    private String getAllTrainingDurationsFromUniversity(StudentDegree studentDegree) {
        StringBuilder dates = new StringBuilder();
        List<StudentAcademicVacation> academicVacations = studentAcademicVacationService.getByDegreeId(studentDegree.getId());
        List<StudentExpel> studentExpels = studentExpelService.getByStudentDegreeId(studentDegree.getId());
        List<RenewedExpelledStudent> renewedExpelledStudents = renewedExpelledStudentService.getRenewedStudentsByStudentDegreeId(studentDegree.getId());

        List<Date> expelDates = new ArrayList<>();
        List<Date> renewDates = new ArrayList<>();

        DateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

        dates.append(formatDateSafely(simpleDateFormat, studentDegree.getAdmissionDate()))
                .append("-");

        if (!studentExpels.isEmpty()) {
            expelDates.addAll(studentExpels.stream().map(StudentExpel::getExpelDate).collect(Collectors.toList()));
            renewDates.addAll(renewedExpelledStudents.stream().map(RenewedExpelledStudent::getRenewDate).collect(Collectors.toList()));
        }
        if (!academicVacations.isEmpty()) {
            expelDates.addAll(academicVacations.stream().map(StudentAcademicVacation::getVacationStartDate).collect(Collectors.toList()));
            renewDates.addAll(academicVacations.stream().map(StudentAcademicVacation::getVacationEndDate).collect(Collectors.toList()));
        }

        expelDates.sort(Date::compareTo);
        renewDates.sort(Date::compareTo);

        if (!expelDates.isEmpty() && !renewDates.isEmpty() && expelDates.size() == renewDates.size()) {
            for (int i = 0; i < expelDates.size(); i++) {
                dates.append(formatDateSafely(simpleDateFormat, expelDates.get(i)))
                        .append(", ")
                        .append(formatDateSafely(simpleDateFormat, renewDates.get(i)))
                        .append("-");
            }
        }

        dates.append(formatDateSafely(simpleDateFormat, studentDegree.getDiplomaDate()));

        return dates.toString();
    }

    private static String getTrainingDuration(StudentGroup studentGroup) {
        StringBuilder result = new StringBuilder();
        if (studentGroup.getStudyYears().intValue() >= 1) {
            result.append(String.format("%1d", studentGroup.getStudyYears().intValue()));
            result.append(" ");
            switch (studentGroup.getStudyYears().intValue()) {
                case 1:
                    result.append("рік");
                    break;
                case 2:
                    result.append("роки");
                    break;
                case 3:
                    result.append("роки");
                    break;
                case 4:
                    result.append("роки");
                    break;
                default:
                    result.append("років");
                    break;
            }
        }

        Double monthsOfStudying = getMonthsFromYears(studentGroup.getStudyYears());
        if (monthsOfStudying != 0) {
            result.append(" ");
            result.append(String.format("%1d", monthsOfStudying.intValue()));
            result.append(" ");
            switch (monthsOfStudying.intValue()) {
                case 1:
                    result.append("місяць");
                    break;
                case 2:
                    result.append("місяці");
                    break;
                case 3:
                    result.append("місяці");
                    break;
                case 4:
                    result.append("місяці");
                    break;
                default:
                    result.append("місяців");
                    break;
            }
        }

        return result.toString();
    }

    private static String getTrainingDurationEng(StudentGroup studentGroup) {
        StringBuilder result = new StringBuilder();
        if (studentGroup.getStudyYears().intValue() >= 1) {
            result.append(String.format("%1d", studentGroup.getStudyYears().intValue()));
            result.append(" ");
            result.append(studentGroup.getStudyYears().intValue() == 1 ? "year" : "years");
        }

        Double monthsOfStudying = getMonthsFromYears(studentGroup.getStudyYears());
        if (monthsOfStudying != 0) {
            result.append(" ");
            result.append(String.format("%1d", monthsOfStudying.intValue()));
            result.append(" ");
            result.append(Math.round(monthsOfStudying) == 1 ? "month" : "months");
        }

        return result.toString();
    }

    private static String formatCredits(BigDecimal credits) {
        if (credits == null || credits.doubleValue() == BigDecimal.ZERO.doubleValue()) {
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

    private static String formatDateSafely(DateFormat format, Date date) {
        if (date == null) {
            return "";
        } else {
            return format.format(date);
        }
    }

    private static String formatDateSafely(String format, Date date) {
        if (date == null) {
            return "";
        } else {
            DateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.format(date);
        }
    }

    private static BigDecimal countCreditsSum(List<Grade> grades) {
        BigDecimal result = new BigDecimal(0.0);
        for (Grade g :
                grades) {
            result = result.add(g.getCourse().getCredits());
        }
        return result;
    }

    private static Double getMonthsFromYears(BigDecimal years) {
        int intPart = years.intValue();
        int monthsPerYear = 12;
        return ((years.doubleValue() - intPart) * monthsPerYear);
    }

    private boolean hasDirectionOfTraining(StudentDegree studentDegree) {
        if (!Strings.isNullOrEmpty(studentDegree.getStudentGroup().getSpecialization().getName()))
            if (!studentDegree.getStudentGroup().getSpecialization().getSpeciality().getCode().contains("."))
                return true;
        return false;
    }

    private void fillCompetenciesTable(WordprocessingMLPackage template, AcquiredCompetencies competencies, String placeholder) {
        String competencySeparator = "\\;";
        String endOfTheSentence = ";";

        Tbl table = TemplateUtil.findTable(template, "AcquiredCompetencies");
        if (table == null) {
            return;
        }
        Text textWithAcquiredCompetenciesPlaceholder = getTextsPlaceholdersFromContentAccessor(table)
                .stream().filter(text -> placeholder.equals(text.getValue().trim())).findFirst().get();
        R parentContainer = (R) textWithAcquiredCompetenciesPlaceholder.getParent();
        P parentParagraph = (P) TemplateUtil.findParentNode(textWithAcquiredCompetenciesPlaceholder, P.class);
        ContentAccessor paragraphsParent = (ContentAccessor) parentParagraph.getParent();

        String competenciesString = placeholder.equals("#AcquiredCompetencies")
                ? competencies.getCompetencies()
                : competencies.getCompetenciesEng();

        String[] competenciesStrings = competenciesString.split(competencySeparator);
        for (int i = 0; i < competenciesStrings.length; i++) {
            String item = competenciesStrings[i];
            P newParagraph = XmlUtils.deepCopy(parentParagraph);
            newParagraph.getContent().clear();

            R container = XmlUtils.deepCopy(parentContainer);
            container.getContent().clear();
            Text competency = XmlUtils.deepCopy(textWithAcquiredCompetenciesPlaceholder);
            competency.setValue(item + (i != competenciesStrings.length-1 ? endOfTheSentence : ""));
            container.getContent().add(competency);
            newParagraph.getContent().add(container);
            paragraphsParent.getContent().add(paragraphsParent.getContent().indexOf(parentParagraph), newParagraph);
        }
    }
}
