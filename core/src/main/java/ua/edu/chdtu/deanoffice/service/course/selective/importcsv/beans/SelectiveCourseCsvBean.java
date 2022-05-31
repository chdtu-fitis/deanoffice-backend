package ua.edu.chdtu.deanoffice.service.course.selective.importcsv.beans;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelectiveCourseCsvBean {

    @CsvBindByName(column = "Семестр")
    private String semester;

    @CsvBindByName(column = "Цикл")
    private String trainingCycle;

    @CsvBindByName(column = "Галузь знань")
    private String fieldOfKnowledge;

    @CsvBindByName(column = "Назва")
    private String courseName;

    @CsvBindByName(column = "Опис")
    private String description;

    @CsvBindByName(column = "Кафедра")
    private String department;

    @CsvBindByName(column = "Викладач")
    private String teacher;
}
