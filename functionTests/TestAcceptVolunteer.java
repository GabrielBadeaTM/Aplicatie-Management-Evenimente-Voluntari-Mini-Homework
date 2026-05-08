import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;


public class TestAcceptVolunteer {

    // ==================== HELPERS ====================

    private SimpleDate date(int year, int month, int day, int hour, int minute) {
        return new SimpleDate(year, month, day, hour, minute);
    }

    private EventDate createValidEventDate() {
        SimpleDate regStart  = date(2024, 1,  1,  9,  0);
        SimpleDate regEnd    = date(2024, 1, 15, 17,  0);
        SimpleDate eventStart = date(2024, 1, 20,  8,  0);
        SimpleDate eventEnd   = date(2024, 1, 22, 18,  0);
        return new EventDate(eventStart, eventEnd, regStart, regEnd);
    }

    /** Date de disponibilitate valide (acoperă integral intervalul eventului). */
    private SimpleDate validFrom() { return date(2024, 1, 20, 8, 0); }
    private SimpleDate validTo()   { return date(2024, 1, 22, 18, 0); }

    private Admin createValidAdmin() {
        return new Admin("John", "Admin", "admin@example.com", "1234567890");
    }

    private Event createValidEvent() {
        return new Event("Valid Event", createValidEventDate(), createValidAdmin());
    }

    private Volunteer createVolunteer(String firstName, String lastName, String email, String phone) {
        return new Volunteer(firstName, lastName, email, phone, 3, TShirtSize.M);
    }

    // ==================== HAPPY-PATH ====================

    @Test
    @DisplayName("acceptVolunteer - cale validă: voluntarul a aplicat și este acceptat")
    void testAcceptVolunteer_Valid() {
        Event event = createValidEvent();
        Volunteer coordinator = createVolunteer("Coord", "One", "c@example.com", "1111111111");
        Volunteer volunteer   = createVolunteer("Vol",   "One", "v@example.com", "2222222222");

        volunteer.applyToEvent(event, validFrom(), validTo());
        Coordinator role = event.assignCoordinator(coordinator);

        role.acceptVolunteer(volunteer, validFrom(), validTo());

        assertTrue(role.hasSubordinate(volunteer));
        assertEquals(1, role.getSubordinates().size());
    }

    @Test
    @DisplayName("acceptVolunteer - voluntarul este adăugat la lista de subordinați")
    void testAcceptVolunteer_AddsToSubordinates() {
        Event event     = createValidEvent();
        Volunteer coord = createVolunteer("Coord", "Test", "coord@example.com", "1111111111");
        Volunteer vol   = createVolunteer("Vol",   "Test", "vol@example.com",   "2222222222");

        vol.applyToEvent(event, validFrom(), validTo());
        Coordinator role = event.assignCoordinator(coord);
        role.acceptVolunteer(vol, validFrom(), validTo());

        assertTrue(role.getSubordinates().contains(vol));
    }

    // ==================== VALIDARE 1: SELF-ASSIGNMENT ====================

    @Test
    @DisplayName("acceptVolunteer - [V1] coordinatorul nu se poate accepta pe sine (bucla [2] = 0)")
    void testAcceptVolunteer_SelfThrows_NoOtherCoordinators() {
        Event event     = createValidEvent();
        Volunteer coord = createVolunteer("Coord", "Self", "self@example.com", "1111111111");

        // Coordinator creat direct (fără event.assignCoordinator) → bucla [2] are 0 iterații
        Coordinator role = new Coordinator(event, coord);

        assertThrows(IllegalArgumentException.class, () ->
            role.acceptVolunteer(coord, validFrom(), validTo())
        );
    }

    @Test
    @DisplayName("acceptVolunteer - [V1] coordinatorul nu se poate accepta pe sine (bucla [2] = 1)")
    void testAcceptVolunteer_SelfThrows_WithRegisteredRole() {
        Event event     = createValidEvent();
        Volunteer coord = createVolunteer("Coord", "Self", "self@example.com", "1111111111");

        Coordinator role = event.assignCoordinator(coord); // bucla [2] va itera o dată

        assertThrows(IllegalArgumentException.class, () ->
            role.acceptVolunteer(coord, validFrom(), validTo())
        );
    }

    // ==================== VALIDARE 2: VOLUNTARUL ESTE DEJA COORDINATOR ====================

