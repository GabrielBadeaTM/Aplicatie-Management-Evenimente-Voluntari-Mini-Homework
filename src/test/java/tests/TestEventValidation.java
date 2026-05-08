package tests;

import models.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;

public class TestEventValidation {

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

    // ==================== EVENT NAME VALIDATION TESTS ====================
    @Test
    @DisplayName("Event valid - nume cu 3 caractere (minim)")
    void testValidEventNameMinimum() {
        Event e = new Event("ABC", createValidEventDate(), createValidAdmin());
        assertEquals("ABC", e.getName());
    }

    @Test
    @DisplayName("Event valid - nume normal")
    void testValidEventNameNormal() {
        Event e = new Event("Spring Conference", createValidEventDate(), createValidAdmin());
        assertEquals("Spring Conference", e.getName());
    }

    @Test
    @DisplayName("Event invalid - nume gol")
    void testInvalidEventNameEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Event("", createValidEventDate(), createValidAdmin());
        });
    }

    @Test
    @DisplayName("Event invalid - nume cu 2 caractere (prea scurt)")
    void testInvalidEventNameTooShort() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Event("AB", createValidEventDate(), createValidAdmin());
        });
    }

    @Test
    @DisplayName("Event invalid - nume null")
    void testInvalidEventNameNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Event(null, createValidEventDate(), createValidAdmin());
        });
    }

    @Test
    @DisplayName("Event invalid - nume doar spații")
    void testInvalidEventNameOnlySpaces() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Event("   ", createValidEventDate(), createValidAdmin());
        });
    }

    // ==================== EVENT DATE VALIDATION TESTS ====================
    @Test
    @DisplayName("Event invalid - EventDate null")
    void testInvalidEventDateNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Event("Valid Event", null, createValidAdmin());
        });
    }

    @Test
    @DisplayName("Event valid - EventDate valid")
    void testValidEventDate() {
        EventDate ed = createValidEventDate();
        Event e = new Event("Valid Event", ed, createValidAdmin());
        assertEquals(ed, e.getEventDate());
    }

    // ==================== ADMIN VALIDATION TESTS ====================
    @Test
    @DisplayName("Event invalid - Admin null")
    void testInvalidAdminNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Event("Valid Event", createValidEventDate(), null);
        });
    }

    @Test
    @DisplayName("Event valid - Admin valid")
    void testValidAdmin() {
        Admin admin = createValidAdmin();
        Event e = new Event("Valid Event", createValidEventDate(), admin);
        assertEquals(admin, e.getAdmin());
    }

    // ==================== DEFAULT CONSTRUCTOR TESTS ====================
    @Test
    @DisplayName("Event default constructor - nume implicit")
    void testDefaultEventName() {
        Event e = new Event();
        assertNotNull(e.getName());
        assertTrue(e.getName().length() >= 3);
    }

    @Test
    @DisplayName("Event default constructor - EventDate implicit")
    void testDefaultEventDate() {
        Event e = new Event();
        assertNotNull(e.getEventDate());
    }

    @Test
    @DisplayName("Event default constructor - Admin implicit")
    void testDefaultAdmin() {
        Event e = new Event();
        assertNotNull(e.getAdmin());
    }

    // ==================== SETTER VALIDATION TESTS ====================
    @Test
    @DisplayName("Setter nume event valid")
    void testSetValidEventName() {
        Event e = new Event("Old Name", createValidEventDate(), createValidAdmin());
        e.setName("New Event Name");
        assertEquals("New Event Name", e.getName());
    }

    @Test
    @DisplayName("Setter nume event invalid aruncă excepție")
    void testSetInvalidEventNameThrows() {
        Event e = new Event("Old Name", createValidEventDate(), createValidAdmin());
        assertThrows(IllegalArgumentException.class, () -> {
            e.setName("AB");
        });
    }

    @Test
    @DisplayName("Setter EventDate valid")
    void testSetValidEventDate() {
        Event e = new Event("Event", createValidEventDate(), createValidAdmin());
        EventDate newDate = createValidEventDate();
        e.setEventDate(newDate);
        assertEquals(newDate, e.getEventDate());
    }

    @Test
    @DisplayName("Setter EventDate null aruncă excepție")
    void testSetEventDateNullThrows() {
        Event e = new Event("Event", createValidEventDate(), createValidAdmin());
        assertThrows(IllegalArgumentException.class, () -> {
            e.setEventDate(null);
        });
    }

    @Test
    @DisplayName("Setter Admin valid")
    void testSetValidAdmin() {
        Admin admin1 = createValidAdmin();
        Event e = new Event("Event", createValidEventDate(), admin1);
        
        Admin admin2 = new Admin("Jane", "Manager", "jane@example.com", "9876543210");
        e.setAdmin(admin2);
        assertEquals(admin2, e.getAdmin());
    }

    @Test
    @DisplayName("Setter Admin null aruncă excepție")
    void testSetAdminNullThrows() {
        Event e = new Event("Event", createValidEventDate(), createValidAdmin());
        assertThrows(IllegalArgumentException.class, () -> {
            e.setAdmin(null);
        });
    }

    // ==================== COORDINATOR MANAGEMENT TESTS ====================
    @Test
    @DisplayName("Event - inițial fără coordinatori")
    void testEventInitiallyHasNoCoordinators() {
        Event e = new Event("Event", createValidEventDate(), createValidAdmin());
        assertEquals(0, e.getCoordinatorRoles().size());
    }

    @Test
    @DisplayName("Event - obținere coordinator roles (lista goală)")
    void testGetCoordinatorRolesEmpty() {
        Event e = new Event("Event", createValidEventDate(), createValidAdmin());
        assertNotNull(e.getCoordinatorRoles());
        assertTrue(e.getCoordinatorRoles().isEmpty());
    }

    // ==================== VOLUNTEER AVAILABILITY TESTS ====================
    @Test
    @DisplayName("Event - obținere disponibilități volunteeri (lista goală)")
    void testGetVolunteerAvailabilitiesEmpty() {
        Event e = new Event("Event", createValidEventDate(), createValidAdmin());
        assertNotNull(e.getVolunteerAvailabilities());
        assertTrue(e.getVolunteerAvailabilities().isEmpty());
    }

    @Test
    @DisplayName("Event - verificare volunteer nu a aplicat")
    void testVolunteerNotApplied() {
        Event e = new Event("Event", createValidEventDate(), createValidAdmin());
        Volunteer v = new Volunteer("Volunteer", "User", "vol@example.com", "1234567890", 5, TShirtSize.M);
        
        assertFalse(e.hasVolunteerApplied(v));
    }

    // ==================== COORDINATOR DUPLICATE ASSIGNMENT TESTS ====================
    @Test
    @DisplayName("Asignare coordinator la același event - returnează rolul existent")
    void testAssignSameCoordinatorTwiceReturnsSameRole() {
        Event e = new Event("Event", createValidEventDate(), createValidAdmin());
        Volunteer coordinator = new Volunteer("Coordinator", "Test", "coord@example.com", "1234567890", 5, TShirtSize.M);
        
        Coordinator role1 = e.assignCoordinator(coordinator);
        Coordinator role2 = e.assignCoordinator(coordinator);
        
        assertSame(role1, role2);
        assertEquals(1, e.getCoordinatorRoles().size());
    }

    @Test
    @DisplayName("Verificare dacă volunteer nu poate fi acceptat de doi coordinatori")
    void testVolunteerCannotBeUnderTwoCoordinatorsInEvent() {
        Event e = new Event("Event", createValidEventDate(), createValidAdmin());
        Volunteer coordinator1 = new Volunteer("Coordinator", "One", "c1@example.com", "1111111111", 10, TShirtSize.M);
        Volunteer coordinator2 = new Volunteer("Coordinator", "Two", "c2@example.com", "2222222222", 8, TShirtSize.L);
        Volunteer volunteer = new Volunteer("Volunteer", "Test", "vol@example.com", "3333333333", 5, TShirtSize.M);
        
        SimpleDate from = new SimpleDate(2024, 1, 20, 8, 0);
        SimpleDate to = new SimpleDate(2024, 1, 22, 18, 0);
        
        volunteer.applyToEvent(e, from, to);
        
        Coordinator role1 = e.assignCoordinator(coordinator1);
        Coordinator role2 = e.assignCoordinator(coordinator2);
        
        // Prima acceptare reușită
        role1.acceptVolunteer(volunteer, from, to);
        assertTrue(role1.hasSubordinate(volunteer));
        
        // A doua acceptare ar trebui să eșueze
        assertThrows(IllegalArgumentException.class, () -> {
            role2.acceptVolunteer(volunteer, from, to);
        });
    }

    @Test
    @DisplayName("Verificare că nu se pot asigna prea mulți coordinatori cu aceeași voluntar")
    void testMultipleCoordinatorsWithDifferentVolunteers() {
        Event e = new Event("Event", createValidEventDate(), createValidAdmin());
        Volunteer coordinator1 = new Volunteer("Coordinator", "One", "c1@example.com", "1111111111", 10, TShirtSize.M);
        Volunteer coordinator2 = new Volunteer("Coordinator", "Two", "c2@example.com", "2222222222", 8, TShirtSize.L);
        Volunteer vol1 = new Volunteer("Volunteer", "One", "v1@example.com", "3333333333", 5, TShirtSize.M);
        Volunteer vol2 = new Volunteer("Volunteer", "Two", "v2@example.com", "4444444444", 3, TShirtSize.L);
        
        SimpleDate from = new SimpleDate(2024, 1, 20, 8, 0);
        SimpleDate to = new SimpleDate(2024, 1, 22, 18, 0);
        
        // Ambii voluntari se aplică
        vol1.applyToEvent(e, from, to);
        vol2.applyToEvent(e, from, to);
        
        Coordinator role1 = e.assignCoordinator(coordinator1);
        Coordinator role2 = e.assignCoordinator(coordinator2);
        
        // Fiecare coordinator acceptă propriul său voluntar
        role1.acceptVolunteer(vol1, from, to);
        role2.acceptVolunteer(vol2, from, to);
        
        assertEquals(1, role1.getSubordinates().size());
        assertEquals(1, role2.getSubordinates().size());
        assertTrue(role1.hasSubordinate(vol1));
        assertTrue(role2.hasSubordinate(vol2));
        assertFalse(role1.hasSubordinate(vol2));
        assertFalse(role2.hasSubordinate(vol1));
    }

}
