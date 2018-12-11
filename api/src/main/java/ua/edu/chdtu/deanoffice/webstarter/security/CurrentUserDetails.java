package ua.edu.chdtu.deanoffice.webstarter.security;

import lombok.Getter;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import static java.util.Collections.emptyList;

@Getter
public class CurrentUserDetails extends org.springframework.security.core.userdetails.User {

    ApplicationUser currentUser;

    public CurrentUserDetails(ApplicationUser user) {
        super(user.getUsername(), user.getPassword(), emptyList());
        this.currentUser = user;
    }
}