    @Test
    @DisplayName("acceptVolunteer - [V2] voluntarul este coordinator → bucla [2] cu 1 iterație aruncă excepție")
    void testAcceptVolunteer_VolunteerIsCoordinator_OneIteration() {
        Event event  = createValidEvent();
        Volunteer c1 = createVolunteer("Coord", "One", "c1@example.com", "1111111111");
        Volunteer c2 = createVolunteer("Coord", "Two", "c2@example.com", "2222222222");

        c2.applyToEvent(event, validFrom(), validTo());

        Coordinator role1 = event.assignCoordinator(c1);
        event.assignCoordinator(c2); // c2 devine coordinator → apare în event.getCoordinatorRoles()

        // c1 încearcă să-l accepte pe c2 ca subordinat — bucla [2] face 2 iterații, c2 se detectează
        assertThrows(IllegalArgumentException.class, () ->
            role1.acceptVolunteer(c2, validFrom(), validTo())
        );
    }

    @Test
    @DisplayName("acceptVolunteer - [V2] voluntarul este coordinator → bucla [2] cu N iterații, detectat la final")
    void testAcceptVolunteer_VolunteerIsCoordinator_NIterations_DetectedLast() {
        Event event  = createValidEvent();
        Volunteer c1 = createVolunteer("Coord", "One",   "c1@example.com", "1111111111");
        Volunteer c2 = createVolunteer("Coord", "Two",   "c2@example.com", "2222222222");
        Volunteer c3 = createVolunteer("Coord", "Three", "c3@example.com", "3333333333");
        Volunteer c4 = createVolunteer("Coord", "Four",  "c4@example.com", "4444444444");

        c4.applyToEvent(event, validFrom(), validTo());

        Coordinator role1 = event.assignCoordinator(c1);
        event.assignCoordinator(c2);
        event.assignCoordinator(c3);
        event.assignCoordinator(c4); // c4 este ultimul → detectat la ultima iterație

        // c1 încearcă să-l accepte pe c4 (ultimul coordinator) — parcurge toată bucla
        assertThrows(IllegalArgumentException.class, () ->
            role1.acceptVolunteer(c4, validFrom(), validTo())
        );
    }

    @Test
    @DisplayName("acceptVolunteer - [V2] bucla [2] cu 0 iterații: voluntarul NU este coordinator (niciun role înregistrat)")
    void testAcceptVolunteer_V2Loop_ZeroIterations_NoException() {
        Event event  = createValidEvent();
        Volunteer c  = createVolunteer("Coord", "One", "c@example.com", "1111111111");
        Volunteer v  = createVolunteer("Vol",   "One", "v@example.com", "2222222222");

        v.applyToEvent(event, validFrom(), validTo());

        // Coordinator creat direct — event.getCoordinatorRoles() este gol → bucla [2] = 0 iterații
        Coordinator role = new Coordinator(event, c);

        assertDoesNotThrow(() -> role.acceptVolunteer(v, validFrom(), validTo()));
        assertTrue(role.hasSubordinate(v));
    }

    // ==================== VALIDARE 3: VOLUNTARUL NU A APLICAT ====================

    @Test
    @DisplayName("acceptVolunteer - [V3] voluntarul nu a aplicat la event → aruncă excepție")
    void testAcceptVolunteer_NotApplied_Throws() {
        Event event  = createValidEvent();
        Volunteer c  = createVolunteer("Coord", "Test", "c@example.com", "1111111111");
        Volunteer v  = createVolunteer("Vol",   "Test", "v@example.com", "2222222222");

        Coordinator role = event.assignCoordinator(c);

        // v nu a apelat applyToEvent → validarea [V3] aruncă excepție
        assertThrows(IllegalArgumentException.class, () ->
            role.acceptVolunteer(v, validFrom(), validTo())
        );
    }

    @Test
    @DisplayName("acceptVolunteer - [V3] bucla [2] cu 0 iterații, voluntarul nu a aplicat → excepție la V3")
    void testAcceptVolunteer_NotApplied_NoCoordinators_Throws() {
        Event event  = createValidEvent();
        Volunteer c  = createVolunteer("Coord", "Test", "c@example.com", "1111111111");
        Volunteer v  = createVolunteer("Vol",   "Test", "v@example.com", "2222222222");

        // Coordinator creat direct → bucla [2] = 0, dar v nu a aplicat → excepție la V3
        Coordinator role = new Coordinator(event, c);

        assertThrows(IllegalArgumentException.class, () ->
            role.acceptVolunteer(v, validFrom(), validTo())
        );
    }

