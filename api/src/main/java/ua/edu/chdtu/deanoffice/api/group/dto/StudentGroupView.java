package ua.edu.chdtu.deanoffice.api.group.dto;

import ua.edu.chdtu.deanoffice.api.general.dto.GeneralView;
import ua.edu.chdtu.deanoffice.api.specialization.dto.SpecializationView;

public class StudentGroupView {
    public interface Basic {}

    public interface Short {}

    public interface WithStudents extends Basic, GeneralView.PersonFullName {}

    public interface AllGroupData extends Basic, SpecializationView.WithDegreeAndSpeciality {}
}
