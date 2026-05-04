public class SimpleDateValidationTest {

    public static void runTests() {
        System.out.println("4. SIMPLE DATE VALIDATION:");
        testDateValidation();
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
}
