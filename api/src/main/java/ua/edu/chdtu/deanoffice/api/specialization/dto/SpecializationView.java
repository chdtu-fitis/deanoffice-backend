package ua.edu.chdtu.deanoffice.api.specialization.dto;

import ua.edu.chdtu.deanoffice.api.general.dto.GeneralView;
import ua.edu.chdtu.deanoffice.api.speciality.dto.SpecialityView;

public class SpecializationView {
    public interface Basic {}

    public interface AcquiredCompetencies {}

    public interface Extended extends WithDegreeAndSpeciality {}

    public interface WithDegreeAndSpeciality extends Basic, SpecialityView.Basic, GeneralView.Named {}

    public interface AcquiredCompetenciesUkr extends AcquiredCompetencies {}

    public interface AcquiredCompetenciesEng extends AcquiredCompetencies {}
}
