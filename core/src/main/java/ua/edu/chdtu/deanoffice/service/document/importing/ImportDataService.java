package ua.edu.chdtu.deanoffice.service.document.importing;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xlsx4j.exceptions.Xlsx4jException;
import org.xlsx4j.org.apache.poi.ss.usermodel.DataFormatter;
import org.xlsx4j.sml.Cell;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.Worksheet;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.entity.superclasses.Sex;
import ua.edu.chdtu.deanoffice.service.DegreeService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ImportDataService {
    private static Logger log = LoggerFactory.getLogger(ImportDataService.class);
    private final DocumentIOService documentIOService;
    private final StudentService studentService;
    private final StudentDegreeService studentDegreeService;
    private final DegreeService degreeService;

    @Autowired
    public ImportDataService(DocumentIOService documentIOService, StudentService studentService, StudentDegreeService studentDegreeService, DegreeService degreeService) {
        this.documentIOService = documentIOService;
        this.studentService = studentService;
        this.studentDegreeService = studentDegreeService;
        this.degreeService = degreeService;
    }

    public List<Student> getStudentsFromStream(InputStream xlsxInputStream) throws IOException, Docx4JException {
        return getStudents(xlsxInputStream);
    }

    public List<Student> getStudentsFromFile(String fileName) throws IOException, Docx4JException {
        return getStudents(fileName);
    }

    private List<Student> getStudents(Object source) throws IOException, Docx4JException {
        SpreadsheetMLPackage xlsxPkg;
        if (source instanceof String) {
            xlsxPkg = documentIOService.loadSpreadsheetDocument((String) source);
        } else {
            xlsxPkg = documentIOService.loadSpreadsheetDocument((InputStream) source);
        }
        List<ImportedData> importedData = importStudents(xlsxPkg);
        return fetchStudentWithStudentDegree(importedData);
    }

    private List<ImportedData> importStudents(SpreadsheetMLPackage xlsxPkg) {
        try {
            WorkbookPart workbookPart = Objects.requireNonNull(xlsxPkg).getWorkbookPart();
            WorksheetPart sheetPart = workbookPart.getWorksheet(0);

            Worksheet worksheet = sheetPart.getContents();
            org.xlsx4j.sml.SheetData sheetData = worksheet.getSheetData();

            DataFormatter formatter = new DataFormatter();
            SheetData sd = new SheetData();
            List<ImportedData> importedData = new ArrayList<>();
            String cellValue;

            for (Row r : sheetData.getRow()) {
                log.debug("importing row: " + r.getR());

                for (Cell c : r.getC()) {
                    cellValue = "";

                    try {
                        cellValue = formatter.formatCellValue(c);
                    } catch (Exception e) {
                        log.debug(e.getMessage());
                    }

                    if (r.getR() == 1) {
                        sd.assignHeader(cellValue, c.getR());
                    } else {
                        sd.setCellData(c.getR(), cellValue);
                    }
                }

                if (r.getR() == 1) {
                    continue;
                }

                importedData.add(sd.getStudentData());
                sd.cleanStudentData();
            }

            return importedData;
        } catch (Docx4JException | Xlsx4jException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private Student fetchStudent(ImportedData data) {
        if (data == null)
            return null;

        List<Student> existsStudent;
        existsStudent = studentService.searchByFullName(data.getFirstName(), data.getLastName(), data.getMiddleName());

        if (existsStudent.size() > 0) {
            return existsStudent.get(0);
        }

        Student student = new Student();
        DateFormat formatter = new SimpleDateFormat("M/dd/yy H:mm");

        try {
            Date birthDate = formatter.parse(data.getBirthday());
            student.setBirthDate(birthDate);
        } catch (Exception e) {
            log.debug(e.getMessage());
        }

        student.setName(data.getFirstName());
        student.setSurname(data.getLastName());
        student.setPatronimic(data.getMiddleName());
        student.setNameEng(data.getFirstNameEn());
        student.setSurnameEng(data.getLastNameEn());
        student.setPatronimicEng(data.getMiddleNameEn());
        student.setSex(data.getPersonsSexName().equals("Чоловіча") ? Sex.MALE : Sex.FEMALE);
        student.setSchool(data.getDocumentIssued());
        student.setTelephone(null);
        student.setRegistrationAddress(null);
        student.setStudentCardNumber(null);
        student.setActualAddress(null);
        student.setFatherName(null);
        student.setFatherInfo(null);
        student.setFatherPhone(null);
        student.setMotherName(null);
        student.setMotherInfo(null);
        student.setMotherInfo(null);
        student.setNotes(null);
        student.setEmail("");
        student.setPhoto(null);
        student.setPrivilege(new Privilege());

        return student;
    }

    private StudentDegree fetchStudentDegree(ImportedData data, Student student) {
        if (data == null)
            return null;

        DateFormat formatter = new SimpleDateFormat("M/dd/yy H:mm");
        StudentDegree studentDegree = new StudentDegree();
        Degree degree = null;

        for (DegreeEnum value : DegreeEnum.values()) {
            if (value.getNameUkr().equals(data.getQualificationGroupName())) {
                degree = degreeService.getDegreeById(value.getId());
            }
        }

        for (EducationDocument eduDocument : EducationDocument.values()) {
            if (eduDocument.getNameUkr().equals(data.getPersonDocumentType())) {
                studentDegree.setPreviousDiplomaType(eduDocument);
            }
        }

        try {
            Date eduDateBegin = formatter.parse(data.getEducationDateBegin());
            studentDegree.setAdmissionOrderDate(eduDateBegin);
        } catch (ParseException e) {
            log.debug(e.getMessage());
        }

        if (student.getId() > 0) {
            List<StudentDegree> studentDegreeList = studentDegreeService.getStudentDegree(student.getId());

            for (StudentDegree sDegree : studentDegreeList) {
                if (studentDegree.getAdmissionOrderDate().equals(sDegree.getAdmissionOrderDate())) {
                    return sDegree;
                }
            }
        }

        studentDegree.setStudent(student);
        studentDegree.setDegree(degree);
        studentDegree.setStudentGroup(null);
        studentDegree.setPreviousDiplomaNumber(data.getDocumentSeries2() + data.getDocumentNumbers2());
        studentDegree.setPayment(data.getPersonEducationPaymentTypeName().equals("Контракт")
                ? Payment.CONTRACT : Payment.BUDGET);
        studentDegree.setProtocolDate(null);
        studentDegree.setProtocolNumber(null);
        studentDegree.setRecordBookNumber(null);
        studentDegree.setSupplementDate(null);
        studentDegree.setSupplementNumber(null);
        studentDegree.setThesisName(null);
        studentDegree.setThesisNameEng(null);
        studentDegree.setContractDate(null);
        studentDegree.setContractNumber(null);

        try {
            Date eduDateEnd = formatter.parse(data.getEducationDateEnd());
            studentDegree.setActive(eduDateEnd != null && eduDateEnd.getTime() > new Date().getTime());
        } catch (ParseException e) {
            log.debug(e.getMessage());
        }

        try {
            DateFormat admissionDateFormatter = new SimpleDateFormat("dd.MM.yyyy");
            final String ADMISSION_REGEXP =
                    "Номер[\\s]+наказу[\\s:]+([\\w\\W]+);[\\W\\w]+Дата[\\s]+наказу[\\s:]*([0-9]{2}.[0-9]{2}.[0-9]{4})";
            Pattern admissionPattern = Pattern.compile(ADMISSION_REGEXP);
            Matcher matcher = admissionPattern.matcher(data.getRefillInfo());

            if (matcher.matches() && matcher.groupCount() > 0) {
                studentDegree.setAdmissionOrderNumber(matcher.group(1));
                studentDegree.setAdmissionOrderDate(admissionDateFormatter.parse(matcher.group(2)));
            }
        } catch (ParseException e) {
            log.debug(e.getMessage());
        }

        try {
            Date prevDiplomaDate = formatter.parse(data.getDocumentDateGet2());
            studentDegree.setPreviousDiplomaDate(prevDiplomaDate);
        } catch (ParseException e) {
            log.debug(e.getMessage());
        }

        return studentDegree;
    }

    private List<Student> fetchStudentWithStudentDegree(List<ImportedData> importedData) {
        if (importedData == null)
            return null;

        List<Student> studentList = new ArrayList<>();

        for (ImportedData data : importedData) {
            Student student = fetchStudent(data);
            StudentDegree studentDegree = fetchStudentDegree(data, student);

            if (!student.getDegrees().contains(studentDegree)) {
                Set<StudentDegree> studentDegrees = student.getDegrees();
                studentDegrees.add(studentDegree);
                student.setDegrees(studentDegrees);
            }

            if (!studentList.contains(student)) {
                studentList.add(student);
            }
        }

        return studentList;
    }
}