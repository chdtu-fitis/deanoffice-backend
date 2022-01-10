package ua.edu.chdtu.deanoffice.service.document.report.academic.reference;

import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.*;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.Speciality;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.document.TemplateUtil;
import ua.edu.chdtu.deanoffice.util.DocumentUtil;
import ua.edu.chdtu.deanoffice.util.PersonUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.*;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.replaceInRow;
import static ua.edu.chdtu.deanoffice.util.LanguageUtil.transliterate;

public class AcademicCertificateBaseService {

    private static final int INDEX_OF_TABLE_WITH_GRADES = 1;
    private static final String DOCUMENT_DELIMITER = "/";
    private static final String NO_GRADES_DESCRIPTION_UKR = "Заліків та іспитів не здавав(ла).";
    private static final String NO_GRADES_DESCRIPTION_EN = "No credits and exams.";
    private static final int EXAMS_AND_CREDITS_INDEX = 0, COURSE_PAPERS_INDEX = 1, INTERNSHIPS_INDEX = 2;

    public HashMap<String, String> getStudentInfoDictionary(StudentSummaryForAcademicReference studentSummary) {
        HashMap<String, String> result = new HashMap();
        StudentDegree studentDegree = studentSummary.getStudentDegree();
        Student student = studentDegree.getStudent();
        result.put("nameUkr", student.getName());
        result.put("surnameUkr", student.getSurname());
        result.put("nameEng", student.getNameEng() == null ? "" : student.getNameEng());
        result.put("surnameEng", student.getSurnameEng() == null ? "" : student.getSurnameEng());
        result.put("birthDate", formatDate(student.getBirthDate()));
        result.put("individualNumber", studentDegree.getEdeboId());

        result.put("facultyNameUkr", studentDegree.getSpecialization().getFaculty().getName());
        result.put("facultyNameEng", studentDegree.getSpecialization().getFaculty().getNameEng());
        result.put("degreeUkr", studentDegree.getSpecialization().getDegree().getName());
        result.put("degreeEng", studentDegree.getSpecialization().getDegree().getNameEng());
        Speciality speciality = studentDegree.getSpecialization().getSpeciality();
        result.put("fieldOfStudyUkr", speciality.getFieldOfStudyCode() + " " + speciality.getFieldOfStudy());
        result.put("fieldOfStudyEng", speciality.getFieldOfStudyCode() + " " + speciality.getFieldOfStudyEng());
        result.put("specialityUkr", speciality.getCode() + " " + speciality.getName());
        result.put("specialityEng", speciality.getCode() + " " + speciality.getNameEng());
        result.put("educationProgramUkr", studentDegree.getSpecialization().getName());
        result.put("educationProgramEng", studentDegree.getSpecialization().getNameEng());
        String specializationUkr = TemplateUtil.getValueSafely(studentDegree.getSpecialization().getCode()) + " "
                + TemplateUtil.getValueSafely(studentDegree.getSpecialization().getSpecializationName());
        result.put("specializationUkr", specializationUkr);
        String specializationEng = TemplateUtil.getValueSafely(studentDegree.getSpecialization().getCode()) + " "
                + TemplateUtil.getValueSafely(studentDegree.getSpecialization().getSpecializationNameEng());
        result.put("specializationEng", specializationEng);
        result.put("CertificateIssuedBy", TemplateUtil.getValueSafely(studentDegree.getSpecialization().getCertificateIssuedBy()));
        result.put("CertificateIssuedByEng", TemplateUtil.getValueSafely(studentDegree.getSpecialization().getCertificateIssuedByEng()));

        DocumentUtil.ModeOfStudyUkrEngNames mode = DocumentUtil.getModeOfStudyUkrEngNames(studentDegree.getTuitionForm());
        result.put("ModeOfStudyUkr", mode.getModeOfStudyUkr());
        result.put("ModeOfStudyEng", mode.getModeOfStudyEng());

//        result.put("dean", PersonUtil.makeNameThenSurnameInCapital(studentDegree.getSpecialization().getFaculty().getDean()));
//        result.put("deanEng", PersonUtil.makeNameThenSurnameInCapital(studentDegree.getSpecialization().getFaculty().getDeanEng()));

        result.put("startStudy", formatDate(studentDegree.getAdmissionDate()));
        result.put("today", formatDate(new Date()));

        DateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        result.put("PreviousDiplomaName", studentDegree.getPreviousDiplomaType().getNameUkr());
        result.put("PreviousDiplomaNameEng", studentDegree.getPreviousDiplomaType().getNameEng());
        result.put("PreviousDiplomaNumber", TemplateUtil.getValueSafely(studentDegree.getPreviousDiplomaNumber()));
        result.put("PreviousDiplomaIssuedBy", studentDegree.getPreviousDiplomaIssuedBy());
        result.put("PreviousDiplomaIssuedByEng", studentDegree.getPreviousDiplomaIssuedByEng());
        if (studentDegree.getPreviousDiplomaDate() != null) {
            result.put("PreviousDiplomaDate", simpleDateFormat.format(studentDegree.getPreviousDiplomaDate()) + " р.");
            DateFormat englishDateFormat = SimpleDateFormat.getDateInstance(DateFormat.LONG, Locale.ENGLISH);
            result.put("PreviousDiplomaDateEng", englishDateFormat.format(studentDegree.getPreviousDiplomaDate()));
        }
        result.put("totalCredits", studentSummary.getTotalCredits().toString());
        return result;
    }

