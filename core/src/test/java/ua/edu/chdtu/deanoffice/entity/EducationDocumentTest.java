package ua.edu.chdtu.deanoffice.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EducationDocumentTest {
    private void assertGetPreviousDiplomaType(Integer degreeId, EducationDocument expectedEducationDocument) {
        assertEquals(EducationDocument.getPreviousDiplomaType(degreeId), expectedEducationDocument);
    }

    @Test
    void getPreviousDiplomaType() {
        assertGetPreviousDiplomaType(1, EducationDocument.SECONDARY_SCHOOL_CERTIFICATE);
        assertGetPreviousDiplomaType(2, EducationDocument.SECONDARY_SCHOOL_CERTIFICATE);

        assertGetPreviousDiplomaType(3, EducationDocument.BACHELOR_DIPLOMA);

        assertGetPreviousDiplomaType(4, EducationDocument.SECONDARY_SCHOOL_CERTIFICATE);
    }

    @Test
    void isExist() {
        assertTrue(EducationDocument.isExist(EducationDocument.BACHELOR_DIPLOMA));
    }

    @Test void getCode() {
        assertEquals(4, EducationDocument.MASTER_DIPLOMA.code);
    }

    @Test void getNameUkr() {
        assertEquals("Диплом магістра", EducationDocument.MASTER_DIPLOMA.nameUkr);
    }

    @Test void getNameEng() {
        assertEquals("Master diploma", EducationDocument.MASTER_DIPLOMA.nameEng);
    }
}
