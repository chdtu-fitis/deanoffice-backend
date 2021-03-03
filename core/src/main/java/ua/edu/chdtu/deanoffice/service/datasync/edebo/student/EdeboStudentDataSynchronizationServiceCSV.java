package ua.edu.chdtu.deanoffice.service.datasync.edebo.student;

import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.service.DegreeService;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.SpecialityService;
import ua.edu.chdtu.deanoffice.service.SpecializationService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentService;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class EdeboStudentDataSynchronizationServiceCSV extends EdeboStudentDataSynchronizationServiceImpl {

    public EdeboStudentDataSynchronizationServiceCSV(StudentService studentService, StudentDegreeService studentDegreeService,
                                                      DegreeService degreeService, SpecialityService specialityService, SpecializationService specializationService,
                                                      FacultyService facultyService) {
        super(null, studentService, studentDegreeService, degreeService, specialityService, specializationService, facultyService);
    }

    protected List<ImportedData> getStudentDegreesFromStream(InputStream inputStream) throws IOException {
        List<ImportedData> beans = new CsvToBeanBuilder(new InputStreamReader(inputStream))
                .withSeparator(';')
                .withType(ImportedData.class)
                .build()
                .parse();
        return beans;
    }
}
