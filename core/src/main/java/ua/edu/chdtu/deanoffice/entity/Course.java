package ua.edu.chdtu.deanoffice.entity;

import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

@Entity
public class Course extends BaseEntity {
    @ManyToOne
    @JoinColumn(name="coursename_id")
    private CourseName courseName;
    @Column(name = "semester", nullable = false)
    private Integer semester;
    @ManyToOne
    @JoinColumn(name="kc_id")
    private KnowledgeControl knowledgeControl;
    @Column(name = "hours", nullable = false)
    private Integer hours;
    @Column(name = "credits", nullable = false, precision = 4, scale = 1)
    private BigDecimal credits;
    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<CourseForGroup> courseForGroups;

    public Set<CourseForGroup> getCourseForGroups() {
        return courseForGroups;
    }

    public void setCourseForGroups(Set<CourseForGroup> courseForGroups) {
        this.courseForGroups = courseForGroups;
    }

    public CourseName getCourseName() {
        return courseName;
    }

    public void setCourseName(CourseName courseName) {
        this.courseName = courseName;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public KnowledgeControl getKnowledgeControl() {
        return knowledgeControl;
    }

    public void setKnowledgeControl(KnowledgeControl knowledgeControl) {
        this.knowledgeControl = knowledgeControl;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public BigDecimal getCredits() {
        return credits;
    }

    public void setCredits(BigDecimal credits) {
        this.credits = credits;
    }
}
