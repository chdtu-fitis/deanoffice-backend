package ua.edu.chdtu.deanoffice.service.datasync.thesis;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.StudentService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.TemplateUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ThesisImportService {

    private DocumentIOService documentIOService;
    private StudentDegreeService studentDegreeService;
    private StudentService studentService;
    private StudentGroupService studentGroupService;

    @Autowired
    public ThesisImportService(DocumentIOService documentIOService,
                               StudentDegreeService studentDegreeService,
                               StudentService studentService,
                               StudentGroupService studentGroupService){
        this.documentIOService = documentIOService;
        this.studentDegreeService = studentDegreeService;
        this.studentService = studentService;
        this.studentGroupService = studentGroupService;
    }

    public ThesisReport getThesisImportReport(InputStream docxInputStream, int facultyId) throws Exception{
        if (docxInputStream == null){
            throw new Exception("Помилка часу виконання");
        }
        try{
            ThesisReport thesisReport = new ThesisReport();
            readingThesisImportedDataFromDocxPkg(getLoadedWordDocument(docxInputStream), thesisReport, facultyId);
            return thesisReport;
        } catch (Docx4JException e){
            e.printStackTrace();
            throw new Exception("Помилка обробки файлу");
        } catch (Exception e){
            e.printStackTrace();
            throw new Exception("Помилка читання файлу");
        } finally {
            docxInputStream.close();
        }
    }

    private WordprocessingMLPackage getLoadedWordDocument(Object source) throws Docx4JException, IOException {
        WordprocessingMLPackage docxPkg;
        if (source instanceof String){
            docxPkg = documentIOService.loadTemplateWordDocument((String) source);
        } else
            docxPkg = documentIOService.loadTemplateWordDocument((InputStream) source);
        return docxPkg;
    }

    private void readingThesisImportedDataFromDocxPkg(WordprocessingMLPackage docxPkg, ThesisReport thesisReport, int facultyId) {
        List<ThesisImportData> thesisImportDatas = new ArrayList();
        String groupName = "";
        List<Tbl> allTables = TemplateUtil.getAllTablesFromDocument(docxPkg);
        for (Tbl tbl: allTables){
            List<Tr> allTableRows = TemplateUtil.getAllRowsFromTable(tbl);
            String[] splitGroupName = TemplateUtil.getAllTextsFromObject(allTableRows.get(0)).get(0).getValue().split(" ", 2);
            groupName = splitGroupName[1];
            allTableRows.remove(1);
            allTableRows.remove(0);
            for (Tr tr: allTableRows){
                String paragraphsContent = "";
                List<Object> allTableCells = TemplateUtil.getAllElementsFromObject(tr, Tc.class);
                allTableCells.remove(0);
                for (Object cellText: allTableCells){
                    if (!paragraphsContent.equals("")){
                        paragraphsContent += "/";
                    }
                    List<Text> allCellText = TemplateUtil.getAllTextsFromObject(cellText);
                    for (Text text: allCellText){
                        paragraphsContent += text.getValue();
                    }
                }
                ThesisImportData thesisImportData = getDataAboutStudentFromRow(paragraphsContent, groupName);
                if (!addToRedListIfDataIsWrong(thesisImportData, thesisReport, facultyId)){
                    thesisImportDatas.add(thesisImportData);
                }
            }
            if (thesisImportDatas.size() != 0){
                addThesisDataToImportedList(thesisImportDatas, thesisReport, facultyId, groupName);
                thesisImportDatas.clear();
            }
        }
    }

    private void addThesisDataToImportedList(List<ThesisImportData> thesisImportDatas, ThesisReport thesisReport, int facultyId, String groupName){
        List<ThesisDataBean> thesisDataBeans = new ArrayList();
        for (ThesisImportData thesisImportData: thesisImportDatas){
            List<Student> student = studentService.searchByFullName(
                    thesisImportData.getFirstName(),
                    thesisImportData.getLastName(),
                    thesisImportData.getMiddleName(),
                    facultyId
            );
            List<StudentDegree> studentDegreeFromDb = studentDegreeService.getAllActiveByStudent(student.get(0).getId());
            if (studentDegreeFromDb.size() != 0){
                thesisDataBeans.add(new ThesisDataBean(studentDegreeFromDb.get(0), thesisImportData.getThesisName(), thesisImportData.getThesisNameEng()));
            }
        }
        thesisReport.addThesisGreen(new ListThesisDatasForGroupBean(groupName,thesisDataBeans));
    }

    private Boolean addToRedListIfDataIsWrong(ThesisImportData thesisImportData, ThesisReport thesisReport, int facultyId){
        if (thesisImportData.getGroupName().equals("")){
            String message = "Відсутня назва групи";
            thesisReport.addThesisRed(new ThesisWithMessageRedBean(message, new ThesisDataBean(thesisImportData)));
            return true;
        }
        StudentGroup studentGroup = studentGroupService.getByNameAndFacultyId(thesisImportData.getGroupName(), facultyId);
        if (studentGroup == null){
            String message = "Дана група не існує";
            thesisReport.addThesisRed(new ThesisWithMessageRedBean(message, new ThesisDataBean(thesisImportData)));
            return true;
        }
        StudentDegree studentDegree = studentDegreeService.getAllStudentDegreeByStudentFullNameAngGroupId(
                thesisImportData.getLastName(),
                thesisImportData.getFirstName(),
                thesisImportData.getMiddleName(),
                studentGroup.getId()
                );
        if (studentDegree == null){
            String message = "Даний студент відсутній";
            thesisReport.addThesisRed(new ThesisWithMessageRedBean(message, new ThesisDataBean(thesisImportData)));
            return true;
        }
        if (thesisImportData.getThesisName().equals("")){
            String message = "Відсутня тема дипломної роботи українською мовою";
            thesisReport.addThesisRed(new ThesisWithMessageRedBean(message, new ThesisDataBean(thesisImportData)));
            return true;
        }
        if (thesisImportData.getThesisNameEng().equals("")){
            String message = "Відсутня тема дипломної роботи англійською мовою";
            thesisReport.addThesisRed(new ThesisWithMessageRedBean(message, new ThesisDataBean(thesisImportData)));
            return true;
        }
        return false;
    }

    private ThesisImportData getDataAboutStudentFromRow(String source, String groupName){
        ThesisImportData thesisImportData = new ThesisImportData();
        String[] allStudentData = source.split("/", 4);
        String[] fullStudentName = allStudentData[0].split(" ", 3);

        if (!fullStudentName[2].equals("")){
            thesisImportData.setLastName(fullStudentName[0]);
            thesisImportData.setFirstName(fullStudentName[1]);
            thesisImportData.setMiddleName(fullStudentName[2]);
        } else if((!fullStudentName[1].equals("")) && (!fullStudentName[0].equals("")) && fullStudentName[2].equals("")){
            thesisImportData.setLastName(fullStudentName[0]);
            thesisImportData.setFirstName(fullStudentName[1]);
        }
        thesisImportData.setThesisName(allStudentData[1]);
        thesisImportData.setThesisNameEng(allStudentData[2]);
        thesisImportData.setFullSupervisorName(allStudentData[3]);
        thesisImportData.setGroupName(groupName);
        return thesisImportData;
    }
}
