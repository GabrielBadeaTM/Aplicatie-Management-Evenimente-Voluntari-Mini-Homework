public class ValidationTest {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("  COMPREHENSIVE VALIDATION & FUNCTIONAL TEST SUITE");
        System.out.println("================================================================================\n");

        // ========== PERSON TESTS ==========
        PersonValidationTest.runTests();

        // ========== DATE TESTS ==========
        System.out.println();
        SimpleDateValidationTest.runTests();

        System.out.println();
        EventDateValidationTest.runTests();

        // ========== VOLUNTEER TESTS ==========
        System.out.println();
        VolunteerValidationTest.runTests();

        // ========== EVENT TESTS ==========
        System.out.println();
        EventValidationTest.runTests();

        // ========== ADMIN TESTS ==========
        System.out.println();
        AdminValidationTest.runTests();

        // ========== COORDINATOR TESTS ==========
        System.out.println();
        CoordinatorValidationTest.runTests();

        // ========== EVENTVOLUNTEERAVAILABILITY TESTS ==========
        System.out.println();
        EventVolunteerAvailabilityValidationTest.runTests();

        // ========== INTEGRATION TESTS ==========
        System.out.println();
        IntegrationTests.runTests();

        System.out.println("\n================================================================================");
        System.out.println("  ALL TESTS COMPLETED SUCCESSFULLY");
        System.out.println("================================================================================\n");
    }
}
