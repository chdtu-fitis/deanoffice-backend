package ua.edu.chdtu.deanoffice.service.datasync.edebo.diploma.number;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.ReflectionUtils;

import java.util.Objects;

@Getter
@Setter
class DiplomaSheetData{
    private DiplomaImportData headerData;
    private DiplomaImportData studentData;

    DiplomaSheetData(){
        headerData = new DiplomaImportData();
        studentData = new DiplomaImportData();
    }

    void assignHeader(String pattern, String columnName) {
        columnName = removeDigits(columnName);
        switch (pattern){
            case DiplomaHeaderPatterns.FIRST_NAME:
                headerData.setFirstName(columnName);
                break;
            case DiplomaHeaderPatterns.LAST_NAME:
                headerData.setLastName(columnName);
                break;
            case DiplomaHeaderPatterns.MIDDLE_NAME:
                headerData.setMiddleName(columnName);
                break;
            case DiplomaHeaderPatterns.FULL_SPECIALITY_NAME:
                headerData.setSpecialityName(columnName);
                break;
            case DiplomaHeaderPatterns.FACULTY_NAME:
                headerData.setFacultyName(columnName);
                break;
            case DiplomaHeaderPatterns.DOCUMENT_SERIES:
                headerData.setDocumentSeries(columnName);
                break;
            case DiplomaHeaderPatterns.DOCUMENT_NUMBER:
                headerData.setDocumentNumber(columnName);
                break;
            case DiplomaHeaderPatterns.AWARD_TYPE_ID:
                headerData.setAwardTypeId(columnName);
                break;
            case DiplomaHeaderPatterns.EDUCATION_ID:
                headerData.setEducationId(columnName);
                break;
        }
    }

    void setCellData(String columnName, String value) {
        String col = removeDigits(columnName);

        ReflectionUtils.doWithFields(studentData.getClass(), field -> {
            field.setAccessible(true);

            if (Objects.equals(col, field.get(headerData))) {
                ReflectionUtils.setField(field, studentData, value.replaceAll("\"", ""));
            }
        });
    }

    void cleanStudentData() {
        studentData = new DiplomaImportData();
    }

    private String removeDigits(String value) {
        return value.replaceAll("[\\d.]", "");
    }
}
