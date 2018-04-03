package ua.edu.chdtu.deanoffice.api.general;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
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

    public static List<NamedDTO> parseToNamedDTO(List data) {
        Type listType = new TypeToken<List<NamedDTO>>() {}.getType();
        return new ModelMapper().map(data, listType);
    }
}
