package ua.edu.chdtu.deanoffice.service.document.report.journal.rating;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import org.docx4j.UnitsOfMeasurement;
import org.docx4j.jaxb.Context;
import org.docx4j.model.properties.table.tr.TrHeight;
import org.docx4j.model.structure.PageSizePaper;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.*;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.Constants;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.service.document.report.journal.ComparatorCourses;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ua.edu.chdtu.deanoffice.util.LanguageUtil.transliterate;

@Service
public class FormRatingDocxService extends FormRatingBase{
    private static final int TABLE_WIDTH = 13959;

    public File formDocument(
            Integer degreeId,
            Integer year,
            int facultyId,
            String tuitionFormText,
            Integer semester
    ) throws Docx4JException, IOException {
        TuitionForm tuitionForm = TuitionForm.valueOf(tuitionFormText);
        List<StudentGroup> studentGroups = groupService.getGroupsByDegreeAndYearAndTuitionForm(degreeId, year, facultyId, tuitionForm);
        WordprocessingMLPackage resultTemplate = createTables(semester, studentGroups);
        String fileName = transliterate(JOURNAL+year+KURS);
        return documentIOService.saveDocumentToTemp(resultTemplate, fileName, FileFormatEnum.DOCX);
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
            addTableCellWithWidth(tr,name,WIDTH_NAME_COLUMN);
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
        PPr ppr = factory.createPPr();
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
        PPrBase.Spacing sp = factory.createPPrBaseSpacing();
        sp.setAfter(BigInteger.ZERO);
        sp.setBefore(BigInteger.ZERO);
        sp.setLine(BigInteger.valueOf(200));
        sp.setLineRule(STLineSpacingRule.AUTO);
        rPr.setRFonts(rfonts);
        ppr.setSpacing(sp);
        r.setRPr(rPr);
        p.getContent().add(r);
        p.setPPr(ppr);
        org.docx4j.wml.Text  t = factory.createText();
        t.setValue(text);
        r.getContent().add(t);
        return  p;
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
        return  (TABLE_WIDTH-WIDTH_NAME_COLUMN-WIDTH_NUMBER_COLUMN)/namesCourses.size();
    }
}