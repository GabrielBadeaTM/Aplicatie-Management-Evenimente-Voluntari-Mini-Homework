import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;

public class TestVolunteerValidation {

    // Helper method
    private SimpleDate date(int year, int month, int day, int hour, int minute) {
        return new SimpleDate(year, month, day, hour, minute);
    }

    private EventDate createValidEventDate() {
        SimpleDate regStart = date(2024, 1, 1, 9, 0);
        SimpleDate regEnd = date(2024, 1, 15, 17, 0);
        SimpleDate eventStart = date(2024, 1, 20, 8, 0);
        SimpleDate eventEnd = date(2024, 1, 22, 18, 0);
        return new EventDate(eventStart, eventEnd, regStart, regEnd);
    }

    private Admin createValidAdmin() {
        return new Admin("John", "Admin", "admin@example.com", "1234567890");
    }

    private Event createValidEvent() {
        return new Event("Valid Event", createValidEventDate(), createValidAdmin());
    }

    // ==================== CONSTRUCTOR VALIDATION TESTS ====================
    @Test
    @DisplayName("Volunteer constructor cu parametri valizi")
    void testValidVolunteerConstructor() {
        Volunteer v = new Volunteer("John", "Volunteer", "john@example.com", "1234567890", 5, TShirtSize.M);
        
        assertEquals("John", v.getFirstName());
        assertEquals("Volunteer", v.getLastName());
        assertEquals(5, v.getYearsOfExperience());
        assertEquals(TShirtSize.M, v.getTShirtSize());
    }

    @Test
    @DisplayName("Volunteer default constructor")
    void testDefaultVolunteerConstructor() {
        Volunteer v = new Volunteer();
        
        assertNotNull(v);
        assertEquals(0, v.getYearsOfExperience());
        assertEquals(TShirtSize.M, v.getTShirtSize());
    }

    // ==================== YEARS OF EXPERIENCE VALIDATION TESTS ====================
    @Test
    @DisplayName("Ani experiență valid - 0 (nou voluntar)")
    void testValidExperienceZero() {
        Volunteer v = new Volunteer("John", "Volunteer", "john@example.com", "1234567890", 0, TShirtSize.M);
        assertEquals(0, v.getYearsOfExperience());
    }

    @Test
    @DisplayName("Ani experiență valid - 50 (mediu)")
    void testValidExperienceMedium() {
        Volunteer v = new Volunteer("John", "Volunteer", "john@example.com", "1234567890", 50, TShirtSize.M);
        assertEquals(50, v.getYearsOfExperience());
    }

    @Test
    @DisplayName("Ani experiență valid - 100 (maxim)")
    void testValidExperienceMaximum() {
        Volunteer v = new Volunteer("John", "Volunteer", "john@example.com", "1234567890", 100, TShirtSize.M);
        assertEquals(100, v.getYearsOfExperience());
    }

