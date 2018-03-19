package ua.edu.chdtu.deanoffice.api.document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class DocumentResponseController {

    private static Logger log = LoggerFactory.getLogger(DocumentResponseController.class);
    private static final String MEDIA_TYPE_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    public static ResponseEntity<Resource> buildDocumentResponseEntity(File result, String asciiName, String mediaType) {
        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(result));
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + asciiName)
                    .contentType(MediaType.parseMediaType(mediaType))
                    .contentLength(result.length())
                    .body(resource);
        } catch (FileNotFoundException e) {
            log.error("Created file not found!", e);
            return ExceptionHandlerAdvice.handleException(e);
        }
    }

    public static ResponseEntity<Resource> buildDocumentResponseEntity(File result, String asciiName) {
        return buildDocumentResponseEntity(result, asciiName, MEDIA_TYPE_DOCX);
    }
}
