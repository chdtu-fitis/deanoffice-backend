package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.CourseName;
import ua.edu.chdtu.deanoffice.entity.DegreeEnum;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameEntity;
import ua.edu.chdtu.deanoffice.repository.CourseNameRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Service
public class CourseNameService {
    private final CurrentYearService currentYearService;
    private final CourseNameRepository courseNameRepository;

    public CourseNameService(CourseNameRepository courseNameRepository, CurrentYearService currentYearService) {
        this.courseNameRepository = courseNameRepository;
        this.currentYearService = currentYearService;
    }

    public List<CourseName> getCourseNames() {
        return this.courseNameRepository.findAll();
    }

    public CourseName saveCourseName(CourseName courseName) {
        return this.courseNameRepository.save(courseName);
    }

    public CourseName getCourseNameByName(String name) {
        return this.courseNameRepository.findByName(name);
    }

    public List<CourseName> getUnusedCoursesNames() {
        return courseNameRepository.findUnusedCoursesNames();
    }

    public void deleteCoursesNamesByIds(List<Integer> ids) {
        courseNameRepository.deleteCourseNameByIdIn(ids);
    }

    public void deleteCourseNameById(int id) {
        courseNameRepository.deleteCourseNameById(id);
    }

    public CourseName getCourseNameById(int id) {
        return courseNameRepository.findCourseNameById(id);
    }

    public Map<CourseName, List<CourseName>> getSimilarCoursesNames() {
        List<CourseName> courseNames = getCourseNames();
        TreeMap<CourseName, List<CourseName>> result = new TreeMap<>(Comparator.comparing(NameEntity::getName));
        HashSet<CourseName> repeatedValues = new HashSet<>();
        int i = 0;
        for (CourseName globalCourseName : courseNames) {
            String globalName = globalCourseName.getName();
            if (repeatedValues.contains(globalCourseName)) {
                i++;
                continue;
            }
            List<CourseName> similars = new ArrayList<>();
            Map<Character, Integer> globalMap = createLettersMap(globalName.toCharArray());
            for (int j = i + 1; j < courseNames.size(); j++) {
                CourseName localCourseName = courseNames.get(j);
                String localName = localCourseName.getName();
                Map<Character, Integer> localMap = createLettersMap(localName.toCharArray());
                if (isSimilar(localMap, globalMap)) {
                    similars.add(localCourseName);
                }
            }
            if (!similars.isEmpty()) {
                result.put(globalCourseName, similars);
                repeatedValues.addAll(similars);
            }
            i++;
        }
        return result;
    }

    private Boolean isSimilar(Map<Character, Integer> localMap, Map<Character, Integer> globalMap) {
        int differentLettersCount = 0;
        for (Character key : globalMap.keySet()) {
            if (localMap.containsKey(key)) {
                differentLettersCount += Math.abs(globalMap.get(key) - localMap.get(key));
                localMap.remove(key);
            } else {
                differentLettersCount += globalMap.get(key);
            }
            if (differentLettersCount > 3) {
                return false;
            }
        }
        for (Character key : localMap.keySet()) {
            differentLettersCount += localMap.get(key);
            if (differentLettersCount > 3) {
                return false;
            }
        }
        return true;
    }

    private Map<Character, Integer> createLettersMap(char[] characters) {
        HashMap<Character, Integer> result = new HashMap<>();
        for (Character letter : characters) {
            if (result.containsKey(letter)) {
                Integer integer = result.get(letter);
                result.put(letter, integer + 1);
            } else {
                result.put(letter, 1);
            }
        }
        return result;
    }

    public Map<String, String> getGraduatesCoursesWithEmptyEngName(int facultyId, int degreeId) {
        int year = currentYearService.getYear();
        List<Object[]> coursesName = courseNameRepository.findAllForGraduatesWithNoEnglishName(year, facultyId, degreeId);

        int shiftYear = getShiftYearByDegreeId(degreeId);
        List<Object[]> selectiveCoursesName = courseNameRepository.findAllSelectiveForGraduatesWithNoEnglishName(year, shiftYear, degreeId);

        coursesName.addAll(selectiveCoursesName);
        return coursesName
                .stream()
                .collect(Collectors.toMap(cn -> (String)cn[0], cn -> cn[1] + "; ", (e1, e2) -> (e1 + e2), LinkedHashMap::new));
    }

    public int getShiftYearByDegreeId(int degreeId) {
        int shiftYear = 4;
        if (degreeId == DegreeEnum.BACHELOR.getId()) shiftYear = 4;
        if (degreeId == DegreeEnum.SPECIALIST.getId()) shiftYear = 1;
        if (degreeId == DegreeEnum.MASTER.getId()) shiftYear = 2;
        if (degreeId == DegreeEnum.PHD.getId()) shiftYear = 4;

        return shiftYear;
    }
}