    public void prepareTable(WordprocessingMLPackage template, StudentSummaryForAcademicReference studentSummary) {
        Tbl table = (Tbl) getAllElementsFromObject(template.getMainDocumentPart(), Tbl.class).get(INDEX_OF_TABLE_WITH_GRADES);
        if(studentSummary.getSemesters().isEmpty()){
            table.getContent().remove(3);
            showNoGradesMessage(table);
        } else {
            prepareRows(table, studentSummary);
        }
    }

    private void showNoGradesMessage(Tbl table){
        Text textInTable = getTextsPlaceholdersFromContentAccessor(table)
                .stream().filter(text -> "#n".equals(text.getValue().trim())).findFirst().get();
        R parentContainer = (R) textInTable.getParent();
        P parentParagraph = (P) TemplateUtil.findParentNode(textInTable, P.class);
        ContentAccessor paragraphsParent = (ContentAccessor) parentParagraph.getParent();
        P newParagraph = XmlUtils.deepCopy(parentParagraph);
        newParagraph.getContent().clear();
        R container = XmlUtils.deepCopy(parentContainer);
        container.getContent().clear();
        Text noGradesMessage = XmlUtils.deepCopy(textInTable);
        noGradesMessage.setValue(NO_GRADES_DESCRIPTION_UKR);
        container.getContent().add(noGradesMessage);
        newParagraph.getContent().add(container);
        paragraphsParent.getContent().add(paragraphsParent.getContent().indexOf(parentParagraph), newParagraph);
        newParagraph = XmlUtils.deepCopy(parentParagraph);
        newParagraph.getContent().clear();
        container = XmlUtils.deepCopy(parentContainer);
        container.getContent().clear();
        noGradesMessage = XmlUtils.deepCopy(textInTable);
        noGradesMessage.setValue(NO_GRADES_DESCRIPTION_EN);
        container.getContent().add(noGradesMessage);
        newParagraph.getContent().add(container);
        paragraphsParent.getContent().add(paragraphsParent.getContent().indexOf(parentParagraph), newParagraph);
        paragraphsParent.getContent().remove(2);
    }

