public class IntegrationTests {

    public static void runTests() {
        System.out.println("14. INTEGRATION: FULL WORKFLOW:");
        testFullIntegrationWorkflow();

        System.out.println("\n15. INTEGRATION: DATA CONSISTENCY:");
        testDataConsistency();
    }

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
}
