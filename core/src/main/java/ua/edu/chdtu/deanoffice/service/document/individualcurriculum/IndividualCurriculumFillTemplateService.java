package ua.edu.chdtu.deanoffice.service.document.individualcurriculum;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Speciality;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.entity.TuitionForm;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.TemplateUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static ua.edu.chdtu.deanoffice.service.document.DocumentIOService.TEMPLATES_PATH;

@Service
public class IndividualCurriculumFillTemplateService {
    private static final Logger log = LoggerFactory.getLogger(IndividualCurriculumFillTemplateService.class);

    private final static String TEMPLATE_PATH = TEMPLATES_PATH + "IndividualCurriculum.docx";

    private final DocumentIOService documentIOService;
    private final CourseForGroupService courseForGroupService;
    private final CurrentYearService currentYearService;

    public IndividualCurriculumFillTemplateService(DocumentIOService documentIOService,
                                                   CourseForGroupService courseForGroupService, CurrentYearService currentYearService) {
        this.documentIOService = documentIOService;
        this.courseForGroupService = courseForGroupService;
        this.currentYearService = currentYearService;
    }

    public WordprocessingMLPackage fillTemplate(Set<StudentDegree> degrees) {
        WordprocessingMLPackage formedDocument = null;

        Map<String, String> commonStudentDegreeInfo = getCommonStudentDegreeInfo(degrees.iterator().next());

        for (StudentDegree degree : degrees) {
            try {
                if (Objects.nonNull(formedDocument)) {
//                    TemplateUtil.addPageBreak(formedDocument);

                    formedDocument.getMainDocumentPart().getContent().addAll(
                            fillTemplateForSingleDegree(commonStudentDegreeInfo, degree).getMainDocumentPart().getContent()
                    );
                } else {
                    formedDocument = fillTemplateForSingleDegree(commonStudentDegreeInfo, degree);
                }
            } catch (Docx4JException e) {
                log.error(e.getMessage());
            }
        }

        return formedDocument;
    }

    private WordprocessingMLPackage fillTemplateForSingleDegree(Map<String, String> commonStudyInfo,
                                                                StudentDegree degree) throws Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(TEMPLATE_PATH);
        StudentGroup studentGroup = degree.getStudentGroup();

        Map<String, String> replacements = new HashMap<>();

        String studentName = degree.getStudent().getFullNameUkr();
        replacements.put("StudentName", studentName);

        replacements.put("RecNum", degree.getRecordBookNumber());

        String tuitionForm = Objects.requireNonNull(
                TuitionForm.getTuitionFormFromUkrName(degree.getTuitionForm().getNameUkr())
        ).getNameUkr();
        replacements.put("TF", tuitionForm);

        String admissionDate = getYearFromDate(degree.getAdmissionDate());
        replacements.put("Begin", TemplateUtil.getValueSafely(admissionDate));
        String endDate = String.valueOf(Integer.parseInt(admissionDate) + studentGroup.getStudyYears().intValue());
        replacements.put("End", TemplateUtil.getValueSafely(endDate));

        int dbCurrentYear = currentYearService.get().getCurrYear();
        String course = String.valueOf(dbCurrentYear - studentGroup.getCreationYear() + studentGroup.getBeginYears());
        replacements.put("Course", TemplateUtil.getValueSafely(course));

        replacements.putAll(commonStudyInfo);

        TemplateUtil.replaceTextPlaceholdersInTemplate(template, replacements);

        return template;
    }

    private String getYearFromDate(Date date) {
        return Objects.nonNull(date) ? date.toString().split("-", 2)[0] : "";
    }

    private Map<String, String> getCommonStudentDegreeInfo(StudentDegree degree) {
        Map<String, String> replacements = new HashMap<>();

        replacements.put("StudyYear", TemplateUtil.getValueSafely(getStudyYear()));

        String faculty = degree.getSpecialization().getFaculty().getName();
        replacements.put("Faculty", TemplateUtil.getValueSafely(faculty.toUpperCase()));

        Speciality speciality = degree.getSpecialization().getSpeciality();
        String specialityName = speciality.getCode() + " " + speciality.getName();
        replacements.put("Speciality", TemplateUtil.getValueSafely(specialityName));
        replacements.put("FieldOfStudy", TemplateUtil.getValueSafely(speciality.getFieldOfStudy()));

        String educationProgram = degree.getSpecialization().getName();
        replacements.put("EducationProgram", TemplateUtil.getValueSafely(educationProgram));

        String degreeName = degree.getSpecialization().getDegree().getName();
        replacements.put("Degree", TemplateUtil.getValueSafely(degreeName));

        replacements.put("AG", TemplateUtil.getValueSafely(degree.getStudentGroup().getName()));

        return replacements;
    }

    private String getStudyYear() {
        int currentYear = currentYearService.get().getCurrYear();
        return String.format("%4d-%4d", currentYear, currentYear + 1);
    }

    private Map<String, String> fillTableWithCoursesInfo() {
        return null;
    }
}
