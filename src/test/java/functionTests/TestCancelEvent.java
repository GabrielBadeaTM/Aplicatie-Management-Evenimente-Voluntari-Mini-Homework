package functionTests;

import models.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;


public class TestCancelEvent {

    // ==================== HELPERS ====================

    private SimpleDate date(int year, int month, int day, int hour, int minute) {
        return new SimpleDate(year, month, day, hour, minute);
    }

    private EventDate createValidEventDate() {
        SimpleDate regStart   = date(2024, 1,  1,  9,  0);
        SimpleDate regEnd     = date(2024, 1, 15, 17,  0);
        SimpleDate eventStart = date(2024, 1, 20,  8,  0);
        SimpleDate eventEnd   = date(2024, 1, 22, 18,  0);
        return new EventDate(eventStart, eventEnd, regStart, regEnd);
    }

    private Admin createAdmin(String email) {
        return new Admin("John", "Admin", email, "1234567890");
    }

    private Admin createValidAdmin() {
        return createAdmin("admin@example.com");
    }

    private Volunteer createVolunteer(String email) {
        return new Volunteer("Vol", "Test", email, "1111111111", 3, TShirtSize.M);
    }

    /** Aplică voluntarul la event folosind supraîncărcarea cu 3 parametri (currentTime = regStart). */
    private void applyVolunteer(Volunteer vol, Event event) {
        SimpleDate from = date(2024, 1, 20,  8,  0);
        SimpleDate to   = date(2024, 1, 22, 18,  0);
        vol.applyToEvent(event, from, to);
    }

    // ==================== [G] VERIFICARE EXISTENȚĂ: RETURN DEVREME ====================

    @Test
    @DisplayName("[G] Event aparținând altui admin → nu este găsit, lista rămâne neschimbată")
    void testCancelEvent_EventFromDifferentAdmin_NoEffect() {
        Admin admin1 = createValidAdmin();
        Admin admin2 = createAdmin("other@example.com");

        Event event1 = admin1.createEvent("Event A", createValidEventDate());
        Event event2 = admin2.createEvent("Event B", createValidEventDate());

        // admin1 încearcă să anuleze un event creat de admin2 → return devreme
        admin1.cancelEvent(event2);

        assertEquals(1, admin1.getCreatedEvents().size());
        assertTrue(admin1.getCreatedEvents().contains(event1));
        assertEquals(1, admin2.getCreatedEvents().size());
        assertTrue(admin2.getCreatedEvents().contains(event2));
    }

    @Test
    @DisplayName("[G] Event deja anulat → al doilea cancelEvent nu produce efecte")
    void testCancelEvent_AlreadyCancelled_NoEffect() {
        Admin admin = createValidAdmin();
        Event event = admin.createEvent("Event A", createValidEventDate());

        admin.cancelEvent(event); // prima anulare
        assertEquals(0, admin.getCreatedEvents().size());

        // A doua anulare → event nu mai este în listă → return devreme
        admin.cancelEvent(event);
        assertEquals(0, admin.getCreatedEvents().size());
    }

    // ==================== [B1] BUCLA VOLUNTARI: 0 / 1 / N ====================

    @Test
    @DisplayName("[B1=0] Event fără voluntari și fără coordinatori → scos din lista adminului")
    void testCancelEvent_NoVolunteers_NoCoordinators() {
        Admin admin = createValidAdmin();
        Event event = admin.createEvent("Empty Event", createValidEventDate());

        admin.cancelEvent(event);

        assertFalse(admin.getCreatedEvents().contains(event));
        assertEquals(0, admin.getCreatedEvents().size());
    }

    @Test
    @DisplayName("[B1=1] Un singur voluntar înscris → cancelApplication apelat o dată")
    void testCancelEvent_OneVolunteer_ApplicationCancelled() {
        Admin admin = createValidAdmin();
        Event event = admin.createEvent("Event A", createValidEventDate());
        Volunteer vol = createVolunteer("vol@example.com");

        applyVolunteer(vol, event);
        assertTrue(event.hasVolunteerApplied(vol));
        assertEquals(1, vol.getEventAvailabilities().size());

        admin.cancelEvent(event);

        // Voluntarul nu mai are event-ul în lista sa
        assertNull(vol.getEventAvailability(event));
        assertEquals(0, vol.getEventAvailabilities().size());
        // Event-ul nu mai are voluntari înscriși
        assertFalse(event.hasVolunteerApplied(vol));
    }

