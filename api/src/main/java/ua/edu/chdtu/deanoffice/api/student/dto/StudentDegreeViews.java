package ua.edu.chdtu.deanoffice.api.student.dto;

import static ua.edu.chdtu.deanoffice.api.general.GeneralView.Named;

public class StudentDegreeViews {
    public interface DetailAndDegree {}
    public interface SearchSimpleDegrees {}
    public interface SimpleAndDegrees {}

    public interface Simple extends SearchSimpleDegrees, SimpleAndDegrees, Named {}
    public interface Detail extends Simple, DetailAndDegree {}
    public interface Degree extends Simple, DetailAndDegree {}
    public interface Personal extends Detail {}
    public interface Search extends SearchSimpleDegrees {}
    public interface Degrees extends SearchSimpleDegrees, DetailAndDegree, SimpleAndDegrees{}
}
