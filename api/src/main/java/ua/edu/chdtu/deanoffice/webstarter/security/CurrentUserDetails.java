package ua.edu.chdtu.deanoffice.webstarter.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

@Getter
public class CurrentUserDetails extends org.springframework.security.core.userdetails.User {

    ApplicationUser currentUser;

    public CurrentUserDetails(ApplicationUser user) {
        super(user.getUsername(), user.getPassword(), get(user));
        this.currentUser = user;
    }

    static List<GrantedAuthority> get(ApplicationUser user) {
        List<GrantedAuthority> a = new ArrayList<>();
        a.add(new SimpleGrantedAuthority(user.getRoles().get(0).getName()));
        return a;
    }
}

