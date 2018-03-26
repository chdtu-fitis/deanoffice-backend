package ua.edu.chdtu.deanoffice.api.specialization.dto;

import ua.edu.chdtu.deanoffice.api.general.dto.GeneralView;
import ua.edu.chdtu.deanoffice.api.speciality.dto.SpecialityView;

public class SpecializationView {
    public interface Basic extends SpecialityView.Basic, GeneralView.BasicDegree {}
}
