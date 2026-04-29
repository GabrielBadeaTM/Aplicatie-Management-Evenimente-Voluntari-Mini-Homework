import java.util.ArrayList;

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

    // CREATE EVENT
    public Event createEvent(String name, EventDate eventDate) {
        Event event = new Event(name, eventDate, this);
        createdEvents.add(event);
        return event;
    }

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
