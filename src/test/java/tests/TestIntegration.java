package tests;

import models.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;

public class TestIntegration {

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

    // ==================== INTEGRATION TEST 1: COMPLETE FLOW ====================
    @Test
    @DisplayName("Flow complet: Creare event → aplicare voluntar → asignare coordinator → acceptare")
    void testCompleteEventFlow() {
        // 1. Admin crează event
        Admin admin = new Admin("Admin", "Manager", "admin@example.com", "1234567890");
        Event event = admin.createEvent("Spring Conference", createValidEventDate());
        
        assertTrue(admin.getCreatedEvents().contains(event));
        assertEquals("Spring Conference", event.getName());
        
        // 2. Voluntar se aplică la event
        Volunteer volunteer = new Volunteer("John", "Developer", "john@example.com", "1111111111", 5, TShirtSize.M);
        admin.addVolunteer(volunteer);
        
        SimpleDate from = date(2024, 1, 20, 8, 0);
        SimpleDate to = date(2024, 1, 22, 18, 0);
        volunteer.applyToEvent(event, from, to);
        
        assertTrue(volunteer.getEventAvailabilities().size() > 0);
        assertTrue(event.hasVolunteerApplied(volunteer));
        
        // 3. Admin asignează coordinator
        Volunteer coordinator = new Volunteer("Jane", "Lead", "jane@example.com", "2222222222", 10, TShirtSize.L);
        admin.addVolunteer(coordinator);
        Coordinator coordRole = admin.assignCoordinator(event, coordinator);
        
        assertNotNull(coordRole);
        assertEquals(coordinator, coordRole.getCoordinator());
        
        // 4. Coordinator acceptă voluntar ca subordinat
        coordRole.acceptVolunteer(volunteer, from, to);
        
        assertTrue(coordRole.hasSubordinate(volunteer));
        assertEquals(1, coordRole.getSubordinates().size());
    }

    // ==================== INTEGRATION TEST 2: EVENT CANCELLATION CASCADE ====================
    @Test
    @DisplayName("Anulare event cu cascade: sterge coordonatori, volunteari, și relații")
    void testEventCancellationCascade() {
        // 1. Setup
        Admin admin = new Admin("Admin", "Manager", "admin@example.com", "1234567890");
        Event event = admin.createEvent("Event to Cancel", createValidEventDate());
        
        // 2. Adaugă volunteeri și aplicații
        Volunteer volunteer1 = new Volunteer("Volunteer", "One", "v1@example.com", "1111111111", 5, TShirtSize.M);
        Volunteer volunteer2 = new Volunteer("Volunteer", "Two", "v2@example.com", "2222222222", 3, TShirtSize.L);
        Volunteer coordinator = new Volunteer("Coordinator", "User", "coord@example.com", "3333333333", 10, TShirtSize.XL);
        
        admin.addVolunteer(volunteer1);
        admin.addVolunteer(volunteer2);
        admin.addVolunteer(coordinator);
        
        SimpleDate from = date(2024, 1, 20, 8, 0);
        SimpleDate to = date(2024, 1, 22, 18, 0);
        
        volunteer1.applyToEvent(event, from, to);
        volunteer2.applyToEvent(event, from, to);
        
        // 3. Asignează coordinator
        Coordinator coordRole = admin.assignCoordinator(event, coordinator);
        coordRole.acceptVolunteer(volunteer1, from, to);
        
        // Verifică starea inițială
        assertEquals(1, admin.getCreatedEvents().size());
        assertEquals(1, volunteer1.getEventAvailabilities().size());
        assertEquals(1, volunteer2.getEventAvailabilities().size());
        assertEquals(1, event.getCoordinatorRoles().size());
        
        // 4. Anulează event
        admin.cancelEvent(event);
        
        // Verifică cascade cleanup
        assertEquals(0, admin.getCreatedEvents().size());
        assertEquals(0, volunteer1.getEventAvailabilities().size());
        assertEquals(0, volunteer2.getEventAvailabilities().size());
    }

