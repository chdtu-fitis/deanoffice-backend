package ua.edu.chdtu.deanoffice.api.student.dto;

public class StudentDegreeViews {
    public interface DetailAndDegree {}

    public interface Simple {}
    public interface Detail extends Simple, DetailAndDegree {}
    public interface Degree extends Simple, DetailAndDegree {}
}