    @Test
    @DisplayName("[B1=N] N voluntari înscriși → cancelApplication apelat pentru fiecare")
    void testCancelEvent_NVolunteers_AllApplicationsCancelled() {
        Admin admin    = createValidAdmin();
        Event event    = admin.createEvent("Event A", createValidEventDate());
        Volunteer vol1 = createVolunteer("v1@example.com");
        Volunteer vol2 = createVolunteer("v2@example.com");
        Volunteer vol3 = createVolunteer("v3@example.com");

        applyVolunteer(vol1, event);
        applyVolunteer(vol2, event);
        applyVolunteer(vol3, event);
        assertEquals(3, event.getEnrolledVolunteers().size());

        admin.cancelEvent(event);

        assertNull(vol1.getEventAvailability(event));
        assertNull(vol2.getEventAvailability(event));
        assertNull(vol3.getEventAvailability(event));
        assertEquals(0, event.getEnrolledVolunteers().size());
    }

    // ==================== [B2] BUCLA COORDINATORI: 0 / 1 / N ====================

    @Test
    @DisplayName("[B2=0] Event fără coordinatori → bucla B2 nu iterează, event șters corect")
    void testCancelEvent_NoCoordinators_EventRemoved() {
        Admin admin    = createValidAdmin();
        Event event    = admin.createEvent("Event A", createValidEventDate());
        Volunteer vol  = createVolunteer("vol@example.com");

        applyVolunteer(vol, event);

        admin.cancelEvent(event);

        assertEquals(0, event.getCoordinatorRoles().size());
        assertFalse(admin.getCreatedEvents().contains(event));
    }

    @Test
    @DisplayName("[B2=1, BI=0] Un coordinator fără subordinați → rolul de coordinator este eliminat")
    void testCancelEvent_OneCoordinator_NoSubordinates() {
        Admin admin    = createValidAdmin();
        Event event    = admin.createEvent("Event A", createValidEventDate());
        Volunteer coord = createVolunteer("coord@example.com");

        // Coordinator fără a aplica la event (nu este în enrolled list)
        event.assignCoordinator(coord);
        assertEquals(1, event.getCoordinatorRoles().size());

        admin.cancelEvent(event);

        // Rolul de coordinator a fost eliminat
        assertEquals(0, event.getCoordinatorRoles().size());
        assertFalse(admin.getCreatedEvents().contains(event));
    }

    @Test
    @DisplayName("[B2=1, BI=1] Un coordinator cu un subordinat → subordinat și rol eliminate")
    void testCancelEvent_OneCoordinator_OneSubordinate() {
        Admin admin    = createValidAdmin();
        Event event    = admin.createEvent("Event A", createValidEventDate());
        Volunteer coord = createVolunteer("coord@example.com");
        Volunteer sub   = createVolunteer("sub@example.com");

        applyVolunteer(sub, event);
        Coordinator role = event.assignCoordinator(coord);
        role.acceptVolunteer(sub,
            date(2024, 1, 20, 8, 0),
            date(2024, 1, 22, 18, 0));

        assertEquals(1, role.getSubordinates().size());
        assertEquals(1, event.getCoordinatorRoles().size());

        admin.cancelEvent(event);

        // Subordinatul nu mai are event-ul în lista sa
        assertNull(sub.getEventAvailability(event));
        // Rolul de coordinator a fost eliminat
        assertEquals(0, event.getCoordinatorRoles().size());
        assertFalse(admin.getCreatedEvents().contains(event));
    }

