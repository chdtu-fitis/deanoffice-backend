package ua.edu.chdtu.deanoffice.api.general.controller;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.dto.KnowledgeControlDTO;
import ua.edu.chdtu.deanoffice.entity.KnowledgeControl;
import ua.edu.chdtu.deanoffice.service.KnowledgeControlService;

import java.lang.reflect.Type;
import java.util.List;

@RestController
public class KnowledgeControlController {
    @Autowired private KnowledgeControlService knowledgeControlService;

    private List<KnowledgeControlDTO> parseToKnowledgeControlDTO(List<KnowledgeControl> courseForGroupList) {
        Type listType = new TypeToken<List<KnowledgeControlDTO>>() {}.getType();
        return new ModelMapper().map(courseForGroupList, listType);
    }

    @GetMapping(path = "/knowledgeControls")
    public List<KnowledgeControlDTO> getAllKnowledgeControls(){
        return parseToKnowledgeControlDTO(this.knowledgeControlService.getAllKnowledgeControls());
    }

}
