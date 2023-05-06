package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Privilege;
import ua.edu.chdtu.deanoffice.repository.PrivilegeRepository;

@Service
public class PrivilegeService {
    private final PrivilegeRepository privilegeRepository;

    @Autowired
    public PrivilegeService(PrivilegeRepository privilegeRepository) {
        this.privilegeRepository = privilegeRepository;
    }

    public Privilege getPrivilegeByName(String name) {
        return privilegeRepository.findPrivilegeByName(name);
    }
}