    @Test
    @DisplayName("[B2=1, BI=N] Un coordinator cu N subordinați → toți subordinații eliminați")
    void testCancelEvent_OneCoordinator_NSubordinates() {
        Admin admin     = createValidAdmin();
        Event event     = admin.createEvent("Event A", createValidEventDate());
        Volunteer coord = createVolunteer("coord@example.com");
        Volunteer sub1  = createVolunteer("s1@example.com");
        Volunteer sub2  = createVolunteer("s2@example.com");
        Volunteer sub3  = createVolunteer("s3@example.com");

        applyVolunteer(sub1, event);
        applyVolunteer(sub2, event);
        applyVolunteer(sub3, event);

        Coordinator role = event.assignCoordinator(coord);
        SimpleDate from  = date(2024, 1, 20, 8, 0);
        SimpleDate to    = date(2024, 1, 22, 18, 0);
        role.acceptVolunteer(sub1, from, to);
        role.acceptVolunteer(sub2, from, to);
        role.acceptVolunteer(sub3, from, to);
        assertEquals(3, role.getSubordinates().size());

        admin.cancelEvent(event);

        // Toți subordinații au aplicația anulată
        assertNull(sub1.getEventAvailability(event));
        assertNull(sub2.getEventAvailability(event));
        assertNull(sub3.getEventAvailability(event));
        assertEquals(0, event.getCoordinatorRoles().size());
    }

    @Test
    @DisplayName("[B2=N, BI=N] N coordinatori cu subordinați → cleanup complet pentru toți")
    void testCancelEvent_NCoordinators_NSubordinates() {
        Admin admin     = createValidAdmin();
        Event event     = admin.createEvent("Event A", createValidEventDate());

        Volunteer coord1 = createVolunteer("c1@example.com");
        Volunteer coord2 = createVolunteer("c2@example.com");
        Volunteer sub1   = createVolunteer("s1@example.com");
        Volunteer sub2   = createVolunteer("s2@example.com");
        Volunteer sub3   = createVolunteer("s3@example.com");
        Volunteer sub4   = createVolunteer("s4@example.com");

        applyVolunteer(sub1, event);
        applyVolunteer(sub2, event);
        applyVolunteer(sub3, event);
        applyVolunteer(sub4, event);

        SimpleDate from = date(2024, 1, 20,  8, 0);
        SimpleDate to   = date(2024, 1, 22, 18, 0);

        Coordinator role1 = event.assignCoordinator(coord1);
        role1.acceptVolunteer(sub1, from, to);
        role1.acceptVolunteer(sub2, from, to);

        Coordinator role2 = event.assignCoordinator(coord2);
        role2.acceptVolunteer(sub3, from, to);
        role2.acceptVolunteer(sub4, from, to);

        assertEquals(2, event.getCoordinatorRoles().size());
        assertEquals(4, event.getEnrolledVolunteers().size());

        admin.cancelEvent(event);

        assertEquals(0, event.getCoordinatorRoles().size());
        assertEquals(0, event.getEnrolledVolunteers().size());
        for (Volunteer sub : new Volunteer[]{sub1, sub2, sub3, sub4}) {
            assertNull(sub.getEventAvailability(event));
        }
        assertFalse(admin.getCreatedEvents().contains(event));
    }

    // ==================== [S] EFECTE SIDE-EFFECT DETALIATE ====================

    @Test
    @DisplayName("[S] Event este eliminat din lista adminului după cancelEvent")
    void testCancelEvent_RemovedFromAdminList() {
        Admin admin  = createValidAdmin();
        Event event  = admin.createEvent("Event A", createValidEventDate());

        assertEquals(1, admin.getCreatedEvents().size());

        admin.cancelEvent(event);

        assertEquals(0, admin.getCreatedEvents().size());
        assertFalse(admin.getCreatedEvents().contains(event));
    }

    @Test
    @DisplayName("[S] Lista enrolledVolunteers a eventului este goală după cancelEvent")
    void testCancelEvent_EventEnrolledListCleared() {
        Admin admin    = createValidAdmin();
        Event event    = admin.createEvent("Event A", createValidEventDate());
        Volunteer vol1 = createVolunteer("v1@example.com");
        Volunteer vol2 = createVolunteer("v2@example.com");

        applyVolunteer(vol1, event);
        applyVolunteer(vol2, event);

        admin.cancelEvent(event);

        assertEquals(0, event.getEnrolledVolunteers().size());
        assertEquals(0, event.getAllRegisteredVolunteers().size());
    }

