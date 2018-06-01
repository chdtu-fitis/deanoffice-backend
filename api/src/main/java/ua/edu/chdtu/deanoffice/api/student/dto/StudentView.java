package ua.edu.chdtu.deanoffice.api.student.dto;

import ua.edu.chdtu.deanoffice.api.general.dto.GeneralView;
import ua.edu.chdtu.deanoffice.api.specialization.dto.SpecializationView;

public class StudentView {
    interface DetailAndDegree {}

    interface SearchSimpleDegrees {}

    interface SimpleAndDegrees extends GeneralView.Named {}

    interface WithActive {}

    interface WithSpecilization extends SpecializationView.WithDegreeAndSpeciality {}

    public interface Simple extends SearchSimpleDegrees, SimpleAndDegrees {}

    public interface Detail extends Simple, DetailAndDegree {}

    public interface Degree extends Simple, DetailAndDegree, WithSpecilization {}

    public interface Personal extends Detail {}

    public interface Search extends SearchSimpleDegrees {}

    public interface Degrees extends SearchSimpleDegrees, DetailAndDegree, SimpleAndDegrees, WithActive, WithSpecilization {}

    public interface Expel extends Simple, GeneralView.Named, WithActive {}

    public interface AcademicVacation extends Simple, GeneralView.Named, WithActive {}
}
