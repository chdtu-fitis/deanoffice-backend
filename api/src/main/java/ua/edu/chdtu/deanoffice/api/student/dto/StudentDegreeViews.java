package ua.edu.chdtu.deanoffice.api.student.dto;

import ua.edu.chdtu.deanoffice.api.general.dto.GeneralView;

public class StudentDegreeViews {
    public interface DetailAndDegree {}
    public interface SearchSimpleDegrees {}
    public interface SimpleAndDegrees extends GeneralView.Named {}

    public interface Simple extends SearchSimpleDegrees, SimpleAndDegrees {}
    public interface Detail extends Simple, DetailAndDegree {}
    public interface Degree extends Simple, DetailAndDegree {}
    public interface Personal extends Detail {}
    public interface Search extends SearchSimpleDegrees {}
    public interface Degrees extends SearchSimpleDegrees, DetailAndDegree, SimpleAndDegrees{}
    public interface Expel extends Simple, GeneralView.OrderReason {}
}