    // ==================== INTEGRATION TEST 3: DELETE ADMIN CASCADE ====================
    @Test
    @DisplayName("Ștergere admin cu cascade: sterge toate eventurile și relații")
    void testAdminDeleteCascade() {
        // 1. Admin crează mai multe event-uri
        Admin admin = new Admin("Admin", "Manager", "admin@example.com", "1234567890");
        
        Event event1 = admin.createEvent("Event 1", createValidEventDate());
        Event event2 = admin.createEvent("Event 2", createValidEventDate());
        
        // 2. Adaugă volunteeri și aplicații
        Volunteer volunteer = new Volunteer("Volunteer", "User", "vol@example.com", "1111111111", 5, TShirtSize.M);
        admin.addVolunteer(volunteer);
        
        SimpleDate from = date(2024, 1, 20, 8, 0);
        SimpleDate to = date(2024, 1, 22, 18, 0);
        
        volunteer.applyToEvent(event1, from, to);
        volunteer.applyToEvent(event2, from, to);
        
        // Verifică starea inițială
        assertEquals(2, admin.getCreatedEvents().size());
        assertEquals(2, volunteer.getEventAvailabilities().size());
        assertEquals(1, admin.getAllVolunteers().size());
        
        // 3. Șterge admin
        admin.deleteAdmin();
        
        // Verifică cascade cleanup
        assertEquals(0, admin.getCreatedEvents().size());
        assertEquals(0, admin.getAllVolunteers().size());
        assertEquals(0, volunteer.getEventAvailabilities().size());
    }

    // ==================== INTEGRATION TEST 4: MULTIPLE APPLICATIONS ====================
    @Test
    @DisplayName("Aplicări multiple: Voluntar se aplică la mai multe event-uri")
    void testMultipleEventApplications() {
        Admin admin = new Admin("Admin", "Manager", "admin@example.com", "1234567890");
        
        // 1. Crează 3 event-uri
        Event event1 = admin.createEvent("Event 1", createValidEventDate());
        Event event2 = admin.createEvent("Event 2", createValidEventDate());
        Event event3 = admin.createEvent("Event 3", createValidEventDate());
        
        // 2. Voluntar se aplică la toate
        Volunteer volunteer = new Volunteer("Volunteer", "Multi", "vol@example.com", "1111111111", 5, TShirtSize.M);
        admin.addVolunteer(volunteer);
        
        SimpleDate from = date(2024, 1, 20, 8, 0);
        SimpleDate to = date(2024, 1, 22, 18, 0);
        
        volunteer.applyToEvent(event1, from, to);
        volunteer.applyToEvent(event2, from, to);
        volunteer.applyToEvent(event3, from, to);
        
        // Verifică
        assertEquals(3, volunteer.getEventAvailabilities().size());
        assertTrue(event1.hasVolunteerApplied(volunteer));
        assertTrue(event2.hasVolunteerApplied(volunteer));
        assertTrue(event3.hasVolunteerApplied(volunteer));
        
        // 3. Anulează aplicare la un event
        volunteer.cancelApplication(event2);
        
        assertEquals(2, volunteer.getEventAvailabilities().size());
        assertFalse(event2.hasVolunteerApplied(volunteer));
        assertTrue(event1.hasVolunteerApplied(volunteer));
        assertTrue(event3.hasVolunteerApplied(volunteer));
    }

