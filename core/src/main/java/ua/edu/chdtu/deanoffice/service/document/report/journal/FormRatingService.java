package ua.edu.chdtu.deanoffice.service.document.report.journal;

import org.docx4j.UnitsOfMeasurement;
import org.docx4j.jaxb.Context;
import org.docx4j.model.properties.table.tr.TrHeight;
import org.docx4j.model.structure.PageSizePaper;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.Constants;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ua.edu.chdtu.deanoffice.util.LanguageUtil.transliterate;

@Service
public class FormRatingService {
    private static final int WIDTH_NUMBER_COLUMN = 400;
    private static final int WIDTH_NAME_COLUMN = 2800;
    private static final int HEIGHT_FIRST_ROW = 200;
    private static final int FONT_SIZE_14 = 28;
    private static final String FONT_FAMILY = "Times New Roman";
    private static final String KURS= "-kurs";
    private static final String JOURNAL= "journal-otsinok-";
    private ObjectFactory factory;

    @Autowired
    private DocumentIOService documentIOService;
    @Autowired
    private CourseForGroupService courseForGroupService;
    @Autowired
    private StudentGroupService groupService;

    public File formDocument(
        Integer degreeId,
        Integer year,
        int facultyId,
        String tuitionFormText,
        Integer semester
    ) throws Docx4JException, IOException {
        TuitionForm tuitionForm = TuitionForm.valueOf(tuitionFormText);
        List<StudentGroup> studentGroups = groupService.getGroupsByDegreeAndYearAndTuitionForm(degreeId, year, facultyId, tuitionForm);
        WordprocessingMLPackage resultTemplate = createDocument(semester, studentGroups);
        String fileName = transliterate(JOURNAL+year+KURS);
        return documentIOService.saveDocumentToTemp(resultTemplate, fileName, FileFormatEnum.DOCX);
    }

    private WordprocessingMLPackage createDocument(Integer semester, List<StudentGroup> studentGroups)
            throws Docx4JException {
        WordprocessingMLPackage wordMLPackage = createTables(semester,studentGroups);
        return wordMLPackage;
    }

    private WordprocessingMLPackage createTables(Integer semester, List<StudentGroup> studentGroups) throws InvalidFormatException {
        List<String> namesStudents = new ArrayList<>();
        List<String> namesCourses = new ArrayList<>();
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage(PageSizePaper.A4,true);
        for(StudentGroup studentGroup: studentGroups) {
            getDataFromDataBase(studentGroup, semester, namesStudents, namesCourses);
            if(namesCourses.size() != 0) {
                int columnWidth = calculateColumnWidth(wordMLPackage, namesCourses);
                factory = Context.getWmlObjectFactory();
                setTitle(wordMLPackage,studentGroup);
                Tbl table = factory.createTbl();
                addBorders(table);
                setHeaders(table,namesCourses,columnWidth);
                setRows(table,namesStudents,namesCourses,columnWidth);
                wordMLPackage.getMainDocumentPart().addObject(table);
            }
        }
        return wordMLPackage;
    }

    private void setTitle(WordprocessingMLPackage wordMLPackage,StudentGroup studentGroup) {
        wordMLPackage.getMainDocumentPart().addObject(getTextWithStyle(studentGroup.getName(),true));
    }

