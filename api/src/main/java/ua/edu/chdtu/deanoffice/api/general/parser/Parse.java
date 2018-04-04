package ua.edu.chdtu.deanoffice.api.general.parser;

import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MatchingStrategy;
import ua.edu.chdtu.deanoffice.api.general.parser.parameterized_type.ListParameterizedType;
import ua.edu.chdtu.deanoffice.api.general.parser.parameterized_type.SetParameterizedType;

import java.util.List;
import java.util.Set;


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

    public static Set toSet(Set source, Class type) {
        return new ModelMapper().map(source, new SetParameterizedType(type));
    }
}
