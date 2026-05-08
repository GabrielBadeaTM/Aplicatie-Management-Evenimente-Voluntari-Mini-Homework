package tests;

import models.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;

public class TestAdminValidation {

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

    // ==================== CONSTRUCTOR TESTS ====================
    @Test
    @DisplayName("Admin constructor cu parametri valizi")
    void testValidAdminConstructor() {
        Admin admin = new Admin("John", "Manager", "john@example.com", "1234567890");
        
        assertEquals("John", admin.getFirstName());
        assertEquals("Manager", admin.getLastName());
        assertEquals("john@example.com", admin.getEmail());
        assertEquals("1234567890", admin.getPhone());
    }

    @Test
    @DisplayName("Admin default constructor")
    void testDefaultAdminConstructor() {
        Admin admin = new Admin();
        
        assertNotNull(admin);
        assertNotNull(admin.getCreatedEvents());
        assertNotNull(admin.getAllVolunteers());
        assertTrue(admin.getCreatedEvents().isEmpty());
        assertTrue(admin.getAllVolunteers().isEmpty());
    }

    // ==================== EVENT CREATION TESTS ====================
    @Test
    @DisplayName("Creare event - adăugare în listă")
    void testCreateEventAddsToList() {
        Admin admin = createValidAdmin();
        Event event = admin.createEvent("New Event", createValidEventDate());
        
        assertTrue(admin.getCreatedEvents().contains(event));
        assertEquals(1, admin.getCreatedEvents().size());
    }

    @Test
    @DisplayName("Creare multiple event-uri")
    void testCreateMultipleEvents() {
        Admin admin = createValidAdmin();
        
        Event event1 = admin.createEvent("Event 1", createValidEventDate());
        Event event2 = admin.createEvent("Event 2", createValidEventDate());
        
        assertEquals(2, admin.getCreatedEvents().size());
        assertTrue(admin.getCreatedEvents().contains(event1));
        assertTrue(admin.getCreatedEvents().contains(event2));
    }

    @Test
    @DisplayName("Creare event duplicate (aceeași nume și dată) aruncă excepție")
    void testCreateDuplicateEventThrows() {
        Admin admin = createValidAdmin();
        EventDate date = createValidEventDate();
        
        admin.createEvent("Duplicate Event", date);
        
        assertThrows(IllegalArgumentException.class, () -> {
            admin.createEvent("Duplicate Event", date);
        });
    }

    @Test
    @DisplayName("Creare event cu detalii diferite (permis)")
    void testCreateDifferentEvents() {
        Admin admin = createValidAdmin();
        EventDate date1 = createValidEventDate();
        EventDate date2 = new EventDate(
            date(2024, 2, 1, 8, 0),
            date(2024, 2, 3, 17, 0),
            date(2024, 1, 20, 9, 0),
            date(2024, 1, 30, 17, 0)
        );
        
        Event event1 = admin.createEvent("Event", date1);
        Event event2 = admin.createEvent("Event", date2);
        
        assertEquals(2, admin.getCreatedEvents().size());
    }

    // ==================== EVENT CANCELLATION TESTS ====================
    @Test
    @DisplayName("Anulare event - eliminare din listă")
    void testCancelEventRemovesFromList() {
        Admin admin = createValidAdmin();
        Event event = admin.createEvent("Event to Cancel", createValidEventDate());
        
        assertEquals(1, admin.getCreatedEvents().size());
        admin.cancelEvent(event);
        assertEquals(0, admin.getCreatedEvents().size());
    }

    @Test
    @DisplayName("Anulare event inexistent - doar mesaj")
    void testCancelNonexistentEvent() {
        Admin admin = createValidAdmin();
        Event nonexistent = new Event("Nonexistent", createValidEventDate(), admin);
        
        // Ar trebui să printeze doar mesaj, nu să arunce excepție
        admin.cancelEvent(nonexistent);
        assertEquals(0, admin.getCreatedEvents().size());
    }

