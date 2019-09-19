package ua.edu.chdtu.deanoffice.webstarter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedMethods("HEAD", "GET", "POST", "PUT", "DELETE")
                        .allowedOrigins("http://localhost:4200","https://is.chdtu.edu.ua:4200", "http://ec2-63-33-192-197.eu-west-1.compute.amazonaws.com")
//                        .allowedOrigins("*")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}