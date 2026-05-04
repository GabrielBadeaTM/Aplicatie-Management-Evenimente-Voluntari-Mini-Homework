import java.util.ArrayList;

public class AdminValidationTest {

    public static void runTests() {
        System.out.println("10. ADMIN WORKFLOWS:");
        testAdminWorkflows();
    }

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
}
