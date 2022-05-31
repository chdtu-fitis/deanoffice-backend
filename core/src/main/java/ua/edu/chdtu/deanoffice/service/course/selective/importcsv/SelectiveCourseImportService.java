package ua.edu.chdtu.deanoffice.service.course.selective.importcsv;

import com.opencsv.bean.*;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.springframework.stereotype.*;
import ua.edu.chdtu.deanoffice.Constants;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.repository.*;
import ua.edu.chdtu.deanoffice.service.TeacherService;
import ua.edu.chdtu.deanoffice.service.course.selective.SelectiveCourseService;
import ua.edu.chdtu.deanoffice.service.course.selective.importcsv.beans.*;
import ua.edu.chdtu.deanoffice.repository.CourseNameRepository;
import ua.edu.chdtu.deanoffice.repository.KnowledgeControlRepository;
import ua.edu.chdtu.deanoffice.repository.TeacherRepository;
import ua.edu.chdtu.deanoffice.repository.DegreeRepository;
import ua.edu.chdtu.deanoffice.repository.DepartmentRepository;
import ua.edu.chdtu.deanoffice.repository.FieldOfKnowledgeRepository;
import ua.edu.chdtu.deanoffice.service.course.CourseService;
import ua.edu.chdtu.deanoffice.service.DegreeService;
import ua.edu.chdtu.deanoffice.service.DepartmentService;
import ua.edu.chdtu.deanoffice.service.FieldOfKnowledgeService;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;

@Service
public class SelectiveCourseImportService {

    private TeacherRepository teacherRepository;
    private DegreeRepository degreeRepository;
    private DepartmentRepository departmentRepository;
    private FieldOfKnowledgeRepository fieldOfKnowledgeRepository;
    private KnowledgeControlRepository knowledgeControlRepository;
    private CourseNameRepository courseNameRepository;

    private SelectiveCourseService selectiveCourseService;
    private FieldOfKnowledgeService fieldOfKnowledgeService;
    private DepartmentService departmentService;
    private DegreeService degreeService;
    private CourseService courseService;
    private TeacherService teacherService;

    private SelectiveCourseRepository selectiveCourseRepository;
    private CourseRepository courseRepository;

    static final String GENERAL_TRAINING_CYCLE_IN_CSV = "Загальної підготовки";

    // TODO: add params
    public SelectiveCourseImportService(SelectiveCourseRepository selectiveCourseRepository, CourseRepository courseRepository) {
        this.selectiveCourseRepository = selectiveCourseRepository;
        this.courseRepository = courseRepository;
    }

    public SelectiveCourseCsvReport formSelectiveCourseCsvImportReport(List<SelectiveCourseCsvBean> importedSelectiveCourses) {
        SelectiveCourseCsvReport csvImportReport = new SelectiveCourseCsvReport();
        List<CorrectSelectiveCourse> correctSelectiveCourses = new ArrayList<CorrectSelectiveCourse>();
        List<IncorrectSelectiveCourse> incorrectSelectiveCourses = new ArrayList<IncorrectSelectiveCourse>();

        for (SelectiveCourseCsvBean selectiveCourse : importedSelectiveCourses) {
            String alert = "";
            Integer semester = 0, fieldOfKnowledge = 0;
            TrainingCycle trainingCycle;
            try {
                semester = Integer.parseInt(selectiveCourse.getSemester());
                trainingCycle = selectiveCourse.getTrainingCycle().equals(GENERAL_TRAINING_CYCLE_IN_CSV) ? TrainingCycle.GENERAL : TrainingCycle.PROFESSIONAL;
                if (trainingCycle == TrainingCycle.PROFESSIONAL)
                    fieldOfKnowledge = Integer.parseInt(selectiveCourse.getFieldOfKnowledge());
                alert = checkSelectiveCourse(selectiveCourse, semester, fieldOfKnowledge, trainingCycle);

                if (alert.equals("")) {
                    correctSelectiveCourses.add(new CorrectSelectiveCourse(semester, trainingCycle, fieldOfKnowledge,
                            selectiveCourse.getCourseName(), selectiveCourse.getDescription(),
                            selectiveCourse.getDepartment(), selectiveCourse.getTeacher()));
                    continue;
                } else {
                    incorrectSelectiveCourses.add(new IncorrectSelectiveCourse(selectiveCourse, alert));
                }
            } catch (NumberFormatException numberFormatException) {
                alert += "Семестр або Галузь знань не є числом; ";
                incorrectSelectiveCourses.add(new IncorrectSelectiveCourse(selectiveCourse, alert));
                continue;
            } catch (IllegalArgumentException illegalArgumentException) {
                alert += "Галузь знань вказана некоректно";
                incorrectSelectiveCourses.add(new IncorrectSelectiveCourse(selectiveCourse, alert));
                continue;
            }
        }
        csvImportReport.setCorrectSelectiveCourses(correctSelectiveCourses);
        csvImportReport.setIncorrectSelectiveCourses(incorrectSelectiveCourses);
        return  csvImportReport;
    }

