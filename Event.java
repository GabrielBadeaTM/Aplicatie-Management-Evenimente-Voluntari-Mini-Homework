import java.util.ArrayList;

/**
 * Represents an Event in the system managed by an Admin.
 * 
 * An Event has:
 * - name: unique identifier (with dates) created by an admin
 * - eventDate: contains registration period and event period
 * - admin: the admin who created this event
 * - coordinatorRoles: volunteers assigned as coordinators for this event
 * - allRegisteredVolunteers: all volunteers who have applied to this event
 * 
 * Key Relationships:
 * 1. Coordinators: Volunteers assigned to manage/coordinate the event
 *    - A coordinator can have subordinate volunteers
 *    - An event can have multiple coordinators
 * 2. Volunteers: Volunteers who applied to the event
 *    - Each volunteer specifies their availability for this event
 *    - Can be directly enrolled or accepted by a coordinator as subordinate
 * 
 * Validation Rules:
 * - Event name must be at least 3 characters
 * - Event cannot have null dates or admin
 * - Volunteers can only apply during the registration window
 * - A volunteer cannot apply twice to the same event
 * - A volunteer cannot be under two different coordinators in the same event
 */
public class Event {

    private String name;
    private EventDate eventDate;

    private Admin admin;
    private ArrayList<Coordinator> coordinatorRoles; // Volunteeri acceptati ca subordinati
    private ArrayList<EventVolunteerAvailability> allRegisteredVolunteers; // Toti volunteeri inscrisi (acceptati sau nu)

    // Default constructor
    public Event() {
        this.name = "Default Event";
        this.eventDate = new EventDate();
        this.admin = new Admin();
        this.coordinatorRoles = new ArrayList<>();
        this.allRegisteredVolunteers = new ArrayList<>();
    }

    // Constructor with parameters
    public Event(String _name,
                 EventDate _eventDate,
                 Admin _admin) {

        setName(_name);
        setEventDate(_eventDate);
        setAdmin(_admin);
        this.coordinatorRoles = new ArrayList<>();
        this.allRegisteredVolunteers = new ArrayList<>();
    }

    // ========== VALIDATION METHODS ==========
    
