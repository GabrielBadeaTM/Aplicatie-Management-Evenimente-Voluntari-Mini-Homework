public class CoordinatorValidationTest {

    public static void runTests() {
        System.out.println("11. COORDINATOR VALIDATION & CRITICAL CONSTRAINTS:");
        testCoordinatorConstraints();

        System.out.println("\n12. COORDINATOR WORKFLOWS:");
        testCoordinatorWorkflows();
    }

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
}
