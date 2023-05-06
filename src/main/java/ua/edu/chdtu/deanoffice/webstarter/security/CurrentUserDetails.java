package ua.edu.chdtu.deanoffice.webstarter.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.Role;

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
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (Role role: user.getRoles())
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
        return grantedAuthorities;
    }
}

