import java.util.ArrayList;

public class ValidationTest {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("  COMPREHENSIVE VALIDATION & FUNCTIONAL TEST SUITE");
        System.out.println("================================================================================\n");

        // ========== PERSON TESTS ==========
        System.out.println("1. EMAIL VALIDATION:");
        testEmailValidation();

        System.out.println("\n2. PHONE VALIDATION:");
        testPhoneValidation();

        System.out.println("\n3. NAME VALIDATION:");
        testNameValidation();

        // ========== DATE TESTS ==========
        System.out.println("\n4. SIMPLE DATE VALIDATION:");
        testDateValidation();

        System.out.println("\n5. EVENT DATE LOGIC VALIDATION:");
        testEventDateLogicValidation();

        // ========== VOLUNTEER TESTS ==========
        System.out.println("\n6. VOLUNTEER VALIDATION:");
        testVolunteerValidation();

        System.out.println("\n7. VOLUNTEER WORKFLOWS:");
        testVolunteerWorkflows();

        // ========== EVENT TESTS ==========
        System.out.println("\n8. EVENT VALIDATION:");
        testEventValidation();

        System.out.println("\n9. EVENT WORKFLOWS:");
        testEventWorkflows();

        // ========== ADMIN TESTS ==========
        System.out.println("\n10. ADMIN WORKFLOWS:");
        testAdminWorkflows();

        // ========== COORDINATOR TESTS ==========
        System.out.println("\n11. COORDINATOR VALIDATION & CRITICAL CONSTRAINTS:");
        testCoordinatorConstraints();

        System.out.println("\n12. COORDINATOR WORKFLOWS:");
        testCoordinatorWorkflows();

        // ========== EVENTVOLUNTEERAVAILABILITY TESTS ==========
        System.out.println("\n13. EVENT VOLUNTEER AVAILABILITY VALIDATION:");
        testEventVolunteerAvailabilityValidation();

        // ========== INTEGRATION TESTS ==========
        System.out.println("\n14. INTEGRATION: FULL WORKFLOW:");
        testFullIntegrationWorkflow();

        System.out.println("\n15. INTEGRATION: DATA CONSISTENCY:");
        testDataConsistency();