    @Test
    @DisplayName("[S] Lista eventAvailabilities a voluntarului nu mai conține event-ul anulat")
    void testCancelEvent_VolunteerAvailabilityListUpdated() {
        Admin admin   = createValidAdmin();
        Event event   = admin.createEvent("Event A", createValidEventDate());
        Volunteer vol = createVolunteer("vol@example.com");

        applyVolunteer(vol, event);
        assertEquals(1, vol.getEventAvailabilities().size());

        admin.cancelEvent(event);

        assertEquals(0, vol.getEventAvailabilities().size());
        assertNull(vol.getEventAvailability(event));
    }

    @Test
    @DisplayName("[S] Lista coordinatorRoles a eventului este goală după cancelEvent")
    void testCancelEvent_CoordinatorRolesCleared() {
        Admin admin    = createValidAdmin();
        Event event    = admin.createEvent("Event A", createValidEventDate());
        Volunteer c1   = createVolunteer("c1@example.com");
        Volunteer c2   = createVolunteer("c2@example.com");

        event.assignCoordinator(c1);
        event.assignCoordinator(c2);
        assertEquals(2, event.getCoordinatorRoles().size());

        admin.cancelEvent(event);

        assertEquals(0, event.getCoordinatorRoles().size());
    }

    @Test
    @DisplayName("[S] Lista subordinaților din Coordinator este goală după cancelEvent")
    void testCancelEvent_CoordinatorSubordinatesCleared() {
        Admin admin     = createValidAdmin();
        Event event     = admin.createEvent("Event A", createValidEventDate());
        Volunteer coord = createVolunteer("coord@example.com");
        Volunteer sub   = createVolunteer("sub@example.com");

        applyVolunteer(sub, event);
        Coordinator role = event.assignCoordinator(coord);
        role.acceptVolunteer(sub, date(2024, 1, 20, 8, 0), date(2024, 1, 22, 18, 0));
        assertEquals(1, role.getSubordinates().size());

        admin.cancelEvent(event);

        assertEquals(0, role.getSubordinates().size());
        assertFalse(role.hasSubordinate(sub));
    }

    // ==================== IZOLARE ÎNTRE RESURSE ====================

    @Test
    @DisplayName("Anularea unui event nu afectează celelalte event-uri ale aceluiași admin")
    void testCancelEvent_OtherEventsUnaffected() {
        Admin admin   = createValidAdmin();
        EventDate ed  = createValidEventDate();
        Event event1  = admin.createEvent("Event One",   ed);
        Event event2  = admin.createEvent("Event Two",   new EventDate(
            date(2024, 3, 10, 8, 0), date(2024, 3, 12, 18, 0),
            date(2024, 2,  1, 9, 0), date(2024, 2, 28, 17, 0)
        ));
        Event event3  = admin.createEvent("Event Three", new EventDate(
            date(2024, 5, 10, 8, 0), date(2024, 5, 12, 18, 0),
            date(2024, 4,  1, 9, 0), date(2024, 4, 28, 17, 0)
        ));

        assertEquals(3, admin.getCreatedEvents().size());

        admin.cancelEvent(event2); // anulăm doar event2

        assertEquals(2, admin.getCreatedEvents().size());
        assertTrue(admin.getCreatedEvents().contains(event1));
        assertFalse(admin.getCreatedEvents().contains(event2));
        assertTrue(admin.getCreatedEvents().contains(event3));
    }

    @Test
    @DisplayName("Voluntarul înscris la 2 event-uri → după anularea unuia, rămâne înscris la celălalt")
    void testCancelEvent_VolunteerStillEnrolledInOtherEvent() {
        Admin admin   = createValidAdmin();
        Event event1  = admin.createEvent("Event One", createValidEventDate());
        Event event2  = admin.createEvent("Event Two",  new EventDate(
            date(2024, 3, 10, 8, 0), date(2024, 3, 12, 18, 0),
            date(2024, 2,  1, 9, 0), date(2024, 2, 28, 17, 0)
        ));
        Volunteer vol = createVolunteer("vol@example.com");

        applyVolunteer(vol, event1);
        vol.applyToEvent(event2,
            date(2024, 3, 10, 8, 0),
            date(2024, 3, 12, 18, 0));

        assertEquals(2, vol.getEventAvailabilities().size());

        admin.cancelEvent(event1); // anulăm doar event1

        // Voluntarul nu mai este înscris la event1
        assertNull(vol.getEventAvailability(event1));
        // Voluntarul rămâne înscris la event2
        assertNotNull(vol.getEventAvailability(event2));
        assertEquals(1, vol.getEventAvailabilities().size());
    }