    @Test
    @DisplayName("Anulare event cu cascade (volunteeri înrolați sunt notificați)")
    void testCancelEventWithCascadeVolunteers() {
        Admin admin = createValidAdmin();
        Event event = admin.createEvent("Event", createValidEventDate());
        
        Volunteer v = new Volunteer("Volunteer", "Test", "vol@example.com", "1234567890", 5, TShirtSize.M);
        admin.addVolunteer(v);
        
        SimpleDate from = date(2024, 1, 20, 8, 0);
        SimpleDate to = date(2024, 1, 22, 18, 0);
        v.applyToEvent(event, from, to);
        
        assertEquals(1, v.getEventAvailabilities().size());
        
        admin.cancelEvent(event);
        
        assertEquals(0, v.getEventAvailabilities().size());
        assertEquals(0, admin.getCreatedEvents().size());
    }

    // ==================== VOLUNTEER MANAGEMENT TESTS ====================
    @Test
    @DisplayName("Adăugare volunteer")
    void testAddVolunteer() {
        Admin admin = createValidAdmin();
        Volunteer v = new Volunteer("Volunteer", "Test", "vol@example.com", "1234567890", 5, TShirtSize.M);
        
        admin.addVolunteer(v);
        
        assertTrue(admin.getAllVolunteers().contains(v));
        assertEquals(1, admin.getAllVolunteers().size());
    }

    @Test
    @DisplayName("Adăugare volunteer duplicate (nu se adaugă din nou)")
    void testAddDuplicateVolunteer() {
        Admin admin = createValidAdmin();
        Volunteer v = new Volunteer("Volunteer", "Test", "vol@example.com", "1234567890", 5, TShirtSize.M);
        
        admin.addVolunteer(v);
        admin.addVolunteer(v);
        
        assertEquals(1, admin.getAllVolunteers().size());
    }

    @Test
    @DisplayName("Adăugare multiple volunteeri")
    void testAddMultipleVolunteers() {
        Admin admin = createValidAdmin();
        
        Volunteer v1 = new Volunteer("Volunteer", "One", "vol1@example.com", "1111111111", 5, TShirtSize.M);
        Volunteer v2 = new Volunteer("Volunteer", "Two", "vol2@example.com", "2222222222", 3, TShirtSize.L);
        
        admin.addVolunteer(v1);
        admin.addVolunteer(v2);
        
        assertEquals(2, admin.getAllVolunteers().size());
        assertTrue(admin.getAllVolunteers().contains(v1));
        assertTrue(admin.getAllVolunteers().contains(v2));
    }

    @Test
    @DisplayName("Obținere volunteeri - lista validă")
    void testGetAllVolunteers() {
        Admin admin = createValidAdmin();
        ArrayList<Volunteer> volunteers = admin.getAllVolunteers();
        
        assertNotNull(volunteers);
        assertTrue(volunteers.isEmpty());
    }

    // ==================== COORDINATOR ASSIGNMENT TESTS ====================
    @Test
    @DisplayName("Asignare coordinator la event")
    void testAssignCoordinator() {
        Admin admin = createValidAdmin();
        Event event = admin.createEvent("Event", createValidEventDate());
        Volunteer v = new Volunteer("Volunteer", "Coord", "coord@example.com", "1234567890", 5, TShirtSize.M);
        
        Coordinator coord = admin.assignCoordinator(event, v);
        
        assertNotNull(coord);
        assertEquals(v, coord.getCoordinator());
        assertEquals(event, coord.getEvent());
    }

