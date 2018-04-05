package ua.edu.chdtu.deanoffice.api.general.parser;

import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MatchingStrategy;
import ua.edu.chdtu.deanoffice.api.general.parser.type.ListParameterizedType;
import ua.edu.chdtu.deanoffice.api.general.parser.type.SetParameterizedType;

import java.util.List;
import java.util.Set;


public class Parser {
    public static Object parse(Object source, Class destination) {
        return new ModelMapper().map(source, destination);
    }
    public static Object parse(Object source, Class destination, MatchingStrategy matchingStrategy) {
        return createModelMapperWithStrategy(matchingStrategy).map(source, destination);
    }

    public static List parse(List source, Class destination) {
        return new ModelMapper().map(source, new ListParameterizedType(destination));
    }
    public static List parse(List source, Class destination, MatchingStrategy matchingStrategy) {
        return createModelMapperWithStrategy(matchingStrategy).map(source, new ListParameterizedType(destination));
    }

    public static Set parse(Set source, Class destination) {
        return new ModelMapper().map(source, new SetParameterizedType(destination));
    }
    public static Set parse(Set source, Class destination, MatchingStrategy matchingStrategy) {
        return createModelMapperWithStrategy(matchingStrategy).map(source, new SetParameterizedType(destination));
    }

    private static ModelMapper createModelMapperWithStrategy(MatchingStrategy matchingStrategy) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(matchingStrategy);
        return modelMapper;
    }
}
