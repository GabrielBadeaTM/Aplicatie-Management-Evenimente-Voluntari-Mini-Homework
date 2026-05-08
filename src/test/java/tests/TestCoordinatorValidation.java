package tests;

import models.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;

public class TestCoordinatorValidation {

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

    // ==================== CONSTRUCTOR TESTS ====================
    @Test
    @DisplayName("Coordinator constructor cu Event și Volunteer valizi")
    void testValidCoordinatorConstructor() {
        Event event = createValidEvent();
        Volunteer v = new Volunteer("Coordinator", "Test", "coord@example.com", "1234567890", 5, TShirtSize.M);
        
        Coordinator c = new Coordinator(event, v);
        
        assertEquals(event, c.getEvent());
        assertEquals(v, c.getCoordinator());
    }

    @Test
    @DisplayName("Coordinator constructor - Event null aruncă excepție")
    void testInvalidCoordinatorEventNull() {
        Volunteer v = new Volunteer("Coordinator", "Test", "coord@example.com", "1234567890", 5, TShirtSize.M);
        
        assertThrows(IllegalArgumentException.class, () -> {
            new Coordinator(null, v);
        });
    }

    @Test
    @DisplayName("Coordinator constructor - Volunteer null aruncă excepție")
    void testInvalidCoordinatorVolunteerNull() {
        Event event = createValidEvent();
        
        assertThrows(IllegalArgumentException.class, () -> {
            new Coordinator(event, null);
        });
    }