    @Test
    @DisplayName("acceptVolunteer - [V3] un alt voluntar a aplicat, dar cel vizat nu → excepție")
    void testAcceptVolunteer_OtherApplied_TargetNotApplied_Throws() {
        Event event  = createValidEvent();
        Volunteer c  = createVolunteer("Coord", "Test", "c@example.com",  "1111111111");
        Volunteer v1 = createVolunteer("Vol",   "One",  "v1@example.com", "2222222222");
        Volunteer v2 = createVolunteer("Vol",   "Two",  "v2@example.com", "3333333333");

        v1.applyToEvent(event, validFrom(), validTo()); // v1 a aplicat, v2 nu
        Coordinator role = event.assignCoordinator(c);

        assertThrows(IllegalArgumentException.class, () ->
            role.acceptVolunteer(v2, validFrom(), validTo())
        );
    }

    // ==================== VALIDARE 4: VOLUNTARUL ESTE SUB ALT COORDINATOR ====================

    @Test
    @DisplayName("acceptVolunteer - [V4] voluntarul este deja sub alt coordinator → aruncă excepție")
    void testAcceptVolunteer_AlreadyUnderAnotherCoordinator_Throws() {
        Event event  = createValidEvent();
        Volunteer c1 = createVolunteer("Coord", "One", "c1@example.com", "1111111111");
        Volunteer c2 = createVolunteer("Coord", "Two", "c2@example.com", "2222222222");
        Volunteer v  = createVolunteer("Vol",   "One", "v@example.com",  "3333333333");

        v.applyToEvent(event, validFrom(), validTo());

        Coordinator role1 = event.assignCoordinator(c1);
        Coordinator role2 = event.assignCoordinator(c2);

        role1.acceptVolunteer(v, validFrom(), validTo()); // v este sub c1
        assertTrue(role1.hasSubordinate(v));

        // c2 încearcă să-l accepte → bucla [4] detectează că v este deja sub c1
        assertThrows(IllegalArgumentException.class, () ->
            role2.acceptVolunteer(v, validFrom(), validTo())
        );
    }

    @Test
    @DisplayName("acceptVolunteer - [V4] voluntarul sub ultimul din N coordinatori → excepție la ultima iterație")
    void testAcceptVolunteer_AlreadyUnderLastOfNCoordinators_Throws() {
        Event event  = createValidEvent();
        Volunteer c1 = createVolunteer("Coord", "One",   "c1@example.com", "1111111111");
        Volunteer c2 = createVolunteer("Coord", "Two",   "c2@example.com", "2222222222");
        Volunteer c3 = createVolunteer("Coord", "Three", "c3@example.com", "3333333333");
        Volunteer c4 = createVolunteer("Coord", "Four",  "c4@example.com", "4444444444");
        Volunteer v  = createVolunteer("Vol",   "One",   "v@example.com",  "5555555555");

        v.applyToEvent(event, validFrom(), validTo());

        Coordinator role1 = event.assignCoordinator(c1);
        Coordinator role2 = event.assignCoordinator(c2);
        Coordinator role3 = event.assignCoordinator(c3);
        Coordinator role4 = event.assignCoordinator(c4);

        role4.acceptVolunteer(v, validFrom(), validTo()); // v este sub c4 (ultimul)

        // c1 încearcă → bucla [4] parcurge c2, c3, c4 înainte să detecteze
        assertThrows(IllegalArgumentException.class, () ->
            role1.acceptVolunteer(v, validFrom(), validTo())
        );
    }

    @Test
    @DisplayName("acceptVolunteer - [V4] condiție 'skip own role': coordinatorul nu se blochează pe sine")
    void testAcceptVolunteer_V4_SkipsOwnRole() {
        Event event  = createValidEvent();
        Volunteer c1 = createVolunteer("Coord", "One", "c1@example.com", "1111111111");
        Volunteer v  = createVolunteer("Vol",   "One", "v@example.com",  "2222222222");

        v.applyToEvent(event, validFrom(), validTo());
        Coordinator role = event.assignCoordinator(c1);

        // c1 acceptă v prima dată — bucla [4] vede rolul lui c1 cu v absent → skip la own role
        assertDoesNotThrow(() -> role.acceptVolunteer(v, validFrom(), validTo()));
        assertTrue(role.hasSubordinate(v));
    }

    @Test
    @DisplayName("acceptVolunteer - [V4] alt coordinator fără subordoanti → voluntarul este acceptat fără excepție")
    void testAcceptVolunteer_V4_OtherCoordinatorHasNoSubordinates_NoException() {
        Event event  = createValidEvent();
        Volunteer c1 = createVolunteer("Coord", "One", "c1@example.com", "1111111111");
        Volunteer c2 = createVolunteer("Coord", "Two", "c2@example.com", "2222222222");
        Volunteer v  = createVolunteer("Vol",   "One", "v@example.com",  "3333333333");

        v.applyToEvent(event, validFrom(), validTo());

        Coordinator role1 = event.assignCoordinator(c1);
        event.assignCoordinator(c2); // c2 există dar nu are niciun subordinat

        // c1 acceptă v — c2 nu are v → bucla [4] trece fără excepție
        assertDoesNotThrow(() -> role1.acceptVolunteer(v, validFrom(), validTo()));
        assertTrue(role1.hasSubordinate(v));
    }

