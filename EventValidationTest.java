public class EventValidationTest {

    public static void runTests() {
        System.out.println("8. EVENT VALIDATION:");
        testEventValidation();

        System.out.println("\n9. EVENT WORKFLOWS:");
        testEventWorkflows();
    }

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
}
