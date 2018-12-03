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
import org.jvnet.jaxb2_commons.ppp.Child;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.AcquiredCompetencies;
import ua.edu.chdtu.deanoffice.entity.Degree;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.ProfessionalQualification;
import ua.edu.chdtu.deanoffice.entity.QualificationForSpecialization;
import ua.edu.chdtu.deanoffice.entity.Speciality;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.entity.TuitionForm;
import ua.edu.chdtu.deanoffice.entity.TuitionTerm;
import ua.edu.chdtu.deanoffice.service.AcquiredCompetenciesService;
import ua.edu.chdtu.deanoffice.service.QualificationForSpecializationService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.TemplateUtil;
import ua.edu.chdtu.deanoffice.util.GradeUtil;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.getTextsPlaceholdersFromContentAccessor;

@Service
public class SupplementTemplateFillService {

    private static final Logger log = LoggerFactory.getLogger(SupplementTemplateFillService.class);
    private static final int FOREIGN_STUDENTS_FACULTY_ID = 8;
    private final DocumentIOService documentIOService;
    private QualificationForSpecializationService qualificationForSpecializationService;
    private AcquiredCompetenciesService acquiredCompetenciesService;

    public SupplementTemplateFillService(DocumentIOService documentIOService,
                                         QualificationForSpecializationService qualificationForSpecializationService,
                                         AcquiredCompetenciesService acquiredCompetenciesService) {
        this.documentIOService = documentIOService;
        this.qualificationForSpecializationService = qualificationForSpecializationService;
        this.acquiredCompetenciesService = acquiredCompetenciesService;
    }

    WordprocessingMLPackage fill(String templateFilepath, StudentSummary studentSummary)
            throws Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(templateFilepath);

        prepareTrainingDirectionPlaceholders(template, studentSummary);

        fillAcquiredCompetencies(template, studentSummary);
        fillTableWithGrades(template, studentSummary);
        fillProfessionalQualificationsTable(template, studentSummary);

        Map<String, String> commonDict = getReplacementsDictionary(studentSummary);
        TemplateUtil.replaceTextPlaceholdersInTemplate(template, commonDict);
        TemplateUtil.replacePlaceholdersInFooter(template, commonDict);

