package ua.edu.chdtu.deanoffice.service.datasync.edebo.student;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.springframework.stereotype.Service;
import org.xlsx4j.org.apache.poi.ss.usermodel.DataFormatter;
import org.xlsx4j.sml.Cell;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.Worksheet;
import ua.edu.chdtu.deanoffice.service.DegreeService;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.SpecialityService;
import ua.edu.chdtu.deanoffice.service.SpecializationService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentService;
import ua.edu.chdtu.deanoffice.util.StringUtil;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class EdeboStudentDataSynchronizationServiceCSV extends EdeboStudentDataSynchronizationServiceImpl{

    public EdeboStudentDataSynchronizationServiceCSV(StudentService studentService, StudentDegreeService studentDegreeService,
                                                      DegreeService degreeService, SpecialityService specialityService, SpecializationService specializationService,
                                                      FacultyService facultyService) {
        super(null, studentService, studentDegreeService, degreeService, specialityService, specializationService, facultyService);
    }

    protected List<ImportedData> getStudentDegreesFromStream(InputStream csvInputStream) throws IOException {
        List<ImportedData> beans = new CsvToBeanBuilder(new InputStreamReader(csvInputStream))
                .withSeparator(';')
                .withType(ImportedData.class)
                .build()
                .parse();
        return beans;
    }


}
