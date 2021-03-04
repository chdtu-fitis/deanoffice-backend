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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EdeboStudentDataSynchronizationServiceCSV extends EdeboStudentDataSynchronizationServiceImpl {

    public EdeboStudentDataSynchronizationServiceCSV(StudentService studentService, StudentDegreeService studentDegreeService,
                                                      DegreeService degreeService, SpecialityService specialityService, SpecializationService specializationService,
                                                      FacultyService facultyService) {
        super(null, studentService, studentDegreeService, degreeService, specialityService, specializationService, facultyService);
    }

    protected List<ImportedData> getStudentDegreesFromStream(InputStream inputStream) {
        List<ImportedData> beans = new CsvToBeanBuilder(new InputStreamReader(inputStream))
                .withSeparator(';')
                .withType(ImportedData.class)
                .build()
                .parse();
        for (ImportedData bean : beans)
            trimAllProperties(bean);
        return beans;
    }

    private void trimAllProperties(ImportedData importedData) {
        try {
            Method[] methods = importedData.getClass().getMethods();
            Map<Method, Method> getAndSetMethods = new HashMap<>();
            for (Method method : methods) {
                String methodName = method.getName();
                if (methodName.length() > 3 && methodName.startsWith("get") && !methodName.equals("getClass") && Character.isUpperCase(methodName.charAt(3))) {
                    String propertyName = methodName.substring(3);
                    for (Method method1 : methods) {
                        if (method1.getName().equals("set" + propertyName)) {
                            getAndSetMethods.put(method, method1);
                            break;
                        }
                    }
                }
            }
            for (Map.Entry<Method, Method> entry : getAndSetMethods.entrySet()) {
                Method getMethod = entry.getKey();
                Method setMethod = entry.getValue();
                setMethod.invoke(importedData, ((String) getMethod.invoke(importedData)).trim());
            }
        } catch(IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