    @Test
    @DisplayName("Anularea event-ului unui admin nu afectează event-urile altui admin")
    void testCancelEvent_OtherAdminEventsUnaffected() {
        Admin admin1  = createValidAdmin();
        Admin admin2  = createAdmin("other@example.com");
        Event event1  = admin1.createEvent("Event A", createValidEventDate());
        Event event2  = admin2.createEvent("Event B", createValidEventDate());

        admin1.cancelEvent(event1);

        assertFalse(admin1.getCreatedEvents().contains(event1));
        assertTrue(admin2.getCreatedEvents().contains(event2));
    }

    // ==================== SCENARII COMBINATE COMPLETE ====================

    @Test
    @DisplayName("Event complet (N voluntari + N coordinatori + N subordinați) → cleanup total")
    void testCancelEvent_FullCascade() {
        Admin admin     = createValidAdmin();
        Event event     = admin.createEvent("Full Event", createValidEventDate());

        Volunteer coord1 = createVolunteer("c1@example.com");
        Volunteer coord2 = createVolunteer("c2@example.com");

        // 4 voluntari liberi (nu sub niciun coordinator)
        Volunteer free1 = createVolunteer("f1@example.com");
        Volunteer free2 = createVolunteer("f2@example.com");

        // 4 voluntari care vor deveni subordinați
        Volunteer sub1  = createVolunteer("s1@example.com");
        Volunteer sub2  = createVolunteer("s2@example.com");
        Volunteer sub3  = createVolunteer("s3@example.com");
        Volunteer sub4  = createVolunteer("s4@example.com");

        applyVolunteer(free1, event);
        applyVolunteer(free2, event);
        applyVolunteer(sub1,  event);
        applyVolunteer(sub2,  event);
        applyVolunteer(sub3,  event);
        applyVolunteer(sub4,  event);

        SimpleDate from = date(2024, 1, 20,  8, 0);
        SimpleDate to   = date(2024, 1, 22, 18, 0);

        Coordinator role1 = event.assignCoordinator(coord1);
        role1.acceptVolunteer(sub1, from, to);
        role1.acceptVolunteer(sub2, from, to);

        Coordinator role2 = event.assignCoordinator(coord2);
        role2.acceptVolunteer(sub3, from, to);
        role2.acceptVolunteer(sub4, from, to);

        assertEquals(6, event.getEnrolledVolunteers().size());
        assertEquals(2, event.getCoordinatorRoles().size());
        assertEquals(1, admin.getCreatedEvents().size());

        admin.cancelEvent(event);

        // Admin: event eliminat
        assertFalse(admin.getCreatedEvents().contains(event));

        // Event: liste golite
        assertEquals(0, event.getEnrolledVolunteers().size());
        assertEquals(0, event.getCoordinatorRoles().size());

        // Voluntari liberi: aplicațiile anulate
        assertNull(free1.getEventAvailability(event));
        assertNull(free2.getEventAvailability(event));

        // Subordinați: aplicațiile anulate și eliminați din coordinator
        for (Volunteer sub : new Volunteer[]{sub1, sub2, sub3, sub4}) {
            assertNull(sub.getEventAvailability(event));
        }
        assertEquals(0, role1.getSubordinates().size());
        assertEquals(0, role2.getSubordinates().size());
    }

    @Test
    @DisplayName("Admin cu N event-uri anulează pe rând fiecare → lista devine goală treptat")
    void testCancelEvent_CancelAllOneByOne() {
        Admin admin  = createValidAdmin();
        int n        = 4;
        Event[] evts = new Event[n];

        EventDate[] dates = new EventDate[]{
            createValidEventDate(),
            new EventDate(date(2024,3,10,8,0), date(2024,3,12,18,0),
                          date(2024,2, 1,9,0), date(2024,2,28,17,0)),
            new EventDate(date(2024,5,10,8,0), date(2024,5,12,18,0),
                          date(2024,4, 1,9,0), date(2024,4,28,17,0)),
            new EventDate(date(2024,8,10,8,0), date(2024,8,12,18,0),
                          date(2024,7, 1,9,0), date(2024,7,28,17,0))
        };

        for (int i = 0; i < n; i++) {
            evts[i] = admin.createEvent("Event " + i, dates[i]);
        }
        assertEquals(n, admin.getCreatedEvents().size());

        for (int i = 0; i < n; i++) {
            admin.cancelEvent(evts[i]);
            assertEquals(n - i - 1, admin.getCreatedEvents().size());
        }

        assertEquals(0, admin.getCreatedEvents().size());
    }

