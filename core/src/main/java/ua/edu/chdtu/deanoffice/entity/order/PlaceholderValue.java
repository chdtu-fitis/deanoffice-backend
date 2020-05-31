package ua.edu.chdtu.deanoffice.entity.order;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class PlaceholderValue {
    private String placeholderName;
    private String value;

    public PlaceholderValue(String name, String value) {
        this.placeholderName = name;
        this.value = value;
    }
}