    // ==================== INTEGRATION TEST 5: MULTIPLE COORDINATORS WITH SUBORDINATES ====================
    @Test
    @DisplayName("Multiple coordinatori: Același event cu mai mulți coordinatori și subordinați")
    void testMultipleCoordinatorsWithSubordinates() {
        Admin admin = new Admin("Admin", "Manager", "admin@example.com", "1234567890");
        Event event = admin.createEvent("Large Event", createValidEventDate());
        
        // 1. Crează coordinatori
        Volunteer coord1 = new Volunteer("Coordinator", "One", "c1@example.com", "1111111111", 10, TShirtSize.M);
        Volunteer coord2 = new Volunteer("Coordinator", "Two", "c2@example.com", "2222222222", 8, TShirtSize.L);
        
        admin.addVolunteer(coord1);
        admin.addVolunteer(coord2);
        
        // 2. Crează volunteeri
        Volunteer vol1 = new Volunteer("Volunteer", "One", "v1@example.com", "3333333333", 5, TShirtSize.M);
        Volunteer vol2 = new Volunteer("Volunteer", "Two", "v2@example.com", "4444444444", 3, TShirtSize.M);
        Volunteer vol3 = new Volunteer("Volunteer", "Three", "v3@example.com", "5555555555", 2, TShirtSize.L);
        
        admin.addVolunteer(vol1);
        admin.addVolunteer(vol2);
        admin.addVolunteer(vol3);
        
        SimpleDate from = date(2024, 1, 20, 8, 0);
        SimpleDate to = date(2024, 1, 22, 18, 0);
        
        // 3. Volunteeri se aplică
        vol1.applyToEvent(event, from, to);
        vol2.applyToEvent(event, from, to);
        vol3.applyToEvent(event, from, to);
        
        // 4. Asignează coordinatori
        Coordinator coordRole1 = admin.assignCoordinator(event, coord1);
        Coordinator coordRole2 = admin.assignCoordinator(event, coord2);
        
        // 5. Coordinatori acceptă subordinați
        coordRole1.acceptVolunteer(vol1, from, to);
        coordRole1.acceptVolunteer(vol2, from, to);
        
        coordRole2.acceptVolunteer(vol3, from, to);
        
        // Verifică
        assertEquals(2, event.getCoordinatorRoles().size());
        assertEquals(2, coordRole1.getSubordinates().size());
        assertEquals(1, coordRole2.getSubordinates().size());
        
        assertTrue(coordRole1.hasSubordinate(vol1));
        assertTrue(coordRole1.hasSubordinate(vol2));
        assertTrue(coordRole2.hasSubordinate(vol3));
        assertFalse(coordRole1.hasSubordinate(vol3));
        assertFalse(coordRole2.hasSubordinate(vol1));
    }

    // ==================== INTEGRATION TEST 6: UPDATE AND RESCHEDULE ====================
    @Test
    @DisplayName("Update și reschedule: Modificare disponibilitate voluntar și coordinator")
    void testUpdateAndReschedule() {
        Admin admin = new Admin("Admin", "Manager", "admin@example.com", "1234567890");
        Event event = admin.createEvent("Event", createValidEventDate());
        
        Volunteer volunteer = new Volunteer("Volunteer", "Flex", "vol@example.com", "1111111111", 5, TShirtSize.M);
        Volunteer coordinator = new Volunteer("Coordinator", "Mgr", "coord@example.com", "2222222222", 8, TShirtSize.L);
        
        admin.addVolunteer(volunteer);
        admin.addVolunteer(coordinator);
        
        // 1. Aplicare inițială
        SimpleDate from1 = date(2024, 1, 20, 8, 0);
        SimpleDate to1 = date(2024, 1, 22, 18, 0);
        
        volunteer.applyToEvent(event, from1, to1);
        
        Coordinator coordRole = admin.assignCoordinator(event, coordinator);
        coordRole.acceptVolunteer(volunteer, from1, to1);
        
        // Verifică disponibilitate inițială
        EventVolunteerAvailability av = volunteer.getEventAvailability(event);
        assertEquals(from1, av.getAvailableFrom());
        assertEquals(to1, av.getAvailableTo());
        
        // 2. Update disponibilitate
        SimpleDate from2 = date(2024, 1, 20, 10, 0);
        SimpleDate to2 = date(2024, 1, 22, 16, 0);
        
        volunteer.updateEventAvailability(event, from2, to2);
        
        // Verifică disponibilitate actualizată
        av = volunteer.getEventAvailability(event);
        assertEquals(from2, av.getAvailableFrom());
        assertEquals(to2, av.getAvailableTo());
    }

