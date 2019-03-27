package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentDegreeShortBean {
    String surname;
    String name;
    String patronymic;
    String diplomaNumber;
    String groupName;

    public StudentDegreeShortBean(String surname, String name,
                                  String patronymic, String diplomaNumber,
                                  String groupName) {
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.groupName = groupName;
        this.diplomaNumber = diplomaNumber;
    }
}
