import models.*;


/**
 * Main class - Demonstrates the complete Volunteer Event Management System.
 * 
 * This program showcases:
 * 1. System initialization with an Admin
 * 2. Event creation with registration and event date windows
 * 3. Volunteer registration in the system
 * 4. Coordinator assignment to events
 * 5. Volunteer application to events with per-event availability
 * 6. Coordinator acceptance of volunteers as subordinates
 * 7. Display of the complete system state
 * 
 * Key Demonstration:
 * - Volunteers can apply to multiple events with DIFFERENT availability for each
 * - Coordinators manage subordinates on a per-event basis
 * - Cascade relationships: Admin → Events → Coordinators → Subordinates
 */
public class Main {

    /**
     * Entry point of the application.
     * 
     * Flow:
     * 1. Create admin and event dates
     * 2. Create volunteers and register them with the admin
     * 3. Create events
     * 4. Assign coordinators to events
     * 5. Have volunteers apply to events with specific availability
     * 6. Have coordinators accept volunteers as subordinates
     * 7. Display final system state
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("  VOLUNTEER MANAGEMENT SYSTEM - NEW VERSION");
        System.out.println("========================================\n");

        // =========================
        // 1. CREATE ADMIN
        // =========================
        Admin admin = new Admin("Ion", "Popescu", "ion@mail.com", "0711111111");
        System.out.println("✓ Admin created: " + admin);

        // =========================
        // 2. CREATE DATES FOR EVENTS
        // =========================
        SimpleDate event1Start = new SimpleDate(2026, 2, 25, 10, 0);
        SimpleDate event1End = new SimpleDate(2026, 2, 28, 18, 0);
        SimpleDate event1RegStart = new SimpleDate(2026, 2, 1, 0, 0);
        SimpleDate event1RegEnd = new SimpleDate(2026, 2, 20, 23, 59);

        EventDate eventDate1 = new EventDate(event1Start, event1End, event1RegStart, event1RegEnd);

        SimpleDate event2Start = new SimpleDate(2026, 3, 5, 10, 0);
        SimpleDate event2End = new SimpleDate(2026, 3, 8, 18, 0);
        SimpleDate event2RegStart = new SimpleDate(2026, 2, 15, 0, 0);
        SimpleDate event2RegEnd = new SimpleDate(2026, 3, 1, 23, 59);

        EventDate eventDate2 = new EventDate(event2Start, event2End, event2RegStart, event2RegEnd);

        // =========================
        // 3. CREATE VOLUNTEERS
        // =========================
        Volunteer vol1 = new Volunteer("Maria", "Georgescu", "maria@mail.com", "0733333333", 5, TShirtSize.M);
        Volunteer vol2 = new Volunteer("Ana", "Ionescu", "ana@mail.com", "0722222222", 3, TShirtSize.S);
        Volunteer vol3 = new Volunteer("Elena", "Popescu", "elena@mail.com", "0744444444", 2, TShirtSize.L);
        Volunteer vol4 = new Volunteer("Cristian", "Vasile", "cristian@mail.com", "0755555555", 1, TShirtSize.M);

        admin.addVolunteer(vol1);
        admin.addVolunteer(vol2);
        admin.addVolunteer(vol3);
        admin.addVolunteer(vol4);

        System.out.println("\n✓ Created 4 volunteers:");
        System.out.println("  1. " + vol1.getFirstName() + " (" + vol1.getYearsOfExperience() + " years exp)");
        System.out.println("  2. " + vol2.getFirstName() + " (" + vol2.getYearsOfExperience() + " years exp)");
        System.out.println("  3. " + vol3.getFirstName() + " (" + vol3.getYearsOfExperience() + " years exp)");
        System.out.println("  4. " + vol4.getFirstName() + " (" + vol4.getYearsOfExperience() + " years exp)");

        // =========================
        // 4. ADMIN CREATES EVENTS
        // =========================
        Event event1 = admin.createEvent("Beach Cleanup - Feb", eventDate1);
        Event event2 = admin.createEvent("Forest Planting - Mar", eventDate2);

        System.out.println("\n✓ Admin created 2 events:");
        System.out.println("  - Event 1: " + event1.getName() + " (Feb 25-28)");
        System.out.println("  - Event 2: " + event2.getName() + " (Mar 5-8)");

        // =========================
        // 5. ADMIN SELECTS COORDINATORS FROM VOLUNTEERS
        //    (Based on experience)
        // =========================
        System.out.println("\n========== REQUIREMENT 1: Coordinators per Event ==========");
        System.out.println("\nAdmin selects coordinators from volunteers based on experience:");

        // For Event 1: Select Maria (5 years) and Ana (3 years) as coordinators
        Coordinator role1_1 = admin.assignCoordinator(event1, vol1);
        admin.assignCoordinator(event1, vol2);

        System.out.println("  Event 1 Coordinators: " + vol1.getFirstName() + ", " + vol2.getFirstName());

        // For Event 2: Select Ana (3 years) as coordinator (can be coordinator for multiple events)
        Coordinator role2_1 = admin.assignCoordinator(event2, vol2);

        System.out.println("  Event 2 Coordinator: " + vol2.getFirstName());
        System.out.println("\n✓ Ana is now coordinator for BOTH events!");

        // =========================
        // 6. COORDINATORS ASSIGN SUBORDINATES
        // =========================
        System.out.println("\n========== REQUIREMENT 2: Coordinators Assign Subordinates ==========");

        // Assignments now via acceptVolunteer() in REQUIREMENT 3

        System.out.println("\nMaria (Coordinator for Event 1) assigns subordinates:");
        System.out.println("  - " + vol3.getFirstName() + " (2 years exp)");
        System.out.println("  - " + vol4.getFirstName() + " (1 year exp)");

        // Assignment via acceptVolunteer() in REQUIREMENT 3

        System.out.println("\nAna (Coordinator for Event 2) assigns subordinate:");
        System.out.println("  - " + vol3.getFirstName() + " (2 years exp)");

        // =========================
        // 7. COORDINATORS ACCEPT VOLUNTEERS WITH PER-EVENT AVAILABILITY
        // =========================
        System.out.println("\n========== REQUIREMENT 3: Per-Event Availability & Coordinator Acceptance ==========");

        // Event 1 availabilities
        SimpleDate vol3_event1_from = new SimpleDate(2026, 2, 26, 10, 0);
        SimpleDate vol3_event1_to = new SimpleDate(2026, 2, 28, 18, 0);

        SimpleDate vol4_event1_from = new SimpleDate(2026, 2, 25, 10, 0);
        SimpleDate vol4_event1_to = new SimpleDate(2026, 2, 27, 18, 0);

        // Event 2 availabilities (different dates for the same volunteers)
        SimpleDate vol3_event2_from = new SimpleDate(2026, 3, 6, 10, 0);
        SimpleDate vol3_event2_to = new SimpleDate(2026, 3, 8, 18, 0);

        // First, volunteers must apply for events before coordinators can accept them
        System.out.println("\nVolunteers apply for events:");
        vol3.applyToEvent(event1, vol3_event1_from, vol3_event1_to);
        System.out.println("  - " + vol3.getFirstName() + " applied to Event 1");
        
        vol4.applyToEvent(event1, vol4_event1_from, vol4_event1_to);
        System.out.println("  - " + vol4.getFirstName() + " applied to Event 1");
        
        vol3.applyToEvent(event2, vol3_event2_from, vol3_event2_to);
        System.out.println("  - " + vol3.getFirstName() + " applied to Event 2");

        // Coordinators accept subordinates for Event 1
        // Note: Coordinators (vol1, vol2) are NOT enrolled as volunteers - they only coordinate
        role1_1.acceptVolunteer(vol3, vol3_event1_from, vol3_event1_to);
        role1_1.acceptVolunteer(vol4, vol4_event1_from, vol4_event1_to);

        // Coordinator accepts subordinates for Event 2
        // Note: Ana (vol2) is coordinator for Event 2, not enrolled as volunteer
        role2_1.acceptVolunteer(vol3, vol3_event2_from, vol3_event2_to);

        System.out.println("\nVolunteers enrolled with DIFFERENT availability per event:\n");

        System.out.println("Event 1 (" + event1.getName() + ") - Feb 25-28:");
        for (EventVolunteerAvailability av : event1.getVolunteerAvailabilities()) {
            System.out.print("  - " + av.getVolunteer().getFirstName() + ": ");
            av.getAvailableFrom().displayInline();
            System.out.print(" to ");
            av.getAvailableTo().displayInline();
            System.out.println();
        }

        System.out.println("\nEvent 2 (" + event2.getName() + ") - Mar 5-8:");
        for (EventVolunteerAvailability av : event2.getVolunteerAvailabilities()) {
            System.out.print("  - " + av.getVolunteer().getFirstName() + ": ");
            av.getAvailableFrom().displayInline();
            System.out.print(" to ");
            av.getAvailableTo().displayInline();
            System.out.println();
        }

        // =========================
        // 8. DISPLAY FINAL SYSTEM STATE
        // =========================
        System.out.println("\n========== FINAL SYSTEM STATE ==========\n");

        System.out.println("===== EVENT 1: " + event1.getName() + " =====");
        event1.display();

        System.out.println("\n===== EVENT 2: " + event2.getName() + " =====");
        event2.display();

        // Show coordinator details
        System.out.println("\n===== COORDINATOR ROLES =====");
        System.out.println("\nEvent 1 Coordinators and their subordinates:");
        for (Coordinator role : event1.getCoordinatorRoles()) {
            System.out.println("\nCoordinator: " + role.getCoordinator().getFirstName() + " " + role.getCoordinator().getLastName());
            System.out.println("Subordinates for " + event1.getName() + ":");
            for (Volunteer subordinate : role.getSubordinates()) {
                System.out.println("  - " + subordinate.getFirstName() + " " + subordinate.getLastName() + " (" + subordinate.getYearsOfExperience() + " years)");
            }
        }

        System.out.println("\nEvent 2 Coordinators and their subordinates:");
        for (Coordinator role : event2.getCoordinatorRoles()) {
            System.out.println("\nCoordinator: " + role.getCoordinator().getFirstName() + " " + role.getCoordinator().getLastName());
            System.out.println("Subordinates for " + event2.getName() + ":");
            for (Volunteer subordinate : role.getSubordinates()) {
                System.out.println("  - " + subordinate.getFirstName() + " " + subordinate.getLastName() + " (" + subordinate.getYearsOfExperience() + " years)");
            }
        }
    }
}
