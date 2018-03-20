package ua.edu.chdtu.deanoffice.api.student.dto;

import static ua.edu.chdtu.deanoffice.api.general.GeneralView.Named;

public class StudentDegreeViews {
    public interface DetailAndDegree {}
    public interface SearchAndSimple {}

    public interface Simple extends SearchAndSimple, Named {}
    public interface Detail extends Simple, DetailAndDegree {}
    public interface Degree extends Simple, DetailAndDegree {}
    public interface Personal extends Detail {}
    public interface Search extends SearchAndSimple {}
}
