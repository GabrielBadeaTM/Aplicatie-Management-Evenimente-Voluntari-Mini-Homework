public class EventDateValidationTest {

    public static void runTests() {
        System.out.println("5. EVENT DATE LOGIC VALIDATION:");
        testEventDateLogicValidation();
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
