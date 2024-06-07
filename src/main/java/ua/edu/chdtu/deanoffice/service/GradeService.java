package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.chdtu.deanoffice.Constants;

import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.EctsGrade;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesStudentDegrees;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.entity.TuitionTerm;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;
import ua.edu.chdtu.deanoffice.repository.CourseForGroupRepository;
import ua.edu.chdtu.deanoffice.repository.CourseRepository;
import ua.edu.chdtu.deanoffice.repository.GradeRepository;
import ua.edu.chdtu.deanoffice.repository.SelectiveCoursesStudentDegreesRepository;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;
import ua.edu.chdtu.deanoffice.util.GradeUtil;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GradeService {
    private static final Integer[] KNOWLEDGE_CONTROL_PART1 = {Constants.EXAM, Constants.CREDIT, Constants.DIFFERENTIATED_CREDIT};
    private static final Integer[] KNOWLEDGE_CONTROL_PART2 = {Constants.COURSEWORK, Constants.COURSE_PROJECT};
    private static final Integer[] KNOWLEDGE_CONTROL_PART3 = {Constants.INTERNSHIP, Constants.NON_GRADED_INTERNSHIP};
    private static final Integer[] KNOWLEDGE_CONTROL_PART4 = {Constants.ATTESTATION, Constants.STATE_EXAM};
    public static final String NEW_GRADED_VALUE = "new graded";

    private final GradeRepository gradeRepository;
    private final CourseRepository courseRepository;
    private final CourseForGroupRepository courseForGroupRepository;
    private final SelectiveCoursesStudentDegreesRepository selectiveCourseRepository;
    private final StudentDegreeRepository studentDegreeRepository;
    private KnowledgeControlService knowledgeControlService;

    @Autowired
    public GradeService(GradeRepository gradeRepository, CourseRepository courseRepository, CourseForGroupRepository courseForGroupRepository,
                        SelectiveCoursesStudentDegreesRepository selectiveCourseRepository, StudentDegreeRepository studentDegreeRepository, KnowledgeControlService knowledgeControlService) {
        this.gradeRepository = gradeRepository;
        this.courseRepository = courseRepository;
        this.courseForGroupRepository = courseForGroupRepository;
        this.selectiveCourseRepository = selectiveCourseRepository;
        this.studentDegreeRepository = studentDegreeRepository;
        this.knowledgeControlService = knowledgeControlService;
    }

    public List<List<Grade>> getGradesByStudentDegreeId(Integer studentDegreeId) {
        StudentDegree studentDegree = studentDegreeRepository.getById(studentDegreeId);
        List<Course> courses = courseRepository.getByGroupId(studentDegree.getStudentGroup().getId());
        List<List<Grade>> grades = new ArrayList<>();

        if (courses.isEmpty()) {
            grades.add(new ArrayList<>());
            grades.add(new ArrayList<>());
            grades.add(new ArrayList<>());
            grades.add(new ArrayList<>());
            return grades;
        }

        List<Integer> courseIds = courses.stream().map(BaseEntity::getId).collect(Collectors.toList());

        grades.add(getGrades(studentDegree, courseIds, Arrays.asList(KNOWLEDGE_CONTROL_PART1)));
        grades.add(getGrades(studentDegree, courseIds, Arrays.asList(KNOWLEDGE_CONTROL_PART2)));
        grades.add(getGrades(studentDegree, courseIds, Arrays.asList(KNOWLEDGE_CONTROL_PART3)));
        grades.add(getGrades(studentDegree, courseIds, Arrays.asList(KNOWLEDGE_CONTROL_PART4)));

        return grades;
    }

    public List<List<Grade>> getGradesByStudentDegreeIdWithSelective(Integer studentDegreeId) {
        StudentDegree studentDegree = studentDegreeRepository.getById(studentDegreeId);
        List<Course> courses = courseRepository.getByGroupId(studentDegree.getStudentGroup().getId());
        List<Course> selectiveCourses = selectiveCourseRepository.findByStudentDegreeIdAndActive(studentDegreeId, true)
                                            .stream()
                                            .map(SelectiveCoursesStudentDegrees::getSelectiveCourse)
                                            .map(SelectiveCourse::getCourse)
                                            .collect(Collectors.toList());

        List<Course> allCourses = new ArrayList<>(courses);

        StudentGroup studentGroup = studentDegree.getStudentGroup();

        if (studentGroup.getTuitionTerm()==TuitionTerm.SHORTENED) {
            Integer semesterShift = (studentGroup.getRealBeginYear() - studentGroup.getBeginYears()) * 2;
            selectiveCourses.forEach(course -> {course.setSemester(course.getSemester() - semesterShift);});
        }

        allCourses.addAll(selectiveCourses);

        List<List<Grade>> grades = new ArrayList<>();

        if (courses.isEmpty()) {
            grades.add(new ArrayList<>());
            grades.add(new ArrayList<>());
            grades.add(new ArrayList<>());
            grades.add(new ArrayList<>());
            return grades;
        }

        List<Integer> courseIds = allCourses.stream().map(BaseEntity::getId).collect(Collectors.toList());

        grades.add(getGrades(studentDegree, courseIds, Arrays.asList(KNOWLEDGE_CONTROL_PART1)));
        grades.add(getGrades(studentDegree, courseIds, Arrays.asList(KNOWLEDGE_CONTROL_PART2)));
        grades.add(getGrades(studentDegree, courseIds, Arrays.asList(KNOWLEDGE_CONTROL_PART3)));
        grades.add(getGrades(studentDegree, courseIds, Arrays.asList(KNOWLEDGE_CONTROL_PART4)));

        return grades;
    }

    private List<Grade> getGrades(StudentDegree studentDegree,
                                  List<Integer> courseIds,
                                  List<Integer> knowledgeControlTypes) {
        return gradeRepository.getByStudentDegreeIdAndCoursesAndKCTypes(studentDegree.getId(),
                courseIds,
                knowledgeControlTypes);
    }

    public List<List<Grade>> filterOnlyPositiveGrades(List<List<Grade>> grades) {
        for (int i = 0; i < grades.size(); i++) {
            List<Grade> oneSortGrades = grades.get(i);
            if (oneSortGrades != null) {
                List<Grade> filteredGrades = oneSortGrades.stream()
                        .filter(grade -> grade.getPoints() != null && grade.getPoints() >= Constants.MINIMAL_SATISFACTORY_POINTS)
                        .collect(Collectors.toList());
                grades.set(i, filteredGrades);
            }
        }
        return grades;
    }

    @Transactional
    public void setAcademicDifferenceByGradeIds(Map<Boolean, List<Integer>> academicDifferenceAndGradeIds) {
        List<Integer> gradesIdsWithAcademicDifference = academicDifferenceAndGradeIds.get(true);
        if (gradesIdsWithAcademicDifference != null && !gradesIdsWithAcademicDifference.isEmpty()) {
            gradeRepository.updateAcademicDifference(true, gradesIdsWithAcademicDifference);
        }
        List<Integer> gradesIdsWithoutAcademicDifference = academicDifferenceAndGradeIds.get(false);
        if (gradesIdsWithoutAcademicDifference != null && !gradesIdsWithoutAcademicDifference.isEmpty()) {
            gradeRepository.updateAcademicDifference(false, gradesIdsWithoutAcademicDifference);
        }
    }

    public List<Grade> setGradeAndEcts(List<Grade> grades) {
        grades.forEach(grade -> {
            grade.setEcts(EctsGrade.getEctsGrade(grade.getPoints()));
            Course course = courseRepository.findById(grade.getCourse().getId()).get();
            grade.setGrade(EctsGrade.getGrade(grade.getPoints(), course.getKnowledgeControl().isGraded()));
        });
        return grades;
    }

    public List<Grade> getAllDifferentiatedGrades(Integer studentDegreeId) {
        StudentDegree studentDegree = studentDegreeRepository.getById(studentDegreeId);
        List<Integer> knowledgeControlTypes = Arrays.asList(Constants.EXAM, Constants.DIFFERENTIATED_CREDIT, Constants.COURSEWORK, Constants.COURSE_PROJECT,
                Constants.ATTESTATION, Constants.INTERNSHIP, Constants.STATE_EXAM);
        List<Integer> courseIds = courseRepository.getByGroupId(studentDegree.getStudentGroup().getId())
                .stream().map(BaseEntity::getId).collect(Collectors.toList());
        return new ArrayList<>(getGrades(studentDegree, courseIds, knowledgeControlTypes));
    }


    public List<Grade> getGradesForStudents(List<Integer> studentsIds, List<Integer> courseIds) {
        if (studentsIds.isEmpty() || courseIds.isEmpty()) return new ArrayList<>();
        return gradeRepository.findGradesByCourseAndBySemesterForStudents(studentsIds, courseIds);
    }

    public List<Grade> insertGrades(List<Grade> grades) {
        return gradeRepository.saveAll(grades);
    }

    public List<Grade> getGradeForStudentAndSemester(Integer studentDegreeId, Integer semester) {
        return gradeRepository.getByStudentDegreeIdAndSemester(studentDegreeId, semester);
    }

    public List<Grade> getGradesByCourseAndGroup(int courseId, int groupId) {
        return gradeRepository.findByCourseAndGroup(courseId, groupId);
    }

    @Transactional
    public void saveGradesByCourse(Course course, List<Grade> grades, Map<String, Boolean> gradedDefinition) {
        List<Grade> gradesToDelete = new ArrayList<>();
        for (Grade grade : grades) {
            Grade possibleOldGrade = gradeRepository.getByStudentDegreeIdAndCourseId(grade.getStudentDegree().getId(), course.getId());
            if (possibleOldGrade != null) {
                gradesToDelete.add(grade);
                synchronizeGrades(grade, possibleOldGrade);
                gradeRepository.save(possibleOldGrade);
                continue;
            }
            grade.setCourse(course);
            Boolean newGradedValue = gradedDefinition.get(NEW_GRADED_VALUE);
            if (newGradedValue != null && grade.getPoints() != null) {
                if (newGradedValue) {
                    grade.setGrade(GradeUtil.getGradeFromPoints(grade.getPoints()));
                } else {
                    grade.setGrade(GradeUtil.getCreditFromPoints(grade.getPoints()));
                }
            }
            gradeRepository.save(grade);
        }
        gradeRepository.deleteAll(gradesToDelete);
    }

    private void synchronizeGrades(Grade gradeFrom, Grade gradeTo) {
        gradeTo.setPoints(gradeFrom.getPoints());
        gradeTo.setEcts(EctsGrade.getEctsGrade(gradeFrom.getPoints()));
        gradeTo.setAcademicDifference(gradeFrom.isAcademicDifference());
        gradeTo.setOnTime(gradeFrom.getOnTime());
        gradeTo.setGrade(EctsGrade.getGrade(gradeFrom.getPoints(), gradeTo.getCourse().getKnowledgeControl().isGraded()));
    }

    public void deleteGradeById(Integer gradeId) {
        gradeRepository.deleteById(gradeId);
    }

    public HashMap<Integer, List<Course>> getAcademicStudentDebtsByGroupId(Integer groupId) {
        List<StudentDegree> studentDegrees = studentDegreeRepository.findStudentDegreeByStudentGroupIdAndActive(groupId, true);
        HashMap<Integer, List<Course>> debts = new HashMap<>();
        for (StudentDegree sd: studentDegrees) {
            List<List<Grade>> grades = getGradesByStudentDegreeIdWithSelective(sd.getId());
            List<Grade> flatGrades = grades.stream()
                    .flatMap(List::stream)
                    .toList();
            for (Grade grade : flatGrades) {
                boolean isPointsPassable = !(grade.getPoints() == null || grade.getPoints() < 60);
                if (isPointsPassable) {
                    continue;
                }
                debts.computeIfAbsent(sd.getId(), g -> new ArrayList<>()).add(grade.getCourse());
            }
            debts.get(sd.getId()).sort(Comparator.comparingInt(Course::getSemester));
        }
        return debts;
    }

    @Transactional
    public void updateNationalGradeByCourseIdAndGradedFalse(int courseId) {
        List<Integer> studentDegreeIds = gradeRepository.getStudentDegreeIdByCourseId(courseId);
        for (Integer studentDegreeId : studentDegreeIds) {
            gradeRepository.updateGradeByCourseIdAndGradedFalse(courseId, studentDegreeId);
        }
    }

    @Transactional
    public void updateNationalGradeByCourseIdAndGradedTrue(int courseId) {
        List<Integer> studentDegreeIds = gradeRepository.getStudentDegreeIdByCourseId(courseId);
        for (Integer studentDegreeId : studentDegreeIds) {
            gradeRepository.updateGradeByCourseIdAndGradedTrue(courseId, studentDegreeId);
        }
    }

    public Map<String, Boolean> evaluateGradedChange(int oldKnowledgeControlId, int newKnowledgeControlId) {
        Map<String, Boolean> gradeDefinition = new HashMap<>();
        if (oldKnowledgeControlId != newKnowledgeControlId) {
            boolean oldGraded = knowledgeControlService.getGradedByKnowledgeControlId(oldKnowledgeControlId);
            boolean newGraded = knowledgeControlService.getGradedByKnowledgeControlId(newKnowledgeControlId);
            if (oldGraded != newGraded) {
                gradeDefinition.put(NEW_GRADED_VALUE, newGraded);
            }
        }
        return gradeDefinition;
    }

    public List<StudentDegree> filterStudentByGrade(List<StudentDegree> studentDegrees, CourseForGroup courseForGroup, boolean isGoodMark) {
        List<StudentDegree> studentDegreeResult = new ArrayList<>();
        studentDegrees.forEach(studentDegree -> {
            boolean isStudentHasGoodMark = gradeRepository.isStudentHaveGoodMarkFromCourse(
                    studentDegree.getId(),
                    studentDegree.getStudentGroup().getId(),
                    courseForGroup.getCourse().getId()
            );
            if (isStudentHasGoodMark == isGoodMark)
                studentDegreeResult.add(studentDegree);
        });
        return studentDegreeResult;
    }

    public List<Grade> getGradesByStudetDegreeIdAndKCTypes(Integer studentDegreeId, List<Integer> knowledgeControlsIds) {
        StudentDegree sd = studentDegreeRepository.findById(studentDegreeId).get();
        List<CourseForGroup> coursesForGroups = courseForGroupRepository.findAllByStudentGroupId(sd.getStudentGroup().getId());
        List<Integer> courseIds = coursesForGroups.stream().map(cfg -> cfg.getCourse().getId()).collect(Collectors.toList());
        return gradeRepository.getByStudentDegreeIdAndCoursesIdsKCTypes(studentDegreeId, courseIds, knowledgeControlsIds);
    }
}
