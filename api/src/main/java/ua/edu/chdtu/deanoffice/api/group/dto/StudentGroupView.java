package ua.edu.chdtu.deanoffice.api.group.dto;

import ua.edu.chdtu.deanoffice.api.general.GeneralView;

public class StudentGroupView {
    public interface BasicGroup {}

    public interface WithStudents extends BasicGroup, GeneralView.PersonFullName {}
    public interface GroupData extends BasicGroup {}
    public interface BasicCourse extends GeneralView.Named, GeneralView.PersonFullName{}
    public interface Course extends BasicCourse {}
}
