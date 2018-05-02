package ua.edu.chdtu.deanoffice.api.general.mapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import ua.edu.chdtu.deanoffice.api.general.mapper.type.ListParameterizedType;
import ua.edu.chdtu.deanoffice.api.general.mapper.type.SetParameterizedType;

import java.util.List;
import java.util.Set;


public class Mapper {
    private static final MatchingStrategy STRICT_MATCHING_STRATEGY = MatchingStrategies.STRICT;
    private static final ModelMapper modelMapper = new ModelMapper();

    public static Object map(Object source, Class destination) {
        return modelMapper.map(source, destination);
    }

    public static List map(List source, Class destination) {
        return modelMapper.map(source, new ListParameterizedType(destination));
    }

    public static Set map(Set source, Class destination) {
        return modelMapper.map(source, new SetParameterizedType(destination));
    }

    public static Object map(Object source, Class destination, MatchingStrategy matchingStrategy) {
        return createModelMapperWithStrategy(matchingStrategy).map(source, destination);
    }

    public static List map(List source, Class destination, MatchingStrategy matchingStrategy) {
        return createModelMapperWithStrategy(matchingStrategy).map(source, new ListParameterizedType(destination));
    }

    public static Set map(Set source, Class destination, MatchingStrategy matchingStrategy) {
        return createModelMapperWithStrategy(matchingStrategy).map(source, new SetParameterizedType(destination));
    }

    public static Object strictMap(Object source, Class destination) {
        return map(source, destination, STRICT_MATCHING_STRATEGY);
    }

    public static List strictMap(List source, Class destination) {
        return map(source, destination, STRICT_MATCHING_STRATEGY);
    }

    public static Set strictMap(Set source, Class destination) {
        return map(source, destination, STRICT_MATCHING_STRATEGY);
    }

    private static ModelMapper createModelMapperWithStrategy(MatchingStrategy matchingStrategy) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(matchingStrategy);
        return modelMapper;
    }
}
