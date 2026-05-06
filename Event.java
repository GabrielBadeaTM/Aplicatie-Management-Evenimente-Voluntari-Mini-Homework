import java.util.ArrayList;

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
    private boolean isValidEventName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() >= 3;
    }

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

    // Unregister a volunteer from this event (called from Volunteer when canceling)
    public void unregisterVolunteer(Volunteer _volunteer) {
        if (_volunteer == null) {
            throw new IllegalArgumentException("Volunteer cannot be null.");
        }
        allRegisteredVolunteers.removeIf(av -> av.getVolunteer().equals(_volunteer));
    }

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

    // Enroll a volunteer with specific availability
    public void enrollVolunteer(Volunteer _volunteer, SimpleDate _from, SimpleDate _to) {
        _volunteer.applyToEvent(this, _from, _to);
    }

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
