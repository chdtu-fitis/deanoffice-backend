package ua.edu.chdtu.deanoffice.api.general.mapper;

import org.modelmapper.ModelMapper;
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

    public static <T> T map(Object source, Class<T> destination) {
        return modelMapper.map(source, destination);
    }

    public static void map(Object source, Object destination){
        modelMapper.map(source, destination);
    }

    public static <T> List<T> map(List source, Class<T> destination) {
        return modelMapper.map(source, new ListParameterizedType(destination));
    }

    public static <T> Set<T> map(Set source, Class<T> destination) {
        return modelMapper.map(source, new SetParameterizedType(destination));
    }

    public static <T> T map(Object source, Class<T> destination, MatchingStrategy matchingStrategy) {
        return createModelMapperWithStrategy(matchingStrategy).map(source, destination);
    }

    public static <T> List<T> map(List source, Class<T> destination, MatchingStrategy matchingStrategy) {
        return createModelMapperWithStrategy(matchingStrategy).map(source, new ListParameterizedType(destination));
    }

    public static <T> Set<T> map(Set source, Class<T> destination, MatchingStrategy matchingStrategy) {
        return createModelMapperWithStrategy(matchingStrategy).map(source, new SetParameterizedType(destination));
    }

    public static void strictMap(Object source, Object destination) {
        createModelMapperWithStrategy(STRICT_MATCHING_STRATEGY).map(source, destination);
    }

    public static <T> T strictMap(Object source, Class<T> destination) {
        return map(source, destination, STRICT_MATCHING_STRATEGY);
    }

    public static <T> List<T> strictMap(List source, Class<T> destination) {
        return map(source, destination, STRICT_MATCHING_STRATEGY);
    }

    public static <T> Set<T> strictMap(Set source, Class<T> destination) {
        return map(source, destination, STRICT_MATCHING_STRATEGY);
    }

    private static ModelMapper createModelMapperWithStrategy(MatchingStrategy matchingStrategy) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(matchingStrategy);
        return modelMapper;
    }

    public static void mapStudentDegreeDTOToStudentDegreeSimpleFields(StudentDegreeDTO dto, StudentDegree entity) {
        entity.setActive(dto.isActive());
        entity.setAdmissionDate(dto.getAdmissionDate());
        entity.setAdmissionOrderNumber(dto.getAdmissionOrderNumber());
        entity.setAdmissionOrderDate(dto.getAdmissionOrderDate());
        entity.setContractDate(dto.getContractDate());
        entity.setContractNumber(dto.getContractNumber());
        entity.setDiplomaNumber(dto.getDiplomaNumber());
        entity.setDiplomaDate(dto.getDiplomaDate());
        entity.setDiplomaWithHonours(dto.isDiplomaWithHonours());
        entity.setPayment(dto.getPayment());
        entity.setPreviousDiplomaDate(dto.getPreviousDiplomaDate());
        entity.setPreviousDiplomaType(dto.getPreviousDiplomaType());
        entity.setPreviousDiplomaNumber(dto.getPreviousDiplomaNumber());
        entity.setPreviousDiplomaIssuedBy(dto.getPreviousDiplomaIssuedBy());
        entity.setPreviousDiplomaIssuedByEng(dto.getPreviousDiplomaIssuedByEng());
        entity.setProtocolDate(dto.getProtocolDate());
        entity.setProtocolNumber(dto.getProtocolNumber());
        entity.setRecordBookNumber(dto.getRecordBookNumber());
        entity.setStudentCardNumber(dto.getStudentCardNumber());
        entity.setSupplementDate(dto.getSupplementDate());
        entity.setSupplementNumber(dto.getSupplementNumber());
        entity.setThesisName(dto.getThesisName());
        entity.setThesisNameEng(dto.getThesisNameEng());
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
