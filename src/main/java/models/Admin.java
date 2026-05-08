package models;

import java.util.ArrayList;

/**
 * Represents an Administrator in the event management system.
 * 
 * Inherits from Person and provides system-wide management capabilities:
 * 1. Event Management: Create and cancel events
 * 2. Volunteer Management: Maintain a registry of available volunteers
 * 3. Coordinator Assignment: Assign volunteers as coordinators for events
 * 
 * Key Responsibilities:
 * - Create events with specific dates and registration windows
 * - Cancel events with cascade cleanup (removes all enrolled volunteers and coordinator roles)
 * - Maintain a master list of available volunteers
 * - Assign coordinators to events (coordinators then manage subordinate volunteers)
 * 
 * Important Notes:
 * - Admins do NOT directly enroll volunteers in events (that's the coordinator's job)
 * - When an admin is deleted, all their created events are also deleted (cascade delete)
 * - Events are unique by (name, dates) - duplicate events cannot be created
 */
public class Admin extends Person {

    private ArrayList<Event> createdEvents;
    private ArrayList<Volunteer> allVolunteers;

    // Default constructor
    public Admin() {
        super();
        this.createdEvents = new ArrayList<>();
        this.allVolunteers = new ArrayList<>();
    }

    // Constructor with params
    public Admin(String _firstName, String _lastName, String _email, String _phone) {
        super(_firstName, _lastName, _email, _phone);
        this.createdEvents = new ArrayList<>();
        this.allVolunteers = new ArrayList<>();
    }

    // =========================
    // EVENT MANAGEMENT
    // =========================

    /**
     * Creates a new event with the specified name and date information.
     * 
     * Validation:
     * - Event name must not already exist with same dates
     * 
     * @param name the event name (must be at least 3 characters)
     * @param eventDate the date object containing registration and event dates
     * @return the newly created Event object
     * @throws IllegalArgumentException if an event with the same name and dates already exists
     */
    // CREATE EVENT
    public Event createEvent(String name, EventDate eventDate) {
        // Check if event with same name and dates already exists
        for (Event existingEvent : createdEvents) {
            if (existingEvent.getName().equals(name) && 
                existingEvent.getEventDate().equals(eventDate)) {
                throw new IllegalArgumentException("Event with name '" + name + 
                    "' and dates " + eventDate + " already exists.");
            }
        }
        
        Event event = new Event(name, eventDate, this);
        createdEvents.add(event);
        return event;
    }

    /**
     * Cancels an event and performs cascade cleanup.
     * 
     * Cascade Operations (in order):
     * 1. Notifies all enrolled volunteers to cancel their applications
     * 2. Removes all coordinator roles and their subordinates from the event
     * 3. Removes the event from the admin's event list
     * 
     * Effect:
     * - Event is deleted from the system
     * - All volunteer-event associations are removed
     * - All coordinator-event associations are removed
     * 
     * @param event the event to cancel
     */
    // CANCEL EVENT - WITH CASCADE DELETE
    public void cancelEvent(Event event) {
        if (!createdEvents.contains(event)) {
            System.out.println("Event not found: " + event.getName());
            return;
        }
        
        // Notify all enrolled volunteers to remove this event
        ArrayList<Volunteer> enrolledVolunteers = new ArrayList<>(event.getEnrolledVolunteers());
        for (Volunteer volunteer : enrolledVolunteers) {
            volunteer.cancelApplication(event);
        }
        
        // Clean up all coordinator roles (and their subordinates)
        ArrayList<Coordinator> coordinators = new ArrayList<>(event.getCoordinatorRoles());
        for (Coordinator coord : coordinators) {
            // Remove all subordinates from the coordinator
            ArrayList<Volunteer> subordinates = new ArrayList<>(coord.getSubordinates());
            for (Volunteer sub : subordinates) {
                coord.removeSubordinate(sub);
            }
            // Remove the coordinator from the event
            event.removeCoordinator(coord.getCoordinator());
        }
        
        // Finally remove from admin list
        createdEvents.remove(event);
        System.out.println("Event cancelled: " + event.getName() + " (cascade cleanup completed)");
    }
    
    /**
     * Deletes this admin from the system.
     * 
     * Cascade Operations:
     * 1. Cancels all events created by this admin (which cascades cleanup)
     * 2. Clears the volunteer registry
     * 
     * Important:
     * - This is a destructive operation that removes all data associated with this admin
     * - All events, coordinators, and volunteer-event associations are deleted
     * 
     * Effect:
     * - Admin is removed from the system
     * - All their created events are deleted
     * - All volunteer registry entries are cleared
     */
    // DELETE ADMIN - WITH CASCADE DELETE (delete all events)
    public void deleteAdmin() {
        // Cancel all created events (which cascades cleanup to volunteers and coordinators)
        ArrayList<Event> eventsCopy = new ArrayList<>(createdEvents);
        for (Event event : eventsCopy) {
            cancelEvent(event);
        }
        
        // Clear volunteer registry
        allVolunteers.clear();
        
        System.out.println("Admin " + getFirstName() + " " + getLastName() + " deleted (all events cleaned up)");
    }

    // =========================
    // VOLUNTEER MANAGEMENT
    // =========================

    /**
     * Registers a volunteer as available in the system.
     * 
     * Important:
     * - This does NOT enroll the volunteer in any specific event
     * - This creates a system-wide registry of available volunteers
     * - A volunteer cannot be added to the registry twice (checked with contains)
     * 
     * @param _volunteer the volunteer to add to the registry
     */
    // Add volunteer to the list of available volunteers
    public void addVolunteer(Volunteer _volunteer) {
        if (!allVolunteers.contains(_volunteer)) {
            allVolunteers.add(_volunteer);
        }
    }

    // Get all available volunteers
    public ArrayList<Volunteer> getAllVolunteers() {
        return allVolunteers;
    }

    // =========================
    // COORDINATOR ASSIGNMENT
    // =========================

    /**
     * Assigns a volunteer as a coordinator for a specific event.
     * 
     * Important Notes:
     * - A volunteer can be a coordinator for multiple events
     * - An event can have multiple coordinators
     * - This creates a Coordinator role object that manages subordinates
     * 
     * @param event the event to assign a coordinator to
     * @param volunteer the volunteer to assign as coordinator
     * @return the Coordinator role object
     */
    // Select a volunteer as coordinator for a specific event
    public Coordinator assignCoordinator(Event event, Volunteer volunteer) {
        return event.assignCoordinator(volunteer);
    }

    // Select multiple volunteers as coordinators for an event
    public void assignCoordinators(Event event, ArrayList<Volunteer> volunteers) {
        for (Volunteer volunteer : volunteers) {
            event.assignCoordinator(volunteer);
        }
    }

    // Remove a coordinator from an event
    public void removeCoordinator(Event event, Volunteer volunteer) {
        event.removeCoordinator(volunteer);
    }

    // =========================
    // VOLUNTEER ENROLLMENT
    // =========================
    // Note: Volunteer enrollment is handled by Event Coordinators through Coordinator.acceptVolunteer()
    // Admins only manage coordinator assignments, not volunteer enrollments.

    public ArrayList<Event> getCreatedEvents() {
        return createdEvents;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "name=" + getFirstName() + " " + getLastName() +
                ", email=" + getEmail() +
                '}';
    }
}
