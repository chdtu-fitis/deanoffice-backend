package ua.edu.chdtu.deanoffice.service.datasync.thesis;

import org.docx4j.TraversalUtil;
import org.docx4j.finders.TableFinder;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
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

import javax.xml.bind.JAXBElement;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
            List<ThesisImportData> thesisImportData = getThesisesFromStream(docxInputStream);
            Objects.requireNonNull(thesisImportData);
            ThesisReport thesisReport = new ThesisReport();
            for (ThesisImportData thesisData: thesisImportData){
                addSynchronizationReportForThesisImportedData(thesisData, thesisReport, facultyId);
            }
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

    private List<ThesisImportData> getThesisesFromStream(InputStream docxInputStream) throws Docx4JException, IOException {
        return getThesisImportInfo(docxInputStream);
    }

    private List<ThesisImportData> getThesisImportInfo(Object source) throws Docx4JException, IOException {
        WordprocessingMLPackage docxPkg;
        if (source instanceof String){
            docxPkg = documentIOService.loadTemplateWordDocument((String) source);
        } else
            docxPkg = documentIOService.loadTemplateWordDocument((InputStream) source);
        return getThesisImportedDataFromDocxPkg(docxPkg);
    }

    private List<ThesisImportData> getThesisImportedDataFromDocxPkg(WordprocessingMLPackage docxPkg) {
        MainDocumentPart mainDocumentPart = docxPkg.getMainDocumentPart();
        List<ThesisImportData> thesisImportDatas = new ArrayList();

        TableFinder finder = new TableFinder();
        new TraversalUtil(mainDocumentPart.getContent(), finder);
        int rowCount = 0;
        int cellCount = 1;

        for (Object table: finder.tblList){
            Tbl tbl = (Tbl) table;
            String groupName = "";
            List<Object> allRows = tbl.getContent();

            for (Object tableRow: allRows){
                if (rowCount == 1){
                    rowCount++;
                    continue;
                }
                ThesisImportData thesisImportData = new ThesisImportData();
                Tr row = (Tr) tableRow;
                List<Object> allCells = row.getContent();
                String cellData = "";
                cellCount = 1;

                for (Object tableCell: allCells){
                    if (cellCount == 1){
                        cellCount++;
                        continue;
                    }
                    if (!cellData.equals("")){
                        cellData += "/";
                    }
                    Tc cell = null;
                    if (tableCell instanceof JAXBElement) {
                        JAXBElement jaxbElement = (JAXBElement) tableCell;
                        cell = (Tc) jaxbElement.getValue();
                    } else
                        cell = (Tc) tableCell;
                    List<Object> allPara = cell.getContent();

                    for (Object tableParagraph: allPara){
                        P paragraph = (P) tableParagraph;
                        if (paragraph.getRsidRPr().equals("00DE0929")){
                            continue;
                        }
                        List<Object> allRun = paragraph.getContent();
                        if (allRun.size() == 0){
                            continue;
                        }

                        for (Object tableRun: allRun){
                            if (!(tableRun instanceof R)){
                                continue;
                            }
                            R run = (R) tableRun;
                            List<Object> allText = run.getContent();

                            for (Object tableText: allText){
                                Text text = null;
                                if (tableText instanceof JAXBElement){
                                    JAXBElement jaxbElement = (JAXBElement) tableText;
                                    text = (Text) jaxbElement.getValue();
                                } else {
                                    text = (Text) tableText;
                                }
                                if (rowCount == 0){
                                    rowCount = 1;
                                    groupName = text.getValue();
                                } else
                                    cellData += text.getValue();
                            }
                        }
                    }
                }
                if(!cellData.equals("")){
                    String rowParts[] = cellData.split("/", 4);
                    String[] studentData = rowParts[0].split(" ", 3);
                    String[] groupString = groupName.split(" ", 2);
                    thesisImportData.setLastName(studentData[0]);
                    thesisImportData.setFirstName(studentData[1]);
                    thesisImportData.setMiddleName(studentData[2]);
                    thesisImportData.setThesisName(rowParts[1]);
                    thesisImportData.setThesisNameEng(rowParts[2]);
                    thesisImportData.setFullSupervisorName(rowParts[3]);
                    thesisImportData.setGroupName(groupString[1]);
                    thesisImportDatas.add(thesisImportData);
                    cellData = "";
                }
            }
            rowCount = 0;
        }
        return thesisImportDatas;
    }

    public void addSynchronizationReportForThesisImportedData(ThesisImportData thesisImportData, ThesisReport thesisReport, int facultyId){
        List<Student> student = studentService.searchByFullName(
                thesisImportData.getFirstName(),
                thesisImportData.getLastName(),
                thesisImportData.getMiddleName(),
                facultyId
        );
        if (student.size() == 0){
            String message = "Даний студент відсутній";
            thesisReport.addThesisRed(new ThesisWithMessageRedBean(message, new ThesisDataBean(thesisImportData)));
            return;
        }
        if (thesisImportData.getGroupName().equals(" ")){
            String message = "Відсутня назва групи";
            thesisReport.addThesisRed(new ThesisWithMessageRedBean(message, new ThesisDataBean(thesisImportData)));
            return;
        }
        StudentGroup studentGroup = studentGroupService.getByName(thesisImportData.getGroupName());
        if (studentGroup == null){
            String message = "Дана група відсутня";
            thesisReport.addThesisRed(new ThesisWithMessageRedBean(message, new ThesisDataBean(thesisImportData)));
            return;
        }
        if (thesisImportData.getThesisName().equals(" ")){
            String message = "Відсутня тема дипломної роботи українською мовою";
            thesisReport.addThesisRed(new ThesisWithMessageRedBean(message, new ThesisDataBean(thesisImportData)));
        }
        if (thesisImportData.getThesisNameEng().equals(" ")){
            String message = "Відсутня тема дипломної роботи англійською мовою";
            thesisReport.addThesisRed(new ThesisWithMessageRedBean(message, new ThesisDataBean(thesisImportData)));
        }
        List<StudentDegree> studentDegreeFromDb = studentDegreeService.getAllActiveByStudent(student.get(0).getId());
        thesisReport.addThesisGreen(new ThesisDataBean(studentDegreeFromDb.get(0), thesisImportData.getThesisName(), thesisImportData.getThesisNameEng()));
    }
}
