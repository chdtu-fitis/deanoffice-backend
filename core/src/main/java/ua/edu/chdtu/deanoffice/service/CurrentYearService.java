package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.CurrentYear;
import ua.edu.chdtu.deanoffice.repository.CurrentYearRepository;

@Service
public class CurrentYearService {

    private CurrentYearRepository currentYearRepository;

    public CurrentYearService(CurrentYearRepository currentYearRepository) {
        this.currentYearRepository = currentYearRepository;
    }

    public CurrentYear get() {
        return currentYearRepository.getOne(1);
    }

    public int getYear() {
        return get().getCurrYear();
    }
}
