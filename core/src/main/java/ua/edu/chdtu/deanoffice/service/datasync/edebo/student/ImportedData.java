package ua.edu.chdtu.deanoffice.service.datasync.edebo.student;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImportedData {
    @CsvBindByName(column = "Ім'я")
    private String firstName;
    @CsvBindByName(column = "Прізвище")
    private String lastName;
    @CsvBindByName(column = "По-батькові")
    private String middleName;
    @CsvBindByName(column = "Ім'я англійською")
    private String firstNameEn;
    @CsvBindByName(column = "Прізвище англійською")
    private String lastNameEn;
    @CsvBindByName(column = "По-батькові англійською")
    private String middleNameEn;

//    private String birthday;
    @CsvBindByName(column = "ID картки")
    private String educationId;
    @CsvBindByName(column = "Стать")
    private String personsSexName;

//    private String educationDateBegin;

//    private String educationDateEnd;
    @CsvBindByName(column = "Структурний підрозділ")
    private String facultyName;
    @CsvBindByName(column = "Освітній ступінь (рівень)")
    private String qualificationGroupName;
    @CsvBindByName(column = "Вступ на основі")
    private String baseQualificationName;
    @CsvBindByName(column = "Форма навчання")
    private String educationFormName;
    @CsvBindByName(column = "Джерело фінансування")
    private String personEducationPaymentTypeName;
    @CsvBindByName(column = "Спеціальність")
    private String fullSpecialityName;
    @CsvBindByName(column = "Спеціалізація")
    private String fullSpecializationName;
    @CsvBindByName(column = "Освітня програма")
    private String programName;
//    @CsvBindByName(column = "")
//    private String programNameEn;
//    @CsvBindByName(column = "")
//    private String personSexId;
    @CsvBindByName(column = "Громадянство")
    private String country;
//    @CsvBindByName(column = "")
//    private String ipnNumber;
//    @CsvBindByName(column = "")
//    private String personDocumentTypeId;
//    @CsvBindByName(column = "")
//    private String documentSeries;
//    @CsvBindByName(column = "")
//    private String documentNumbers;
//    @CsvBindByName(column = "")
//    private String documentDateGet;
//    @CsvBindByName(column = "")
//    private String documentIssued;
    @CsvBindByName(column = "Документ про освіту")
    private String eduDocInfo;
    @CsvBindByName(column = "Попередній заклад освіти")
    private String universityFrom;
    @CsvBindByName(column = "Наказ про зарахування")
    private String refillInfo;
    @CsvBindByName(column = "Інформація про попереднє навчання")
    private String eduFromInfo;
//    @CsvBindByName(column = "")
//    private String personDocumentType;

//    private String personDocumentTypeName;
//
//    private String documentSeries2;
//
//    private String documentNumbers2;
//
//    private String documentDateGet2;
//
//    private String documentIssued2;

    @CsvBindByName(column = "Чи здобувався ступень за іншою спеціальністю")
    private String x1;
    @CsvBindByName(column = "Чи скорочений термін навчання")
    private String x2;
    @CsvBindByName(column = "Академічна довідка (освітня декларація)")
    private String x3;
    @CsvBindByName(column = "Інформація про зарахування")
    private String x4;

    public ImportedData() {
        this.firstName = "";
        this.lastName = "";
        this.middleName = "";
        this.firstNameEn = "";
        this.lastNameEn = "";
        this.middleNameEn = "";
//        this.birthday = "";
        this.educationId = "";
        this.personsSexName = "";
//        this.educationDateBegin = "";
//        this.educationDateEnd = "";
        this.facultyName = "";
        this.qualificationGroupName = "";
        this.baseQualificationName = "";
        this.educationFormName = "";
        this.personEducationPaymentTypeName = "";
        this.fullSpecialityName = "";
        this.fullSpecializationName = "";
        this.programName = "";
//        this.programNameEn = "";
//        this.personSexId = "";
        this.country = "";
//        this.ipnNumber = "";
//        this.personDocumentTypeId = "";
//        this.documentSeries = "";
//        this.documentNumbers = "";
//        this.documentDateGet = "";
//        this.documentIssued = "";
        this.eduDocInfo = "";
        this.universityFrom = "";
        this.refillInfo = "";
        this.eduFromInfo = "";
//        this.personDocumentType = "";
//        this.personDocumentTypeName = "";
//        this.documentSeries2 = "";
//        this.documentNumbers2 = "";
//        this.documentDateGet2 = "";
//        this.documentIssued2 = "";
    }
}
