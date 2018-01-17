package ua.edu.chdtu.deanoffice.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString

@Entity
@Table(name = "test")
public class TestEntity {
    //TODO cr: час позбавлятися від тестового функціоналу
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;
}