    // ==================== EFECT SIDE-EFFECT: ACTUALIZARE DISPONIBILITATE ====================

    @Test
    @DisplayName("acceptVolunteer - disponibilitatea este actualizată cu noile date")
    void testAcceptVolunteer_UpdatesAvailabilityDates() {
        Event event  = createValidEvent();
        Volunteer c  = createVolunteer("Coord", "Test", "c@example.com", "1111111111");
        Volunteer v  = createVolunteer("Vol",   "Test", "v@example.com", "2222222222");

        SimpleDate origFrom = date(2024, 1, 20,  8, 0);
        SimpleDate origTo   = date(2024, 1, 22, 18, 0);
        v.applyToEvent(event, origFrom, origTo);

        SimpleDate newFrom = date(2024, 1, 20, 10, 0);
        SimpleDate newTo   = date(2024, 1, 21, 16, 0);

        Coordinator role = event.assignCoordinator(c);
        role.acceptVolunteer(v, newFrom, newTo); // furnizează date noi

        EventVolunteerAvailability av = v.getEventAvailability(event);
        assertNotNull(av);
        assertEquals(newFrom, av.getAvailableFrom());
        assertEquals(newTo,   av.getAvailableTo());
    }

    @Test
    @DisplayName("acceptVolunteer - dacă datele furnizate sunt identice, disponibilitatea rămâne neschimbată")
    void testAcceptVolunteer_SameDates_AvailabilityUnchanged() {
        Event event  = createValidEvent();
        Volunteer c  = createVolunteer("Coord", "Test", "c@example.com", "1111111111");
        Volunteer v  = createVolunteer("Vol",   "Test", "v@example.com", "2222222222");

        v.applyToEvent(event, validFrom(), validTo());

        Coordinator role = event.assignCoordinator(c);
        role.acceptVolunteer(v, validFrom(), validTo());

        EventVolunteerAvailability av = v.getEventAvailability(event);
        assertNotNull(av);
        assertEquals(validFrom(), av.getAvailableFrom());
        assertEquals(validTo(),   av.getAvailableTo());
    }

    // ==================== ACCEPTARE MULTIPLĂ / DUPLICATE ====================

    @Test
    @DisplayName("acceptVolunteer - acceptare duplicată de același coordinator nu dublează subordinatul")
    void testAcceptVolunteer_DuplicateByCoordinator_NoDoubleAdd() {
        Event event  = createValidEvent();
        Volunteer c  = createVolunteer("Coord", "Test", "c@example.com", "1111111111");
        Volunteer v  = createVolunteer("Vol",   "Test", "v@example.com", "2222222222");

        v.applyToEvent(event, validFrom(), validTo());
        Coordinator role = event.assignCoordinator(c);

        role.acceptVolunteer(v, validFrom(), validTo());
        // A doua acceptare de același coordinator nu aruncă excepție (skip own role la V4)
        // și nu adaugă din nou (addSubordinate verifică cu contains)
        assertDoesNotThrow(() -> role.acceptVolunteer(v, validFrom(), validTo()));
        assertEquals(1, role.getSubordinates().size());
    }

    @Test
    @DisplayName("acceptVolunteer - N voluntari diferiți acceptați de același coordinator")
    void testAcceptVolunteer_NVolunteers_AllAccepted() {
        Event event  = createValidEvent();
        Volunteer c  = createVolunteer("Coord", "Test", "c@example.com", "1111111111");
        Volunteer v1 = createVolunteer("Vol", "One",   "v1@example.com", "2222222222");
        Volunteer v2 = createVolunteer("Vol", "Two",   "v2@example.com", "3333333333");
        Volunteer v3 = createVolunteer("Vol", "Three", "v3@example.com", "4444444444");

        v1.applyToEvent(event, validFrom(), validTo());
        v2.applyToEvent(event, validFrom(), validTo());
        v3.applyToEvent(event, validFrom(), validTo());

        Coordinator role = event.assignCoordinator(c);
        role.acceptVolunteer(v1, validFrom(), validTo());
        role.acceptVolunteer(v2, validFrom(), validTo());
        role.acceptVolunteer(v3, validFrom(), validTo());

        assertEquals(3, role.getSubordinates().size());
        assertTrue(role.hasSubordinate(v1));
        assertTrue(role.hasSubordinate(v2));
        assertTrue(role.hasSubordinate(v3));
    }