    // ==================== INTEGRATION TEST 7: COMPLETE WORKFLOW WITH ERRORS ====================
    @Test
    @DisplayName("Workflow complet cu validări: erori și recuperare")
    void testCompleteWorkflowWithValidations() {
        Admin admin = new Admin("Admin", "Manager", "admin@example.com", "1234567890");
        Event event = admin.createEvent("Event", createValidEventDate());
        
        Volunteer volunteer = new Volunteer("Volunteer", "Test", "vol@example.com", "1111111111", 5, TShirtSize.M);
        admin.addVolunteer(volunteer);
        
        SimpleDate from = date(2024, 1, 20, 8, 0);
        SimpleDate to = date(2024, 1, 22, 18, 0);
        
        // 1. Aplicare validă
        volunteer.applyToEvent(event, from, to);
        assertTrue(event.hasVolunteerApplied(volunteer));
        
        // 2. Încercare de aplicare dublă - ar trebui să arunce excepție
        assertThrows(IllegalArgumentException.class, () -> {
            volunteer.applyToEvent(event, from, to);
        });
        
        // 3. Verifică că voluntar este încă aplicat
        assertTrue(event.hasVolunteerApplied(volunteer));
        
        // 4. Încercare asignare coordinator cu volunteer null - ar trebui să arunce excepție
        Volunteer coordinator = new Volunteer("Coordinator", "Mgr", "coord@example.com", "2222222222", 8, TShirtSize.L);
        admin.addVolunteer(coordinator);
        
        // 5. Asignare validă
        Coordinator coordRole = admin.assignCoordinator(event, coordinator);
        assertNotNull(coordRole);
        
        // 6. Acceptare validă
        coordRole.acceptVolunteer(volunteer, from, to);
        assertTrue(coordRole.hasSubordinate(volunteer));
    }

    // ==================== INTEGRATION TEST 8: VOLUNTEER CANNOT BE UNDER TWO COORDINATORS ====================
    @Test
    @DisplayName("Voluntar nu poate fi sub doi coordinatori - integrare completă")
    void testVolunteerCannotBeUnderTwoCoordinatorsIntegration() {
        // 1. Setup
        Admin admin = new Admin("Admin", "Manager", "admin@example.com", "1234567890");
        Event event = admin.createEvent("Complex Event", createValidEventDate());
        
        // 2. Crează 2 coordinatori și 1 voluntar
        Volunteer coordinator1 = new Volunteer("Coordinator", "Alpha", "c1@example.com", "1111111111", 10, TShirtSize.M);
        Volunteer coordinator2 = new Volunteer("Coordinator", "Beta", "c2@example.com", "2222222222", 9, TShirtSize.L);
        Volunteer volunteer = new Volunteer("Volunteer", "Worker", "vol@example.com", "3333333333", 5, TShirtSize.M);
        
        admin.addVolunteer(coordinator1);
        admin.addVolunteer(coordinator2);
        admin.addVolunteer(volunteer);
        
        SimpleDate from = date(2024, 1, 20, 8, 0);
        SimpleDate to = date(2024, 1, 22, 18, 0);
        
        // 3. Voluntarul se aplică
        volunteer.applyToEvent(event, from, to);
        assertTrue(event.hasVolunteerApplied(volunteer));
        
        // 4. Asignează ambii coordinatori la event
        Coordinator role1 = admin.assignCoordinator(event, coordinator1);
        Coordinator role2 = admin.assignCoordinator(event, coordinator2);
        
        assertTrue(event.getCoordinatorRoles().contains(role1));
        assertTrue(event.getCoordinatorRoles().contains(role2));
        
        // 5. Coordinatorul 1 acceptă voluntarul
        role1.acceptVolunteer(volunteer, from, to);
        assertTrue(role1.hasSubordinate(volunteer));
        assertEquals(1, role1.getSubordinates().size());
        
        // 6. Coordinatorul 2 încearcă să accepte același voluntar - ar trebui să arunce excepție
        assertThrows(IllegalArgumentException.class, () -> {
            role2.acceptVolunteer(volunteer, from, to);
        }, "Volunteer should not be accepted by second coordinator");
        
        // 7. Verifică starea finală
        assertTrue(role1.hasSubordinate(volunteer));
        assertFalse(role2.hasSubordinate(volunteer));
        assertEquals(1, role1.getSubordinates().size());
        assertEquals(0, role2.getSubordinates().size());
    }

