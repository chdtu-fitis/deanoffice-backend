package ua.edu.chdtu.deanoffice.api.student.synchronization;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.SyncronizationController;

@RestController
@RequestMapping("/thesis")
public class ThesisController {
    @PostMapping("/thesis-import")
    public ResponseEntity importThesis(@RequestParam("file") MultipartFile uploadFile){
        if (uploadFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Файл не було надіслано");
        }
        try {


        } catch (Exception e){
            e.getStackTrace();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/thesis-import/update")
    public ResponseEntity studentSaveChanges(){
        try {

            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }
    //    static SpreadsheetMLPackage importFile(String filePath){
//        SpreadsheetMLPackage docxPkg;
//        try{
//            docxPkg = SpreadsheetMLPackage.load(new java.io.File(filePath));
//        } catch (Docx4JException e){
//            e.printStackTrace();
//        }
//        return null;
//    }
    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, SyncronizationController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}