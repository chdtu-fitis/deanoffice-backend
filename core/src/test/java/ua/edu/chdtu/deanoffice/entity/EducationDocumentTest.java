package ua.edu.chdtu.deanoffice.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EducationDocumentTest {
    private void assertGetPreviousDiplomaType(Integer degreeId, EducationDocument expectedEducationDocument) {
        assertEquals(EducationDocument.getPreviousDiplomaType(degreeId), expectedEducationDocument);
    }

    @Test
    void getPreviousDiplomaType() {
        assertGetPreviousDiplomaType(1, EducationDocument.SECONDARY_SCHOOL_CERTIFICATE);
        assertGetPreviousDiplomaType(4, EducationDocument.SECONDARY_SCHOOL_CERTIFICATE);
    }

    @Test
    void isExist() {
        assertTrue(EducationDocument.isExist(EducationDocument.BACHELOR_DIPLOMA));
    }
}