    private String checkSelectiveCourse(SelectiveCourseCsvBean selectiveCourse, Integer semester, Integer fieldOfKnowledge,
                                        TrainingCycle trainingCycle) {
        String alert = "";
        if (selectiveCourse.getTeacher().length() > 0 && selectiveCourse.getTeacher().length() < 7) {
            alert += "Викладач вказаний неправильно";
        } else if (selectiveCourse.getDepartment().length() < 1) {
            alert += "Кафедра вказана неправильно";
        } else if (selectiveCourse.getDescription().length() < 1) {
            alert += "Опис вказаний неправильно";
        } else if (selectiveCourse.getCourseName().length() < 2) {
            alert += "Назва дисципліни вказана неправильно";
        } else if (trainingCycle == TrainingCycle.PROFESSIONAL && (fieldOfKnowledge < 1 || fieldOfKnowledge > 30)) {
            alert += "Галузь знань вказана неправильно";
        } else if (semester < 1 || semester > 8) {
            alert += "Семестр вказаний неправильно";
        }
        return alert;
    }

    public List<SelectiveCourseCsvBean> getSelectiveCoursesFromStream(InputStream inputStream)  throws Exception {
        if (inputStream == null) {
            throw new Exception("Помилка читання файлу!");
        }
        List<SelectiveCourseCsvBean> importedSelectiveCourses = new CsvToBeanBuilder(new InputStreamReader(inputStream))
                .withSeparator(';')
                .withType(SelectiveCourseCsvBean.class)
                .build()
                .parse();
        for (SelectiveCourseCsvBean selectiveCourse : importedSelectiveCourses) {
            trimAllProperties(selectiveCourse);
        }
        return  importedSelectiveCourses;
    }

    private static void trimAllProperties(SelectiveCourseCsvBean selectedCourseCsvBean) {
        selectedCourseCsvBean.setCourseName(selectedCourseCsvBean.getCourseName().trim());
        selectedCourseCsvBean.setSemester(selectedCourseCsvBean.getSemester().trim());
        selectedCourseCsvBean.setDepartment(selectedCourseCsvBean.getDepartment().trim());
        selectedCourseCsvBean.setDescription(selectedCourseCsvBean.getDescription().trim());
        selectedCourseCsvBean.setTrainingCycle(selectedCourseCsvBean.getTrainingCycle().trim());
        selectedCourseCsvBean.setFieldOfKnowledge(selectedCourseCsvBean.getFieldOfKnowledge().trim());
        selectedCourseCsvBean.setTeacher(selectedCourseCsvBean.getTeacher().trim());
    }