    private void prepareRows(Tbl table, StudentSummaryForAcademicReference studentSummary) {
        List<Tr> tableRows = (List<Tr>) (Object) getAllElementsFromObject(table, Tr.class);
        Tr rowWithSignature = tableRows.get(2);
        Tr rowWithCourse = tableRows.get(3);
        Set<Integer> semestersSet = studentSummary.getSemesters().keySet();
        int currentRow = 3;
        int currentSemester;
        Iterator<Integer> setIterator = semestersSet.iterator();
        if (setIterator.hasNext()) {
            currentSemester = setIterator.next();
            for (SemesterDetails semesterDetails : studentSummary.getSemesters().values()) {
                currentRow = insertOneKcSortRows(table, rowWithSignature, rowWithCourse, currentRow, getSignatureDictionary(currentSemester), semesterDetails.getGrades().get(EXAMS_AND_CREDITS_INDEX));
                if (setIterator.hasNext()) {
                    currentSemester = setIterator.next();
                }
            }
        }
        List<List<Grade>> coursePapersAndInternships = getCoursePapersAndInternships(studentSummary);
        if (coursePapersAndInternships.get(0).size() > 0) {
            Map<String,String> replacements = new HashMap<String,String>();
            replacements.put("n","Курсові роботи (проекти) / Term Papers (Projects)");
            currentRow = insertOneKcSortRows(table, rowWithSignature, rowWithCourse, currentRow, replacements, coursePapersAndInternships.get(0));
        }
        if (coursePapersAndInternships.get(1).size() > 0) {
            Map<String,String> replacements = new HashMap<String,String>();
            replacements.put("n","Практики / Internships");
            insertOneKcSortRows(table, rowWithSignature, rowWithCourse, currentRow, replacements, coursePapersAndInternships.get(1));
        }

        table.getContent().remove(2);
        table.getContent().remove(table.getContent().size()-2);
    }

    private int insertOneKcSortRows(Tbl table, Tr rowWithSignature, Tr rowWithCourse, int currentRow, Map<String, String> header, List<Grade> gradeSet) {
        Tr newRowWithSignature = XmlUtils.deepCopy(rowWithSignature);
        replaceInRow(newRowWithSignature, header);
        table.getContent().add(currentRow, newRowWithSignature);
        currentRow++;
        for (Grade grade : gradeSet) {
            Tr newRowWithCourse = XmlUtils.deepCopy(rowWithCourse);
            replaceInRow(newRowWithCourse, getCourseDictionary(grade));
            table.getContent().add(currentRow, newRowWithCourse);
            currentRow++;
        }
        return currentRow;
    }

    private List<List<Grade>> getCoursePapersAndInternships(StudentSummaryForAcademicReference studentSummary) {
        List<List<Grade>> coursePapersAndInternships = new ArrayList<List<Grade>>();
        coursePapersAndInternships.add(new ArrayList<>());
        coursePapersAndInternships.add(new ArrayList<>());
        for (SemesterDetails semesterDetails : studentSummary.getSemesters().values()) {
            if (semesterDetails.getGrades().get(COURSE_PAPERS_INDEX).size()>0) {
                for (Grade grade : semesterDetails.getGrades().get(COURSE_PAPERS_INDEX)) {
                    coursePapersAndInternships.get(0).add(grade);
                }
            }
            if (semesterDetails.getGrades().get(INTERNSHIPS_INDEX).size()>0) {
                for (Grade grade : semesterDetails.getGrades().get(INTERNSHIPS_INDEX)) {
                    coursePapersAndInternships.get(1).add(grade);
                }
            }
        }
        return coursePapersAndInternships;
    }

    private Map<String, String> getSignatureDictionary(int semester) {
        HashMap<String, String> result = new HashMap<>();
        int coursePart = (semester - 1) / 2 + 1;
        int semesterPart = (semester - 1) % 2 + 1;
        String semesterDisplay;
        if (semesterPart == 1) {
            semesterDisplay = "I";
        } else {
            semesterDisplay = "ІI";
        }
        String ukrainianPart = coursePart + " курс" + " " + semesterDisplay + " семестр";
        String englishPart = coursePart + " year" + " " + semesterDisplay + " semester";
        result.put("n", ukrainianPart + DOCUMENT_DELIMITER + englishPart);
        return result;
    }

    private Map<String, String> getCourseDictionary(Grade grade) {
        HashMap<String, String> result = new HashMap<>();
        result.put("s", grade.getCourse().getCourseName().getName() + DOCUMENT_DELIMITER + grade.getCourse().getCourseName().getNameEng());
        result.put("c", grade.getCourse().getCredits().toString());
        result.put("g", getGradeDisplay(grade));
        return result;
    }

    private String getGradeDisplay(Grade grade) {
        String result = "";
        result += grade.getPoints() + DOCUMENT_DELIMITER;
        result += grade.getEcts().getNationalGradeUkr(grade) + DOCUMENT_DELIMITER;
        result += grade.getEcts().getNationalGradeEng(grade);
        return result;
    }

    protected String formatDate(Date date) {
        String result;
        if (date != null) {
            result = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date);
        } else {
            result = "";
        }
        return result;
    }
}
