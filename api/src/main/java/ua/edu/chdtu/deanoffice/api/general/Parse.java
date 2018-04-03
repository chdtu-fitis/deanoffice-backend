package ua.edu.chdtu.deanoffice.api.general;

import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

public class Parse {
    public static Object toObject(Object source, Class type) {
        return new ModelMapper().map(source, type);
    }


    public static List toList(List source, Class type) {
        ModelMapper modelMapper = new ModelMapper();
        return (List) source.stream().map(el -> modelMapper.map(el, type)).collect(Collectors.toList());
    }
}
