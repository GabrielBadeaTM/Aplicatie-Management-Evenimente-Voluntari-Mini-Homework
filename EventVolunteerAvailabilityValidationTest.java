public class EventVolunteerAvailabilityValidationTest {

    public static void runTests() {
        System.out.println("13. EVENT VOLUNTEER AVAILABILITY VALIDATION:");
        testEventVolunteerAvailabilityValidation();
    }

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
}
