package ua.edu.chdtu.deanoffice.api.document.diplomasupplement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseNameWithoutEngDTO {
     private String name;
     private String message;

     public CourseNameWithoutEngDTO(String name, String message) {
          this.name = name;
          this.message = message;
     }
}