    @Test
    @DisplayName("Asignare multiple coordinatori la același event")
    void testAssignMultipleCoordinators() {
        Admin admin = createValidAdmin();
        Event event = admin.createEvent("Event", createValidEventDate());
        
        Volunteer v1 = new Volunteer("Volunteer", "One", "v1@example.com", "1111111111", 5, TShirtSize.M);
        Volunteer v2 = new Volunteer("Volunteer", "Two", "v2@example.com", "2222222222", 3, TShirtSize.L);
        
        Coordinator coord1 = admin.assignCoordinator(event, v1);
        Coordinator coord2 = admin.assignCoordinator(event, v2);
        
        assertEquals(2, event.getCoordinatorRoles().size());
        assertTrue(event.getCoordinatorRoles().contains(coord1));
        assertTrue(event.getCoordinatorRoles().contains(coord2));
    }

    @Test
    @DisplayName("Ștergere admin cu cascade (toate eventurile sunt șterse)")
    void testDeleteAdminWithCascade() {
        Admin admin = createValidAdmin();
        
        Event event1 = admin.createEvent("Event 1", createValidEventDate());
        Event event2 = admin.createEvent("Event 2", createValidEventDate());
        
        assertEquals(2, admin.getCreatedEvents().size());
        
        admin.deleteAdmin();
        
        assertEquals(0, admin.getCreatedEvents().size());
        assertEquals(0, admin.getAllVolunteers().size());
    }

    // ==================== GETTERS TESTS ====================
    @Test
    @DisplayName("Getter createdEvents - inițial gol")
    void testGetCreatedEventsEmpty() {
        Admin admin = createValidAdmin();
        assertNotNull(admin.getCreatedEvents());
        assertTrue(admin.getCreatedEvents().isEmpty());
    }

    @Test
    @DisplayName("Getter createdEvents - conține event-uri")
    void testGetCreatedEventsNotEmpty() {
        Admin admin = createValidAdmin();
        admin.createEvent("Event 1", createValidEventDate());
        admin.createEvent("Event 2", createValidEventDate());
        
        ArrayList<Event> events = admin.getCreatedEvents();
        assertEquals(2, events.size());
    }

    // ==================== DUPLICATE COORDINATOR ASSIGNMENT TESTS ====================
    @Test
    @DisplayName("Asignare coordinator la același event - obținere rol existent")
    void testAssignSameCoordinatorTwiceReturnsExistingRole() {
        Admin admin = createValidAdmin();
        Event event = admin.createEvent("Event", createValidEventDate());
        Volunteer coordinator = new Volunteer("Coordinator", "Test", "coord@example.com", "1234567890", 10, TShirtSize.M);
        admin.addVolunteer(coordinator);
        
        // Prima asignare
        Coordinator role1 = admin.assignCoordinator(event, coordinator);
        assertEquals(1, event.getCoordinatorRoles().size());
        
        // A doua asignare aceluiași coordinator - ar trebui să returneze rolul existent
        Coordinator role2 = admin.assignCoordinator(event, coordinator);
        
        assertSame(role1, role2);
        assertEquals(1, event.getCoordinatorRoles().size());
    }

    @Test
    @DisplayName("Ștergere coordinator inexistent")
    void testRemoveNonexistentCoordinator() {
        Admin admin = createValidAdmin();
        Event event = admin.createEvent("Event", createValidEventDate());
        Volunteer coordinator1 = new Volunteer("Coordinator", "One", "c1@example.com", "1111111111", 10, TShirtSize.M);
        Volunteer coordinator2 = new Volunteer("Coordinator", "Two", "c2@example.com", "2222222222", 8, TShirtSize.L);
        
        admin.addVolunteer(coordinator1);
        admin.addVolunteer(coordinator2);
        
        admin.assignCoordinator(event, coordinator1);
        
        // Încearcă să șteargă coordinator2 care nu a fost asignat
        admin.removeCoordinator(event, coordinator2);
        
        // coordinator1 ar trebui să rămână
        assertEquals(1, event.getCoordinatorRoles().size());
        assertTrue(event.isCoordinator(coordinator1));
    }

