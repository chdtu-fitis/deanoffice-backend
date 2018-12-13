package ua.edu.chdtu.deanoffice.api.general.mapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import ua.edu.chdtu.deanoffice.api.general.mapper.type.ListParameterizedType;
import ua.edu.chdtu.deanoffice.api.general.mapper.type.SetParameterizedType;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentDegreeDTO;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;

import java.util.List;
import java.util.Set;

public class Mapper {
    private static final MatchingStrategy STRICT_MATCHING_STRATEGY = MatchingStrategies.STRICT;
    private static final ModelMapper modelMapper = new ModelMapper();

    public static Object map(Object source, Class destination) {
        return modelMapper.map(source, destination);
    }

    public static void map(Object source, Object destination){
        modelMapper.map(source, destination);
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

    public static void strictMap(Object source, Object destination) {
        createModelMapperWithStrategy(STRICT_MATCHING_STRATEGY).map(source, destination);
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

    public static void mapStudentDegreeDTOToStudentDegreeSimpleFields(StudentDegreeDTO dto, StudentDegree entity) {
        modelMapper.getConfiguration().setMatchingStrategy(STRICT_MATCHING_STRATEGY);
        modelMapper.addMappings(new PropertyMap<StudentDegreeDTO, StudentDegree>() {
            @Override
            protected void configure() {
                skip(destination.getStudentPreviousUniversities());
                skip(destination.getSpecialization());
                skip(destination.getStudent());
                skip(destination.getStudentGroup());
            }
        });
        modelMapper.map(dto, entity);
//        return modelMapper.createTypeMap(StudentDegreeDTO.class, StudentDegree.class)
//                .addMapping(StudentDegreeDTO::getRecordBookNumber, StudentDegree::setRecordBookNumber)
//                .addMapping(StudentDegreeDTO::getDiplomaNumber, StudentDegree::setDiplomaNumber)
//                .addMapping(StudentDegreeDTO::getDiplomaDate, StudentDegree::setDiplomaDate)
//                .addMapping(StudentDegreeDTO::getSupplementNumber, StudentDegree::setSupplementNumber)
//                .addMapping(StudentDegreeDTO::getSupplementDate, StudentDegree::setSupplementDate)
//                .addMapping(StudentDegreeDTO::getThesisName, StudentDegree::setThesisName)
//                .addMapping(StudentDegreeDTO::getThesisNameEng, StudentDegree::setThesisNameEng)
//                .addMapping(StudentDegreeDTO::getProtocolNumber, StudentDegree::setProtocolNumber)
//                .addMapping(StudentDegreeDTO::getProtocolDate, StudentDegree::setProtocolDate)
//                .addMapping(StudentDegreeDTO::getPreviousDiplomaType, StudentDegree::setPreviousDiplomaType)
//                .addMapping(StudentDegreeDTO::getPreviousDiplomaNumber, StudentDegree::setPreviousDiplomaNumber)
//                .addMapping(StudentDegreeDTO::getPreviousDiplomaDate, StudentDegree::setPreviousDiplomaDate)
//                .addMapping(StudentDegreeDTO::getPayment, StudentDegree::setPayment)
//                .addMapping(StudentDegreeDTO::getStudentCardNumber, StudentDegree::setStudentCardNumber)
//                .addMapping(StudentDegreeDTO::getAdmissionOrderDate, StudentDegree::setAdmissionOrderDate)
//                .addMapping(StudentDegreeDTO::getAdmissionOrderNumber, StudentDegree::setAdmissionOrderNumber)
//                .addMapping(StudentDegreeDTO::getContractDate, StudentDegree::setContractDate)
//                .addMapping(StudentDegreeDTO::getContractNumber, StudentDegree::setContractNumber)
//                .addMapping(StudentDegreeDTO::getPreviousDiplomaIssuedBy, StudentDegree::setPreviousDiplomaIssuedBy)
//                .addMapping(StudentDegreeDTO::getPreviousDiplomaIssuedByEng, StudentDegree::setPreviousDiplomaIssuedByEng)
//                .addMapping(StudentDegreeDTO::getAdmissionDate, StudentDegree::setAdmissionDate)
//                .addMapping(StudentDegreeDTO::isDiplomaWithHonours, StudentDegree::setDiplomaWithHonours);
    }

}