    // ==================== TESTE SUPLIMENTARE (15) ====================

    @Test
    @DisplayName("[G] cancelEvent(null) -> aruncă NullPointerException (din cauza lipsei validării null în Admin)")
    void testCancelEvent_NullEvent_ThrowsNPE() {
        Admin admin = createValidAdmin();
        Event event = admin.createEvent("Event A", createValidEventDate());

        // Deoarece Admin.cancelEvent nu verifică dacă event este null înainte de a apela event.getName()
        // ne așteptăm să arunce NullPointerException
        assertThrows(NullPointerException.class, () -> admin.cancelEvent(null));
        
        // Verificăm ca lista de evenimente să rămână neschimbată
        assertEquals(1, admin.getCreatedEvents().size());
        assertTrue(admin.getCreatedEvents().contains(event));
    }

    @Test
    @DisplayName("[G] Event creat manual (neînregistrat în Admin) -> cancelEvent se întoarce devreme")
    void testCancelEvent_UnregisteredEvent_NoEffect() {
        Admin admin = createValidAdmin();
        Event event1 = admin.createEvent("Event A", createValidEventDate());
        
        // Event creat prin constructor, nu prin admin.createEvent
        Event unregisteredEvent = new Event("Unregistered", createValidEventDate(), admin);

        admin.cancelEvent(unregisteredEvent);

        assertEquals(1, admin.getCreatedEvents().size());
        assertTrue(admin.getCreatedEvents().contains(event1));
    }

    @Test
    @DisplayName("[S] Voluntarul este și coordinator -> ambele asocieri sunt eliminate corect")
    void testCancelEvent_VolunteerIsCoordinator_BothRolesCleaned() {
        Admin admin = createValidAdmin();
        Event event = admin.createEvent("Event A", createValidEventDate());
        Volunteer coordVol = createVolunteer("coordvol@example.com");

        applyVolunteer(coordVol, event);
        event.assignCoordinator(coordVol);

        assertEquals(1, event.getEnrolledVolunteers().size());
        assertEquals(1, event.getCoordinatorRoles().size());

        admin.cancelEvent(event);

        assertEquals(0, coordVol.getEventAvailabilities().size());
        assertEquals(0, event.getEnrolledVolunteers().size());
        assertEquals(0, event.getCoordinatorRoles().size());
    }

    @Test
    @DisplayName("[B1=N] Performanță/Scalabilitate: Anularea unui event cu 100 de voluntari")
    void testCancelEvent_LargeScaleVolunteers_CleanedProperly() {
        Admin admin = createValidAdmin();
        Event event = admin.createEvent("Large Event", createValidEventDate());

        Volunteer[] volunteers = new Volunteer[100];
        for (int i = 0; i < 100; i++) {
            volunteers[i] = createVolunteer("vol" + i + "@example.com");
            applyVolunteer(volunteers[i], event);
        }

        assertEquals(100, event.getEnrolledVolunteers().size());

        admin.cancelEvent(event);

        assertEquals(0, event.getEnrolledVolunteers().size());
        for (int i = 0; i < 100; i++) {
            assertNull(volunteers[i].getEventAvailability(event));
        }
    }

    @Test
    @DisplayName("[B2=N] Performanță/Scalabilitate: Anularea unui event cu 50 de coordinatori")
    void testCancelEvent_LargeScaleCoordinators_CleanedProperly() {
        Admin admin = createValidAdmin();
        Event event = admin.createEvent("Coord Event", createValidEventDate());

        for (int i = 0; i < 50; i++) {
            Volunteer coord = createVolunteer("coord" + i + "@example.com");
            event.assignCoordinator(coord);
        }

        assertEquals(50, event.getCoordinatorRoles().size());

        admin.cancelEvent(event);

        assertEquals(0, event.getCoordinatorRoles().size());
    }