    // ==================== IZOLARE ÎNTRE EVENT-URI ====================

    @Test
    @DisplayName("acceptVolunteer - voluntarul acceptat în event diferit nu afectează event-ul curent")
    void testAcceptVolunteer_DifferentEvent_NoCrossContamination() {
        Event event1 = createValidEvent();
        Event event2 = new Event("Other Event", createValidEventDate(), createValidAdmin());

        Volunteer c1 = createVolunteer("Coord", "One", "c1@example.com", "1111111111");
        Volunteer c2 = createVolunteer("Coord", "Two", "c2@example.com", "2222222222");
        Volunteer v  = createVolunteer("Vol",   "One", "v@example.com",  "3333333333");

        v.applyToEvent(event1, validFrom(), validTo());
        v.applyToEvent(event2, validFrom(), validTo());

        Coordinator role1 = event1.assignCoordinator(c1);
        Coordinator role2 = event2.assignCoordinator(c2);

        role1.acceptVolunteer(v, validFrom(), validTo()); // v este sub c1 în event1

        // c2 încearcă în event2 — bucla [4] verifică doar roluri din event2, nu event1
        assertDoesNotThrow(() -> role2.acceptVolunteer(v, validFrom(), validTo()));
        assertTrue(role1.hasSubordinate(v));
        assertTrue(role2.hasSubordinate(v));
    }

    @Test
    @DisplayName("acceptVolunteer - voluntarul este coordinator în alt event, poate fi subordinat în event-ul curent")
    void testAcceptVolunteer_VolunteerCoordinatorInOtherEvent_AcceptedHere() {
        Event event1 = createValidEvent();
        Event event2 = new Event("Other Event", createValidEventDate(), createValidAdmin());

        Volunteer c  = createVolunteer("Coord", "Main",  "c@example.com",  "1111111111");
        Volunteer v  = createVolunteer("Vol",   "Multi", "vm@example.com", "2222222222");

        // v este coordinator în event2, dar simplu voluntar în event1
        event2.assignCoordinator(v);
        v.applyToEvent(event1, validFrom(), validTo());

        Coordinator role = event1.assignCoordinator(c);

        // Validarea [V2] din Coordinator.acceptVolunteer verifică doar event.getCoordinatorRoles()
        // (adică event1), nu event2 → acceptarea trebuie să reușească
        assertDoesNotThrow(() -> role.acceptVolunteer(v, validFrom(), validTo()));
        assertTrue(role.hasSubordinate(v));
    }

    // ==================== TESTE SUPLIMENTARE (15) ====================

    @Test
    @DisplayName("acceptVolunteer - datele se actualizează corect la acceptări succesive cu intervale diferite")
    void testAcceptVolunteer_UpdatesDatesOnSuccessiveAccepts() {
        Event event = createValidEvent();
        Volunteer c = createVolunteer("Coord", "Update", "c@example.com", "1111111111");
        Volunteer v = createVolunteer("Vol", "Update", "v@example.com", "2222222222");

        v.applyToEvent(event, validFrom(), validTo());
        Coordinator role = event.assignCoordinator(c);

        // Prima acceptare
        SimpleDate from1 = date(2024, 1, 20, 10, 0);
        SimpleDate to1 = date(2024, 1, 20, 14, 0);
        role.acceptVolunteer(v, from1, to1);

        // A doua acceptare cu date modificate
        SimpleDate from2 = date(2024, 1, 21, 10, 0);
        SimpleDate to2 = date(2024, 1, 21, 14, 0);
        role.acceptVolunteer(v, from2, to2);

        EventVolunteerAvailability av = v.getEventAvailability(event);
        assertEquals(from2, av.getAvailableFrom(), "Data de început trebuie să se actualizeze la ultima valoare.");
        assertEquals(to2, av.getAvailableTo(), "Data de final trebuie să se actualizeze la ultima valoare.");
        assertEquals(1, role.getSubordinates().size(), "Voluntarul nu trebuie să fie duplicat în lista de subordinați.");
    }

