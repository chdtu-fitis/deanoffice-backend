package ua.edu.chdtu.deanoffice.util;

import java.net.URI;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;


public class Util {
    public static URI getNewResourceLocation(Integer id) {
        return fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
    }
}