    private void setRows(Tbl table, List<String> namesStudents, List<String> namesCourses,int columnWidth) {
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

    private void setHeaders(Tbl table, List<String> namesCourses,int columnWidth) {
        Tr tr = factory.createTr();
        addTableCellWithWidth(tr, "", WIDTH_NUMBER_COLUMN);
        addTableCellWithWidth(tr, "", WIDTH_NAME_COLUMN);
        setRowHeight(tr,HEIGHT_FIRST_ROW);
        for (String nameCourse:namesCourses){
            Tc tableCell = factory.createTc();
            tableCell.getContent().add(getTextWithStyle(nameCourse,false));
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

    private P getTextWithStyle(String text,Boolean isBold) {
        P p = factory.createP();
        R r = factory.createR();
        RPr rPr = factory.createRPr();
        HpsMeasure size = new HpsMeasure();
        size.setVal(BigInteger.valueOf(FONT_SIZE_14));
        rPr.setSz(size);
        if(isBold){
            rPr.setB(new BooleanDefaultTrue());
        }
        RFonts rfonts = Context.getWmlObjectFactory().createRFonts();
        rfonts.setAscii(FONT_FAMILY);
        rfonts.setHAnsi(FONT_FAMILY);
        rPr.setRFonts(rfonts);
        r.setRPr(rPr);
        p.getContent().add(r);
        org.docx4j.wml.Text  t = factory.createText();
        t.setValue(text);
        r.getContent().add(t);
        return  p;
    }

    private void getDataFromDataBase(StudentGroup studentGroup, Integer semester, List<String> namesStudents, List<String> namesCourses) {
        namesStudents.clear();
        namesCourses.clear();
        List<CourseForGroup> courseForGroups = courseForGroupService.getCoursesForGroupBySemester(studentGroup.getId(), semester);
        List<StudentDegree> students = studentGroup.getStudentDegrees();
        prepareCourses(courseForGroups, namesCourses);
        prepareNames(students, namesStudents);
    }

    private void setRowHeight(Tr tr, int height){
        TrPr trPr = Context.getWmlObjectFactory().createTrPr();
        tr.setTrPr(trPr);
        TrHeight thr = new TrHeight();
        thr.set(trPr);
        int twip = UnitsOfMeasurement.pxToTwip(height);
        ((CTHeight)thr.getObject()).setVal(BigInteger.valueOf(twip));
    }

    private void addTableCellWithWidth(Tr row, String content, int width){
        Tc tableCell = factory.createTc();
        tableCell.getContent().add(getTextWithStyle(content,false));
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

    private int calculateColumnWidth(WordprocessingMLPackage wordMLPackage, List<String> namesCourses){
        int allowableTableWidth = wordMLPackage.
        getDocumentModel().
        getSections().
        get(0).
        getPageDimensions().
        getWritableWidthTwips();
        return  (allowableTableWidth-WIDTH_NAME_COLUMN-WIDTH_NUMBER_COLUMN)/namesCourses.size();
    }

    private void prepareCourses(List<CourseForGroup> courseForGroups, List<String> namesCourses){
        courseForGroups.sort(new ComparatorCourses());
        Map<Integer,String> retrenchments = createRetrenchments();
        for(CourseForGroup courseForGroup:courseForGroups) {
            Integer kcId = Integer.valueOf(courseForGroup.getCourse().getKnowledgeControl().getId());
            if (retrenchments.containsKey(kcId)){
                namesCourses.add(courseForGroup.getCourse().getCourseName().getName() + retrenchments.get(kcId));
            } else {
                namesCourses.add(courseForGroup.getCourse().getCourseName().getName());
            }
        }
    }

    private void prepareNames(List<StudentDegree> students, List<String> namesStudents){
        for(StudentDegree studentDegree:students){
            if(studentDegree.getPayment() == Payment.CONTRACT){
                namesStudents.add(studentDegree.getStudent().getInitialsUkr()+" (к)");
            } else {
                namesStudents.add(studentDegree.getStudent().getInitialsUkr());
            }
        }
    }

    private Map<Integer,String> createRetrenchments(){
        HashMap<Integer,String> retrenchments = new HashMap<>();
        retrenchments.put(Constants.EXAM," (ісп)");
        retrenchments.put(Constants.CREDIT," (з)");
        retrenchments.put(Constants.COURSEWORK,"(КР)");
        retrenchments.put(Constants.COURSE_PROJECT,"(КП)");
        retrenchments.put(Constants.DIFFERENTIATED_CREDIT," (д/з)");
        retrenchments.put(Constants.STATE_EXAM," (д/ісп)");
        retrenchments.put(Constants.ATTESTATION," (а)");
        retrenchments.put(Constants.INTERNSHIP," (П)");
        retrenchments.put(Constants.NON_GRADED_INTERNSHIP," (пз)");
        return retrenchments;
    }
}
