package ua.edu.chdtu.deanoffice.webstarter.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.repository.ApplicationUserRepository;

import javax.annotation.PostConstruct;

@Component
@Scope(scopeName = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ApplicationUserService {

    @Autowired
    ApplicationUserRepository applicationUserRepository;

    ApplicationUser applicationUser;

    @PostConstruct
    public void init() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = (String) authentication.getPrincipal();
        this.applicationUser = applicationUserRepository.findByUsername(userName);
    }

    public ApplicationUser getApplicationUser() {
        return applicationUser;
    }
}
