package ua.edu.chdtu.deanoffice.api.document;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class DocumentResponseController {

    protected static final String MEDIA_TYPE_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    protected static final String MEDIA_TYPE_PDF = "application/pdf";
    protected static final String MEDIA_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    protected static ResponseEntity buildDocumentResponseEntity(File result, String asciiName, String mediaType) {
        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(result));
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + asciiName)
                    .header("content-filename",  asciiName)
                    .header("Access-Control-Expose-Headers",  "content-filename")
                    .contentType(MediaType.parseMediaType(mediaType))
                    .contentLength(result.length())
                    .body(resource);
        } catch (FileNotFoundException exception) {
            return handleException(exception);
        }
    }

    private static ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, DocumentResponseController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
