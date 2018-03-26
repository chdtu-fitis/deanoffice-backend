package ua.edu.chdtu.deanoffice.api.group.dto;

import ua.edu.chdtu.deanoffice.api.general.GeneralView;

public class StudentGroupView {
    public interface Basic {}

    public interface WithStudents extends Basic, GeneralView.PersonFullName {}
    public interface GroupData extends Basic {}
}