    // ==================== INTEGRATION TEST 9: COORDINATOR CANNOT BE DUPLICATE ====================
    @Test
    @DisplayName("Coordinator nu poate fi asignat de două ori la același event")
    void testCoordinatorCannotAssignedTwice() {
        Admin admin = new Admin("Admin", "Manager", "admin@example.com", "1234567890");
        Event event = admin.createEvent("Event", createValidEventDate());
        
        Volunteer coordinator = new Volunteer("Coordinator", "Test", "coord@example.com", "1111111111", 10, TShirtSize.M);
        admin.addVolunteer(coordinator);
        
        // Prima asignare
        Coordinator role1 = admin.assignCoordinator(event, coordinator);
        assertEquals(1, event.getCoordinatorRoles().size());
        
        // A doua asignare - ar trebui să returneze rolul existent
        Coordinator role2 = admin.assignCoordinator(event, coordinator);
        
        assertSame(role1, role2);
        assertEquals(1, event.getCoordinatorRoles().size());
    }

    // ==================== INTEGRATION TEST 10: COMPLEX SCENARIO WITH MULTIPLE CONSTRAINTS ====================
    @Test
    @DisplayName("Scenariu complex: Multiple events, multiple coordinatori, volunteer constraints")
    void testComplexScenarioWithMultipleConstraints() {
        Admin admin = new Admin("Admin", "Manager", "admin@example.com", "1234567890");
        
        // Crează 2 event-uri
        Event event1 = admin.createEvent("Event 1", createValidEventDate());
        Event event2 = admin.createEvent("Event 2", createValidEventDate());
        
        // Crează volunteeri
        Volunteer coord1 = new Volunteer("Coordinator", "First", "c1@example.com", "1111111111", 10, TShirtSize.M);
        Volunteer coord2 = new Volunteer("Coordinator", "Second", "c2@example.com", "2222222222", 9, TShirtSize.L);
        Volunteer vol1 = new Volunteer("Volunteer", "First", "v1@example.com", "3333333333", 5, TShirtSize.M);
        Volunteer vol2 = new Volunteer("Volunteer", "Second", "v2@example.com", "4444444444", 3, TShirtSize.L);
        
        admin.addVolunteer(coord1);
        admin.addVolunteer(coord2);
        admin.addVolunteer(vol1);
        admin.addVolunteer(vol2);
        
        SimpleDate from = date(2024, 1, 20, 8, 0);
        SimpleDate to = date(2024, 1, 22, 18, 0);
        
        // === EVENT 1 SETUP ===
        // Ambii volunteeri se aplică la event 1
        vol1.applyToEvent(event1, from, to);
        vol2.applyToEvent(event1, from, to);
        
        // coord1 este coordinator pentru event1
        Coordinator role1 = admin.assignCoordinator(event1, coord1);
        
        // coord1 acceptă ambii volunteeri
        role1.acceptVolunteer(vol1, from, to);
        role1.acceptVolunteer(vol2, from, to);
        
        assertEquals(2, role1.getSubordinates().size());
        
        // === EVENT 2 SETUP ===
        // Ambii volunteeri se aplică la event 2
        vol1.applyToEvent(event2, from, to);
        vol2.applyToEvent(event2, from, to);
        
        // coord2 este coordinator pentru event2
        Coordinator role2 = admin.assignCoordinator(event2, coord2);
        
        // coord2 acceptă ambii volunteeri
        role2.acceptVolunteer(vol1, from, to);
        role2.acceptVolunteer(vol2, from, to);
        
        assertEquals(2, role2.getSubordinates().size());
        
        // === VERIFICATION ===
        // vol1 și vol2 trebuie să fie sub role1 în event1
        assertTrue(role1.hasSubordinate(vol1));
        assertTrue(role1.hasSubordinate(vol2));
        
        // vol1 și vol2 trebuie să fie sub role2 în event2
        assertTrue(role2.hasSubordinate(vol1));
        assertTrue(role2.hasSubordinate(vol2));
        
        // Ambii volunteeri au 2 aplicații (la 2 event-uri)
        assertEquals(2, vol1.getEventAvailabilities().size());
        assertEquals(2, vol2.getEventAvailabilities().size());
    }