        return template;
    }

    private void prepareTrainingDirectionPlaceholders(WordprocessingMLPackage template, StudentSummary studentSummary) {
        Map<String, String> replacements = new HashMap<>();
        if (hasDirectionOfTraining(studentSummary.getStudentDegree())) {
            insertSpecializationPlaceholders(template);
        }
        replacements.put("TrainingDirectionType", "спеціальність");
        replacements.put("TrainingDirectionTypeEng", "Speciality");
        TemplateUtil.replaceTextPlaceholdersInTemplate(template, replacements, false);
    }

    private Map<String, String> getCertificationType(StudentSummary studentSummary) {
        Map<String, String> result = new HashMap<>();

        String certificationName = "";
        String certificationNameEng = "";

        if (studentSummary.getGrades().get(3).stream().allMatch(grade -> {
            String courseNameUkr = grade.getCourse().getCourseName().getName();
            return (!Strings.isNullOrEmpty(courseNameUkr)
                    && (courseNameUkr.contains("іспит") || courseNameUkr.contains("екзамен")));
        })
                ) {
            certificationName = "Державний іспит.";
            certificationNameEng = "State exam.";
        } else {
            String degreeName = "";
            String degreeNameEng = "";
            switch (studentSummary.getStudentGroup().getSpecialization().getDegree().getId()) {
                case 1: {
                    degreeName = "бакалавра";
                    degreeNameEng = "bachelor's";
                    break;
                }
                case 2: {
                    degreeName = "спеціаліста";
                    degreeNameEng = "specialists's";
                    break;
                }

                case 3: {
                    degreeName = "магістра";
                    degreeNameEng = "master's";
                    break;
                }

            }
            certificationName += "Кваліфікаційна робота " + degreeName + " на тему:";
            certificationNameEng += "Qualification work of a " + degreeNameEng + " degree on a subject:";
        }
        result.put("CertificationName", certificationName);
        result.put("CertificationNameEng", certificationNameEng);

        return result;
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

        for (List<Grade> gradesSection : studentSummary.getGrades()) {
            for (Grade grade : gradesSection) {
                Map<String, String> replacements = SupplementTemplateFillService.getGradeDictionary(grade);
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

            Text pageBreakPlaceholder = TemplateUtil.getTextsPlaceholdersFromContentAccessor(template.getMainDocumentPart())
                    .stream().filter(text -> "#PossiblePageBreak".equals(text.getValue().trim())).findFirst().get();
            P parentParagraph = (P) TemplateUtil.findParentNode(pageBreakPlaceholder, P.class);
            parentParagraph.getContent().add(run);
            template.getMainDocumentPart().getContent().remove(pageBreakPlaceholder);
        }
    }

    private void fillProfessionalQualificationsTable(WordprocessingMLPackage template, StudentSummary studentSummary) {
        int templateRowIndex = 0;

        List<ProfessionalQualification> professionalQualifications = getProfessionalQualifications(studentSummary);
        if (professionalQualifications == null) {
            return;
        }
        Tbl professionalQualificationsTable = TemplateUtil.findTable(template, "ProfCode");
        if (professionalQualificationsTable == null) {
            return;
        }
        Text tablePlaceholder = getTextsPlaceholdersFromContentAccessor(professionalQualificationsTable)
                .stream().filter(text -> "#ProfCode".equals(text.getValue().trim())).findFirst().get();

        professionalQualificationsTable = (Tbl) TemplateUtil.findParentNode(tablePlaceholder, Tbl.class);

        List<Tr> tableRows = TemplateUtil.getAllRowsFromTable(professionalQualificationsTable);
        Tr templateRow = tableRows.get(templateRowIndex);
        int rowToAddIndex = templateRowIndex + 1;

        for (ProfessionalQualification qualification : professionalQualifications) {
            Map<String, String> replacements = SupplementTemplateFillService.getProfessionalQualificationDictionary(qualification);
            TemplateUtil.addRowToTable(professionalQualificationsTable, templateRow, rowToAddIndex, replacements);
            rowToAddIndex++;
        }
        professionalQualificationsTable.getContent().remove(templateRow);
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
        result.put("ECTSGrade", GradeUtil.getEctsGrade(grade));
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

    private static Map<String, String> getProfessionalQualificationDictionary(ProfessionalQualification qualification) {
        Map<String, String> result = new HashMap<>();
        result.put("ProfCode", qualification.getCode());
        result.put("ProfName", qualification.getName());
        result.put("ProfNameEng", qualification.getNameEng());
        return result;
    }

    private Map<String, String> getStudentInfoDictionary(StudentSummary studentSummary) {
        Map<String, String> result = new HashMap<>();

        StudentDegree studentDegree = studentSummary.getStudentDegree();
        Specialization specialization = studentSummary.getStudentGroup().getSpecialization();
        Speciality speciality = specialization.getSpeciality();
        Degree degree = specialization.getDegree();
        StudentGroup group = studentSummary.getStudentGroup();

        result.put("SurnameUkr", TemplateUtil.getValueSafely(studentSummary.getStudent().getSurname().toUpperCase(), "Ім'я"));
        result.put("SurnameEng", TemplateUtil.getValueSafely(studentSummary.getStudent().getSurnameEng(), "Surname").toUpperCase());
        result.put("NameUkr", TemplateUtil.getValueSafely(studentSummary.getStudent().getName().toUpperCase(), "Прізвище"));
        result.put("NameEng", TemplateUtil.getValueSafely(studentSummary.getStudent().getNameEng(), "Name").toUpperCase());
        result.put("PatronimicUkr", TemplateUtil.getValueSafely(studentSummary.getStudent().getPatronimic().toUpperCase(), ""));
        result.put("PatronimicEng", TemplateUtil.getValueSafely(studentSummary.getStudent().getPatronimicEng().toUpperCase(), ""));

        DateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        DateFormat yearDateFormat = new SimpleDateFormat("yyyy");
        result.put("BirthDate", studentSummary.getStudent().getBirthDate() != null
                ? simpleDateFormat.format(studentSummary.getStudent().getBirthDate())
                : "BirthDate");

        TuitionForm tuitionForm = studentSummary.getStudentGroup().getTuitionForm();
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

        result.put("SpecializationUkr", TemplateUtil.getValueSafely(specialization.getName()));
        result.put("SpecializationEng", TemplateUtil.getValueSafely(specialization.getNameEng()));
        result.put("SpecialityUkr", TemplateUtil.getValueSafely(speciality.getName()));
        result.put("SpecialityEng", TemplateUtil.getValueSafely(speciality.getNameEng()));
        result.put("DegreeUkr", TemplateUtil.getValueSafely(degree.getName()));
        result.put("DegreeEng", TemplateUtil.getValueSafely(degree.getNameEng()));
        result.put("DEGREEUKR", TemplateUtil.getValueSafely(degree.getName()).toUpperCase());
        result.put("DEGREEENG", TemplateUtil.getValueSafely(degree.getNameEng()).toUpperCase());
        result.put("TheoreticalTrainingCredits", formatCredits(countCreditsSum(studentSummary.getGrades().get(0))
                .add(countCreditsSum(studentSummary.getGrades().get(1)))));
        result.put("PracticalTrainingCredits", formatCredits(countCreditsSum(studentSummary.getGrades().get(2))));
        result.put("ThesisDevelopmentCredits", formatCredits(countCreditsSum(studentSummary.getGrades().get(3))));
        result.put("DegreeRequiredCredits", formatCredits(studentSummary.getTotalCredits()));

        result.put("CertificateNum", specialization.getCertificateNumber());
        result.put("CertificateDate", specialization.getCertificateDate() != null
                ? simpleDateFormat.format(specialization.getCertificateDate())
                : "CertificateDate");

        result.put("FieldOfStudy", TemplateUtil.getValueSafely(speciality.getFieldOfStudy()));
        result.put("FieldOfStudyEng", TemplateUtil.getValueSafely(speciality.getFieldOfStudyEng()));
        result.put("QualificationLevel", TemplateUtil.getValueSafely(degree.getQualificationLevelDescription()));
        result.put("QualificationLevelEng", TemplateUtil.getValueSafely(degree.getQualificationLevelDescriptionEng()));

        String admissionRequirementsPlaceholder = "AdmissionRequirements";
        String admissionRequirementsPlaceholderEng = "AdmissionRequirementsEng";
        if (studentDegree.getStudentGroup().getSpecialization().getFaculty().getId()
                == FOREIGN_STUDENTS_FACULTY_ID) {
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
        result.put("ProfessionalStatus", TemplateUtil.getValueSafely(degree.getProfessionalStatus()));
        result.put("ProfessionalStatusEng", TemplateUtil.getValueSafely(degree.getProfessionalStatusEng()));

        result.put("TrainingDuration", getTrainingDuration(group));
        result.put("TrainingDurationEng", getTrainingDurationEng(group));
        result.put("TrainingStart", formatDateSafely(simpleDateFormat, studentDegree.getAdmissionDate()));
        result.put("TrainingEnd", formatDateSafely(simpleDateFormat, studentDegree.getDiplomaDate()));

        result.put("ProgramHeadName", TemplateUtil.getValueSafely(specialization.getEducationalProgramHeadName()));
        result.put("ProgramHeadNameEng", TemplateUtil.getValueSafely(specialization.getEducationalProgramHeadNameEng()));
        result.put("ProgramHeadInfo", TemplateUtil.getValueSafely(specialization.getEducationalProgramHeadInfo()));
        result.put("ProgramHeadInfoEng", TemplateUtil.getValueSafely(specialization.getEducationalProgramHeadInfoEng()));

        result.putAll(getCertificationType(studentSummary));
        if (!result.get("CertificationName").equals("Державний іспит.")) {
            result.put("ThesisNameUkr", "«" + TemplateUtil.getValueSafely(studentDegree.getThesisName()) + "»");
            result.put("ThesisNameEng", "\"" + TemplateUtil.getValueSafely(studentDegree.getThesisNameEng()) + "\"");
        }
        result.put("ProtocolNumber", TemplateUtil.getValueSafely(studentDegree.getProtocolNumber()));
        result.put("PreviousDiplomaNumber", TemplateUtil.getValueSafely(studentDegree.getPreviousDiplomaNumber()));

        int dateStyle = DateFormat.LONG;
        DateFormat protocolDateFormatUkr = DateFormat.getDateInstance(dateStyle, new Locale("uk", "UA"));
        result.put("ProtocolDateUkr", studentDegree.getProtocolDate() == null ? ""
                : protocolDateFormatUkr.format(studentDegree.getProtocolDate()));
        DateFormat protocolDateFormatEng = DateFormat.getDateInstance(dateStyle, Locale.ENGLISH);
        result.put("ProtocolDateEng", studentDegree.getProtocolDate() == null ? ""
                : protocolDateFormatEng.format(studentDegree.getProtocolDate()));

        result.put("SupplNumber", TemplateUtil.getValueSafely(studentDegree.getSupplementNumber(), "СС № НОМЕРДОД"));
        result.put("SupplDate", studentDegree.getSupplementDate() == null ? "ДАТА ДОД"
                : simpleDateFormat.format(studentDegree.getSupplementDate()));
        result.put("DiplNumber", TemplateUtil.getValueSafely(studentDegree.getDiplomaNumber(), "МСС № НОМЕРДИП"));
        result.put("DiplDate", studentDegree.getDiplomaDate() == null ? "ДАТА ДИПЛ"
                : simpleDateFormat.format(studentDegree.getDiplomaDate()));

        if (studentDegree.isDiplomaWithHonours()) {
            result.put("DiplomaHonours", "З ВІДЗНАКОЮ");
            result.put("DiplomaHonoursEng", "WITH HONOURS");
        }

        result.put("CurrentYear", studentDegree.getSupplementDate() == null ? "ДАТА ДОД"
                : yearDateFormat.format(studentDegree.getSupplementDate()));

        result.put("PreviousDiplomaName", studentDegree.getPreviousDiplomaType().getNameUkr());
        result.put("PreviousDiplomaNameEng", studentDegree.getPreviousDiplomaType().getNameEng());
        result.put("PreviousDiplomaOrigin", studentDegree.getPreviousDiplomaIssuedBy());
        result.put("PreviousDiplomaOriginEng", studentDegree.getPreviousDiplomaIssuedByEng());
        result.put("PreviousDiplomaNumber", studentDegree.getPreviousDiplomaNumber());
        if (studentDegree.getPreviousDiplomaDate() != null) {
            result.put("PreviousDiplomaDate", simpleDateFormat.format(studentDegree.getPreviousDiplomaDate()) + " р.");
            DateFormat englishDateFormat = SimpleDateFormat.getDateInstance(DateFormat.LONG, Locale.ENGLISH);
            result.put("PreviousDiplomaDateEng", englishDateFormat.format(studentDegree.getPreviousDiplomaDate()));
        }

        return result;
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

    private void insertSpecializationPlaceholders(WordprocessingMLPackage template) {
        List<Text> placeholders = TemplateUtil.getTextsPlaceholdersFromContentAccessor(template.getMainDocumentPart());
        Text trainingDirectionTypeEngPlaceholder = placeholders.stream()
                .filter(text -> text.getValue().contains("TrainingDirectionTypeEng")).findFirst().get();
        ContentAccessor parentR = (ContentAccessor) trainingDirectionTypeEngPlaceholder.getParent();
        ContentAccessor parentP = (ContentAccessor) ((Child) parentR).getParent();

        Text trainingDirectionTypePlaceholder = placeholders.stream()
                .filter(text -> text.getValue().endsWith("TrainingDirectionType")).findFirst().get();

        R r1 = XmlUtils.deepCopy((R) trainingDirectionTypePlaceholder.getParent());
        r1.getContent().clear();
        r1.getContent().add(TemplateUtil.createLineBreak());
        Text newSpecializationName = XmlUtils.deepCopy(trainingDirectionTypePlaceholder);
        newSpecializationName.setValue("освітня програма");
        r1.getContent().add(newSpecializationName);
        parentP.getContent().add(r1);

        R r2 = XmlUtils.deepCopy((R) trainingDirectionTypeEngPlaceholder.getParent());
        r2.getContent().clear();
        Text space = XmlUtils.deepCopy(trainingDirectionTypePlaceholder);
        space.setValue(" / ");
        space.setSpace("preserve");
        r2.getContent().add(space);

        Text newSpecializationNameEng = XmlUtils.deepCopy(trainingDirectionTypePlaceholder);
        newSpecializationNameEng.setValue("Educational program");
        r2.getContent().add(newSpecializationNameEng);
        parentP.getContent().add(r2);

        Text specialityEngPlaceholder = placeholders.stream()
                .filter(text -> text.getValue().contains("SpecialityEng")).findFirst().get();
        ContentAccessor specialityPlaceholderR = (ContentAccessor) specialityEngPlaceholder.getParent();
        ContentAccessor specialityPlaceholderP = (ContentAccessor) ((Child) specialityPlaceholderR).getParent();

        R r3 = XmlUtils.deepCopy((R) trainingDirectionTypePlaceholder.getParent());
        r3.getContent().clear();
        r3.getContent().add(TemplateUtil.createLineBreak());
        Text newSpecializationPlaceholderUkr = XmlUtils.deepCopy(trainingDirectionTypePlaceholder);
        newSpecializationPlaceholderUkr.setValue("#SpecializationUkr");
        r3.getContent().add(newSpecializationPlaceholderUkr);
        specialityPlaceholderP.getContent().add(r3);

        R r4 = XmlUtils.deepCopy((R) trainingDirectionTypeEngPlaceholder.getParent());
        r4.getContent().clear();
        r4.getContent().add(space);

        Text newSpecializationPlaceholderEng = XmlUtils.deepCopy(trainingDirectionTypePlaceholder);
        newSpecializationPlaceholderEng.setValue("#SpecializationEng");
        r4.getContent().add(newSpecializationPlaceholderEng);
        specialityPlaceholderP.getContent().add(r4);
    }

    private boolean hasDirectionOfTraining(StudentDegree studentDegree) {
        if (!Strings.isNullOrEmpty(studentDegree.getStudentGroup().getSpecialization().getName()))
            if (!studentDegree.getStudentGroup().getSpecialization().getSpeciality().getCode().contains("."))
                return true;
        return false;
    }

    private void fillCompetenciesTable(WordprocessingMLPackage template, AcquiredCompetencies competencies, String placeholder) {
        String competencySeparator = "\\.";
        String endOfTheSentence = ".";

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

        for (String item : competenciesString.split(competencySeparator)) {
            P newParagraph = XmlUtils.deepCopy(parentParagraph);
            newParagraph.getContent().clear();

            R container = XmlUtils.deepCopy(parentContainer);
            container.getContent().clear();
            Text competency = XmlUtils.deepCopy(textWithAcquiredCompetenciesPlaceholder);
            competency.setValue(item + endOfTheSentence);
            container.getContent().add(competency);
            newParagraph.getContent().add(container);
            paragraphsParent.getContent().add(paragraphsParent.getContent().indexOf(parentParagraph), newParagraph);
        }
    }

    private List<ProfessionalQualification> getProfessionalQualifications(StudentSummary studentSummary) {
        StudentGroup studentGroup = studentSummary.getStudentGroup();
        List<QualificationForSpecialization> qualificationsForSpecialization = qualificationForSpecializationService
                .findAllBySpecializationIdAndYear(studentGroup.getSpecialization().getId());
        List<ProfessionalQualification> professionalQualifications = qualificationsForSpecialization
                .stream().map(QualificationForSpecialization::getProfessionalQualification).collect(Collectors.toList());
        if (professionalQualifications.isEmpty()) {
            log.debug("There are no qualifications for this group");
            return null;
        }
        return professionalQualifications;
    }

}
