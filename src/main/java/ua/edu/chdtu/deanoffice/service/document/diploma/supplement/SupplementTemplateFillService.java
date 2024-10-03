package ua.edu.chdtu.deanoffice.service.document.diploma.supplement;

import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
import org.springframework.core.env.Environment;
import org.springframework.http.converter.json.GsonBuilderUtils;
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
    private Environment environment;
    public SupplementTemplateFillService(DocumentIOService documentIOService,
                                         QualificationForSpecializationService qualificationForSpecializationService,
                                         AcquiredCompetenciesService acquiredCompetenciesService,
                                         StudentAcademicVacationService studentAcademicVacationService,
                                         StudentExpelService studentExpelService,
                                         RenewedExpelledStudentService renewedExpelledStudentService,
                                         Environment environment) {
        this.documentIOService = documentIOService;
        this.qualificationForSpecializationService = qualificationForSpecializationService;
        this.acquiredCompetenciesService = acquiredCompetenciesService;
        this.studentAcademicVacationService = studentAcademicVacationService;
        this.studentExpelService = studentExpelService;
        this.renewedExpelledStudentService = renewedExpelledStudentService;
        this.environment = environment;
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
                studentSummary.getStudentDegree().getSpecialization().getId());
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
                Map<String, String> replacements = getGradeDictionary(grade);
                replacements.put("Number", String.format("%2d", gradeNumber++));
                TemplateUtil.addRowToTable(tableWithGrades, templateRow, rowToAddIndex, replacements);
                rowToAddIndex++;
            }
            sectionNumber++;
            //Need to skip header of the next section
            rowToAddIndex++;
        }
        for (List<Grade> gradesSection : studentSummary.getRecreditedGrades()) {
            for (Grade grade : gradesSection) {
                Map<String, String> replacements = getGradeDictionary(grade);
                replacements.put("Number", String.format("%2d", gradeNumber++));
                TemplateUtil.addRowToTable(tableWithGrades, templateRow, rowToAddIndex, replacements);
                rowToAddIndex++;
            }
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

    private Map<String, String> getGradeDictionary(Grade grade) {
        Map<String, String> result = new HashMap<>();
        result.put("Credits", formatCredits(grade.getCourse().getCredits()));
        result.put("LocalGrade", grade.getPoints() == null ? "" : String.format("%d", grade.getPoints()));
        result.put("NationalGradeUkr", grade.getNationalGradeUkr());
        result.put("NationalGradeEng", grade.getNationalGradeEng());
        result.put("CourseNameUkr", grade.getCourse().getCourseName().getName());
        result.put("CourseNameEng", grade.getCourse().getCourseName().getNameEng());
        return result;
    }

    private Map<String, String> getTotalDictionary(StudentSummary studentSummary) {
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
        Specialization specialization = studentSummary.getStudentDegree().getSpecialization();
        Speciality speciality = specialization.getSpeciality();
        Degree degree = specialization.getDegree();

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
        result.put("EdeboId", TemplateUtil.getValueSafely(studentDegree.getEdeboId()));

        result.put("DegreeUkr", TemplateUtil.getValueSafely(degree.getName()));
        result.put("DegreeEng", TemplateUtil.getValueSafely(degree.getNameEng()));
        result.put("QualUkr", TemplateUtil.getValueSafely(degree.getName()) + " " + speciality.getNameGenitive());
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
        result.put("DegreeRequiredCredits", "" + specialization.getNormativeCreditsNumber());

        result.put("CertificateNum", specialization.getCertificateNumber());
        result.put("CertificateDate", specialization.getCertificateDate() != null
                ? simpleDateFormat.format(specialization.getCertificateDate())
                : "CertificateDate");
        result.put("CertificateIssuedBy", specialization.getCertificateIssuedBy());
        result.put("CertificateIssuedByEng", specialization.getCertificateIssuedByEng());

        result.put("QualificationLevel", TemplateUtil.getValueSafely(degree.getQualificationLevelDescription()));
        result.put("QualificationLevelEng", TemplateUtil.getValueSafely(degree.getQualificationLevelDescriptionEng()));

        Map<String, String> admissionRequirements = getAdmissionRequirements(degree, specialization, speciality);
        result.put("AdmissionRequirements", admissionRequirements.get("ukr"));
        result.put("AdmissionRequirementsEng", admissionRequirements.get("eng"));

        result.put("FurtherStudyAccess", TemplateUtil.getValueSafely(degree.getFurtherStudyAccess()));
        result.put("FurtherStudyAccessEng", TemplateUtil.getValueSafely(degree.getFurtherStudyAccessEng()));
        result.put("RegulatedProfessionAccess", TemplateUtil.getValueSafely(speciality.getRegulatedProfessionAccess()));
        result.put("RegulatedProfessionAccessEng", TemplateUtil.getValueSafely(speciality.getRegulatedProfessionAccessEng()));

        result.put("TrainingDuration", getTrainingDuration(specialization));
        result.put("TrainingDurationEng", getTrainingDurationEng(specialization));

//        Map<String, String> allPreviousUniversities = getAllPreviousUniversities(studentDegree);
        List<StudyPeriodInAUniversity> studyPeriods = getStudyPeriods(studentDegree);
        String allPreviousUniversities = "";
        String allPreviousUniversitiesEng = "";
        String allTrainingDurationsFromUniversity = "";
        for (StudyPeriodInAUniversity studyPeriod : studyPeriods) {
            allPreviousUniversities += studyPeriod.getUniversityNameUkr() + "\n";
            allPreviousUniversitiesEng += studyPeriod.getUniversityNameEng() + "\n";
            allTrainingDurationsFromUniversity += (studyPeriod.getStartDate() != null ? simpleDateFormat.format(studyPeriod.getStartDate()) : "")
                    + " - " + (studyPeriod.getEndDate() != null ? simpleDateFormat.format(studyPeriod.getEndDate()) : "") + "\n";
        }
        result.put("AllTrainingDurationsAndUniversitiesUkr", allPreviousUniversities);
        result.put("AllTrainingDurationsAndUniversitiesEng", TemplateUtil.getValueSafely(allPreviousUniversitiesEng));

//        String allTrainingDurationsFromUniversity = getAllTrainingDurationsFromUniversity(studentDegree);
        result.put("TrainingDurations", allTrainingDurationsFromUniversity);

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

    //---------------------------------------------------
    private Map<String, String> getAdmissionRequirements(Degree degree, Specialization specialization, Speciality speciality) {
        Map<String, String> result = new HashMap<>();

        if (specialization.getFaculty().getId() == Constants.FOREIGN_STUDENTS_FACULTY_ID) {
            result.put("ukr", degree.getAdmissionForeignRequirements());
            result.put("eng", degree.getAdmissionForeignRequirementsEng());
        } else {
            result.put("ukr", degree.getAdmissionRequirements().replace("$courses$", speciality.getEntranceCertificates()));
            result.put("eng", degree.getAdmissionRequirementsEng().replace("$courses$", speciality.getEntranceCertificatesEng()));
        }
        return result;
    }

    //----------------------------------------------------------
    private List<StudyPeriodInAUniversity> getStudyPeriods(StudentDegree studentDegree) {
        List<StudyPeriodInAUniversity> studyPeriods = new ArrayList<>();
        List<StudyPeriodInAUniversity> previousUniversitiesPeriods = getPreviousUniversitiesPeriods(studentDegree);
        List<Date[]> studyBreakPeriods = getExpelAndAcademicVacationPeriods(studentDegree);
        if (previousUniversitiesPeriods.size() == 0 && studyBreakPeriods.size() == 0) {
            studyPeriods.add(new StudyPeriodInAUniversity(Constants.UNIVERSITY_NAME, Constants.UNIVERSITY_NAME_ENG,
                    studentDegree.getAdmissionDate(), studentDegree.getDiplomaDate()));
        }
        if (previousUniversitiesPeriods.size() != 0 && studyBreakPeriods.size() == 0) {
            studyPeriods.addAll(previousUniversitiesPeriods);
            studyPeriods.add(new StudyPeriodInAUniversity(Constants.UNIVERSITY_NAME, Constants.UNIVERSITY_NAME_ENG,
                    studentDegree.getAdmissionDate(), studentDegree.getDiplomaDate()));
        }
        if (studyBreakPeriods.size() != 0) {
            List<StudyPeriodInAUniversity> thisUniversityStudyPeriods = new ArrayList<>();
            thisUniversityStudyPeriods.add(new StudyPeriodInAUniversity(Constants.UNIVERSITY_NAME, Constants.UNIVERSITY_NAME_ENG,
                    studentDegree.getAdmissionDate(), studyBreakPeriods.get(0)[0]));
            for (int i = 0; i < studyBreakPeriods.size(); i++) {
                Date endDate = i == studyBreakPeriods.size() - 1 ? studentDegree.getDiplomaDate() : studyBreakPeriods.get(i + 1)[0];
                thisUniversityStudyPeriods.add(new StudyPeriodInAUniversity(Constants.UNIVERSITY_NAME, Constants.UNIVERSITY_NAME_ENG,
                        studyBreakPeriods.get(i)[1], endDate));
            }
            Collections.sort(thisUniversityStudyPeriods);

            studyPeriods.addAll(previousUniversitiesPeriods);
            studyPeriods.addAll(thisUniversityStudyPeriods);
        }
        return studyPeriods;
    }

    private List<StudyPeriodInAUniversity> getPreviousUniversitiesPeriods(StudentDegree studentDegree) {
        return studentDegree.getStudentPreviousUniversities().stream()
                .map(spu -> new StudyPeriodInAUniversity(spu.getUniversityName(), spu.getUniversityNameEng(), spu.getStudyStartDate(), spu.getStudyEndDate()))
                .sorted()
                .collect(Collectors.toList());
    }

    private List<Date[]> getExpelAndAcademicVacationPeriods(StudentDegree studentDegree) {
        List<Date[]> studyBreakPeriods = new ArrayList<>();
        List<StudentAcademicVacation> academicVacations = studentAcademicVacationService.getByDegreeId(studentDegree.getId());
        academicVacations.forEach(academicVacation -> {
            RenewedAcademicVacationStudent renewedStudent = studentAcademicVacationService.getRenewedByAcademicVacationId(academicVacation.getId());
            if (renewedStudent != null) {
                Date[] period = new Date[2];
                period[0] = academicVacation.getVacationStartDate();
                period[1] = renewedStudent.getRenewDate();
                studyBreakPeriods.add(period);
            }
        });
        List<StudentExpel> studentExpels = studentExpelService.getByStudentDegreeId(studentDegree.getId());
        studentExpels.forEach(studentExpel -> {
            RenewedExpelledStudent renewedExpelledStudent = renewedExpelledStudentService.getRenewedStudentByExpelledId(studentExpel.getId());
            if (renewedExpelledStudent != null) {
                Date[] period = new Date[2];
                period[0] = studentExpel.getExpelDate();
                period[1] = renewedExpelledStudent.getRenewDate();
                studyBreakPeriods.add(period);
            }
        });
        return studyBreakPeriods;
    }

    //-----------------------------------------------------------------------
    private static String getTrainingDuration(Specialization specialization) {
        StringBuilder result = new StringBuilder();
        if (specialization.getNormativeTermOfStudy().intValue() >= 1) {
            result.append(String.format("%1d", specialization.getNormativeTermOfStudy().intValue()));
            result.append(" ");
            switch (specialization.getNormativeTermOfStudy().intValue()) {
                case 1:
                    result.append("рік");
                    break;
                case 2:
                case 3:
                case 4:
                    result.append("роки");
                    break;
                default:
                    result.append("років");
                    break;
            }
        }

        int monthsOfStudying = getMonthsFromYears(specialization.getNormativeTermOfStudy());
        if (monthsOfStudying != 0) {
            result.append(" ");
            result.append(String.format("%1d", monthsOfStudying));
            result.append(" ");
            switch (monthsOfStudying) {
                case 1:
                    result.append("місяць");
                    break;
                case 2:
                case 3:
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

    private static String getTrainingDurationEng(Specialization specialization) {
        StringBuilder result = new StringBuilder();
        if (specialization.getNormativeTermOfStudy().intValue() >= 1) {
            result.append(String.format("%1d", specialization.getNormativeTermOfStudy().intValue()));
            result.append(" ");
            result.append(specialization.getNormativeTermOfStudy().intValue() == 1 ? "year" : "years");
        }

        int monthsOfStudying = getMonthsFromYears(specialization.getNormativeTermOfStudy());
        if (monthsOfStudying != 0) {
            result.append(" ");
            result.append(String.format("%1d", monthsOfStudying));
            result.append(" ");
            result.append(Math.round(monthsOfStudying) == 1 ? "month" : "months");
        }

        return result.toString();
    }

    private String formatCredits(BigDecimal credits) {
        if (credits == null || credits.doubleValue() == BigDecimal.ZERO.doubleValue()) {
            return "-";
        } else {
            String formattedCredits = String.format("%.1f", credits);
            if (formattedCredits.split(environment.getProperty("server.decimal-point", String.class))[1].equals("0")) {
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

    private static int getMonthsFromYears(BigDecimal years) {
        int intPart = years.intValue();
        int monthsPerYear = 12;
        return (int)Math.round((years.doubleValue() - intPart) * monthsPerYear);
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

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private class StudyPeriodInAUniversity implements Comparable<StudyPeriodInAUniversity> {
        private String universityNameUkr;
        private String universityNameEng;
        private Date startDate;
        private Date endDate;

        public int compareTo(StudyPeriodInAUniversity studyPeriodInAUniversity) {
            return startDate.compareTo(studyPeriodInAUniversity.getStartDate());
        }
    }
}
