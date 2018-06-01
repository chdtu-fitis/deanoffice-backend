package ua.edu.chdtu.deanoffice.service.document.importing;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.ReflectionUtils;

import java.util.Objects;

@Getter
@Setter
class SheetData {
    private ImportedData headerData;
    private ImportedData studentData;

    SheetData() {
        headerData = new ImportedData();
        studentData = new ImportedData();
    }

    void assignHeader(String pattern, String columnName) {
        columnName = removeDigits(columnName);

        switch (pattern) {
            case HeaderPatterns.FIRST_NAME:
                headerData.setFirstName(columnName);
                break;
            case HeaderPatterns.LAST_NAME:
                headerData.setLastName(columnName);
                break;
            case HeaderPatterns.MIDDLE_NAME:
                headerData.setMiddleName(columnName);
                break;
            case HeaderPatterns.FIRST_NAME_EN:
                headerData.setFirstNameEn(columnName);
                break;
            case HeaderPatterns.LAST_NAME_EN:
                headerData.setLastNameEn(columnName);
                break;
            case HeaderPatterns.MIDDLE_NAME_EN:
                headerData.setMiddleNameEn(columnName);
                break;
            case HeaderPatterns.BIRTHDAY:
                headerData.setBirthday(columnName);
                break;
            case HeaderPatterns.EDUCATION_ID:
                headerData.setEducationId(columnName);
                break;
            case HeaderPatterns.PERSONS_SEX_NAME:
                headerData.setPersonsSexName(columnName);
                break;
            case HeaderPatterns.EDUCATION_DATE_BEGIN:
                headerData.setEducationDateBegin(columnName);
                break;
            case HeaderPatterns.EDUCATION_DATE_END:
                headerData.setEducationDateEnd(columnName);
                break;
            case HeaderPatterns.FACULTY_NAME:
                headerData.setFacultyName(columnName);
                break;
            case HeaderPatterns.QUALIFICATION_GROUP_NAME:
                headerData.setQualificationGroupName(columnName);
                break;
            case HeaderPatterns.BASE_QUALIFICATION_NAME:
                headerData.setBaseQualificationName(columnName);
                break;
            case HeaderPatterns.EDUCATION_FORM_NAME:
                headerData.setEducationFormName(columnName);
                break;
            case HeaderPatterns.PAYMENT_TYPE_NAME:
                headerData.setPersonEducationPaymentTypeName(columnName);
                break;
            case HeaderPatterns.FULL_SPECIALITY_NAME:
                headerData.setFullSpecialityName(columnName);
                break;
            case HeaderPatterns.FULL_SPECIALIZATION_NAME:
                headerData.setFullSpecializationName(columnName);
                break;
            case HeaderPatterns.PROGRAM_NAME:
                headerData.setProgramName(columnName);
                break;
            case HeaderPatterns.PROGRAM_NAME_EN:
                headerData.setProgramNameEn(columnName);
                break;
            case HeaderPatterns.PERSON_SEX_ID:
                headerData.setPersonSexId(columnName);
                break;
            case HeaderPatterns.COUNTRY_ID:
                headerData.setCountryId(columnName);
                break;
            case HeaderPatterns.IPN_NUMBER:
                headerData.setIpnNumber(columnName);
                break;
            case HeaderPatterns.PERSON_DOCUMENT_TYPE_ID:
                headerData.setPersonDocumentTypeId(columnName);
                break;
            case HeaderPatterns.DOCUMENT_SERIES:
                headerData.setDocumentSeries(columnName);
                break;
            case HeaderPatterns.DOCUMENT_NUMBERS:
                headerData.setDocumentNumbers(columnName);
                break;
            case HeaderPatterns.DOCUMENT_DATE_GET:
                headerData.setDocumentDateGet(columnName);
                break;
            case HeaderPatterns.DOCUMENT_ISSUED:
                headerData.setDocumentIssued(columnName);
                break;
            case HeaderPatterns.EDU_DOC_INFO:
                headerData.setEduDocInfo(columnName);
                break;
            case HeaderPatterns.UNIVERSITY_FROM:
                headerData.setUniversityFrom(columnName);
                break;
            case HeaderPatterns.REFILL_INFO:
                headerData.setRefillInfo(columnName);
                break;
            case HeaderPatterns.EDU_FROM_INFO:
                headerData.setEduFromInfo(columnName);
                break;
            case HeaderPatterns.PERSON_DOCUMENT_TYPE:
                headerData.setPersonDocumentType(columnName);
                break;
            case HeaderPatterns.PERSON_DOCUMENT_TYPE_NAME:
                headerData.setPersonDocumentTypeName(columnName);
                break;
            case HeaderPatterns.DOCUMENT_SERIES_2:
                headerData.setDocumentSeries2(columnName);
                break;
            case HeaderPatterns.DOCUMENT_NUMBERS_2:
                headerData.setDocumentNumbers2(columnName);
                break;
            case HeaderPatterns.DOCUMENT_DATE_GET_2:
                headerData.setDocumentDateGet2(columnName);
                break;
            case HeaderPatterns.DOCUMENT_ISSUED_2:
                headerData.setDocumentIssued2(columnName);
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
        studentData = new ImportedData();
    }

    private String removeDigits(String value) {
        return value.replaceAll("[\\d.]", "");
    }
}
