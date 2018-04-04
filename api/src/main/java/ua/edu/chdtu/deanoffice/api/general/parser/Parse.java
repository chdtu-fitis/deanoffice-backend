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
    public static Object toObject(Object source, Class type, MatchingStrategy matchingStrategy) {
        return createModelMapperWithStrategy(matchingStrategy).map(source, type);
    }


    public static List toList(List source, Class type) {
        return new ModelMapper().map(source, new ListParameterizedType(type));
    }
    public static List toList(List source, Class type, MatchingStrategy matchingStrategy) {
        return createModelMapperWithStrategy(matchingStrategy).map(source, new ListParameterizedType(type));
    }

    public static Set toSet(Set source, Class type) {
        return new ModelMapper().map(source, new SetParameterizedType(type));
    }
    public static Set toSet(Set sourse, Class type, MatchingStrategy matchingStrategy) {
        return createModelMapperWithStrategy(matchingStrategy).map(sourse, new SetParameterizedType(type));
    }

    private static ModelMapper createModelMapperWithStrategy(MatchingStrategy matchingStrategy) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(matchingStrategy);
        return modelMapper;
    }
}
