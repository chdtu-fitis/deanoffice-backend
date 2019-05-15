package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.Collection;

@Entity
@Getter
@Setter // Насколько уместный в данном классе сеттер?
public class ApplicationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String password;
    private String firstName;
    private String lastName;
    private String username;

    @ManyToOne
    private Faculty faculty;

    @ManyToMany(mappedBy = "users_roles")
    private Collection<Role> roles;
}
