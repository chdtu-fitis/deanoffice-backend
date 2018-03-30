package ua.edu.chdtu.deanoffice.api.general;

import java.net.URI;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;


public class Util {
    public static URI getNewResourceLocation(Integer id) {
        return fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
    }

    public static URI getNewResourceLocation(Object[] ids) {
        String id = Arrays.stream(ids).map(Object::toString).collect(Collectors.joining(","));
        return fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
    }
}
