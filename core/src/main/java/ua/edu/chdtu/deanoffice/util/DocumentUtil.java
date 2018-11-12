package ua.edu.chdtu.deanoffice.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Vladimir on 12/11/18.
 **/
public class DocumentUtil {
    public static String getJavaTempDirectory() {
        return System.getProperty("java.io.tmpdir");
    }

    public static String getFileCreationDateAndTime() {
        return new SimpleDateFormat(" dd-MM-yyyy HH-mm").format(new Date());
    }

    public static String cleanFileName(final String fileName) {
        return fileName
                .replaceAll(" +", " ")
                .replaceAll("[^a-zA-Z0-9_-]+", "");
    }

}