    // ==================== INTEGRATION TEST 11: VOLUNTEER CANNOT ENROLL TWICE IN SAME EVENT ====================
    @Test
    @DisplayName("Voluntar nu se poate înrola de două ori în același event")
    void testVolunteerCannotEnrollTwiceInSameEvent() {
        Admin admin = new Admin("Admin", "Manager", "admin@example.com", "1234567890");
        Event event = admin.createEvent("Event", createValidEventDate());
        
        Volunteer volunteer = new Volunteer("Volunteer", "Test", "vol@example.com", "1111111111", 5, TShirtSize.M);
        admin.addVolunteer(volunteer);
        
        SimpleDate from = date(2024, 1, 20, 8, 0);
        SimpleDate to = date(2024, 1, 22, 18, 0);
        
        // Prima înrolare - succesul
        volunteer.applyToEvent(event, from, to);
        assertEquals(1, volunteer.getEventAvailabilities().size());
        
        // A doua înrolare - ar trebui să eșueze
        assertThrows(IllegalArgumentException.class, () -> {
            volunteer.applyToEvent(event, from, to);
        });
        
        // Verifică că a rămas cu o singură aplicație
        assertEquals(1, volunteer.getEventAvailabilities().size());
    }

    // ==================== INTEGRATION TEST 12: COORDINATOR CANNOT BE VOLUNTEER IN SAME EVENT ====================
    @Test
    @DisplayName("Un coordinator nu poate fi acceptat ca subordinat în același event")
    void testCoordinatorCannotBeVolunteerInSameEvent() {
        Admin admin = new Admin("Admin", "Manager", "admin@example.com", "1234567890");
        Event event = admin.createEvent("Event", createValidEventDate());
        
        Volunteer coordinator = new Volunteer("Coordinator", "Test", "coord@example.com", "1111111111", 10, TShirtSize.M);
        Volunteer regularVolunteer = new Volunteer("Volunteer", "Regular", "vol@example.com", "2222222222", 5, TShirtSize.L);
        
        admin.addVolunteer(coordinator);
        admin.addVolunteer(regularVolunteer);
        
        SimpleDate from = date(2024, 1, 20, 8, 0);
        SimpleDate to = date(2024, 1, 22, 18, 0);
        
        // coordinator se aplică
        coordinator.applyToEvent(event, from, to);
        
        // Crează roluri de coordinator
        Coordinator coordRole = admin.assignCoordinator(event, coordinator);
        
        // regularVolunteer se aplică
        regularVolunteer.applyToEvent(event, from, to);
        
        // coordRole încearcă să accepte coordinator ca subordinat
        assertThrows(IllegalArgumentException.class, () -> {
            coordRole.acceptVolunteer(coordinator, from, to);
        });
        
        // Dar poate accepta regularVolunteer
        coordRole.acceptVolunteer(regularVolunteer, from, to);
        assertTrue(coordRole.hasSubordinate(regularVolunteer));
    }

}
