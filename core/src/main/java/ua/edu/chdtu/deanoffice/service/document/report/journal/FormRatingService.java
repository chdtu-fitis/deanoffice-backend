package ua.edu.chdtu.deanoffice.service.document.report.journal;

import org.docx4j.UnitsOfMeasurement;
import org.docx4j.XmlUtils;
import org.docx4j.dml.CTTableCell;
import org.docx4j.jaxb.Context;
import org.docx4j.model.properties.table.tr.TrHeight;
import org.docx4j.model.structure.PageSizePaper;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.service.document.report.academicdifference.UnpassedCourse;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ua.edu.chdtu.deanoffice.service.document.DocumentIOService.TEMPLATES_PATH;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.*;
import static ua.edu.chdtu.deanoffice.util.LanguageUtil.transliterate;

@Service
public class FormRatingService {
    private static final int WIDTH_NUMBER_COLUMN = 400;
    private static final int WIDTH_NAME_COLUMN = 2500;

    private WordprocessingMLPackage wordMLPackage;
    private ObjectFactory factory;
    @Autowired
    private DocumentIOService documentIOService;
    @Autowired
    private CourseForGroupService courseForGroupService;
    @Autowired
    private StudentGroupService groupService;
    private List<String> namesStudents;
    private List<String> namesCourses;
    private int columnWidth;

    public File formDocument(
        Integer degreeId,
        Integer year,
        String tuitionFormText,
        Integer semester
    ) throws Docx4JException, IOException {
        namesStudents = new ArrayList<>();
        namesCourses = new ArrayList<>();
        getDataFromDataBase(degreeId,year,tuitionFormText,semester);
        WordprocessingMLPackage resultTemplate = createDocument();
        String fileName = transliterate("якась назва");

        return documentIOService.saveDocumentToTemp(resultTemplate, fileName, FileFormatEnum.DOCX);
    }

    private WordprocessingMLPackage createDocument()
            throws Docx4JException {
        WordprocessingMLPackage wordMLPackage = createTable();
        HashMap<String, String> result = new HashMap();
        result.put("group", "Пз-154");
        return wordMLPackage;
    }
    private WordprocessingMLPackage createTable() throws InvalidFormatException {
        wordMLPackage = WordprocessingMLPackage.createPackage(PageSizePaper.A4,true);
        factory = Context.getWmlObjectFactory();
        Tbl table = factory.createTbl();
        calculateColumnWidth(wordMLPackage);
        addBorders(table);
        setHeaders(table);
        setRows(table);
        wordMLPackage.getMainDocumentPart().addObject(table);
        return wordMLPackage;
    }

    private void setRows(Tbl table) {
        int number = 1;
        for (String name:namesStudents){
            Tr tr = factory.createTr();
            table.getContent().add(tr);
            addTableCellWithWidth(tr,number+".",WIDTH_NUMBER_COLUMN);
            addTableCellWithWidth(tr,name,WIDTH_NUMBER_COLUMN);
            for(int i=1;i<=namesCourses.size();i++){
                addTableCellWithWidth(tr,"",columnWidth);
            }
            number++;
        }
    }

    private void setHeaders(Tbl table) {
        Tr tr = factory.createTr();
        addTableCellWithWidth(tr, "", WIDTH_NUMBER_COLUMN);
        addTableCellWithWidth(tr, "", WIDTH_NAME_COLUMN);
        setRowHeight(tr,100);
        for (String nameCourse:namesCourses){
            Tc tableCell = factory.createTc();
            tableCell.getContent().add(wordMLPackage.getMainDocumentPart().createParagraphOfText(nameCourse));
            TcPr tableCellProperties = new TcPr();
            TblWidth tableWidth = new TblWidth();
            tableWidth.setW(BigInteger.valueOf(columnWidth));
            TextDirection td = new TextDirection();
            td.setVal("btLr");
            tableCellProperties.setTextDirection(td);
            tableCellProperties.setTcW(tableWidth);
            tableCell.setTcPr(tableCellProperties);
            tr.getContent().add(tableCell);
        }
        table.getContent().add(tr);
    }

    private void getDataFromDataBase(Integer degreeId, Integer year, String tuitionFormText, int semester) {
        TuitionForm tuitionForm = TuitionForm.valueOf(tuitionFormText);

        StudentGroup studentGroup = groupService.getById(429);
        List<CourseForGroup> courseForGroups = courseForGroupService.getCoursesForGroupBySemester(studentGroup.getId(), 6);
        List<StudentDegree> students = studentGroup.getStudentDegrees();
        for (StudentDegree studentDegree:students){
            namesStudents.add(studentDegree.getStudent().getInitialsUkr());
        }
        for (CourseForGroup courseForGroup:courseForGroups){
            namesCourses.add(courseForGroup.getCourse().getCourseName().getName());
        }
    }

    private void setRowHeight(Tr tr,int height){
        TrPr trPr = Context.getWmlObjectFactory().createTrPr();
        tr.setTrPr(trPr);
        TrHeight thr = new TrHeight();
        thr.set(trPr);
        int twip = UnitsOfMeasurement.pxToTwip(height);
        ((CTHeight)thr.getObject()).setVal(BigInteger.valueOf(twip));
    }

    private void addTableCellWithWidth(Tr row, String content, int width){
        Tc tableCell = factory.createTc();
        tableCell.getContent().add(
                wordMLPackage.getMainDocumentPart().createParagraphOfText(
                        content));
        if (width > 0) {
            setCellWidth(tableCell, width);
        }
        row.getContent().add(tableCell);
    }

    private void setCellWidth(Tc tableCell, int width) {
        TcPr tableCellProperties = new TcPr();
        TblWidth tableWidth = new TblWidth();
        tableWidth.setW(BigInteger.valueOf(width));
        tableCellProperties.setTcW(tableWidth);
        tableCell.setTcPr(tableCellProperties);
    }

    private void addBorders(Tbl table) {
        table.setTblPr(new TblPr());
        CTBorder border = new CTBorder();
        border.setColor("auto");
        border.setSz(new BigInteger("4"));
        border.setSpace(new BigInteger("0"));
        border.setVal(STBorder.SINGLE);

        TblBorders borders = new TblBorders();
        borders.setBottom(border);
        borders.setLeft(border);
        borders.setRight(border);
        borders.setTop(border);
        borders.setInsideH(border);
        borders.setInsideV(border);
        table.getTblPr().setTblBorders(borders);
    }

    private void calculateColumnWidth(WordprocessingMLPackage wordMLPackage){
        int allowableTableWidth = wordMLPackage.
        getDocumentModel().
        getSections().
        get(0).
        getPageDimensions().
        getWritableWidthTwips();
        columnWidth = (allowableTableWidth-WIDTH_NAME_COLUMN-WIDTH_NUMBER_COLUMN)/namesCourses.size();
    }



}
