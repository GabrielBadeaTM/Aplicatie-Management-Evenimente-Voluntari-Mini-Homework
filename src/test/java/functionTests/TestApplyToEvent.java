package functionTests;

import models.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class TestApplyToEvent {

    // ==================== HELPERS ====================

    private SimpleDate date(int year, int month, int day, int hour, int minute) {
        return new SimpleDate(year, month, day, hour, minute);
    }

    /**
     * Event standard folosit în majoritatea testelor:
     *   Înregistrare: 2024-01-01 09:00 → 2024-01-15 17:00
     *   Event:        2024-01-20 08:00 → 2024-01-22 18:00
     */
    private EventDate createValidEventDate() {
        SimpleDate regStart   = date(2024,  1,  1,  9,  0);
        SimpleDate regEnd     = date(2024,  1, 15, 17,  0);
        SimpleDate eventStart = date(2024,  1, 20,  8,  0);
        SimpleDate eventEnd   = date(2024,  1, 22, 18,  0);
        return new EventDate(eventStart, eventEnd, regStart, regEnd);
    }

    private Admin createValidAdmin() {
        return new Admin("John", "Admin", "admin@example.com", "1234567890");
    }

    private Event createValidEvent() {
        return new Event("Valid Event", createValidEventDate(), createValidAdmin());
    }

    private Volunteer createVolunteer(String firstName, String lastName, String email, String phone) {
        return new Volunteer(firstName, lastName, email, phone, 3, TShirtSize.M);
    }

    /** currentTime valid: în mijlocul ferestrei de înregistrare */
    private SimpleDate validCurrentTime() { return date(2024, 1, 8, 12, 0); }

    /** Date de disponibilitate care acoperă exact întregul interval al eventului */
    private SimpleDate eventStart() { return date(2024, 1, 20,  8,  0); }
    private SimpleDate eventEnd()   { return date(2024, 1, 22, 18,  0); }

    // ==================== HAPPY-PATH ====================

    @Test
    @DisplayName("applyToEvent(4 param) - cale validă: voluntarul este înregistrat")
    void testApply_FourParam_Valid() {
        Event event     = createValidEvent();
        Volunteer vol   = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        vol.applyToEvent(event, eventStart(), eventEnd(), validCurrentTime());

        assertNotNull(vol.getEventAvailability(event));
        assertEquals(1, vol.getEventAvailabilities().size());
    }

    @Test
    @DisplayName("applyToEvent(3 param) - supraîncărcare validă: delegă cu currentTime = regStart")
    void testApply_ThreeParam_Valid() {
        Event event   = createValidEvent();
        Volunteer vol = createVolunteer("Ana", "Ion", "ana@example.com", "0987654321");

        // Supraîncărcarea setează currentTime = regStart → mereu valid pentru fereastra de înregistrare
        vol.applyToEvent(event, eventStart(), eventEnd());

        assertNotNull(vol.getEventAvailability(event));
    }

    // ==================== VALIDARE 1: APLICARE DUPLICATĂ ====================

    @Test
    @DisplayName("[V1] Lista goală → getEventAvailability returnează null → aplicare validă")
    void testApply_V1_EmptyList_NoException() {
        Event event   = createValidEvent();
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        // lista eventAvailabilities este goală → getEventAvailability parcurge 0 elemente
        assertDoesNotThrow(() -> vol.applyToEvent(event, eventStart(), eventEnd(), validCurrentTime()));
    }

    @Test
    @DisplayName("[V1] Voluntarul a aplicat deja la același event → excepție")
    void testApply_V1_AlreadyApplied_Throws() {
        Event event   = createValidEvent();
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        vol.applyToEvent(event, eventStart(), eventEnd(), validCurrentTime());

        // A doua aplicare la același event → getEventAvailability != null → excepție
        assertThrows(IllegalArgumentException.class, () ->
            vol.applyToEvent(event, eventStart(), eventEnd(), validCurrentTime())
        );
    }

    @Test
    @DisplayName("[V1] Voluntarul a aplicat la 1 alt event → getEventAvailability parcurge 1 element → aplicare validă")
    void testApply_V1_OneOtherEvent_NoException() {
        Event event1 = createValidEvent();
        Event event2 = new Event("Other Event", createValidEventDate(), createValidAdmin());
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        vol.applyToEvent(event1, eventStart(), eventEnd()); // 1 element în listă
        // Aplicare la event2 → getEventAvailability parcurge lista cu 1 element, nu găsește event2
        assertDoesNotThrow(() -> vol.applyToEvent(event2, eventStart(), eventEnd()));

        assertEquals(2, vol.getEventAvailabilities().size());
    }

    @Test
    @DisplayName("[V1] Voluntarul a aplicat la N alte event-uri → getEventAvailability parcurge N elemente → aplicare validă")
    void testApply_V1_NExistingEvents_NoException() {
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        // Creează și aplică la 4 event-uri diferite
        for (int i = 0; i < 4; i++) {
            Event ev = new Event("Event " + i, createValidEventDate(), createValidAdmin());
            vol.applyToEvent(ev, eventStart(), eventEnd());
        }
        assertEquals(4, vol.getEventAvailabilities().size());

        // Al 5-lea event diferit → getEventAvailability parcurge 4 elemente fără să găsească
        Event event5 = new Event("Event 5", createValidEventDate(), createValidAdmin());
        assertDoesNotThrow(() -> vol.applyToEvent(event5, eventStart(), eventEnd()));
        assertEquals(5, vol.getEventAvailabilities().size());
    }

    // ==================== VALIDARE 2: FEREASTRA DE ÎNREGISTRARE ====================
    // compareDates: diferențiază la nivel de an → lună → zi → oră*60+minut

    @Test
    @DisplayName("[V2] currentTime < regStart la nivel de AN → excepție")
    void testApply_V2_BeforeRegStart_YearLevel_Throws() {
        Event event   = createValidEvent(); // regStart = 2024-01-01 09:00
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        SimpleDate currentTime = date(2023, 12, 31, 23, 59); // an diferit

        assertThrows(IllegalArgumentException.class, () ->
            vol.applyToEvent(event, eventStart(), eventEnd(), currentTime)
        );
    }

    @Test
    @DisplayName("[V2] currentTime < regStart la nivel de LUNĂ → excepție")
    void testApply_V2_BeforeRegStart_MonthLevel_Throws() {
        Event event   = createValidEvent(); // regStart = 2024-01-01 09:00
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        SimpleDate currentTime = date(2023, 11, 1, 9, 0); // lună diferită, același an → an 2023 < 2024

        assertThrows(IllegalArgumentException.class, () ->
            vol.applyToEvent(event, eventStart(), eventEnd(), currentTime)
        );
    }

    @Test
    @DisplayName("[V2] currentTime < regStart la nivel de ZI → excepție")
    void testApply_V2_BeforeRegStart_DayLevel_Throws() {
        // regStart = 2024-01-01 09:00 → zi mai mică imposibilă (ziua 1 e minimul)
        // Folosim un event cu regStart pe ziua 5
        SimpleDate regStart   = date(2024, 1,  5,  9,  0);
        SimpleDate regEnd     = date(2024, 1, 15, 17,  0);
        SimpleDate evStart    = date(2024, 1, 20,  8,  0);
        SimpleDate evEnd      = date(2024, 1, 22, 18,  0);
        EventDate eventDate   = new EventDate(evStart, evEnd, regStart, regEnd);
        Event event           = new Event("Day Test", eventDate, createValidAdmin());
        Volunteer vol         = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        SimpleDate currentTime = date(2024, 1, 4, 9, 0); // cu o zi înainte de regStart

        assertThrows(IllegalArgumentException.class, () ->
            vol.applyToEvent(event, evStart, evEnd, currentTime)
        );
    }

    @Test
    @DisplayName("[V2] currentTime < regStart la nivel de MINUT → excepție")
    void testApply_V2_BeforeRegStart_MinuteLevel_Throws() {
        // regStart = 2024-01-01 09:00
        Event event   = createValidEvent();
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        SimpleDate currentTime = date(2024, 1, 1, 8, 59); // cu 1 minut înainte

        assertThrows(IllegalArgumentException.class, () ->
            vol.applyToEvent(event, eventStart(), eventEnd(), currentTime)
        );
    }

    @Test
    @DisplayName("[V2] currentTime == regStart (limita inferioară exactă) → valid")
    void testApply_V2_ExactlyAtRegStart_Valid() {
        Event event   = createValidEvent(); // regStart = 2024-01-01 09:00
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        SimpleDate currentTime = date(2024, 1, 1, 9, 0); // exact regStart

        assertDoesNotThrow(() -> vol.applyToEvent(event, eventStart(), eventEnd(), currentTime));
    }

    @Test
    @DisplayName("[V2] currentTime == regEnd (limita superioară exactă) → valid")
    void testApply_V2_ExactlyAtRegEnd_Valid() {
        Event event   = createValidEvent(); // regEnd = 2024-01-15 17:00
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        SimpleDate currentTime = date(2024, 1, 15, 17, 0); // exact regEnd

        assertDoesNotThrow(() -> vol.applyToEvent(event, eventStart(), eventEnd(), currentTime));
    }

    @Test
    @DisplayName("[V2] currentTime > regEnd la nivel de MINUT → excepție")
    void testApply_V2_AfterRegEnd_MinuteLevel_Throws() {
        Event event   = createValidEvent(); // regEnd = 2024-01-15 17:00
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        SimpleDate currentTime = date(2024, 1, 15, 17, 1); // cu 1 minut după regEnd

        assertThrows(IllegalArgumentException.class, () ->
            vol.applyToEvent(event, eventStart(), eventEnd(), currentTime)
        );
    }

    @Test
    @DisplayName("[V2] currentTime > regEnd la nivel de ZI → excepție")
    void testApply_V2_AfterRegEnd_DayLevel_Throws() {
        Event event   = createValidEvent(); // regEnd = 2024-01-15 17:00
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        SimpleDate currentTime = date(2024, 1, 16, 9, 0); // ziua imediat următoare

        assertThrows(IllegalArgumentException.class, () ->
            vol.applyToEvent(event, eventStart(), eventEnd(), currentTime)
        );
    }

    @Test
    @DisplayName("[V2] currentTime > regEnd la nivel de AN → excepție")
    void testApply_V2_AfterRegEnd_YearLevel_Throws() {
        Event event   = createValidEvent(); // regEnd = 2024-01-15
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        SimpleDate currentTime = date(2025, 1, 1, 9, 0); // an ulterior

        assertThrows(IllegalArgumentException.class, () ->
            vol.applyToEvent(event, eventStart(), eventEnd(), currentTime)
        );
    }

    @Test
    @DisplayName("[V2] currentTime în mijlocul ferestrei → valid")
    void testApply_V2_MiddleOfWindow_Valid() {
        Event event   = createValidEvent();
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        assertDoesNotThrow(() ->
            vol.applyToEvent(event, eventStart(), eventEnd(), validCurrentTime())
        );
    }

    // ==================== VALIDARE 3: DISPONIBILITATE ÎN AFARA INTERVALULUI EVENTULUI ====================

    @Test
    @DisplayName("[V3] availableFrom < eventStart → excepție")
    void testApply_V3_AvailableFromBeforeEventStart_Throws() {
        Event event   = createValidEvent(); // eventStart = 2024-01-20 08:00
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        SimpleDate from = date(2024, 1, 19, 8, 0); // o zi înainte de eventStart

        assertThrows(IllegalArgumentException.class, () ->
            vol.applyToEvent(event, from, eventEnd(), validCurrentTime())
        );
    }

    @Test
    @DisplayName("[V3] availableFrom < eventStart la nivel de MINUT → excepție")
    void testApply_V3_AvailableFromBeforeEventStart_MinuteLevel_Throws() {
        Event event   = createValidEvent(); // eventStart = 2024-01-20 08:00
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        SimpleDate from = date(2024, 1, 20, 7, 59); // cu 1 minut înainte de eventStart

        assertThrows(IllegalArgumentException.class, () ->
            vol.applyToEvent(event, from, eventEnd(), validCurrentTime())
        );
    }

    @Test
    @DisplayName("[V3] availableFrom == eventStart (limita inferioară exactă) → valid")
    void testApply_V3_AvailableFromExactlyAtEventStart_Valid() {
        Event event   = createValidEvent(); // eventStart = 2024-01-20 08:00
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        assertDoesNotThrow(() ->
            vol.applyToEvent(event, eventStart(), eventEnd(), validCurrentTime())
        );
    }

    @Test
    @DisplayName("[V3] availableTo > eventEnd → excepție")
    void testApply_V3_AvailableToAfterEventEnd_Throws() {
        Event event   = createValidEvent(); // eventEnd = 2024-01-22 18:00
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        SimpleDate to = date(2024, 1, 23, 8, 0); // o zi după eventEnd

        assertThrows(IllegalArgumentException.class, () ->
            vol.applyToEvent(event, eventStart(), to, validCurrentTime())
        );
    }

    @Test
    @DisplayName("[V3] availableTo > eventEnd la nivel de MINUT → excepție")
    void testApply_V3_AvailableToAfterEventEnd_MinuteLevel_Throws() {
        Event event   = createValidEvent(); // eventEnd = 2024-01-22 18:00
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        SimpleDate to = date(2024, 1, 22, 18, 1); // cu 1 minut după eventEnd

        assertThrows(IllegalArgumentException.class, () ->
            vol.applyToEvent(event, eventStart(), to, validCurrentTime())
        );
    }

    @Test
    @DisplayName("[V3] availableTo == eventEnd (limita superioară exactă) → valid")
    void testApply_V3_AvailableToExactlyAtEventEnd_Valid() {
        Event event   = createValidEvent(); // eventEnd = 2024-01-22 18:00
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        assertDoesNotThrow(() ->
            vol.applyToEvent(event, eventStart(), eventEnd(), validCurrentTime())
        );
    }

    @Test
    @DisplayName("[V3] availableFrom și availableTo strict în interior → valid")
    void testApply_V3_AvailabilityStrictlyInsideEventRange_Valid() {
        Event event   = createValidEvent(); // event: 20 → 22 ian
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        // Disponibilitate parțială (doar ziua 21)
        SimpleDate from = date(2024, 1, 21,  8,  0);
        SimpleDate to   = date(2024, 1, 21, 18,  0);

        assertDoesNotThrow(() -> vol.applyToEvent(event, from, to, validCurrentTime()));
    }

    @Test
    @DisplayName("[V3] ambele limite greșite simultan: from < start și to > end → excepție")
    void testApply_V3_BothOutOfBounds_Throws() {
        Event event   = createValidEvent();
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        SimpleDate from = date(2024, 1, 19,  8,  0); // înainte de eventStart
        SimpleDate to   = date(2024, 1, 23, 18,  0); // după eventEnd

        // Condiția OR: prima sub-condiție (from < start) este suficientă
        assertThrows(IllegalArgumentException.class, () ->
            vol.applyToEvent(event, from, to, validCurrentTime())
        );
    }

    // ==================== CONSTRUCTOR EVA: availableFrom >= availableTo ====================

    @Test
    @DisplayName("[EVA] availableFrom == availableTo → excepție din constructorul EventVolunteerAvailability")
    void testApply_EVA_FromEqualsTo_Throws() {
        Event event   = createValidEvent();
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        SimpleDate sameDate = eventStart(); // from == to

        // Trece de V1, V2, V3 dar EVA aruncă excepție (from nu e strict < to)
        assertThrows(IllegalArgumentException.class, () ->
            vol.applyToEvent(event, sameDate, sameDate, validCurrentTime())
        );
    }

    @Test
    @DisplayName("[EVA] availableFrom > availableTo (inversate) → excepție din EVA")
    void testApply_EVA_FromAfterTo_Throws() {
        Event event   = createValidEvent();
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        SimpleDate from = date(2024, 1, 21, 18, 0); // mai târziu
        SimpleDate to   = date(2024, 1, 21,  8, 0); // mai devreme

        assertThrows(IllegalArgumentException.class, () ->
            vol.applyToEvent(event, from, to, validCurrentTime())
        );
    }

    // ==================== EFECTE SIDE-EFFECT ====================

    @Test
    @DisplayName("[S1] Lista eventAvailabilities a voluntarului este actualizată după aplicare")
    void testApply_SideEffect_VolunteerListUpdated() {
        Event event   = createValidEvent();
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        assertEquals(0, vol.getEventAvailabilities().size());

        vol.applyToEvent(event, eventStart(), eventEnd(), validCurrentTime());

        assertEquals(1, vol.getEventAvailabilities().size());
        assertEquals(event, vol.getEventAvailabilities().get(0).getEvent());
    }

    @Test
    @DisplayName("[S2] Lista enrolledVolunteers a eventului este actualizată după aplicare")
    void testApply_SideEffect_EventListUpdated() {
        Event event   = createValidEvent();
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        assertFalse(event.hasVolunteerApplied(vol));

        vol.applyToEvent(event, eventStart(), eventEnd(), validCurrentTime());

        assertTrue(event.hasVolunteerApplied(vol));
        assertEquals(1, event.getEnrolledVolunteers().size());
    }

    @Test
    @DisplayName("[S1+S2] Datele de disponibilitate sunt stocate corect în obiectul EVA")
    void testApply_SideEffect_CorrectDatesStored() {
        Event event   = createValidEvent();
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        SimpleDate from = date(2024, 1, 20, 10,  0);
        SimpleDate to   = date(2024, 1, 21, 16,  0);

        vol.applyToEvent(event, from, to, validCurrentTime());

        EventVolunteerAvailability av = vol.getEventAvailability(event);
        assertNotNull(av);
        assertEquals(from, av.getAvailableFrom());
        assertEquals(to,   av.getAvailableTo());
        assertEquals(event, av.getEvent());
        assertEquals(vol,   av.getVolunteer());
    }

    // ==================== APLICARE LA MULTIPLE EVENT-URI ====================

    @Test
    @DisplayName("Aplicare la 2 event-uri diferite → ambele înregistrate corect")
    void testApply_TwoEvents_BothRegistered() {
        Event event1  = createValidEvent();
        Event event2  = new Event("Alt Event", createValidEventDate(), createValidAdmin());
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        vol.applyToEvent(event1, eventStart(), eventEnd(), validCurrentTime());
        vol.applyToEvent(event2, eventStart(), eventEnd(), validCurrentTime());

        assertEquals(2, vol.getEventAvailabilities().size());
        assertNotNull(vol.getEventAvailability(event1));
        assertNotNull(vol.getEventAvailability(event2));
        assertTrue(event1.hasVolunteerApplied(vol));
        assertTrue(event2.hasVolunteerApplied(vol));
    }

    @Test
    @DisplayName("Aplicare la N event-uri diferite → toate înregistrate, getEventAvailability parcurge lista")
    void testApply_NEvents_AllRegistered() {
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");
        int n = 5;
        Event[] events = new Event[n];

        for (int i = 0; i < n; i++) {
            events[i] = new Event("Event " + i, createValidEventDate(), createValidAdmin());
            vol.applyToEvent(events[i], eventStart(), eventEnd());
        }

        assertEquals(n, vol.getEventAvailabilities().size());
        for (int i = 0; i < n; i++) {
            assertNotNull(vol.getEventAvailability(events[i]));
        }
    }

    @Test
    @DisplayName("Aplicare la event1, apoi aplicare duplicată la event1 → excepție; event2 rămâne disponibil")
    void testApply_DuplicateFirstEvent_SecondStillAvailable() {
        Event event1  = createValidEvent();
        Event event2  = new Event("Alt Event", createValidEventDate(), createValidAdmin());
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        vol.applyToEvent(event1, eventStart(), eventEnd());

        assertThrows(IllegalArgumentException.class, () ->
            vol.applyToEvent(event1, eventStart(), eventEnd())
        );

        // Lista nu s-a modificat, event2 poate fi aplicat în continuare
        assertEquals(1, vol.getEventAvailabilities().size());
        assertDoesNotThrow(() -> vol.applyToEvent(event2, eventStart(), eventEnd()));
        assertEquals(2, vol.getEventAvailabilities().size());
    }

    // ==================== SUPRAÎNCĂRCAREA CU 3 PARAMETRI ====================

    @Test
    @DisplayName("applyToEvent(3 param) - fereastra de înregistrare este întotdeauna valabilă")
    void testApply_ThreeParam_AlwaysValidRegistrationWindow() {
        Event event   = createValidEvent();
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        // Supraîncărcarea folosește currentTime = regStart → compareDates(regStart, regStart) = 0 → valid
        assertDoesNotThrow(() -> vol.applyToEvent(event, eventStart(), eventEnd()));
        assertNotNull(vol.getEventAvailability(event));
    }

    @Test
    @DisplayName("applyToEvent(3 param) - validările V3 și EVA sunt aplicate și fără currentTime")
    void testApply_ThreeParam_V3StillValidated() {
        Event event   = createValidEvent();
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        SimpleDate from = date(2024, 1, 19, 8, 0); // înainte de eventStart → V3 aruncă excepție

        assertThrows(IllegalArgumentException.class, () ->
            vol.applyToEvent(event, from, eventEnd())
        );
    }

    @Test
    @DisplayName("applyToEvent(3 param) - aplicare duplicată aruncă excepție și prin supraîncărcare")
    void testApply_ThreeParam_DuplicateThrows() {
        Event event   = createValidEvent();
        Volunteer vol = createVolunteer("Ion", "Popescu", "ion@example.com", "1234567890");

        vol.applyToEvent(event, eventStart(), eventEnd());

        assertThrows(IllegalArgumentException.class, () ->
            vol.applyToEvent(event, eventStart(), eventEnd()) // supraîncărcarea cu 3 param
        );
    }
}
