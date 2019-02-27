package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public StudentTransfer studentTransfer(StudentTransfer studentTransfer){

        return null;
    }
}
