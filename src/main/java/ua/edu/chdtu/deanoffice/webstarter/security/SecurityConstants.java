package ua.edu.chdtu.deanoffice.webstarter.security;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class SecurityConstants {
    public static String SECRET;
    public static long EXPIRATION_TIME;
    public static String TOKEN_PREFIX;
    public static String HEADER_STRING;

    public SecurityConstants(Environment environment) {
        SECRET = environment.getProperty("security.secret", String.class);
        EXPIRATION_TIME = environment.getProperty("security.expiration-time", long.class);
        TOKEN_PREFIX = environment.getProperty("security.token-prefix", String.class);
        HEADER_STRING = environment.getProperty("security.header-string", String.class);
    }
}