    @Test
    @DisplayName("[G] Idempotenta: Apelarea succesivă a cancelEvent pe același event nu produce erori")
    void testCancelEvent_MultipleCancelCalls_Idempotent() {
        Admin admin = createValidAdmin();
        Event event = admin.createEvent("Event A", createValidEventDate());
        Volunteer vol = createVolunteer("v@example.com");
        
        applyVolunteer(vol, event);

        admin.cancelEvent(event); // Primul apel
        assertDoesNotThrow(() -> admin.cancelEvent(event)); // Al doilea apel
        assertDoesNotThrow(() -> admin.cancelEvent(event)); // Al treilea apel

        assertEquals(0, admin.getCreatedEvents().size());
        assertEquals(0, event.getEnrolledVolunteers().size());
    }

    @Test
    @DisplayName("[S] După anulare, un event cu același nume și date poate fi creat din nou (eliberare constrângere unicitate)")
    void testCancelEvent_RecreateSameEventAfterCancel_Success() {
        Admin admin = createValidAdmin();
        String eventName = "Unique Event";
        EventDate eventDate = createValidEventDate();

        Event event1 = admin.createEvent(eventName, eventDate);
        admin.cancelEvent(event1);

        // Dacă event1 nu ar fi fost eliminat din listă, createEvent ar arunca IllegalArgumentException
        assertDoesNotThrow(() -> {
            Event event2 = admin.createEvent(eventName, eventDate);
            assertNotNull(event2);
            assertEquals(1, admin.getCreatedEvents().size());
        });
    }

    @Test
    @DisplayName("[S] Proprietățile de bază ale Adminului rămân intacte după anularea eventului")
    void testCancelEvent_AdminPropertiesRemainIntact() {
        Admin admin = createValidAdmin();
        Event event = admin.createEvent("Event A", createValidEventDate());

        admin.cancelEvent(event);

        assertEquals("John", admin.getFirstName());
        assertEquals("Admin", admin.getLastName());
        assertEquals("admin@example.com", admin.getEmail());
    }

    @Test
    @DisplayName("[S] Registrul global de voluntari al Adminului (allVolunteers) nu este afectat de cancelEvent")
    void testCancelEvent_AdminVolunteerRegistryIntact() {
        Admin admin = createValidAdmin();
        Event event = admin.createEvent("Event A", createValidEventDate());
        Volunteer vol = createVolunteer("v@example.com");

        admin.addVolunteer(vol);
        applyVolunteer(vol, event);

        admin.cancelEvent(event);

        // Voluntarul a fost scos din event, dar rămâne în registrul adminului
        assertEquals(1, admin.getAllVolunteers().size());
        assertTrue(admin.getAllVolunteers().contains(vol));
    }

    @Test
    @DisplayName("[S] Lista brută allRegisteredVolunteers din interiorul Eventului este golită complet")
    void testCancelEvent_ClearsAllRegisteredVolunteersListInEvent() {
        Admin admin = createValidAdmin();
        Event event = admin.createEvent("Event A", createValidEventDate());
        Volunteer vol = createVolunteer("v@example.com");

        applyVolunteer(vol, event);
        assertEquals(1, event.getAllRegisteredVolunteers().size());

        admin.cancelEvent(event);

        assertEquals(0, event.getAllRegisteredVolunteers().size());
    }

    @Test
    @DisplayName("[S] Obiectul Event reține proprietățile sale (nume, date) chiar dacă a fost anulat")
    void testCancelEvent_EventPropertiesRetainedAfterCancel() {
        Admin admin = createValidAdmin();
        EventDate ed = createValidEventDate();
        Event event = admin.createEvent("Retained Event", ed);

        admin.cancelEvent(event);

        assertEquals("Retained Event", event.getName());
        assertEquals(ed, event.getEventDate());
        assertNotNull(event.getAdmin());
    }

