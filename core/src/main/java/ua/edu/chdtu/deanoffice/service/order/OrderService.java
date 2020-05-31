package ua.edu.chdtu.deanoffice.service.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.order.OrderType;
import ua.edu.chdtu.deanoffice.repository.order.OrderTemplateVersionRepository;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderTemplateVersionRepository orderTemplateVersionRepository;

    public String getParagraphByOrderType(OrderType orderType) {
        return orderTemplateVersionRepository.findByDbTableName(orderType.toString()).getParagraphTemplate();
    }
}