    /**
     * Validates an event name.
     * A valid event name must not be null, not empty, and be at least 3 characters long.
     * 
     * @param name the event name to validate
     * @return true if the name is valid, false otherwise
     */
    private boolean isValidEventName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() >= 3;
    }

    // ===== GETTERS FOR EVENT INFORMATION =====
    
    /**
     * Gets the event name.
     * @return the name of the event
     */
    // Getters
    public String getName() {
        return name;
    }

    public EventDate getEventDate() {
        return eventDate;
    }

    public Admin getAdmin() {
        return admin;
    }

    public ArrayList<Coordinator> getCoordinatorRoles() {
        return coordinatorRoles;
    }

    // Getter for all registered volunteers
    public ArrayList<EventVolunteerAvailability> getAllRegisteredVolunteers() {
        return allRegisteredVolunteers;
    }

    // Helper method to get all volunteer availabilities for this event
    // Uses the local cached list for better performance
    public ArrayList<EventVolunteerAvailability> getVolunteerAvailabilities() {
        return new ArrayList<>(allRegisteredVolunteers);
    }

    // Helper method to check if a volunteer has applied to this event
    public boolean hasVolunteerApplied(Volunteer _volunteer) {
        for (EventVolunteerAvailability av : allRegisteredVolunteers) {
            if (av.getVolunteer().equals(_volunteer)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Registers a volunteer's availability for this event.
     * Called from Volunteer.applyToEvent() when the volunteer applies.
     * 
     * Validations:
     * - Availability cannot be null
     * - Availability must belong to this event
     * - Availability is not added if it already exists
     * 
     * @param _availability the EventVolunteerAvailability to register
     * @throws IllegalArgumentException if validations fail
     */
    // Register a volunteer for this event (called from Volunteer when applying)
    public void registerVolunteer(EventVolunteerAvailability _availability) {
        if (_availability == null) {
            throw new IllegalArgumentException("EventVolunteerAvailability cannot be null.");
        }
        if (_availability.getEvent() == null || !_availability.getEvent().equals(this)) {
            throw new IllegalArgumentException("EventVolunteerAvailability must belong to this event.");
        }
        if (!allRegisteredVolunteers.contains(_availability)) {
            allRegisteredVolunteers.add(_availability);
        }
    }

    /**
     * Unregisters a volunteer from this event.
     * Called from Volunteer.cancelApplication() when the volunteer cancels.
     * 
     * Effect:
     * - Removes all EventVolunteerAvailability records for this volunteer
     * 
     * @param _volunteer the volunteer to unregister
     * @throws IllegalArgumentException if volunteer is null
     */
    // Unregister a volunteer from this event (called from Volunteer when canceling)
    public void unregisterVolunteer(Volunteer _volunteer) {
        if (_volunteer == null) {
            throw new IllegalArgumentException("Volunteer cannot be null.");
        }
        allRegisteredVolunteers.removeIf(av -> av.getVolunteer().equals(_volunteer));
    }

    // ===== SETTERS WITH VALIDATION =====
    
    /**
     * Sets the event name with validation.
     * 
     * @param _name the new event name (must be at least 3 characters)
     * @throws IllegalArgumentException if the name is invalid
     */
    // Setters
    public void setName(String _name) {
        if (!isValidEventName(_name)) {
            throw new IllegalArgumentException("Invalid event name: '" + _name + "'. Event name must be at least 3 characters long and not empty.");
        }
        this.name = _name;
    }

    public void setEventDate(EventDate _eventDate) {
        if (_eventDate == null) {
            throw new IllegalArgumentException("Event date cannot be null.");
        }
        this.eventDate = _eventDate;
    }

    public void setAdmin(Admin _admin) {
        if (_admin == null) {
            throw new IllegalArgumentException("Admin cannot be null.");
        }
        this.admin = _admin;
    }

    // =========================
    // COORDINATOR MANAGEMENT
    // =========================

    /**
     * Assigns a volunteer as a coordinator for this event.
     * If the volunteer is already a coordinator, returns the existing role.
     * 
     * @param _volunteer the volunteer to assign as coordinator
     * @return the Coordinator role object for this event
     * @throws IllegalArgumentException if volunteer is null
     */
    // Assign a volunteer as a coordinator for this event
    public Coordinator assignCoordinator(Volunteer _volunteer) {
        // Check if volunteer is already a coordinator for this event
        if (isCoordinator(_volunteer)) {
            return getCoordinatorRole(_volunteer);
        }
        Coordinator role = new Coordinator(this, _volunteer);
        coordinatorRoles.add(role);
        return role;
    }

    // Remove a coordinator from this event
    // Remove a coordinator from this event - with cascade cleanup of subordinates
    public void removeCoordinatorWithCleanup(Volunteer _volunteer) {
        Coordinator coordinatorRole = getCoordinatorRole(_volunteer);
        if (coordinatorRole != null) {
            // Remove all subordinates first
            ArrayList<Volunteer> subordinates = new ArrayList<>(coordinatorRole.getSubordinates());
            for (Volunteer sub : subordinates) {
                coordinatorRole.removeSubordinate(sub);
                // Also remove subordinate from the event's volunteer list
                removeVolunteer(sub);
            }
        }
        coordinatorRoles.removeIf(role -> role.getCoordinator().equals(_volunteer));
    }
    
    // Remove a coordinator from this event
    public void removeCoordinator(Volunteer _volunteer) {
        // Use the proper cascade cleanup method
        removeCoordinatorWithCleanup(_volunteer);
    }

    // Get coordinator role for a specific volunteer
    public Coordinator getCoordinatorRole(Volunteer _volunteer) {
        for (Coordinator role : coordinatorRoles) {
            if (role.getCoordinator().equals(_volunteer)) {
                return role;
            }
        }
        return null;
    }

    // Check if a volunteer is a coordinator for this event
    public boolean isCoordinator(Volunteer _volunteer) {
        return getCoordinatorRole(_volunteer) != null;
    }

    // =========================
    // VOLUNTEER MANAGEMENT
    // =========================

    /**
     * Enrolls a volunteer for this event with specified availability.
     * This is a convenience method that delegates to the volunteer.
     * 
     * @param _volunteer the volunteer to enroll
     * @param _from the start date of the volunteer's availability
     * @param _to the end date of the volunteer's availability
     */
    // Enroll a volunteer with specific availability
    public void enrollVolunteer(Volunteer _volunteer, SimpleDate _from, SimpleDate _to) {
        _volunteer.applyToEvent(this, _from, _to);
    }

    /**
     * Gets all unique volunteers enrolled in this event.
     * 
     * Important:
     * - This returns only the unique volunteer objects, not the availability records
     * - A volunteer appears only once in this list even if they have multiple availability periods
     * 
     * @return list of all volunteers who have applied to this event
     */
    // Get all volunteers enrolled in this event
    public ArrayList<Volunteer> getEnrolledVolunteers() {
        ArrayList<Volunteer> volunteers = new ArrayList<>();
        ArrayList<EventVolunteerAvailability> availabilities = getVolunteerAvailabilities();
        for (EventVolunteerAvailability av : availabilities) {
            if (!volunteers.contains(av.getVolunteer())) {
                volunteers.add(av.getVolunteer());
            }
        }
        return volunteers;
    }

    // Get availability for a specific volunteer
    public EventVolunteerAvailability getVolunteerAvailability(Volunteer _volunteer) {
        for (EventVolunteerAvailability av : allRegisteredVolunteers) {
            if (av.getVolunteer().equals(_volunteer)) {
                return av;
            }
        }
        return null;
    }

    // Remove a volunteer from the event
    public void removeVolunteer(Volunteer _volunteer) {
        _volunteer.cancelApplication(this);
    }

    // Display
    public void display() {
        System.out.println("Event Name: " + name);

        System.out.println("\nEvent Dates:");
        eventDate.display();

        System.out.println("\nAdmin:");
        System.out.println(admin);

        System.out.println("\nCoordinator Roles:");
        for (Coordinator role : coordinatorRoles) {
            System.out.println(role);
        }

        System.out.println("\nEnrolled Volunteers:");
        ArrayList<EventVolunteerAvailability> availabilities = getVolunteerAvailabilities();
        for (EventVolunteerAvailability av : availabilities) {
            System.out.println(av);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Event other = (Event) obj;
        // Two events are equal if they have the same name, admin, and date
        // This prevents false positives when two events have the same name but different details
        return name != null && name.equals(other.name) &&
               admin != null && admin.equals(other.admin) &&
               eventDate != null && eventDate.equals(other.eventDate);
    }

    @Override
    public int hashCode() {
        // hashCode must be consistent with equals()
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (admin != null ? admin.hashCode() : 0);
        result = 31 * result + (eventDate != null ? eventDate.hashCode() : 0);
        return result;
    }

        @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", eventDate=" + eventDate +
                ", admin=" + admin +
                ", coordinators=" + coordinatorRoles.size() +
                ", volunteers=" + getVolunteerAvailabilities().size() +
                '}';
    }
}
