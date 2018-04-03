package ua.edu.chdtu.deanoffice.api.general;

import org.modelmapper.ModelMapper;

public class Parse {
    public static Object toObject(Object source, Class type) {
        return new ModelMapper().map(source, type);
    }
}
