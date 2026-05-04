import java.util.ArrayList;

public class VolunteerValidationTest {

    public static void runTests() {
        System.out.println("6. VOLUNTEER VALIDATION:");
        testVolunteerValidation();

        System.out.println("\n7. VOLUNTEER WORKFLOWS:");
        testVolunteerWorkflows();
    }

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
}
