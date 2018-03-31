package ua.edu.chdtu.deanoffice.api.course.dto;

import ua.edu.chdtu.deanoffice.api.general.GeneralView;

public class CourseForGroupView {
    public interface Basic extends GeneralView.Named, GeneralView.PersonFullName{}
    public interface Course extends Basic {}
}