    @Test
    @DisplayName("[S] Datele personale ale subordinatului nu sunt corupte/șterse la anularea eventului")
    void testCancelEvent_SubordinatePersonalDataIntact() {
        Admin admin = createValidAdmin();
        Event event = admin.createEvent("Event A", createValidEventDate());
        Volunteer coord = createVolunteer("coord@example.com");
        Volunteer sub = new Volunteer("Ion", "Popescu", "ion@example.com", "0700000000", 5, TShirtSize.L);

        applyVolunteer(sub, event);
        Coordinator role = event.assignCoordinator(coord);
        role.acceptVolunteer(sub, date(2024, 1, 20, 8, 0), date(2024, 1, 22, 18, 0));

        admin.cancelEvent(event);

        assertEquals("Ion", sub.getFirstName());
        assertEquals("Popescu", sub.getLastName());
        assertEquals(5, sub.getYearsOfExperience());
        assertEquals(TShirtSize.L, sub.getTShirtSize());
    }

    @Test
    @DisplayName("Izolare: Coordinatorul a două eventuri pierde doar rolul din eventul anulat")
    void testCancelEvent_CoordinatorFreeToCoordinateOtherEventsAfterCancel() {
        Admin admin = createValidAdmin();
        Event event1 = admin.createEvent("Event 1", createValidEventDate());
        Event event2 = admin.createEvent("Event 2", new EventDate(
            date(2024,3,10,8,0), date(2024,3,12,18,0),
            date(2024,2, 1,9,0), date(2024,2,28,17,0)
        ));
        Volunteer coord = createVolunteer("coord@example.com");

        event1.assignCoordinator(coord);
        event2.assignCoordinator(coord);

        admin.cancelEvent(event1);

        assertFalse(event1.isCoordinator(coord));
        assertTrue(event2.isCoordinator(coord));
    }

    @Test
    @DisplayName("Izolare: Subordinatul a două eventuri își pierde doar legătura cu eventul anulat")
    void testCancelEvent_SubordinateLosesOnlyCancelledEventLink() {
        Admin admin = createValidAdmin();
        Event event1 = admin.createEvent("Event 1", createValidEventDate());
        Event event2 = admin.createEvent("Event 2", new EventDate(
            date(2024,3,10,8,0), date(2024,3,12,18,0),
            date(2024,2, 1,9,0), date(2024,2,28,17,0)
        ));
        
        Volunteer coord = createVolunteer("coord@example.com");
        Volunteer sub = createVolunteer("sub@example.com");

        applyVolunteer(sub, event1);
        sub.applyToEvent(event2, date(2024,3,10,8,0), date(2024,3,12,18,0));

        Coordinator role1 = event1.assignCoordinator(coord);
        Coordinator role2 = event2.assignCoordinator(coord);

        role1.acceptVolunteer(sub, date(2024, 1, 20, 8, 0), date(2024, 1, 22, 18, 0));
        role2.acceptVolunteer(sub, date(2024, 3, 10, 8, 0), date(2024, 3, 12, 18, 0));

        admin.cancelEvent(event1);

        assertFalse(role1.hasSubordinate(sub));
        assertTrue(role2.hasSubordinate(sub));
        assertNull(sub.getEventAvailability(event1));
        assertNotNull(sub.getEventAvailability(event2));
    }

    @Test
    @DisplayName("[G] Anularea unui event șterge instanțele din `createdEvents` dar păstrează ordinea celorlalte")
    void testCancelEvent_PreservesOrderOfRemainingEvents() {
        Admin admin = createValidAdmin();
        Event event1 = admin.createEvent("Event 1", createValidEventDate());
        Event event2 = admin.createEvent("Event 2", new EventDate(
            date(2024,3,10,8,0), date(2024,3,12,18,0),
            date(2024,2, 1,9,0), date(2024,2,28,17,0)
        ));
        Event event3 = admin.createEvent("Event 3", new EventDate(
            date(2024,5,10,8,0), date(2024,5,12,18,0),
            date(2024,4, 1,9,0), date(2024,4,28,17,0)
        ));

        admin.cancelEvent(event2);

        assertEquals(2, admin.getCreatedEvents().size());
        assertEquals(event1, admin.getCreatedEvents().get(0));
        assertEquals(event3, admin.getCreatedEvents().get(1));
    }
}
