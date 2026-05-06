import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class TestEventDateValidation {

    // Helper method to create valid test dates
    private SimpleDate date(int year, int month, int day, int hour, int minute) {
        return new SimpleDate(year, month, day, hour, minute);
    }

    // ==================== VALID EVENT DATE SEQUENCE TESTS ====================
    @Test
    @DisplayName("EventDate valid - ordine corectă: regStart < regEnd < eventStart < eventEnd")
    void testValidEventDateSequence() {
        SimpleDate regStart = date(2024, 1, 1, 9, 0);
        SimpleDate regEnd = date(2024, 1, 15, 17, 0);
        SimpleDate eventStart = date(2024, 1, 20, 8, 0);
        SimpleDate eventEnd = date(2024, 1, 22, 18, 0);

        EventDate ed = new EventDate(eventStart, eventEnd, regStart, regEnd);
        
        assertNotNull(ed);
        assertEquals(regStart, ed.getRegistrationStart());
        assertEquals(regEnd, ed.getRegistrationEnd());
        assertEquals(eventStart, ed.getStartDate());
        assertEquals(eventEnd, ed.getEndDate());
    }

    @Test
    @DisplayName("EventDate valid - registrationEnd = eventStart (limita)")
    void testValidEventDateRegistrationEndEqualsEventStart() {
        SimpleDate regStart = date(2024, 1, 1, 9, 0);
        SimpleDate regEnd = date(2024, 1, 20, 8, 0);
        SimpleDate eventStart = date(2024, 1, 20, 8, 0);
        SimpleDate eventEnd = date(2024, 1, 22, 18, 0);

        EventDate ed = new EventDate(eventStart, eventEnd, regStart, regEnd);
        assertNotNull(ed);
    }

    // ==================== REGISTRATION DATE ORDER TESTS ====================
    @Test
    @DisplayName("EventDate invalid - registrationStart = registrationEnd")
    void testInvalidEventDateRegistrationStartEqualsEnd() {
        SimpleDate regStart = date(2024, 1, 1, 9, 0);
        SimpleDate regEnd = date(2024, 1, 1, 9, 0);
        SimpleDate eventStart = date(2024, 1, 20, 8, 0);
        SimpleDate eventEnd = date(2024, 1, 22, 18, 0);

        assertThrows(IllegalArgumentException.class, () -> {
            new EventDate(eventStart, eventEnd, regStart, regEnd);
        });
    }

    @Test
    @DisplayName("EventDate invalid - registrationStart > registrationEnd")
    void testInvalidEventDateRegistrationStartAfterEnd() {
        SimpleDate regStart = date(2024, 1, 20, 9, 0);
        SimpleDate regEnd = date(2024, 1, 1, 17, 0);
        SimpleDate eventStart = date(2024, 1, 25, 8, 0);
        SimpleDate eventEnd = date(2024, 1, 27, 18, 0);

        assertThrows(IllegalArgumentException.class, () -> {
            new EventDate(eventStart, eventEnd, regStart, regEnd);
        });
    }

    // ==================== EVENT DATE ORDER TESTS ====================
    @Test
    @DisplayName("EventDate invalid - eventStart = eventEnd")
    void testInvalidEventDateEventStartEqualsEnd() {
        SimpleDate regStart = date(2024, 1, 1, 9, 0);
        SimpleDate regEnd = date(2024, 1, 15, 17, 0);
        SimpleDate eventStart = date(2024, 1, 20, 8, 0);
        SimpleDate eventEnd = date(2024, 1, 20, 8, 0);

        assertThrows(IllegalArgumentException.class, () -> {
            new EventDate(eventStart, eventEnd, regStart, regEnd);
        });
    }

    @Test
    @DisplayName("EventDate invalid - eventStart > eventEnd")
    void testInvalidEventDateEventStartAfterEnd() {
        SimpleDate regStart = date(2024, 1, 1, 9, 0);
        SimpleDate regEnd = date(2024, 1, 15, 17, 0);
        SimpleDate eventStart = date(2024, 1, 27, 8, 0);
        SimpleDate eventEnd = date(2024, 1, 22, 18, 0);

        assertThrows(IllegalArgumentException.class, () -> {
            new EventDate(eventStart, eventEnd, regStart, regEnd);
        });
    }

    // ==================== REGISTRATION vs EVENT TIMING TESTS ====================
    @Test
    @DisplayName("EventDate invalid - registrationEnd > eventStart")
    void testInvalidEventDateRegistrationEndAfterEventStart() {
        SimpleDate regStart = date(2024, 1, 1, 9, 0);
        SimpleDate regEnd = date(2024, 1, 20, 10, 0);
        SimpleDate eventStart = date(2024, 1, 20, 9, 0);
        SimpleDate eventEnd = date(2024, 1, 22, 18, 0);

        assertThrows(IllegalArgumentException.class, () -> {
            new EventDate(eventStart, eventEnd, regStart, regEnd);
        });
    }

    // ==================== NULL VALIDATION TESTS ====================
    @Test
    @DisplayName("EventDate invalid - startDate null")
    void testInvalidEventDateStartDateNull() {
        SimpleDate regStart = date(2024, 1, 1, 9, 0);
        SimpleDate regEnd = date(2024, 1, 15, 17, 0);
        SimpleDate eventEnd = date(2024, 1, 22, 18, 0);

        assertThrows(IllegalArgumentException.class, () -> {
            new EventDate(null, eventEnd, regStart, regEnd);
        });
    }

    @Test
    @DisplayName("EventDate invalid - endDate null")
    void testInvalidEventDateEndDateNull() {
        SimpleDate regStart = date(2024, 1, 1, 9, 0);
        SimpleDate regEnd = date(2024, 1, 15, 17, 0);
        SimpleDate eventStart = date(2024, 1, 20, 8, 0);

        assertThrows(IllegalArgumentException.class, () -> {
            new EventDate(eventStart, null, regStart, regEnd);
        });
    }

    @Test
    @DisplayName("EventDate invalid - registrationStart null")
    void testInvalidEventDateRegistrationStartNull() {
        SimpleDate regEnd = date(2024, 1, 15, 17, 0);
        SimpleDate eventStart = date(2024, 1, 20, 8, 0);
        SimpleDate eventEnd = date(2024, 1, 22, 18, 0);

        assertThrows(IllegalArgumentException.class, () -> {
            new EventDate(eventStart, eventEnd, null, regEnd);
        });
    }

    @Test
    @DisplayName("EventDate invalid - registrationEnd null")
    void testInvalidEventDateRegistrationEndNull() {
        SimpleDate regStart = date(2024, 1, 1, 9, 0);
        SimpleDate eventStart = date(2024, 1, 20, 8, 0);
        SimpleDate eventEnd = date(2024, 1, 22, 18, 0);

        assertThrows(IllegalArgumentException.class, () -> {
            new EventDate(eventStart, eventEnd, regStart, null);
        });
    }

    // ==================== SETTER VALIDATION TESTS ====================
    @Test
    @DisplayName("Setter startDate valid")
    void testSetValidStartDate() {
        SimpleDate regStart = date(2024, 1, 1, 9, 0);
        SimpleDate regEnd = date(2024, 1, 15, 17, 0);
        SimpleDate eventStart = date(2024, 1, 20, 8, 0);
        SimpleDate eventEnd = date(2024, 1, 22, 18, 0);

        EventDate ed = new EventDate(eventStart, eventEnd, regStart, regEnd);
        SimpleDate newStart = date(2024, 1, 21, 8, 0);
        ed.setStartDate(newStart);
        
        assertEquals(newStart, ed.getStartDate());
    }

    @Test
    @DisplayName("Setter startDate null aruncă excepție")
    void testSetStartDateNullThrows() {
        SimpleDate regStart = date(2024, 1, 1, 9, 0);
        SimpleDate regEnd = date(2024, 1, 15, 17, 0);
        SimpleDate eventStart = date(2024, 1, 20, 8, 0);
        SimpleDate eventEnd = date(2024, 1, 22, 18, 0);

        EventDate ed = new EventDate(eventStart, eventEnd, regStart, regEnd);
        
        assertThrows(IllegalArgumentException.class, () -> {
            ed.setStartDate(null);
        });
    }

    // ==================== MULTI-DAY EVENT TESTS ====================
    @Test
    @DisplayName("EventDate valid - eveniment de 3 zile")
    void testValidMultiDayEvent() {
        SimpleDate regStart = date(2024, 5, 1, 8, 0);
        SimpleDate regEnd = date(2024, 5, 10, 18, 0);
        SimpleDate eventStart = date(2024, 5, 15, 9, 0);
        SimpleDate eventEnd = date(2024, 5, 17, 17, 0);

        EventDate ed = new EventDate(eventStart, eventEnd, regStart, regEnd);
        assertNotNull(ed);
    }

    @Test
    @DisplayName("EventDate valid - registrare de 2 săptămâni")
    void testValidLongRegistrationPeriod() {
        SimpleDate regStart = date(2024, 1, 1, 8, 0);
        SimpleDate regEnd = date(2024, 1, 15, 18, 0);
        SimpleDate eventStart = date(2024, 2, 1, 9, 0);
        SimpleDate eventEnd = date(2024, 2, 3, 17, 0);

        EventDate ed = new EventDate(eventStart, eventEnd, regStart, regEnd);
        assertNotNull(ed);
    }

}