    @Test
    @DisplayName("Ștergere coordinator care a fost asignat")
    void testRemoveAssignedCoordinator() {
        Admin admin = createValidAdmin();
        Event event = admin.createEvent("Event", createValidEventDate());
        Volunteer coordinator = new Volunteer("Coordinator", "Test", "coord@example.com", "1234567890", 10, TShirtSize.M);
        admin.addVolunteer(coordinator);
        
        admin.assignCoordinator(event, coordinator);
        assertEquals(1, event.getCoordinatorRoles().size());
        
        admin.removeCoordinator(event, coordinator);
        
        assertEquals(0, event.getCoordinatorRoles().size());
        assertFalse(event.isCoordinator(coordinator));
    }

    // ==================== MULTIPLE COORDINATORS WITH SAME VOLUNTEER CONSTRAINT TESTS ====================
    @Test
    @DisplayName("Voluntar nu poate fi acceptat de coordinatori diferiți în același event")
    void testVolunteerCannotBeUnderMultipleCoordinators() {
        Admin admin = createValidAdmin();
        Event event = admin.createEvent("Event", createValidEventDate());
        
        Volunteer coordinator1 = new Volunteer("Coordinator", "One", "c1@example.com", "1111111111", 10, TShirtSize.M);
        Volunteer coordinator2 = new Volunteer("Coordinator", "Two", "c2@example.com", "2222222222", 8, TShirtSize.L);
        Volunteer volunteer = new Volunteer("Volunteer", "Test", "vol@example.com", "3333333333", 5, TShirtSize.M);
        
        admin.addVolunteer(coordinator1);
        admin.addVolunteer(coordinator2);
        admin.addVolunteer(volunteer);
        
        SimpleDate from = date(2024, 1, 20, 8, 0);
        SimpleDate to = date(2024, 1, 22, 18, 0);
        
        volunteer.applyToEvent(event, from, to);
        
        Coordinator role1 = admin.assignCoordinator(event, coordinator1);
        Coordinator role2 = admin.assignCoordinator(event, coordinator2);
        
        // Coordinatorul 1 acceptă voluntarul
        role1.acceptVolunteer(volunteer, from, to);
        assertTrue(role1.hasSubordinate(volunteer));
        
        // Coordinatorul 2 încearcă să accepte același voluntar
        assertThrows(IllegalArgumentException.class, () -> {
            role2.acceptVolunteer(volunteer, from, to);
        });
    }

    @Test
    @DisplayName("Voluntar poate fi sub coordinatori diferiți în event-uri diferite")
    void testVolunteerCanBeUnderDifferentCoordinatorsInDifferentEvents() {
        Admin admin = createValidAdmin();
        Event event1 = admin.createEvent("Event 1", createValidEventDate());
        Event event2 = admin.createEvent("Event 2", createValidEventDate());
        
        Volunteer coordinator1 = new Volunteer("Coordinator", "One", "c1@example.com", "1111111111", 10, TShirtSize.M);
        Volunteer coordinator2 = new Volunteer("Coordinator", "Two", "c2@example.com", "2222222222", 8, TShirtSize.L);
        Volunteer volunteer = new Volunteer("Volunteer", "Test", "vol@example.com", "3333333333", 5, TShirtSize.M);
        
        admin.addVolunteer(coordinator1);
        admin.addVolunteer(coordinator2);
        admin.addVolunteer(volunteer);
        
        SimpleDate from = date(2024, 1, 20, 8, 0);
        SimpleDate to = date(2024, 1, 22, 18, 0);
        
        volunteer.applyToEvent(event1, from, to);
        volunteer.applyToEvent(event2, from, to);
        
        Coordinator role1 = admin.assignCoordinator(event1, coordinator1);
        Coordinator role2 = admin.assignCoordinator(event2, coordinator2);
        
        // Ambii coordinatori acceptă voluntarul în event-urile lor
        role1.acceptVolunteer(volunteer, from, to);
        role2.acceptVolunteer(volunteer, from, to);
        
        assertTrue(role1.hasSubordinate(volunteer));
        assertTrue(role2.hasSubordinate(volunteer));
    }

}
