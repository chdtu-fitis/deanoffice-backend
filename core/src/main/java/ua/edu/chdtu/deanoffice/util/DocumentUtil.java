package ua.edu.chdtu.deanoffice.util;

import ua.edu.chdtu.deanoffice.entity.TuitionForm;

import java.text.SimpleDateFormat;
import java.util.Date;

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

    public static ModeOfStudyUkrEngNames getModeOfStudyUkrEngNames(TuitionForm tuitionForm) {
        String modeOfStudyUkr = "";
        String modeOfStudyEng = "";
        switch (tuitionForm) {
            case FULL_TIME:
                modeOfStudyUkr = "Очна (денна)";
                modeOfStudyEng = "Full-time";
                break;
            case EXTRAMURAL:
                modeOfStudyUkr = "Заочна";
                modeOfStudyEng = "Intermittent";
                break;
        }
        return new ModeOfStudyUkrEngNames(modeOfStudyUkr, modeOfStudyEng);
    }

    public static class ModeOfStudyUkrEngNames {
        private String modeOfStudyUkr;
        private String modeOfStudyEng;

        public ModeOfStudyUkrEngNames(String modeOfStudyUkr, String modeOfStudyEng) {
            this.modeOfStudyUkr = modeOfStudyUkr;
            this.modeOfStudyEng = modeOfStudyEng;
        }

        public String getModeOfStudyUkr() {
            return modeOfStudyUkr;
        }

        public String getModeOfStudyEng() {
            return modeOfStudyEng;
        }
    }
}
