package ua.edu.chdtu.deanoffice.webstarter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUserArgumentResolver;
import java.util.List;

@Component
public class WebConfig extends WebMvcConfigurerAdapter {
    @Autowired
    CurrentUserArgumentResolver currentUserArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(currentUserArgumentResolver);
    }
}
