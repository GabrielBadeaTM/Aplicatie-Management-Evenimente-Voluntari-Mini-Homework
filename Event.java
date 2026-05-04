import java.util.ArrayList;

public class Event {

    private String name;
    private EventDate eventDate;

    private Admin admin;
    private ArrayList<Coordinator> coordinatorRoles;
    private ArrayList<EventVolunteerAvailability> volunteerAvailabilities;

    // Default constructor
    public Event() {
        this.name = "Default Event";
        this.eventDate = new EventDate();
        this.admin = new Admin();
        this.coordinatorRoles = new ArrayList<>();
        this.volunteerAvailabilities = new ArrayList<>();
    }

    // Constructor with parameters
    public Event(String _name,
                 EventDate _eventDate,
                 Admin _admin) {

        setName(_name);
        setEventDate(_eventDate);
        setAdmin(_admin);
        this.coordinatorRoles = new ArrayList<>();
        this.volunteerAvailabilities = new ArrayList<>();
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

    public ArrayList<EventVolunteerAvailability> getVolunteerAvailabilities() {
        return volunteerAvailabilities;
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
        EventVolunteerAvailability availability = new EventVolunteerAvailability(this, _volunteer, _from, _to);
        volunteerAvailabilities.add(availability);
        _volunteer.applyToEvent(this, _from, _to);
    }

    // Get all volunteers enrolled in this event
    public ArrayList<Volunteer> getEnrolledVolunteers() {
        ArrayList<Volunteer> volunteers = new ArrayList<>();
        for (EventVolunteerAvailability av : volunteerAvailabilities) {
            volunteers.add(av.getVolunteer());
        }
        return volunteers;
    }

    // Get availability for a specific volunteer
    public EventVolunteerAvailability getVolunteerAvailability(Volunteer _volunteer) {
        for (EventVolunteerAvailability av : volunteerAvailabilities) {
            if (av.getVolunteer().equals(_volunteer)) {
                return av;
            }
        }
        return null;
    }

    // Remove a volunteer from the event
    public void removeVolunteer(Volunteer _volunteer) {
        volunteerAvailabilities.removeIf(av -> av.getVolunteer().equals(_volunteer));
        // Also remove from volunteer's applied events to maintain bidirectional consistency
        if (_volunteer.getAppliedEvents().contains(this)) {
            _volunteer.getAppliedEvents().remove(this);
        }
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
        for (EventVolunteerAvailability av : volunteerAvailabilities) {
            System.out.println(av);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Event other = (Event) obj;
        return name != null && name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

        @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", eventDate=" + eventDate +
                ", admin=" + admin +
                ", coordinators=" + coordinatorRoles.size() +
                ", volunteers=" + volunteerAvailabilities.size() +
                '}';
    }
}
