import java.util.ArrayList;

/**
 * Represents a Volunteer assigned as a Coordinator for a specific Event.
 * 
 * Structure:
 * - event: the event this coordination role is for
 * - coordinator: the volunteer who is acting as a coordinator
 * - subordinates: list of volunteers managed by this coordinator for this event
 * 
 * Key Characteristics:
 * - A volunteer can be a coordinator for multiple events
 * - Each event can have multiple coordinators
 * - A coordinator can manage multiple subordinate volunteers for their event
 * - A coordinator cannot be a subordinate to themselves
 * - Subordinates must have applied to the event before being accepted by a coordinator
 * 
 * Coordinator Responsibilities:
 * - Accept volunteers as subordinates by calling acceptVolunteer()
 * - Manage the availability of their subordinates
 * - Ensure subordinates don't report to multiple coordinators in the same event
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
    
    /**
     * Checks if an object is null.
     * 
     * @param obj the object to check
     * @return true if the object is null, false otherwise
     */
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

    // ===== SUBORDINATE MANAGEMENT =====
    
    /**
     * Adds a subordinate volunteer to this coordinator's team for this event.
     * Duplicate subordinates are not added.
     * 
     * @param _volunteer the volunteer to add as subordinate
     */
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

    /**
     * Accepts a volunteer as a subordinate for this coordinator's event.
     * 
     * Validations:
     * - The coordinator cannot be their own subordinate
     * - The volunteer must have already applied to this event
     * - The volunteer cannot be under another coordinator for the same event
     * 
     * Effect:
     * - Adds the volunteer to the coordinator's subordinates list
     * - Updates the volunteer's availability for this specific event
     * 
     * @param _volunteer the volunteer to accept as subordinate
     * @param _from the availability start date for this event
     * @param _to the availability end date for this event
     * @throws IllegalArgumentException if any validation fails
     */
    // Accept a volunteer for this event
    public void acceptVolunteer(Volunteer _volunteer, SimpleDate _from, SimpleDate _to) {
        // Validation: coordinator cannot be their own subordinate
        if (_volunteer.equals(coordinator)) {
            throw new IllegalArgumentException("Coordinator cannot be their own subordinate.");
        }
        
        // Validation: volunteer cannot be a coordinator for this event
        for (Coordinator coordRole : event.getCoordinatorRoles()) {
            if (coordRole.getCoordinator().equals(_volunteer)) {
                throw new IllegalArgumentException("Volunteer cannot be a subordinate if they are already a coordinator for this event.");
            }
        }
        
        // Validation: volunteer must have already applied to this event
        if (!event.hasVolunteerApplied(_volunteer)) {
            throw new IllegalArgumentException("Volunteer must have applied to the event before accepting as subordinate.");
        }
        
        // Validation: volunteer cannot be under two different coordinators in the same event
        for (Coordinator existingCoordRole : event.getCoordinatorRoles()) {
            if (!existingCoordRole.getCoordinator().equals(this.coordinator) && 
                existingCoordRole.hasSubordinate(_volunteer)) {
                throw new IllegalArgumentException("Volunteer is already under another coordinator for this event.");
            }
        }
        
        addSubordinate(_volunteer);
        
        // Update the volunteer's availability if they've already applied
        // Otherwise, the volunteer is already properly enrolled via their prior application
        EventVolunteerAvailability existing = _volunteer.getEventAvailability(event);
        if (existing != null) {
            existing.setAvailableFrom(_from);
            existing.setAvailableTo(_to);
        }
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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Coordinator other = (Coordinator) obj;
        return event != null && event.equals(other.event) && coordinator != null && coordinator.equals(other.coordinator);
    }

    @Override
    public int hashCode() {
        int result = event != null ? event.hashCode() : 0;
        result = 31 * result + (coordinator != null ? coordinator.hashCode() : 0);
        return result;
    }

        @Override
    public String toString() {
        return coordinator.getFirstName() + " " + coordinator.getLastName() +
               " - Coordinator for " + event.getName() +
               " (Subordinates: " + subordinates.size() + ")";
    }
}
