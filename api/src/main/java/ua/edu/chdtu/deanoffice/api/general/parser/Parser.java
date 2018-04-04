package ua.edu.chdtu.deanoffice.api.general.parser;

import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MatchingStrategy;
import ua.edu.chdtu.deanoffice.api.general.parser.parameterized_type.ListParameterizedType;
import ua.edu.chdtu.deanoffice.api.general.parser.parameterized_type.SetParameterizedType;

import java.util.List;
import java.util.Set;


public class Parser {
    public static Object parse(Object source, Class destinationClass) {
        return new ModelMapper().map(source, destinationClass);
    }
    public static Object parse(Object source, Class destinationClass, MatchingStrategy matchingStrategy) {
        return createModelMapperWithStrategy(matchingStrategy).map(source, destinationClass);
    }


    public static List parse(List source, Class destinationClass) {
        return new ModelMapper().map(source, new ListParameterizedType(destinationClass));
    }
    public static List parse(List source, Class destinationClass, MatchingStrategy matchingStrategy) {
        return createModelMapperWithStrategy(matchingStrategy).map(source, new ListParameterizedType(destinationClass));
    }

    public static Set parse(Set source, Class destinationClass) {
        return new ModelMapper().map(source, new SetParameterizedType(destinationClass));
    }
    public static Set parse(Set source, Class destinationClass, MatchingStrategy matchingStrategy) {
        return createModelMapperWithStrategy(matchingStrategy).map(source, new SetParameterizedType(destinationClass));
    }

    private static ModelMapper createModelMapperWithStrategy(MatchingStrategy matchingStrategy) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(matchingStrategy);
        return modelMapper;
    }
}
