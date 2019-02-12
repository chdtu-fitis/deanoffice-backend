package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.RenewedExpelledStudent;
import ua.edu.chdtu.deanoffice.repository.RenewedExpelledStudentRepository;

@Service
public class RenewedExpelledStudentService {
    private final RenewedExpelledStudentRepository renewedExpelledStudentRepository;

    @Autowired
    public RenewedExpelledStudentService(
            RenewedExpelledStudentRepository renewedExpelledStudentRepository
    ) {
        this.renewedExpelledStudentRepository = renewedExpelledStudentRepository;
    }

    public RenewedExpelledStudent getRenewedStudentByExpelledId(Integer expelId){
        return renewedExpelledStudentRepository.findRenewedStudentByExpelId(expelId);
    }
}
