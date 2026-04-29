import java.util.ArrayList;

/**
 * Represents a Volunteer assigned as a Coordinator for a specific Event.
 * A Volunteer can be a Coordinator for multiple events, and each event can have multiple Coordinators.
 * A Coordinator can have subordinate Volunteers for a specific event.
 */
public class Coordinator {

    private Event event;
    private Volunteer coordinator;
    private ArrayList<Volunteer> subordinates;

    // Constructor
    public Coordinator(Event _event, Volunteer _coordinator) {
        setEvent(_event);
        setCoordinator(_coordinator);
        this.subordinates = new ArrayList<>();
    }

    // ========== VALIDATION METHODS ==========
    private boolean isObjectNull(Object obj) {
        return obj == null;
    }

    // Getters
    public Event getEvent() {
        return event;
    }

    public Volunteer getCoordinator() {
        return coordinator;
    }

    public ArrayList<Volunteer> getSubordinates() {
        return subordinates;
    }

    // Setters
    public void setEvent(Event _event) {
        if (isObjectNull(_event)) {
            throw new IllegalArgumentException("Event cannot be null.");
        }
        this.event = _event;
    }

    public void setCoordinator(Volunteer _coordinator) {
        if (isObjectNull(_coordinator)) {
            throw new IllegalArgumentException("Coordinator cannot be null.");
        }
        this.coordinator = _coordinator;
    }

    // Add subordinate
    public void addSubordinate(Volunteer _volunteer) {
        if (!subordinates.contains(_volunteer)) {
            subordinates.add(_volunteer);
        }
    }

    // Remove subordinate
    public void removeSubordinate(Volunteer _volunteer) {
        subordinates.remove(_volunteer);
    }

    // Check if a volunteer is a subordinate
    public boolean hasSubordinate(Volunteer _volunteer) {
        return subordinates.contains(_volunteer);
    }

    // Accept a volunteer for this event
    public void acceptVolunteer(Volunteer _volunteer, SimpleDate _from, SimpleDate _to) {
        // Validation: coordinator cannot be their own subordinate
        if (_volunteer.equals(coordinator)) {
            throw new IllegalArgumentException("Coordinator cannot be their own subordinate.");
        }
        addSubordinate(_volunteer);
        event.enrollVolunteer(_volunteer, _from, _to);
    }

    // Display
    public void display() {
        System.out.println("Coordinator: " + coordinator.getFirstName() + " " + coordinator.getLastName());
        System.out.println("Event: " + event.getName());
        System.out.println("Subordinates: " + subordinates.size());
        for (Volunteer v : subordinates) {
            System.out.println("  - " + v.getFirstName() + " " + v.getLastName());
        }
    }

    @Override
    public String toString() {
        return coordinator.getFirstName() + " " + coordinator.getLastName() +
               " - Coordinator for " + event.getName() +
               " (Subordinates: " + subordinates.size() + ")";
    }
}