    @Test
    @DisplayName("Ani experiență invalid - -1 (negativ)")
    void testInvalidExperienceNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Volunteer("John", "Volunteer", "john@example.com", "1234567890", -1, TShirtSize.M);
        });
    }

    @Test
    @DisplayName("Ani experiență invalid - 101 (prea mare)")
    void testInvalidExperienceTooLarge() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Volunteer("John", "Volunteer", "john@example.com", "1234567890", 101, TShirtSize.M);
        });
    }

    // ==================== T-SHIRT SIZE VALIDATION TESTS ====================
    @Test
    @DisplayName("T-Shirt Size valid - S")
    void testValidTShirtSizeSmall() {
        Volunteer v = new Volunteer("John", "Volunteer", "john@example.com", "1234567890", 5, TShirtSize.S);
        assertEquals(TShirtSize.S, v.getTShirtSize());
    }

    @Test
    @DisplayName("T-Shirt Size valid - M")
    void testValidTShirtSizeMedium() {
        Volunteer v = new Volunteer("John", "Volunteer", "john@example.com", "1234567890", 5, TShirtSize.M);
        assertEquals(TShirtSize.M, v.getTShirtSize());
    }

    @Test
    @DisplayName("T-Shirt Size valid - L")
    void testValidTShirtSizeLarge() {
        Volunteer v = new Volunteer("John", "Volunteer", "john@example.com", "1234567890", 5, TShirtSize.L);
        assertEquals(TShirtSize.L, v.getTShirtSize());
    }

    @Test
    @DisplayName("T-Shirt Size valid - XL")
    void testValidTShirtSizeXLarge() {
        Volunteer v = new Volunteer("John", "Volunteer", "john@example.com", "1234567890", 5, TShirtSize.XL);
        assertEquals(TShirtSize.XL, v.getTShirtSize());
    }

    @Test
    @DisplayName("T-Shirt Size invalid - null")
    void testInvalidTShirtSizeNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Volunteer("John", "Volunteer", "john@example.com", "1234567890", 5, null);
        });
    }

    // ==================== APPLY TO EVENT TESTS ====================
    @Test
    @DisplayName("Aplicare la event valid")
    void testValidApplyToEvent() {
        Admin admin = createValidAdmin();
        Event event = createValidEvent();
        admin.addVolunteer(new Volunteer("John", "Vol", "john@example.com", "1234567890", 5, TShirtSize.M));
        
        Volunteer v = new Volunteer("John", "Vol", "john@example.com", "1234567890", 5, TShirtSize.M);
        SimpleDate from = date(2024, 1, 20, 8, 0);
        SimpleDate to = date(2024, 1, 22, 18, 0);
        
        v.applyToEvent(event, from, to);
        
        assertTrue(v.getEventAvailabilities().size() > 0);
    }

    @Test
    @DisplayName("Aplicare dublă la același event aruncă excepție")
    void testInvalidDoublyApplyToSameEvent() {
        Volunteer v = new Volunteer("John", "Vol", "john@example.com", "1234567890", 5, TShirtSize.M);
        Event event = createValidEvent();
        
        SimpleDate from = date(2024, 1, 20, 8, 0);
        SimpleDate to = date(2024, 1, 22, 18, 0);
        
        v.applyToEvent(event, from, to);
        
        assertThrows(IllegalArgumentException.class, () -> {
            v.applyToEvent(event, from, to);
        });
    }

    @Test
    @DisplayName("Aplicare în afara perioadei de înregistrare aruncă excepție")
    void testInvalidApplyOutsideRegistrationWindow() {
        Volunteer v = new Volunteer("John", "Vol", "john@example.com", "1234567890", 5, TShirtSize.M);
        Event event = createValidEvent();
        
        SimpleDate from = date(2024, 1, 20, 8, 0);
        SimpleDate to = date(2024, 1, 22, 18, 0);
        SimpleDate currentTime = date(2024, 1, 16, 9, 0); // După registrationEnd
        
        assertThrows(IllegalArgumentException.class, () -> {
            v.applyToEvent(event, from, to, currentTime);
        });
    }

    @Test
    @DisplayName("Aplicare în perioada de înregistrare")
    void testValidApplyInsideRegistrationWindow() {
        Volunteer v = new Volunteer("John", "Vol", "john@example.com", "1234567890", 5, TShirtSize.M);
        Event event = createValidEvent();
        
        SimpleDate from = date(2024, 1, 20, 8, 0);
        SimpleDate to = date(2024, 1, 22, 18, 0);
        SimpleDate currentTime = date(2024, 1, 10, 9, 0); // În perioada de înregistrare
        
        v.applyToEvent(event, from, to, currentTime);
        assertTrue(v.getEventAvailabilities().size() > 0);
    }

    @Test
    @DisplayName("Aplicare cu disponibilitate în limitele evenimentului")
    void testValidApplyWithinEventBounds() {
        Volunteer v = new Volunteer("John", "Vol", "john@example.com", "1234567890", 5, TShirtSize.M);
        Event event = createValidEvent();
        
        SimpleDate from = date(2024, 1, 20, 9, 0);
        SimpleDate to = date(2024, 1, 22, 17, 0);
        
        v.applyToEvent(event, from, to);
        assertNotNull(v.getEventAvailability(event));
    }

    @Test
    @DisplayName("Aplicare cu disponibilitate în afara limitelor evenimentului aruncă excepție")
    void testInvalidApplyOutsideEventBounds() {
        Volunteer v = new Volunteer("John", "Vol", "john@example.com", "1234567890", 5, TShirtSize.M);
        Event event = createValidEvent();
        
        SimpleDate from = date(2024, 1, 19, 8, 0); // Înainte de event start
        SimpleDate to = date(2024, 1, 22, 18, 0);
        
        assertThrows(IllegalArgumentException.class, () -> {
            v.applyToEvent(event, from, to);
        });
    }

    // ==================== CANCEL APPLICATION TESTS ====================
    @Test
    @DisplayName("Anulare aplicare la event")
    void testCancelApplication() {
        Volunteer v = new Volunteer("John", "Vol", "john@example.com", "1234567890", 5, TShirtSize.M);
        Event event = createValidEvent();
        
        SimpleDate from = date(2024, 1, 20, 8, 0);
        SimpleDate to = date(2024, 1, 22, 18, 0);
        
        v.applyToEvent(event, from, to);
        assertEquals(1, v.getEventAvailabilities().size());
        
        v.cancelApplication(event);
        assertEquals(0, v.getEventAvailabilities().size());
    }

    @Test
    @DisplayName("Anulare toate aplicările")
    void testCancelAllApplications() {
        Volunteer v = new Volunteer("John", "Vol", "john@example.com", "1234567890", 5, TShirtSize.M);
        
        Event event1 = createValidEvent();
        Event event2 = new Event("Second Event", createValidEventDate(), createValidAdmin());
        
        SimpleDate from = date(2024, 1, 20, 8, 0);
        SimpleDate to = date(2024, 1, 22, 18, 0);
        
        v.applyToEvent(event1, from, to);
        v.applyToEvent(event2, from, to);
        assertEquals(2, v.getEventAvailabilities().size());
        
        v.cancelAllApplications();
        assertEquals(0, v.getEventAvailabilities().size());
    }

    // ==================== SETTER TESTS ====================
    @Test
    @DisplayName("Setter experiență valid")
    void testSetValidExperience() {
        Volunteer v = new Volunteer("John", "Vol", "john@example.com", "1234567890", 5, TShirtSize.M);
        v.setYearsOfExperience(15);
        assertEquals(15, v.getYearsOfExperience());
    }

    @Test
    @DisplayName("Setter experiență invalid aruncă excepție")
    void testSetInvalidExperienceThrows() {
        Volunteer v = new Volunteer("John", "Vol", "john@example.com", "1234567890", 5, TShirtSize.M);
        assertThrows(IllegalArgumentException.class, () -> {
            v.setYearsOfExperience(150);
        });
    }

    @Test
    @DisplayName("Setter T-Shirt Size valid")
    void testSetValidTShirtSize() {
        Volunteer v = new Volunteer("John", "Vol", "john@example.com", "1234567890", 5, TShirtSize.M);
        v.setTShirtSize(TShirtSize.XL);
        assertEquals(TShirtSize.XL, v.getTShirtSize());
    }

    @Test
    @DisplayName("Setter T-Shirt Size invalid aruncă excepție")
    void testSetInvalidTShirtSizeThrows() {
        Volunteer v = new Volunteer("John", "Vol", "john@example.com", "1234567890", 5, TShirtSize.M);
        assertThrows(IllegalArgumentException.class, () -> {
            v.setTShirtSize(null);
        });
    }

    // ==================== GETTERS TESTS ====================
    @Test
    @DisplayName("Getter eventAvailabilities - inițial gol")
    void testGetEventAvailabilitiesEmpty() {
        Volunteer v = new Volunteer("John", "Vol", "john@example.com", "1234567890", 5, TShirtSize.M);
        assertTrue(v.getEventAvailabilities().isEmpty());
    }

}