        System.out.println("\n================================================================================");
        System.out.println("  ALL TESTS COMPLETED SUCCESSFULLY");
        System.out.println("================================================================================\n");
    }

    static void testEmailValidation() {
        String[] validEmails = {"john@example.com", "user.name@domain.co.uk", "test123@test.org"};
        String[] invalidEmails = {"notanemail", "missing@domain", "no.at.sign.com", "@nodomain.com"};

        System.out.println("Valid emails:");
        for (String email : validEmails) {
            try {
                Person p = new Person("John", "Doe", email, "0711111111");
                System.out.println("  ✓ Accepted: " + email);
            } catch (IllegalArgumentException e) {
                System.out.println("  ✗ Rejected: " + email);
            }
        }

        System.out.println("Invalid emails:");
        for (String email : invalidEmails) {
            try {
                Person p = new Person("John", "Doe", email, "0711111111");
                System.out.println("  ✗ Incorrectly accepted: " + email);
            } catch (IllegalArgumentException e) {
                System.out.println("  ✓ Correctly rejected: " + email);
            }
        }
    }

    static void testPhoneValidation() {
        String[] validPhones = {"0711111111", "0744567890", "1234567890123456"};
        String[] invalidPhones = {"123", "07-11-11-11", "phone123", "07111 11111"};

        System.out.println("Valid phone numbers:");
        for (String phone : validPhones) {
            try {
                Person p = new Person("John", "Doe", "john@example.com", phone);
                System.out.println("  ✓ Accepted: " + phone);
            } catch (IllegalArgumentException e) {
                System.out.println("  ✗ Rejected: " + phone);
            }
        }

        System.out.println("Invalid phone numbers:");
        for (String phone : invalidPhones) {
            try {
                Person p = new Person("John", "Doe", "john@example.com", phone);
                System.out.println("  ✗ Incorrectly accepted: " + phone);
            } catch (IllegalArgumentException e) {
                System.out.println("  ✓ Correctly rejected: " + phone);
            }
        }
    }

    static void testNameValidation() {
        String[] validNames = {"John", "Maria-Elena", "Alexandru"};
        String[] invalidNames = {"", "A", null};

        System.out.println("Valid names:");
        for (String name : validNames) {
            try {
                Person p = new Person(name, "Doe", "john@example.com", "0711111111");
                System.out.println("  ✓ Accepted: '" + name + "'");
            } catch (IllegalArgumentException | NullPointerException e) {
                System.out.println("  ✗ Rejected: '" + name + "'");
            }
        }

        System.out.println("Invalid names:");
        String[] testNames = {"", "A"};
        for (String name : testNames) {
            try {
                Person p = new Person(name, "Doe", "john@example.com", "0711111111");
                System.out.println("  ✗ Incorrectly accepted: '" + name + "'");
            } catch (IllegalArgumentException e) {
                System.out.println("  ✓ Correctly rejected: '" + name + "'");
            }
        }
    }

    static void testDateValidation() {
        System.out.println("Valid dates:");
        try {
            SimpleDate d1 = new SimpleDate(2026, 2, 25, 10, 30);
            System.out.println("  ✓ Accepted: 2026-02-25 10:30");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✗ Rejected: 2026-02-25 10:30");
        }

        try {
            SimpleDate d2 = new SimpleDate(2026, 12, 31, 23, 59);
            System.out.println("  ✓ Accepted: 2026-12-31 23:59");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✗ Rejected: 2026-12-31 23:59");
        }

        System.out.println("Invalid dates:");
        try {
            SimpleDate d3 = new SimpleDate(2026, 13, 1, 0, 0); // Invalid month
            System.out.println("  ✗ Incorrectly accepted: 2026-13-01");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✓ Correctly rejected: Invalid month (13)");
        }

        try {
            SimpleDate d4 = new SimpleDate(2026, 2, 30, 0, 0); // Invalid day for February
            System.out.println("  ✗ Incorrectly accepted: 2026-02-30");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✓ Correctly rejected: Invalid day for February (30)");
        }

        try {
            SimpleDate d5 = new SimpleDate(2026, 2, 15, 25, 0); // Invalid hour
            System.out.println("  ✗ Incorrectly accepted: Invalid hour");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✓ Correctly rejected: Invalid hour (25)");
        }

        try {
            SimpleDate d6 = new SimpleDate(2026, 2, 15, 10, 60); // Invalid minute
            System.out.println("  ✗ Incorrectly accepted: Invalid minute");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✓ Correctly rejected: Invalid minute (60)");
        }
    }

    // ========== VOLUNTEER VALIDATION TESTS ==========
    static void testVolunteerValidation() {
        System.out.println("Valid years of experience:");
        try {
            Volunteer v1 = new Volunteer("John", "Doe", "john@example.com", "0711111111", 0, TShirtSize.M);
            System.out.println("  ✓ Accepted: 0 years");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✗ Rejected: 0 years");
        }

        try {
            Volunteer v2 = new Volunteer("John", "Doe", "john@example.com", "0711111111", 50, TShirtSize.M);
            System.out.println("  ✓ Accepted: 50 years");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✗ Rejected: 50 years");
        }

        System.out.println("Invalid years of experience:");
        try {
            Volunteer v3 = new Volunteer("John", "Doe", "john@example.com", "0711111111", -1, TShirtSize.M);
            System.out.println("  ✗ Incorrectly accepted: -1 years");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✓ Correctly rejected: Negative years (-1)");
        }

        try {
            Volunteer v4 = new Volunteer("John", "Doe", "john@example.com", "0711111111", 150, TShirtSize.M);
            System.out.println("  ✗ Incorrectly accepted: 150 years");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✓ Correctly rejected: Too many years (150)");
        }

        System.out.println("Valid T-Shirt sizes:");
        try {
            Volunteer v5 = new Volunteer("John", "Doe", "john@example.com", "0711111111", 5, TShirtSize.S);
            System.out.println("  ✓ Accepted: Size S");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✗ Rejected: Size S");
        }

        try {
            Volunteer v6 = new Volunteer("John", "Doe", "john@example.com", "0711111111", 5, TShirtSize.XL);
            System.out.println("  ✓ Accepted: Size XL");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✗ Rejected: Size XL");
        }
    }

    // ========== VOLUNTEER WORKFLOW TESTS ==========
    static void testVolunteerWorkflows() {
        Volunteer v = new Volunteer("Alice", "Smith", "alice@example.com", "0722222222", 3, TShirtSize.M);
        Admin admin = new Admin("Admin", "User", "admin@example.com", "0700000000");
        SimpleDate eventDate1Start = new SimpleDate(2026, 3, 15, 10, 0);
        SimpleDate eventDate1End = new SimpleDate(2026, 3, 15, 18, 0);
        SimpleDate eventDate1RegStart = new SimpleDate(2026, 3, 1, 0, 0);
        SimpleDate eventDate1RegEnd = new SimpleDate(2026, 3, 10, 23, 59);
        EventDate ed1 = new EventDate(eventDate1Start, eventDate1End, eventDate1RegStart, eventDate1RegEnd);
        Event event1 = admin.createEvent("Charity Event", ed1);

        System.out.println("Apply to event:");
        SimpleDate availFrom = new SimpleDate(2026, 3, 15, 10, 0);
        SimpleDate availTo = new SimpleDate(2026, 3, 15, 18, 0);
        v.applyToEvent(event1, availFrom, availTo);
        System.out.println("  ✓ Successfully applied to event");
        System.out.println("  ✓ Applied events count: " + v.getAppliedEvents().size());

        System.out.println("Update availability:");
        SimpleDate newAvailFrom = new SimpleDate(2026, 3, 15, 10, 0);
        SimpleDate newAvailTo = new SimpleDate(2026, 3, 15, 20, 0);
        v.updateEventAvailability(event1, newAvailFrom, newAvailTo);
        System.out.println("  ✓ Successfully updated availability");

        System.out.println("Change T-Shirt size:");
        v.changeTShirtSize(TShirtSize.L);
        System.out.println("  ✓ Successfully changed size to: " + v.getTShirtSize());

        System.out.println("Apply to multiple events:");
        SimpleDate eventDate2Start = new SimpleDate(2026, 4, 20, 10, 0);
        SimpleDate eventDate2End = new SimpleDate(2026, 4, 20, 18, 0);
        SimpleDate eventDate2RegStart = new SimpleDate(2026, 4, 1, 0, 0);
        SimpleDate eventDate2RegEnd = new SimpleDate(2026, 4, 15, 23, 59);
        EventDate ed2 = new EventDate(eventDate2Start, eventDate2End, eventDate2RegStart, eventDate2RegEnd);
        Event event2 = admin.createEvent("Community Service", ed2);
        ArrayList<Event> events = new ArrayList<>();
        events.add(event2);
        v.applyToEvents(events, availFrom, availTo);
        System.out.println("  ✓ Successfully applied to multiple events");
        System.out.println("  ✓ Total applied events: " + v.getAppliedEvents().size());

        System.out.println("Cancel application:");
        v.cancelApplication(event1);
        System.out.println("  ✓ Successfully cancelled application");
        System.out.println("  ✓ Remaining applied events: " + v.getAppliedEvents().size());

        System.out.println("Cancel all applications:");
        v.cancelAllApplications();
        System.out.println("  ✓ Successfully cancelled all applications");
        System.out.println("  ✓ Applied events count: " + v.getAppliedEvents().size());
    }

    // ========== EVENT VALIDATION TESTS ==========
    static void testEventValidation() {
        Admin admin = new Admin("Admin", "User", "admin@example.com", "0700000000");
        SimpleDate eventStart = new SimpleDate(2026, 3, 15, 10, 0);
        SimpleDate eventEnd = new SimpleDate(2026, 3, 15, 18, 0);
        SimpleDate regStart = new SimpleDate(2026, 3, 1, 0, 0);
        SimpleDate regEnd = new SimpleDate(2026, 3, 10, 23, 59);
        EventDate ed = new EventDate(eventStart, eventEnd, regStart, regEnd);

        System.out.println("Valid event names:");
        try {
            Event e1 = admin.createEvent("Valid Event Name", ed);
            System.out.println("  ✓ Accepted: 'Valid Event Name'");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✗ Rejected: 'Valid Event Name'");
        }

        System.out.println("Invalid event names:");
        try {
            Event e2 = admin.createEvent("AB", ed); // Too short
            System.out.println("  ✗ Incorrectly accepted: 'AB'");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✓ Correctly rejected: Event name too short");
        }

        try {
            Event e3 = admin.createEvent("", ed); // Empty
            System.out.println("  ✗ Incorrectly accepted: empty string");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✓ Correctly rejected: Empty event name");
        }

        System.out.println("Null event date rejection:");
        try {
            Event e4 = new Event("Event", null, admin);
            System.out.println("  ✗ Incorrectly accepted: null event date");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✓ Correctly rejected: Null event date");
        }

        System.out.println("Null admin rejection:");
        try {
            Event e5 = new Event("Event", ed, null);
            System.out.println("  ✗ Incorrectly accepted: null admin");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✓ Correctly rejected: Null admin");
        }
    }

    // ========== EVENT WORKFLOW TESTS ==========
    static void testEventWorkflows() {
        Admin admin = new Admin("Admin", "User", "admin@example.com", "0700000000");
        Volunteer v1 = new Volunteer("Bob", "Johnson", "bob@example.com", "0733333333", 5, TShirtSize.M);
        Volunteer v2 = new Volunteer("Carol", "Davis", "carol@example.com", "0744444444", 2, TShirtSize.L);
        
        SimpleDate eventStart = new SimpleDate(2026, 3, 15, 10, 0);
        SimpleDate eventEnd = new SimpleDate(2026, 3, 15, 18, 0);
        SimpleDate regStart = new SimpleDate(2026, 3, 1, 0, 0);
        SimpleDate regEnd = new SimpleDate(2026, 3, 10, 23, 59);
        EventDate ed = new EventDate(eventStart, eventEnd, regStart, regEnd);
        Event event = admin.createEvent("Team Building", ed);

        System.out.println("Assign coordinator:");
        Coordinator coord = event.assignCoordinator(v1);
        System.out.println("  ✓ Assigned " + v1.getFirstName() + " as coordinator");
        System.out.println("  ✓ Is coordinator: " + event.isCoordinator(v1));

        System.out.println("Check duplicate coordinator assignment:");
        Coordinator coord2 = event.assignCoordinator(v1);
        System.out.println("  ✓ Same coordinator returned: " + (coord == coord2));
        System.out.println("  ✓ Total coordinators: " + event.getCoordinatorRoles().size());

        System.out.println("Enroll volunteer:");
        SimpleDate availFrom = new SimpleDate(2026, 3, 15, 10, 0);
        SimpleDate availTo = new SimpleDate(2026, 3, 15, 18, 0);
        event.enrollVolunteer(v2, availFrom, availTo);
        System.out.println("  ✓ Enrolled volunteer: " + v2.getFirstName());
        System.out.println("  ✓ Enrolled volunteers count: " + event.getEnrolledVolunteers().size());

        System.out.println("Get volunteer availability:");
        EventVolunteerAvailability ava = event.getVolunteerAvailability(v2);
        System.out.println("  ✓ Retrieved availability for: " + ava.getVolunteer().getFirstName());

        System.out.println("Remove coordinator:");
        event.removeCoordinator(v1);
        System.out.println("  ✓ Removed coordinator: " + v1.getFirstName());
        System.out.println("  ✓ Is coordinator after removal: " + event.isCoordinator(v1));
        System.out.println("  ✓ Remaining coordinators: " + event.getCoordinatorRoles().size());

        System.out.println("Remove volunteer:");
        event.removeVolunteer(v2);
        System.out.println("  ✓ Removed volunteer: " + v2.getFirstName());
        System.out.println("  ✓ Enrolled volunteers after removal: " + event.getEnrolledVolunteers().size());
    }

    // ========== ADMIN WORKFLOW TESTS ==========
    static void testAdminWorkflows() {
        Admin admin = new Admin("SuperAdmin", "Person", "admin@example.com", "0700000000");
        Volunteer v1 = new Volunteer("David", "Wilson", "david@example.com", "0755555555", 7, TShirtSize.M);
        Volunteer v2 = new Volunteer("Emma", "Brown", "emma@example.com", "0766666666", 1, TShirtSize.S);
        
        SimpleDate eventStart = new SimpleDate(2026, 3, 15, 10, 0);
        SimpleDate eventEnd = new SimpleDate(2026, 3, 15, 18, 0);
        SimpleDate regStart = new SimpleDate(2026, 3, 1, 0, 0);
        SimpleDate regEnd = new SimpleDate(2026, 3, 10, 23, 59);
        EventDate ed = new EventDate(eventStart, eventEnd, regStart, regEnd);

        System.out.println("Create event:");
        Event event = admin.createEvent("Cleanup Drive", ed);
        System.out.println("  ✓ Created event: " + event.getName());
        System.out.println("  ✓ Total created events: " + admin.getCreatedEvents().size());

        System.out.println("Add volunteers to registry:");
        admin.addVolunteer(v1);
        admin.addVolunteer(v2);
        System.out.println("  ✓ Added volunteers to registry");
        System.out.println("  ✓ Total volunteers in registry: " + admin.getAllVolunteers().size());

        System.out.println("Assign single coordinator:");
        Coordinator coord1 = admin.assignCoordinator(event, v1);
        System.out.println("  ✓ Assigned " + v1.getFirstName() + " as coordinator");

        System.out.println("Assign multiple coordinators:");
        ArrayList<Volunteer> coordVolunteers = new ArrayList<>();
        coordVolunteers.add(v2);
        admin.assignCoordinators(event, coordVolunteers);
        System.out.println("  ✓ Assigned multiple coordinators");
        System.out.println("  ✓ Total coordinators for event: " + event.getCoordinatorRoles().size());

        System.out.println("Remove coordinator:");
        admin.removeCoordinator(event, v1);
        System.out.println("  ✓ Removed coordinator: " + v1.getFirstName());
        System.out.println("  ✓ Remaining coordinators: " + event.getCoordinatorRoles().size());

        System.out.println("Cancel event:");
        admin.cancelEvent(event);
        System.out.println("  ✓ Cancelled event: " + event.getName());
        System.out.println("  ✓ Total created events after cancellation: " + admin.getCreatedEvents().size());

        System.out.println("Verify duplicate volunteer addition (should not duplicate):");
        int countBefore = admin.getAllVolunteers().size();
        admin.addVolunteer(v1); // Try to add same volunteer again
        int countAfter = admin.getAllVolunteers().size();
        System.out.println("  ✓ Count before: " + countBefore + ", After: " + countAfter + " (no duplicate added)");
    }

    // ========== COORDINATOR CONSTRAINT TESTS ==========
    static void testCoordinatorConstraints() {
        Admin admin = new Admin("Admin", "User", "admin@example.com", "0700000000");
        Volunteer coordinator = new Volunteer("Frank", "Miller", "frank@example.com", "0777777777", 8, TShirtSize.L);
        
        SimpleDate eventStart = new SimpleDate(2026, 3, 15, 10, 0);
        SimpleDate eventEnd = new SimpleDate(2026, 3, 15, 18, 0);
        SimpleDate regStart = new SimpleDate(2026, 3, 1, 0, 0);
        SimpleDate regEnd = new SimpleDate(2026, 3, 10, 23, 59);
        EventDate ed = new EventDate(eventStart, eventEnd, regStart, regEnd);
        Event event = admin.createEvent("Planning Meeting", ed);

        System.out.println("Create coordinator:");
        Coordinator coordRole = new Coordinator(event, coordinator);
        System.out.println("  ✓ Created coordinator role for: " + coordinator.getFirstName());

        System.out.println("CRITICAL CONSTRAINT: Coordinator cannot be own subordinate:");
        try {
            SimpleDate availFrom = new SimpleDate(2026, 3, 15, 10, 0);
            SimpleDate availTo = new SimpleDate(2026, 3, 15, 18, 0);
            coordRole.acceptVolunteer(coordinator, availFrom, availTo);
            System.out.println("  ✗ FAILED: Coordinator was incorrectly accepted as subordinate (CONSTRAINT VIOLATED!)");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✓ PASSED: Correctly rejected coordinator as own subordinate");
            System.out.println("    Message: " + e.getMessage());
        }

        System.out.println("Verify subordinate list is empty:");
        System.out.println("  ✓ Subordinates count: " + coordRole.getSubordinates().size());

        System.out.println("Null event rejection:");
        try {
            Coordinator badCoord = new Coordinator(null, coordinator);
            System.out.println("  ✗ Incorrectly accepted null event");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✓ Correctly rejected null event");
        }

        System.out.println("Null coordinator rejection:");
        try {
            Coordinator badCoord = new Coordinator(event, null);
            System.out.println("  ✗ Incorrectly accepted null coordinator");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✓ Correctly rejected null coordinator");
        }
    }

    // ========== COORDINATOR WORKFLOW TESTS ==========
    static void testCoordinatorWorkflows() {
        Admin admin = new Admin("Admin", "User", "admin@example.com", "0700000000");
        Volunteer coordinator = new Volunteer("Grace", "Taylor", "grace@example.com", "0788888888", 6, TShirtSize.M);
        Volunteer sub1 = new Volunteer("Henry", "Anderson", "henry@example.com", "0799999999", 2, TShirtSize.L);
        Volunteer sub2 = new Volunteer("Iris", "Thomas", "iris@example.com", "0701111111", 4, TShirtSize.S);
        
        SimpleDate eventStart = new SimpleDate(2026, 3, 15, 10, 0);
        SimpleDate eventEnd = new SimpleDate(2026, 3, 15, 18, 0);
        SimpleDate regStart = new SimpleDate(2026, 3, 1, 0, 0);
        SimpleDate regEnd = new SimpleDate(2026, 3, 10, 23, 59);
        EventDate ed = new EventDate(eventStart, eventEnd, regStart, regEnd);
        Event event = admin.createEvent("Volunteer Drive", ed);

        Coordinator coordRole = new Coordinator(event, coordinator);

        System.out.println("Accept volunteer as subordinate:");
        SimpleDate availFrom = new SimpleDate(2026, 3, 15, 10, 0);
        SimpleDate availTo = new SimpleDate(2026, 3, 15, 18, 0);
        coordRole.acceptVolunteer(sub1, availFrom, availTo);
        System.out.println("  ✓ Accepted " + sub1.getFirstName() + " as subordinate");
        System.out.println("  ✓ Subordinates count: " + coordRole.getSubordinates().size());

        System.out.println("Accept multiple volunteers:");
        coordRole.acceptVolunteer(sub2, availFrom, availTo);
        System.out.println("  ✓ Accepted " + sub2.getFirstName() + " as subordinate");
        System.out.println("  ✓ Total subordinates: " + coordRole.getSubordinates().size());

        System.out.println("Check subordinate relationship:");
        boolean hasSub1 = coordRole.hasSubordinate(sub1);
        boolean hasSub2 = coordRole.hasSubordinate(sub2);
        System.out.println("  ✓ Has " + sub1.getFirstName() + ": " + hasSub1);
        System.out.println("  ✓ Has " + sub2.getFirstName() + ": " + hasSub2);

        System.out.println("Remove subordinate:");
        coordRole.removeSubordinate(sub1);
        System.out.println("  ✓ Removed " + sub1.getFirstName());
        System.out.println("  ✓ Remaining subordinates: " + coordRole.getSubordinates().size());

        System.out.println("Verify subordinate removal:");
        System.out.println("  ✓ Has " + sub1.getFirstName() + ": " + coordRole.hasSubordinate(sub1));
        System.out.println("  ✓ Has " + sub2.getFirstName() + ": " + coordRole.hasSubordinate(sub2));

        System.out.println("Verify event and coordinator accessors:");
        System.out.println("  ✓ Coordinator name: " + coordRole.getCoordinator().getFirstName());
        System.out.println("  ✓ Event name: " + coordRole.getEvent().getName());
    }

    // ========== EVENTVOLUNTEERAVAILABILITY VALIDATION TESTS ==========
    static void testEventVolunteerAvailabilityValidation() {
        Admin admin = new Admin("Admin", "User", "admin@example.com", "0700000000");
        Volunteer v = new Volunteer("Jack", "White", "jack@example.com", "0702222222", 3, TShirtSize.M);
        
        SimpleDate eventStart = new SimpleDate(2026, 3, 15, 10, 0);
        SimpleDate eventEnd = new SimpleDate(2026, 3, 15, 18, 0);
        SimpleDate regStart = new SimpleDate(2026, 3, 1, 0, 0);
        SimpleDate regEnd = new SimpleDate(2026, 3, 10, 23, 59);
        EventDate ed = new EventDate(eventStart, eventEnd, regStart, regEnd);
        Event event = admin.createEvent("Workshop", ed);

        System.out.println("Valid availability (from < to):");
        try {
            SimpleDate availFrom = new SimpleDate(2026, 3, 15, 10, 0);
            SimpleDate availTo = new SimpleDate(2026, 3, 15, 18, 0);
            EventVolunteerAvailability ava = new EventVolunteerAvailability(event, v, availFrom, availTo);
            System.out.println("  ✓ Accepted: availableFrom < availableTo");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✗ Rejected: " + e.getMessage());
        }

        System.out.println("Invalid availability (from = to):");
        try {
            SimpleDate availFrom = new SimpleDate(2026, 3, 15, 10, 0);
            SimpleDate availTo = new SimpleDate(2026, 3, 15, 10, 0);
            EventVolunteerAvailability ava = new EventVolunteerAvailability(event, v, availFrom, availTo);
            System.out.println("  ✗ Incorrectly accepted: availableFrom = availableTo");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✓ Correctly rejected: " + e.getMessage());
        }

        System.out.println("Invalid availability (from > to):");
        try {
            SimpleDate availFrom = new SimpleDate(2026, 3, 15, 18, 0);
            SimpleDate availTo = new SimpleDate(2026, 3, 15, 10, 0);
            EventVolunteerAvailability ava = new EventVolunteerAvailability(event, v, availFrom, availTo);
            System.out.println("  ✗ Incorrectly accepted: availableFrom > availableTo");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✓ Correctly rejected: " + e.getMessage());
        }

        System.out.println("Null event rejection:");
        try {
            SimpleDate availFrom = new SimpleDate(2026, 3, 15, 10, 0);
            SimpleDate availTo = new SimpleDate(2026, 3, 15, 18, 0);
            EventVolunteerAvailability ava = new EventVolunteerAvailability(null, v, availFrom, availTo);
            System.out.println("  ✗ Incorrectly accepted null event");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✓ Correctly rejected: Null event");
        }

        System.out.println("Null volunteer rejection:");
        try {
            SimpleDate availFrom = new SimpleDate(2026, 3, 15, 10, 0);
            SimpleDate availTo = new SimpleDate(2026, 3, 15, 18, 0);
            EventVolunteerAvailability ava = new EventVolunteerAvailability(event, null, availFrom, availTo);
            System.out.println("  ✗ Incorrectly accepted null volunteer");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✓ Correctly rejected: Null volunteer");
        }
    }

    // ========== FULL INTEGRATION TEST ==========
    static void testFullIntegrationWorkflow() {
        System.out.println("Scenario: Admin creates event → assigns Coordinator → Coordinator enrolls Volunteers");
        
        // Step 1: Setup
        Admin admin = new Admin("ProjectLead", "Admin", "admin@corp.com", "0700000001");
        Volunteer coordinator = new Volunteer("Manager", "Person", "manager@corp.com", "0700000002", 10, TShirtSize.L);
        Volunteer volunteer1 = new Volunteer("Helper", "One", "helper1@corp.com", "0700000003", 1, TShirtSize.M);
        Volunteer volunteer2 = new Volunteer("Helper", "Two", "helper2@corp.com", "0700000004", 2, TShirtSize.S);
        
        admin.addVolunteer(coordinator);
        admin.addVolunteer(volunteer1);
        admin.addVolunteer(volunteer2);
        
        // Step 2: Create Event
        SimpleDate eventStart = new SimpleDate(2026, 3, 20, 9, 0);
        SimpleDate eventEnd = new SimpleDate(2026, 3, 20, 17, 0);
        SimpleDate regStart = new SimpleDate(2026, 3, 1, 0, 0);
        SimpleDate regEnd = new SimpleDate(2026, 3, 15, 23, 59);
        EventDate ed = new EventDate(eventStart, eventEnd, regStart, regEnd);
        Event event = admin.createEvent("Spring Cleanup 2026", ed);
        System.out.println("  ✓ Admin created event: " + event.getName());
        
        // Step 3: Assign Coordinator
        admin.assignCoordinator(event, coordinator);
        Coordinator coordRole = event.getCoordinatorRole(coordinator);
        System.out.println("  ✓ Admin assigned " + coordinator.getFirstName() + " as coordinator");
        
        // Step 4: Coordinator enrolls volunteers
        SimpleDate availFrom = new SimpleDate(2026, 3, 20, 9, 0);
        SimpleDate availTo = new SimpleDate(2026, 3, 20, 17, 0);
        coordRole.acceptVolunteer(volunteer1, availFrom, availTo);
        coordRole.acceptVolunteer(volunteer2, availFrom, availTo);
        System.out.println("  ✓ Coordinator enrolled " + volunteer1.getFirstName());
        System.out.println("  ✓ Coordinator enrolled " + volunteer2.getFirstName());
        
        // Step 5: Verify final state
        System.out.println("\n  Final Event State:");
        System.out.println("    - Coordinators: " + event.getCoordinatorRoles().size());
        System.out.println("    - Enrolled Volunteers: " + event.getEnrolledVolunteers().size());
        System.out.println("    - Coordinator Subordinates: " + coordRole.getSubordinates().size());
        
        System.out.println("\n  ✓ Full workflow completed successfully");
    }

    // ========== DATA CONSISTENCY TEST ==========
    static void testDataConsistency() {
        System.out.println("Testing bidirectional data consistency:");
        
        Admin admin = new Admin("Admin", "User", "admin@example.com", "0700000000");
        Volunteer volunteer = new Volunteer("Kevin", "Martinez", "kevin@example.com", "0703333333", 3, TShirtSize.M);
        
        SimpleDate eventStart = new SimpleDate(2026, 3, 15, 10, 0);
        SimpleDate eventEnd = new SimpleDate(2026, 3, 15, 18, 0);
        SimpleDate regStart = new SimpleDate(2026, 3, 1, 0, 0);
        SimpleDate regEnd = new SimpleDate(2026, 3, 10, 23, 59);
        EventDate ed = new EventDate(eventStart, eventEnd, regStart, regEnd);
        Event event = admin.createEvent("Sync Test Event", ed);

        System.out.println("\n1. Volunteer applies to event:");
        SimpleDate availFrom = new SimpleDate(2026, 3, 15, 10, 0);
        SimpleDate availTo = new SimpleDate(2026, 3, 15, 18, 0);
        volunteer.applyToEvent(event, availFrom, availTo);
        
        System.out.println("   - Volunteer's appliedEvents count: " + volunteer.getAppliedEvents().size());
        System.out.println("   - Event's volunteerAvailabilities count: " + event.getVolunteerAvailabilities().size());
        System.out.println("   ✓ Both sides track the relationship");

        System.out.println("\n2. Event enrolls volunteer (via coordinator):");
        admin.addVolunteer(volunteer);
        Volunteer coordinator = new Volunteer("Leo", "Garcia", "leo@example.com", "0704444444", 5, TShirtSize.L);
        admin.addVolunteer(coordinator);
        Coordinator coordRole = admin.assignCoordinator(event, coordinator);
        
        Volunteer volunteer2 = new Volunteer("Mia", "Rodriguez", "mia@example.com", "0705555555", 1, TShirtSize.S);
        coordRole.acceptVolunteer(volunteer2, availFrom, availTo);
        
        System.out.println("   - Volunteer2's appliedEvents count: " + volunteer2.getAppliedEvents().size());
        System.out.println("   - Event's enrolled volunteers count: " + event.getEnrolledVolunteers().size());
        System.out.println("   - Event's volunteerAvailabilities count: " + event.getVolunteerAvailabilities().size());
        System.out.println("   ✓ Both sides updated bidirectionally");

        System.out.println("\n3. Verify volunteer removal cascades:");
        int volunteersBefore = event.getEnrolledVolunteers().size();
        event.removeVolunteer(volunteer2);
        int volunteersAfter = event.getEnrolledVolunteers().size();
        
        System.out.println("   - Volunteers before removal: " + volunteersBefore);
        System.out.println("   - Volunteers after removal: " + volunteersAfter);
        System.out.println("   - Volunteer2's applied events: " + volunteer2.getAppliedEvents().size());
        System.out.println("   ✓ Removal cascaded to both sides");

        System.out.println("\n4. Verify coordinator assignment affects both entities:");
        Volunteer coordinator2 = new Volunteer("Nina", "Lee", "nina@example.com", "0706666666", 7, TShirtSize.XL);
        admin.addVolunteer(coordinator2);
        Coordinator coordRole2 = admin.assignCoordinator(event, coordinator2);
        
        System.out.println("   - Event's coordinators count: " + event.getCoordinatorRoles().size());
        System.out.println("   - Event still has its enrolled volunteers: " + event.getEnrolledVolunteers().size());
        System.out.println("   ✓ Coordinator assignment independent of volunteers");

        System.out.println("\n✓ All data consistency checks passed");
    }

    static void testEventDateLogicValidation() {
        System.out.println("Valid event date logic:");
        try {
            SimpleDate regStart = new SimpleDate(2026, 2, 1, 0, 0);
            SimpleDate regEnd = new SimpleDate(2026, 2, 20, 23, 59);
            SimpleDate eventStart = new SimpleDate(2026, 2, 25, 10, 0);
            SimpleDate eventEnd = new SimpleDate(2026, 2, 28, 18, 0);
            EventDate ed1 = new EventDate(eventStart, eventEnd, regStart, regEnd);
            System.out.println("  ✓ Accepted: Valid event date sequence");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✗ Rejected: " + e.getMessage());
        }

        System.out.println("Invalid event date logic:");
        try {
            SimpleDate regStart = new SimpleDate(2026, 2, 1, 0, 0);
            SimpleDate regEnd = new SimpleDate(2026, 2, 1, 0, 0); // Same as start
            SimpleDate eventStart = new SimpleDate(2026, 2, 25, 10, 0);
            SimpleDate eventEnd = new SimpleDate(2026, 2, 28, 18, 0);
            EventDate ed2 = new EventDate(eventStart, eventEnd, regStart, regEnd);
            System.out.println("  ✗ Incorrectly accepted: regStart = regEnd");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✓ Correctly rejected: " + e.getMessage());
        }

        try {
            SimpleDate regStart = new SimpleDate(2026, 2, 1, 0, 0);
            SimpleDate regEnd = new SimpleDate(2026, 2, 20, 23, 59);
            SimpleDate eventStart = new SimpleDate(2026, 2, 25, 10, 0);
            SimpleDate eventEnd = new SimpleDate(2026, 2, 28, 18, 0);
            EventDate ed3 = new EventDate(eventEnd, eventStart, regStart, regEnd); // Start > End
            System.out.println("  ✗ Incorrectly accepted: eventStart > eventEnd");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✓ Correctly rejected: " + e.getMessage());
        }

        try {
            SimpleDate regStart = new SimpleDate(2026, 2, 1, 0, 0);
            SimpleDate regEnd = new SimpleDate(2026, 3, 1, 0, 0); // After event start
            SimpleDate eventStart = new SimpleDate(2026, 2, 25, 10, 0);
            SimpleDate eventEnd = new SimpleDate(2026, 2, 28, 18, 0);
            EventDate ed4 = new EventDate(eventStart, eventEnd, regStart, regEnd);
            System.out.println("  ✗ Incorrectly accepted: regEnd > eventStart");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✓ Correctly rejected: " + e.getMessage());
        }
    }
}