    @Test
    @DisplayName("acceptVolunteer - aruncă excepție dacă fromDate este null (propagare din disponibilitate)")
    void testAcceptVolunteer_NullFromDate_Throws() {
        Event event = createValidEvent();
        Volunteer c = createVolunteer("Coord", "NullTest", "c@example.com", "1111111111");
        Volunteer v = createVolunteer("Vol", "NullTest", "v@example.com", "2222222222");

        v.applyToEvent(event, validFrom(), validTo());
        Coordinator role = event.assignCoordinator(c);

        // Funcția setter din EventVolunteerAvailability va arunca IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () ->
            role.acceptVolunteer(v, null, validTo())
        );
    }

    @Test
    @DisplayName("acceptVolunteer - aruncă excepție dacă toDate este null (propagare din disponibilitate)")
    void testAcceptVolunteer_NullToDate_Throws() {
        Event event = createValidEvent();
        Volunteer c = createVolunteer("Coord", "NullTest", "c@example.com", "1111111111");
        Volunteer v = createVolunteer("Vol", "NullTest", "v@example.com", "2222222222");

        v.applyToEvent(event, validFrom(), validTo());
        Coordinator role = event.assignCoordinator(c);

        assertThrows(IllegalArgumentException.class, () ->
            role.acceptVolunteer(v, validFrom(), null)
        );
    }

    @Test
    @DisplayName("acceptVolunteer - mai mulți coordonatori pot accepta voluntari diferiți în același event fără conflict")
    void testAcceptVolunteer_MultipleCoordinators_DifferentSubordinates() {
        Event event = createValidEvent();
        Volunteer c1 = createVolunteer("Coord", "One", "c1@example.com", "1111111111");
        Volunteer c2 = createVolunteer("Coord", "Two", "c2@example.com", "2222222222");
        Volunteer v1 = createVolunteer("Vol", "One", "v1@example.com", "333333333");
        Volunteer v2 = createVolunteer("Vol", "Two", "v2@example.com", "444444444");

        v1.applyToEvent(event, validFrom(), validTo());
        v2.applyToEvent(event, validFrom(), validTo());

        Coordinator role1 = event.assignCoordinator(c1);
        Coordinator role2 = event.assignCoordinator(c2);

        role1.acceptVolunteer(v1, validFrom(), validTo());
        role2.acceptVolunteer(v2, validFrom(), validTo());

        assertTrue(role1.hasSubordinate(v1));
        assertFalse(role1.hasSubordinate(v2));
        assertTrue(role2.hasSubordinate(v2));
        assertFalse(role2.hasSubordinate(v1));
    }

    @Test
    @DisplayName("acceptVolunteer - voluntarul poate fi re-acceptat cu succes după ce a fost eliminat manual din listă")
    void testAcceptVolunteer_RemoveAndReaccept() {
        Event event = createValidEvent();
        Volunteer c = createVolunteer("Coord", "One", "c1@example.com", "1111111111");
        Volunteer v = createVolunteer("Vol", "One", "v1@example.com", "2222222222");

        v.applyToEvent(event, validFrom(), validTo());
        Coordinator role = event.assignCoordinator(c);

        role.acceptVolunteer(v, validFrom(), validTo());
        role.removeSubordinate(v); // Se simulează eliminarea din echipa coordonatorului
        assertFalse(role.hasSubordinate(v));

        assertDoesNotThrow(() -> role.acceptVolunteer(v, validFrom(), validTo()));
        assertTrue(role.hasSubordinate(v));
    }

    @Test
    @DisplayName("acceptVolunteer - dimensiunea listei de subordinați reflectă corect numărul de voluntari unici")
    void testAcceptVolunteer_SubordinateSizeCorrectness() {
        Event event = createValidEvent();
        Volunteer c = createVolunteer("Coord", "One", "c1@e.com", "1111111111");
        Volunteer v1 = createVolunteer("Vol", "One", "v1@e.com", "2222222222");
        Volunteer v2 = createVolunteer("Vol", "Two", "v2@e.com", "333333333");

        v1.applyToEvent(event, validFrom(), validTo());
        v2.applyToEvent(event, validFrom(), validTo());
        Coordinator role = event.assignCoordinator(c);

        assertEquals(0, role.getSubordinates().size());

        role.acceptVolunteer(v1, validFrom(), validTo());
        assertEquals(1, role.getSubordinates().size());

        role.acceptVolunteer(v1, validFrom(), validTo()); // Apel duplicat
        assertEquals(1, role.getSubordinates().size(), "Nu ar trebui să adauge duplicate.");

        role.acceptVolunteer(v2, validFrom(), validTo());
        assertEquals(2, role.getSubordinates().size());
    }

    @Test
    @DisplayName("acceptVolunteer - disponibilitatea actualizată la un event nu o modifică pe cea de la alt event (Izolare)")
    void testAcceptVolunteer_AvailabilityIsolationBetweenEvents() {
        Event event1 = createValidEvent();
        Event event2 = new Event("Event 2", createValidEventDate(), createValidAdmin());

        Volunteer c = createVolunteer("Coord", "One", "c1@e.com", "1111111111");
        Volunteer v = createVolunteer("Vol", "One", "v1@e.com", "2222222222");

        SimpleDate origFrom = date(2024, 1, 20, 8, 0);
        SimpleDate origTo = date(2024, 1, 22, 18, 0);

        v.applyToEvent(event1, origFrom, origTo);
        v.applyToEvent(event2, origFrom, origTo);

        Coordinator role = event1.assignCoordinator(c);

        SimpleDate newFrom = date(2024, 1, 21, 10, 0);
        SimpleDate newTo = date(2024, 1, 21, 14, 0);
        
        // Acceptăm doar la event1 cu date noi
        role.acceptVolunteer(v, newFrom, newTo);

        // Verificăm event1 - trebuie să aibă noile date
        assertEquals(newFrom, v.getEventAvailability(event1).getAvailableFrom());
        
        // Verificăm event2 - trebuie să aibă datele originale
        assertEquals(origFrom, v.getEventAvailability(event2).getAvailableFrom());
        assertEquals(origTo, v.getEventAvailability(event2).getAvailableTo());
    }

    @Test
    @DisplayName("acceptVolunteer - funcționează corect pentru un interval de disponibilitate foarte restrâns (ex. o oră)")
    void testAcceptVolunteer_NarrowAvailabilityWindow() {
        Event event = createValidEvent();
        Volunteer c = createVolunteer("Coord", "One", "c1@e.com", "1111111111");
        Volunteer v = createVolunteer("Vol", "One", "v1@e.com", "2222222222");

        v.applyToEvent(event, validFrom(), validTo());
        Coordinator role = event.assignCoordinator(c);

        SimpleDate narrowFrom = date(2024, 1, 21, 12, 0);
        SimpleDate narrowTo = date(2024, 1, 21, 13, 0);

        role.acceptVolunteer(v, narrowFrom, narrowTo);
        assertEquals(narrowFrom, v.getEventAvailability(event).getAvailableFrom());
        assertEquals(narrowTo, v.getEventAvailability(event).getAvailableTo());
    }

    @Test
    @DisplayName("acceptVolunteer - performanță/scalabilitate: un coordonator poate accepta 50 de voluntari")
    void testAcceptVolunteer_LargeNumberOfVolunteers() {
        Event event = createValidEvent();
        Volunteer c = createVolunteer("Coord", "One", "c1@e.com", "1111111111");
        Coordinator role = event.assignCoordinator(c);

        for (int i = 0; i < 50; i++) {
            Volunteer v = createVolunteer("Vol", String.valueOf(i) + String.valueOf(i), "v" + i + "@e.com", "55555555555" + i);
            v.applyToEvent(event, validFrom(), validTo());
            role.acceptVolunteer(v, validFrom(), validTo());
        }

        assertEquals(50, role.getSubordinates().size(), "Coordonatorul ar trebui să aibă toți cei 50 de voluntari acceptați.");
    }

    @Test
    @DisplayName("acceptVolunteer - idempotenta: apelarea repetată cu aceleași date păstrează o stare consistentă")
    void testAcceptVolunteer_RepeatedAccepts_SameDates() {
        Event event = createValidEvent();
        Volunteer c = createVolunteer("Coord", "One", "c1@e.com", "1111111111");
        Volunteer v = createVolunteer("Vol", "One", "v1@e.com", "2222222222");

        v.applyToEvent(event, validFrom(), validTo());
        Coordinator role = event.assignCoordinator(c);

        role.acceptVolunteer(v, validFrom(), validTo());
        role.acceptVolunteer(v, validFrom(), validTo());
        role.acceptVolunteer(v, validFrom(), validTo());

        assertEquals(1, role.getSubordinates().size());
        assertTrue(role.hasSubordinate(v));
    }

    @Test
    @DisplayName("acceptVolunteer - un voluntar acceptat de C1 nu este vizibil în lista de subordinați a lui C2")
    void testAcceptVolunteer_AcceptedByC1_NotVisibleToC2() {
        Event event = createValidEvent();
        Volunteer c1 = createVolunteer("Coord", "One", "c1@e.com", "1111111111");
        Volunteer c2 = createVolunteer("Coord", "Two", "c2@e.com", "2222222222");
        Volunteer v = createVolunteer("Vol", "One", "v1@e.com", "333333333");

        v.applyToEvent(event, validFrom(), validTo());
        Coordinator role1 = event.assignCoordinator(c1);
        Coordinator role2 = event.assignCoordinator(c2);

        role1.acceptVolunteer(v, validFrom(), validTo());

        assertTrue(role1.hasSubordinate(v));
        assertFalse(role2.hasSubordinate(v), "Lista altui coordonator nu ar trebui să conțină acest voluntar.");
    }

    @Test
    @DisplayName("acceptVolunteer - se permite acceptarea setând limitele fix pe capetele evenimentului")
    void testAcceptVolunteer_ExactEventBoundaries() {
        Event event = createValidEvent();
        Volunteer c = createVolunteer("Coord", "One", "c1@e.com", "1111111111");
        Volunteer v = createVolunteer("Vol", "One", "v1@e.com", "2222222222");

        v.applyToEvent(event, validFrom(), validTo());
        Coordinator role = event.assignCoordinator(c);

        SimpleDate eventStart = event.getEventDate().getStartDate();
        SimpleDate eventEnd = event.getEventDate().getEndDate();

        role.acceptVolunteer(v, eventStart, eventEnd);

        assertEquals(eventStart, v.getEventAvailability(event).getAvailableFrom());
        assertEquals(eventEnd, v.getEventAvailability(event).getAvailableTo());
    }

    @Test
    @DisplayName("acceptVolunteer - după acceptare, referințele obiectelor din disponibilitate rămân intacte")
    void testAcceptVolunteer_AvailabilityReferencesRemainIntact() {
        Event event = createValidEvent();
        Volunteer c = createVolunteer("Coord", "One", "c1@e.com", "1111111111");
        Volunteer v = createVolunteer("Vol", "One", "v1@e.com", "2222222222");

        v.applyToEvent(event, validFrom(), validTo());
        Coordinator role = event.assignCoordinator(c);

        role.acceptVolunteer(v, validFrom(), validTo());

        EventVolunteerAvailability av = v.getEventAvailability(event);
        assertEquals(event, av.getEvent());
        assertEquals(v, av.getVolunteer());
    }

    @Test
    @DisplayName("acceptVolunteer - permutație cross-event: V1 este coordonator E1 și subordinat E2; V2 invers")
    void testAcceptVolunteer_CrossEventRoleSwap() {
        Event event1 = createValidEvent();
        Event event2 = new Event("Event 2", createValidEventDate(), createValidAdmin());

        Volunteer v1 = createVolunteer("Vol", "One", "v1@e.com", "1111111111");
        Volunteer v2 = createVolunteer("Vol", "Two", "v2@e.com", "2222222222");

        // Aplicații încrucișate
        v2.applyToEvent(event1, validFrom(), validTo()); // V2 aplică la E1
        v1.applyToEvent(event2, validFrom(), validTo()); // V1 aplică la E2

        // Roluri
        Coordinator roleV1_E1 = event1.assignCoordinator(v1); // V1 devine coord la E1
        Coordinator roleV2_E2 = event2.assignCoordinator(v2); // V2 devine coord la E2

        // Fiecare îl acceptă pe celălalt la evenimentul unde este coordonator
        assertDoesNotThrow(() -> roleV1_E1.acceptVolunteer(v2, validFrom(), validTo()));
        assertDoesNotThrow(() -> roleV2_E2.acceptVolunteer(v1, validFrom(), validTo()));

        assertTrue(roleV1_E1.hasSubordinate(v2));
        assertTrue(roleV2_E2.hasSubordinate(v1));
    }

    @Test
    @DisplayName("acceptVolunteer - acceptarea funcționează imaculat chiar dacă în sistem există zeci de voluntari asociați care așteaptă")
    void testAcceptVolunteer_ManyRegisteredButNotAccepted() {
        Event event = createValidEvent();
        Volunteer c = createVolunteer("Coord", "One", "c1@e.com", "1111111111");
        Coordinator role = event.assignCoordinator(c);

        Volunteer target = createVolunteer("Target", "Vol", "target@e.com", "00000000000");
        target.applyToEvent(event, validFrom(), validTo());

        // Simulăm zgomot pe background adăugând alți 10 voluntari
        for (int i = 0; i < 10; i++) {
            Volunteer other = createVolunteer("Other", "Vol" + String.valueOf(i), "o" + i + "@e.com", "1111111111");
            other.applyToEvent(event, validFrom(), validTo());
        }

        // Doar voluntarul 'target' este acceptat de coordonator
        assertDoesNotThrow(() -> role.acceptVolunteer(target, validFrom(), validTo()));
        
        assertEquals(1, role.getSubordinates().size());
        assertTrue(role.hasSubordinate(target));
        
        // În sistem sunt 11 voluntari, dar coordonatorul se ocupă doar de 1
        assertEquals(11, event.getAllRegisteredVolunteers().size());
    }
}