    public List<String> createSelectiveCoursesFromImportData(List<SelectiveCourseImportBean> selectiveCourseImportBeans) {
        List<String> selectiveCoursesDataErrors = new ArrayList<>();
        int i = 0;
        for (SelectiveCourseImportBean importBean : selectiveCourseImportBeans) {
            String dataErrorDescription = "Рядок № " + ++i + "; ";

            CourseName courseName = getImportedCourseName(importBean.getCourseName());
            Course course = getImportedCourse(importBean.getSemester(), courseName, 120, 30);

            Teacher teacher = null;
            if (importBean.getTeacher() != null && !importBean.getTeacher().equals("")) {
                try {
                    teacher = getImportedTeacher(importBean.getTeacher(), importBean.getDepartment());
                } catch (SelectiveCourseImportException e) {
                    dataErrorDescription += e.getMessage() +";";
                    selectiveCoursesDataErrors.add(dataErrorDescription);
                    continue;
                }
            }

            FieldOfKnowledge fieldOfKnowledge = fieldOfKnowledgeService.getFieldOfKnowledgeByCode(importBean.getFieldOfKnowledge());

            try {
                Degree degree = getImportedDegree(importBean.getDegreeId());
                Department department = getImportedDepartment(importBean.getDepartment());

                SelectiveCourse selectiveCourse = new SelectiveCourse();
                selectiveCourse.setCourse(course);
                selectiveCourse.setTeacher(teacher);
                selectiveCourse.setDegree(degree);
                selectiveCourse.setDepartment(department);
                selectiveCourse.setFieldOfKnowledge(fieldOfKnowledge);
                selectiveCourse.setTrainingCycle(importBean.getTrainingCycle());
                selectiveCourse.setDescription(importBean.getDescription());
                selectiveCourse.setStudyYear(importBean.getStudyYear());
                selectiveCourseService.create(selectiveCourse);
            } catch (SelectiveCourseImportException e) {
                dataErrorDescription += e.getMessage() + ";";
                selectiveCoursesDataErrors.add(dataErrorDescription);
            }
        }
        return selectiveCoursesDataErrors;
    }

    private CourseName getImportedCourseName(String courseNameStr) {
        CourseName foundCourseName = courseNameRepository.findByName(courseNameStr);
        if (foundCourseName != null) {
            return foundCourseName;
        } else {
            CourseName courseName = new CourseName();
            courseName.setName(courseNameStr);
            return courseNameRepository.save(courseName);
        }
    }

    private Course getImportedCourse(int semester, CourseName courseName,
                                     int hours, int hoursPerCredit) {
        Course foundCourse = courseRepository.findOne(semester, Constants.CREDIT, courseName.getId(), hours, hoursPerCredit);
        if (foundCourse != null) {
            return foundCourse;
        } else {
            Course course = new Course();
            course.setCourseName(courseName);
            course.setSemester(semester);
            course.setHours(120);
            course.setKnowledgeControl(knowledgeControlRepository.findOne(Constants.CREDIT));
            course.setHoursPerCredit(30);
            course.setCredits(new BigDecimal(4));
            return courseRepository.save(course);
        }
    }

    private Teacher getImportedTeacher(String teacher, String department) throws SelectiveCourseImportException {
        Teacher foundTeacher = teacherService.getTeacherBySurnameAndInitialsAndDepartment(getTeacherSurnameAndInitials(teacher), department);
        if (foundTeacher != null) {
            return foundTeacher;
        } else {
            throw new SelectiveCourseImportException("Неправильно вказаний викладач");
        }
    }

    private Degree getImportedDegree(int degreeId) throws  SelectiveCourseImportException {
        Degree foundDegree = degreeService.getById(degreeId);
        if (foundDegree!= null) {
            return foundDegree;
        } else {
            throw new SelectiveCourseImportException("Освітній ступінь вказаний неправильно");
        }
    }

    private Department getImportedDepartment(String departmentAbbr) throws  SelectiveCourseImportException {
        if (Objects.equals(departmentAbbr, "")) {
            throw new SelectiveCourseImportException("Абревіатура кафедри відсутня");
        }
        Department foundDepartment = departmentService.getByAbbr(departmentAbbr);
        if (foundDepartment!= null) {
            return foundDepartment;
        } else {
            throw new SelectiveCourseImportException("Кафедра не знайдена");
        }
    }

    private String[] getTeacherSurnameAndInitials(String teacher) {
        String[] teacherSurnameAndInitials = {"", "", ""};

        if (teacher != null && !teacher.equals("")) {
            String[] splitName = teacher.split("[ \\t\\u202F\\u00A0]", 2);
            teacherSurnameAndInitials[0] = splitName[0];
            String[] teacherInitials = splitName[1].split("\\.");
            teacherSurnameAndInitials[1] = teacherInitials[0].trim();
            teacherSurnameAndInitials[2] = teacherInitials[1].trim();
            return teacherSurnameAndInitials;
        }
        return teacherSurnameAndInitials;
    }
}

class SelectiveCourseImportException extends Exception {
    public SelectiveCourseImportException(String errorMessage) {
        super(errorMessage);
    }
}
