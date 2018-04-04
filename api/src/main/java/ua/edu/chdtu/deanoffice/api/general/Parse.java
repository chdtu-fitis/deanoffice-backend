package ua.edu.chdtu.deanoffice.api.general;

import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MatchingStrategy;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;


public class Parse {
    public static Object toObject(Object source, Class type) {
        return new ModelMapper().map(source, type);
    }
    public static Object toObject(Object source, Class type, MatchingStrategy matchingStrategies) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(matchingStrategies);
        return modelMapper.map(source, type);
    }


    public static List toList(List source, Class type) {
        return new ModelMapper().map(source, new ListParameterizedType(type));
    }
    public static List toList(List source, Class type, MatchingStrategy matchingStrategies) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(matchingStrategies);
        return modelMapper.map(source, new ListParameterizedType(type));
    }
}

class ListParameterizedType implements ParameterizedType {

    private Type type;

    ListParameterizedType(Type type) {
        this.type = type;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return new Type[] {type};
    }

    @Override
    public Type getRawType() {
        return List.class;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }
}