    @Test
    @DisplayName("Coordinator constructor - ambii parametri null aruncă excepție")
    void testInvalidCoordinatorBothNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Coordinator(null, null);
        });
    }

    // ==================== SUBORDINATE MANAGEMENT TESTS ====================
    @Test
    @DisplayName("Adăugare subordinat")
    void testAddSubordinate() {
        Event event = createValidEvent();
        Volunteer coordinator = new Volunteer("Coordinator", "Test", "coord@example.com", "1234567890", 5, TShirtSize.M);
        Volunteer subordinate = new Volunteer("Subordinate", "Test", "sub@example.com", "9876543210", 3, TShirtSize.L);
        
        Coordinator c = new Coordinator(event, coordinator);
        c.addSubordinate(subordinate);
        
        assertEquals(1, c.getSubordinates().size());
        assertTrue(c.getSubordinates().contains(subordinate));
    }

    @Test
    @DisplayName("Adăugare subordinat duplicate (nu se adaugă din nou)")
    void testAddDuplicateSubordinate() {
        Event event = createValidEvent();
        Volunteer coordinator = new Volunteer("Coordinator", "Test", "coord@example.com", "1234567890", 5, TShirtSize.M);
        Volunteer subordinate = new Volunteer("Subordinate", "Test", "sub@example.com", "9876543210", 3, TShirtSize.L);
        
        Coordinator c = new Coordinator(event, coordinator);
        c.addSubordinate(subordinate);
        c.addSubordinate(subordinate);
        
        assertEquals(1, c.getSubordinates().size());
    }

    @Test
    @DisplayName("Adăugare multiple subordinați")
    void testAddMultipleSubordinates() {
        Event event = createValidEvent();
        Volunteer coordinator = new Volunteer("Coordinator", "Test", "coord@example.com", "1234567890", 5, TShirtSize.M);
        Volunteer sub1 = new Volunteer("Sub", "One", "sub1@example.com", "1111111111", 3, TShirtSize.M);
        Volunteer sub2 = new Volunteer("Sub", "Two", "sub2@example.com", "2222222222", 2, TShirtSize.L);
        
        Coordinator c = new Coordinator(event, coordinator);
        c.addSubordinate(sub1);
        c.addSubordinate(sub2);
        
        assertEquals(2, c.getSubordinates().size());
        assertTrue(c.getSubordinates().contains(sub1));
        assertTrue(c.getSubordinates().contains(sub2));
    }

    @Test
    @DisplayName("Verificare subordinat - prezent")
    void testHasSubordinateTrue() {
        Event event = createValidEvent();
        Volunteer coordinator = new Volunteer("Coordinator", "Test", "coord@example.com", "1234567890", 5, TShirtSize.M);
        Volunteer subordinate = new Volunteer("Subordinate", "Test", "sub@example.com", "9876543210", 3, TShirtSize.L);
        
        Coordinator c = new Coordinator(event, coordinator);
        c.addSubordinate(subordinate);
        
        assertTrue(c.hasSubordinate(subordinate));
    }

    @Test
    @DisplayName("Verificare subordinat - absent")
    void testHasSubordinateFalse() {
        Event event = createValidEvent();
        Volunteer coordinator = new Volunteer("Coordinator", "Test", "coord@example.com", "1234567890", 5, TShirtSize.M);
        Volunteer other = new Volunteer("Other", "Test", "other@example.com", "5555555555", 3, TShirtSize.M);
        
        Coordinator c = new Coordinator(event, coordinator);
        
        assertFalse(c.hasSubordinate(other));
    }

    @Test
    @DisplayName("Ștergere subordinat")
    void testRemoveSubordinate() {
        Event event = createValidEvent();
        Volunteer coordinator = new Volunteer("Coordinator", "Test", "coord@example.com", "1234567890", 5, TShirtSize.M);
        Volunteer subordinate = new Volunteer("Subordinate", "Test", "sub@example.com", "9876543210", 3, TShirtSize.L);
        
        Coordinator c = new Coordinator(event, coordinator);
        c.addSubordinate(subordinate);
        assertEquals(1, c.getSubordinates().size());
        
        c.removeSubordinate(subordinate);
        assertEquals(0, c.getSubordinates().size());
        assertFalse(c.hasSubordinate(subordinate));
    }

    // ==================== ACCEPT VOLUNTEER TESTS ====================
    @Test
    @DisplayName("Acceptare volunteer - volunteer a aplicat la event")
    void testAcceptVolunteerValid() {
        Event event = createValidEvent();
        Volunteer coordinator = new Volunteer("Coordinator", "Test", "coord@example.com", "1234567890", 5, TShirtSize.M);
        Volunteer volunteer = new Volunteer("Volunteer", "Test", "vol@example.com", "9876543210", 3, TShirtSize.L);
        
        SimpleDate from = date(2024, 1, 20, 8, 0);
        SimpleDate to = date(2024, 1, 22, 18, 0);
        
        volunteer.applyToEvent(event, from, to);
        
        Coordinator c = new Coordinator(event, coordinator);
        c.acceptVolunteer(volunteer, from, to);
        
        assertTrue(c.hasSubordinate(volunteer));
    }

    @Test
    @DisplayName("Acceptare volunteer - coordonator ca subordinat aruncă excepție")
    void testAcceptVolunteerCoordinatorSelfThrows() {
        Event event = createValidEvent();
        Volunteer coordinator = new Volunteer("Coordinator", "Test", "coord@example.com", "1234567890", 5, TShirtSize.M);
        
        Coordinator c = new Coordinator(event, coordinator);
        
        assertThrows(IllegalArgumentException.class, () -> {
            c.acceptVolunteer(coordinator, date(2024, 1, 20, 8, 0), date(2024, 1, 22, 18, 0));
        });
    }

    @Test
    @DisplayName("Acceptare volunteer - volunteer nu a aplicat la event aruncă excepție")
    void testAcceptVolunteerNotAppliedThrows() {
        Event event = createValidEvent();
        Volunteer coordinator = new Volunteer("Coordinator", "Test", "coord@example.com", "1234567890", 5, TShirtSize.M);
        Volunteer volunteer = new Volunteer("Volunteer", "Test", "vol@example.com", "9876543210", 3, TShirtSize.L);
        
        Coordinator c = new Coordinator(event, coordinator);
        
        assertThrows(IllegalArgumentException.class, () -> {
            c.acceptVolunteer(volunteer, date(2024, 1, 20, 8, 0), date(2024, 1, 22, 18, 0));
        });
    }

    // ==================== GETTERS TESTS ====================
    @Test
    @DisplayName("Getter Event")
    void testGetEvent() {
        Event event = createValidEvent();
        Volunteer v = new Volunteer("Coordinator", "Test", "coord@example.com", "1234567890", 5, TShirtSize.M);
        
        Coordinator c = new Coordinator(event, v);
        assertEquals(event, c.getEvent());
    }

    @Test
    @DisplayName("Getter Coordinator")
    void testGetCoordinator() {
        Event event = createValidEvent();
        Volunteer v = new Volunteer("Coordinator", "Test", "coord@example.com", "1234567890", 5, TShirtSize.M);
        
        Coordinator c = new Coordinator(event, v);
        assertEquals(v, c.getCoordinator());
    }

    @Test
    @DisplayName("Getter Subordinates - inițial gol")
    void testGetSubordinatesEmpty() {
        Event event = createValidEvent();
        Volunteer v = new Volunteer("Coordinator", "Test", "coord@example.com", "1234567890", 5, TShirtSize.M);
        
        Coordinator c = new Coordinator(event, v);
        assertNotNull(c.getSubordinates());
        assertTrue(c.getSubordinates().isEmpty());
    }

    // ==================== SETTER TESTS ====================
    @Test
    @DisplayName("Setter Event valid")
    void testSetValidEvent() {
        Event event1 = createValidEvent();
        Volunteer v = new Volunteer("Coordinator", "Test", "coord@example.com", "1234567890", 5, TShirtSize.M);
        
        Coordinator c = new Coordinator(event1, v);
        
        Event event2 = new Event("New Event", createValidEventDate(), createValidAdmin());
        c.setEvent(event2);
        
        assertEquals(event2, c.getEvent());
    }

    @Test
    @DisplayName("Setter Event null aruncă excepție")
    void testSetEventNullThrows() {
        Event event = createValidEvent();
        Volunteer v = new Volunteer("Coordinator", "Test", "coord@example.com", "1234567890", 5, TShirtSize.M);
        
        Coordinator c = new Coordinator(event, v);
        
        assertThrows(IllegalArgumentException.class, () -> {
            c.setEvent(null);
        });
    }

    @Test
    @DisplayName("Setter Coordinator valid")
    void testSetValidCoordinator() {
        Event event = createValidEvent();
        Volunteer v1 = new Volunteer("Coordinator", "One", "coord1@example.com", "1111111111", 5, TShirtSize.M);
        
        Coordinator c = new Coordinator(event, v1);
        
        Volunteer v2 = new Volunteer("Coordinator", "Two", "coord2@example.com", "2222222222", 3, TShirtSize.L);
        c.setCoordinator(v2);
        
        assertEquals(v2, c.getCoordinator());
    }

    @Test
    @DisplayName("Setter Coordinator null aruncă excepție")
    void testSetCoordinatorNullThrows() {
        Event event = createValidEvent();
        Volunteer v = new Volunteer("Coordinator", "Test", "coord@example.com", "1234567890", 5, TShirtSize.M);
        
        Coordinator c = new Coordinator(event, v);
        
        assertThrows(IllegalArgumentException.class, () -> {
            c.setCoordinator(null);
        });
    }

    // ==================== VOLUNTEER UNDER TWO COORDINATORS TESTS ====================
    @Test
    @DisplayName("Voluntar nu poate fi sub doi coordinatori diferiți în același event")
    void testVolunteerCannotBeUnderTwoCoordinators() {
        Event event = createValidEvent();
        Volunteer coordinator1 = new Volunteer("Coordinator", "One", "coord1@example.com", "1111111111", 10, TShirtSize.M);
        Volunteer coordinator2 = new Volunteer("Coordinator", "Two", "coord2@example.com", "2222222222", 8, TShirtSize.L);
        Volunteer volunteer = new Volunteer("Volunteer", "Test", "vol@example.com", "3333333333", 5, TShirtSize.M);
        
        SimpleDate from = date(2024, 1, 20, 8, 0);
        SimpleDate to = date(2024, 1, 22, 18, 0);
        
        volunteer.applyToEvent(event, from, to);
        
        // Obține rolurile de coordinator din event
        Coordinator coordRole1 = event.assignCoordinator(coordinator1);
        Coordinator coordRole2 = event.assignCoordinator(coordinator2);
        
        // Acceptă voluntarul sub coordinatorul 1
        coordRole1.acceptVolunteer(volunteer, from, to);
        assertTrue(coordRole1.hasSubordinate(volunteer));
        
        // Încearcă să accepte același voluntar sub coordinatorul 2 - ar trebui să arunce excepție
        assertThrows(IllegalArgumentException.class, () -> {
            coordRole2.acceptVolunteer(volunteer, from, to);
        });
    }

    @Test
    @DisplayName("Voluntar nu poate fi acceptat de doi coordinatori - verifică că al doilea refuză")
    void testSecondCoordinatorRefusesVolunteerAlreadyUnderAnother() {
        Event event = createValidEvent();
        Volunteer coordinator1 = new Volunteer("Coordinator", "One", "c1@example.com", "1111111111", 10, TShirtSize.M);
        Volunteer coordinator2 = new Volunteer("Coordinator", "Two", "c2@example.com", "2222222222", 9, TShirtSize.M);
        Volunteer volunteer = new Volunteer("Volunteer", "Worker", "vol@example.com", "3333333333", 3, TShirtSize.L);
        
        SimpleDate from = date(2024, 1, 20, 8, 0);
        SimpleDate to = date(2024, 1, 22, 18, 0);
        
        volunteer.applyToEvent(event, from, to);
        
        // Obține rolurile de coordinator din event (nu creează local)
        Coordinator role1 = event.assignCoordinator(coordinator1);
        Coordinator role2 = event.assignCoordinator(coordinator2);
        
        // Coordinatorul 1 acceptă voluntarul
        role1.acceptVolunteer(volunteer, from, to);
        assertEquals(1, role1.getSubordinates().size());
        assertEquals(0, role2.getSubordinates().size());
        
        // Coordinatorul 2 încearcă să accepte același voluntar
        assertThrows(IllegalArgumentException.class, () -> {
            role2.acceptVolunteer(volunteer, from, to);
        }, "Volunteer is already under another coordinator");
        
        // Verifică că voluntarul rămâne doar sub coordinatorul 1
        assertEquals(1, role1.getSubordinates().size());
        assertEquals(0, role2.getSubordinates().size());
    }

    @Test
    @DisplayName("Un volunteer poate fi sub doi coordinatori DACĂ sunt în event-uri diferite")
    void testVolunteerCanBeUnderCoordinatorsInDifferentEvents() {
        // Crează 2 event-uri diferite
        Event event1 = createValidEvent();
        Event event2 = new Event("Other Event", createValidEventDate(), createValidAdmin());
        
        Volunteer coordinator1 = new Volunteer("Coordinator", "One", "c1@example.com", "1111111111", 10, TShirtSize.M);
        Volunteer coordinator2 = new Volunteer("Coordinator", "Two", "c2@example.com", "2222222222", 8, TShirtSize.L);
        Volunteer volunteer = new Volunteer("Volunteer", "Multi", "vol@example.com", "3333333333", 5, TShirtSize.M);
        
        SimpleDate from = date(2024, 1, 20, 8, 0);
        SimpleDate to = date(2024, 1, 22, 18, 0);
        
        // Voluntarul se aplică la ambele event-uri
        volunteer.applyToEvent(event1, from, to);
        volunteer.applyToEvent(event2, from, to);
        
        // Creează roluri de coordinator pentru fiecare event
        Coordinator role1 = new Coordinator(event1, coordinator1);
        Coordinator role2 = new Coordinator(event2, coordinator2);
        
        // Ambii coordinatori acceptă voluntarul în event-urile lor respective
        role1.acceptVolunteer(volunteer, from, to);
        role2.acceptVolunteer(volunteer, from, to);
        
        // Ambii coordinatori ar trebui să aibă voluntarul ca subordinat
        assertTrue(role1.hasSubordinate(volunteer));
        assertTrue(role2.hasSubordinate(volunteer));
        assertEquals(1, role1.getSubordinates().size());
        assertEquals(1, role2.getSubordinates().size());
    }

    // ==================== COORDINATOR-SPECIFIC CONSTRAINT TESTS ====================
    @Test
    @DisplayName("Coordinator nu poate fi acceptat ca subordinat al altuia")
    void testCoordinatorCannotBeSubordinate() {
        Event event = createValidEvent();
        Volunteer coordinator1 = new Volunteer("Coordinator", "Main", "c1@example.com", "1111111111", 10, TShirtSize.M);
        Volunteer coordinator2 = new Volunteer("Coordinator", "Sub", "c2@example.com", "2222222222", 8, TShirtSize.L);
        
        SimpleDate from = date(2024, 1, 20, 8, 0);
        SimpleDate to = date(2024, 1, 22, 18, 0);
        
        // coordinator2 se aplică
        coordinator2.applyToEvent(event, from, to);
        
        // Creează roluri de coordinator
        Coordinator role1 = new Coordinator(event, coordinator1);
        Coordinator role2 = new Coordinator(event, coordinator2);
        event.assignCoordinator(coordinator1);
        event.assignCoordinator(coordinator2);
        
        // coordinator1 încearcă să accepte coordinator2 ca subordinat
        assertThrows(IllegalArgumentException.class, () -> {
            role1.acceptVolunteer(coordinator2, from, to);
        });
    }

    @Test
    @DisplayName("Ștergere subordinat care nu există")
    void testRemoveNonexistentSubordinate() {
        Event event = createValidEvent();
        Volunteer coordinator = new Volunteer("Coordinator", "Test", "coord@example.com", "1234567890", 5, TShirtSize.M);
        Volunteer volunteer1 = new Volunteer("Volunteer", "One", "vol1@example.com", "1111111111", 3, TShirtSize.M);
        Volunteer volunteer2 = new Volunteer("Volunteer", "Two", "vol2@example.com", "2222222222", 3, TShirtSize.L);
        
        Coordinator c = new Coordinator(event, coordinator);
        c.addSubordinate(volunteer1);
        
        // Încearcă să șteargă un subordinat care nu există
        c.removeSubordinate(volunteer2);
        
        // volunteer1 ar trebui să rămână
        assertTrue(c.hasSubordinate(volunteer1));
        assertFalse(c.hasSubordinate(volunteer2));
    }

    @Test
    @DisplayName("Verificare dacă toți subordinații sunt corecți după ștergere")
    void testSubordinatesCorrectAfterRemoval() {
        Event event = createValidEvent();
        Volunteer coordinator = new Volunteer("Coordinator", "Test", "coord@example.com", "1234567890", 5, TShirtSize.M);
        Volunteer sub1 = new Volunteer("Sub", "One", "s1@example.com", "1111111111", 3, TShirtSize.M);
        Volunteer sub2 = new Volunteer("Sub", "Two", "s2@example.com", "2222222222", 3, TShirtSize.L);
        Volunteer sub3 = new Volunteer("Sub", "Three", "s3@example.com", "3333333333", 2, TShirtSize.M);
        
        Coordinator c = new Coordinator(event, coordinator);
        c.addSubordinate(sub1);
        c.addSubordinate(sub2);
        c.addSubordinate(sub3);
        
        assertEquals(3, c.getSubordinates().size());
        
        c.removeSubordinate(sub2);
        
        assertEquals(2, c.getSubordinates().size());
        assertTrue(c.hasSubordinate(sub1));
        assertFalse(c.hasSubordinate(sub2));
        assertTrue(c.hasSubordinate(sub3));
    }

}
