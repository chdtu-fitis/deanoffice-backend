package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.chdtu.deanoffice.entity.StudentTransfer;
import ua.edu.chdtu.deanoffice.repository.StudentTransferRepository;

@Service
public class StudentTransferService {
    private final StudentTransferRepository studentTransferRepository;

    @Autowired
    public StudentTransferService (
            StudentTransferRepository studentTransferRepository
    ){
        this.studentTransferRepository = studentTransferRepository;
    }

    public StudentTransfer save(StudentTransfer studentTransfer){
        return studentTransferRepository.save(studentTransfer);
    }

    @Transactional
    public  void updateSpecialization(Integer newSpecializationId, Integer studentDegreeId){
        studentTransferRepository.updateSpecialization(newSpecializationId,studentDegreeId);
    }
}
