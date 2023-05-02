package ua.edu.chdtu.deanoffice.service.datasync.thesis;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.datasync.thesis.beans.ListThesisDataForGroupBean;
import ua.edu.chdtu.deanoffice.service.datasync.thesis.beans.ThesisDataBean;
import ua.edu.chdtu.deanoffice.service.datasync.thesis.beans.ThesisDataWithMessageBean;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.TemplateUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static ua.edu.chdtu.deanoffice.util.PersonUtil.correctCaseInName;

@Service
public class ThesisImportService {

    private DocumentIOService documentIOService;
    private StudentDegreeService studentDegreeService;
    private StudentGroupService studentGroupService;

    @Autowired
    public ThesisImportService(DocumentIOService documentIOService,
                               StudentDegreeService studentDegreeService,
                               StudentGroupService studentGroupService) {
        this.documentIOService = documentIOService;
        this.studentDegreeService = studentDegreeService;
        this.studentGroupService = studentGroupService;
    }

    public ThesisReport getThesisImportReport(InputStream docxInputStream, int facultyId) throws Docx4JException, IOException {
        try {
            ThesisReport thesisReport = new ThesisReport();
            readingThesisImportedDataFromDocxPkg(getLoadedWordDocument(docxInputStream), thesisReport, facultyId);
            return thesisReport;
        } catch (Docx4JException | IOException e) {
            throw new Docx4JException("Помилка обробки файлу");
        } finally {
            docxInputStream.close();
        }
    }

    private WordprocessingMLPackage getLoadedWordDocument(Object source) throws Docx4JException, IOException {
        WordprocessingMLPackage docxPkg;
        if (source instanceof String) {
            docxPkg = documentIOService.loadWordDocument((String) source);
        } else
            docxPkg = documentIOService.loadWordDocument((InputStream) source);
        return docxPkg;
    }

    private void readingThesisImportedDataFromDocxPkg(WordprocessingMLPackage docxPkg, ThesisReport thesisReport, int facultyId) {
        List<ThesisImportData> thesisImportData = new ArrayList();
        String groupName = "";
        List<Tbl> allTables = TemplateUtil.getAllTablesFromDocument(docxPkg);
        for (Tbl tbl : allTables) {
            List<Tr> allTableRows = TemplateUtil.getAllRowsFromTable(tbl);
            groupName = getGroupNameFromTableRow(TemplateUtil.getAllTextsFromObject(allTableRows.get(0)));
            if (groupName.length() < 4)
                continue;
            allTableRows.remove(1);
            allTableRows.remove(0);
            for (Tr tr : allTableRows) {
                String[] rowsContent = new String[4];
                List<Object> allTableCells = TemplateUtil.getAllElementsFromObject(tr, Tc.class);
                for (int cellNumber = 1; cellNumber < allTableCells.size(); cellNumber++) {
                    rowsContent[cellNumber - 1] = "";
                    List<Object> allParagraphsFromCell = TemplateUtil.getAllElementsFromObject(allTableCells.get(cellNumber), P.class);
                    for (Object paragraph: allParagraphsFromCell){
                        List<Text> allParagraphText = TemplateUtil.getAllTextsFromObject(paragraph);
                        for (int textPice = 0; textPice < allParagraphText.size(); textPice++) {
                            if (allParagraphText.size() == 1){
                                rowsContent[cellNumber - 1] += allParagraphText.get(textPice).getValue().trim() + " ";
                            } else {
                                rowsContent[cellNumber - 1] += allParagraphText.get(textPice).getValue();
                            }
                        }
                    }
                }
                ThesisImportData thesisData = getDataAboutStudentFromRow(rowsContent, groupName);
                if (!addToRedListIfDataIsWrong(thesisData, thesisReport, facultyId)) {
                    thesisImportData.add(thesisData);
                }
            }
            if (thesisImportData.size() != 0) {
                addThesisDataToImportedList(thesisImportData, thesisReport, facultyId, groupName);
                thesisImportData.clear();
            }
        }
    }

    private String getGroupNameFromTableRow(List<Text> groupData) {
        String groupString = "";
        for (Text group : groupData) {
            groupString += group.getValue();
        }
        String[] groupStringParts = groupString.split(" ", 2);
        return groupStringParts.length > 1 ? groupStringParts[1].trim() : "";
    }

    private void addThesisDataToImportedList(List<ThesisImportData> thesisImportData, ThesisReport thesisReport, int facultyId, String groupName) {
        List<ThesisDataBean> thesisDataBeans = new ArrayList();
        StudentGroup studentGroup = studentGroupService.getByNameAndFacultyId(thesisImportData.get(0).getGroupName(), facultyId);

        for (ThesisImportData importData : thesisImportData) {
            StudentDegree studentDegree = studentDegreeService.getByStudentFullNameAndGroupId(
                    importData.getStudentFullName(),
                    studentGroup.getId()
            );
            thesisDataBeans.add(new ThesisDataBean(studentDegree, importData.getThesisName(), importData.getThesisNameEng(), importData.getFullSupervisorName()));
        }
        thesisReport.addThesisDataForImportToGreenList(new ListThesisDataForGroupBean(groupName, thesisDataBeans));
    }

    private boolean addToRedListIfDataIsWrong(ThesisImportData thesisImportData, ThesisReport thesisReport, int facultyId) {
        if (thesisImportData.getGroupName().equals("")) {
            String message = "Відсутня назва групи";
            thesisReport.addThesisWithMissingDataToRedList(new ThesisDataWithMessageBean(message, new ThesisDataBean(thesisImportData)));
            return true;
        }
        StudentGroup studentGroup = studentGroupService.getByNameAndFacultyId(thesisImportData.getGroupName(), facultyId);
        if (studentGroup == null) {
            String message = "Дана група не існує";
            thesisReport.addThesisWithMissingDataToRedList(new ThesisDataWithMessageBean(message, new ThesisDataBean(thesisImportData)));
            return true;
        }
        StudentDegree studentDegree = studentDegreeService.getByStudentFullNameAndGroupId(
                thesisImportData.getStudentFullName(),
                studentGroup.getId()
        );
        if (studentDegree == null) {
            String message = "Даний студент відсутній";
            thesisReport.addThesisWithMissingDataToRedList(new ThesisDataWithMessageBean(message, new ThesisDataBean(thesisImportData)));
            return true;
        }
        if (thesisImportData.getThesisName().equals("")) {
            String message = "Відсутня тема дипломної роботи українською мовою";
            thesisReport.addThesisWithMissingDataToRedList(new ThesisDataWithMessageBean(message, new ThesisDataBean(thesisImportData)));
            return true;
        }
        if (thesisImportData.getThesisNameEng().equals("")) {
            String message = "Відсутня тема дипломної роботи англійською мовою";
            thesisReport.addThesisWithMissingDataToRedList(new ThesisDataWithMessageBean(message, new ThesisDataBean(thesisImportData)));
            return true;
        }
        return false;
    }

    private ThesisImportData getDataAboutStudentFromRow(String[] rowsContent, String groupName) {
        ThesisImportData thesisImportData = new ThesisImportData();
        thesisImportData.setStudentFullName(correctCaseInName(rowsContent[0].trim()));
        thesisImportData.setThesisName(rowsContent[1].trim());
        thesisImportData.setThesisNameEng(rowsContent[2].trim());
        thesisImportData.setFullSupervisorName(rowsContent[3].trim());
        thesisImportData.setGroupName(groupName.trim());
        return thesisImportData;
    }
}
