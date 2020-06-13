package ua.edu.chdtu.deanoffice.service.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderParsedParagraphDto {

    private List<OrderParagraphPiece> paragraphFields;

}