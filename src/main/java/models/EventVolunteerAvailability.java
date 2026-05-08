package models;


/**
 * Represents a Volunteer's availability for a specific Event.
 * 
 * Key Features:
 * - Allows volunteers to specify different availability windows for different events
 * - A single volunteer can have different availability periods for multiple events
 * - Stores the event, volunteer, and the time period they are available (availableFrom to availableTo)
 * 
 * Uniqueness:
 * - Each Event-Volunteer pair can have only one availability record
 * - Two availabilities are considered equal if they reference the same event and volunteer
 * 
 * Validation:
 * - availableFrom must be before availableTo
 * - Both dates must be within the event's date range (this is validated when volunteer applies)
 */
public class EventVolunteerAvailability {

    private Event event;
    private Volunteer volunteer; // asta dispare pentru ca e deja inclus in voluntar
    private SimpleDate availableFrom;
    private SimpleDate availableTo;

    /**
     * Constructor for EventVolunteerAvailability.
     * 
     * Creates a record of a volunteer's availability for a specific event.
     * 
     * @param _event the event the volunteer is available for
     * @param _volunteer the volunteer
     * @param _availableFrom the start date of availability (must be before _availableTo)
     * @param _availableTo the end date of availability
     * @throws IllegalArgumentException if any validation fails
     */
    // Constructor
    public EventVolunteerAvailability(Event _event, Volunteer _volunteer, 
                                     SimpleDate _availableFrom, SimpleDate _availableTo) {
        setEvent(_event);
        setVolunteer(_volunteer);
        setAvailableFrom(_availableFrom);
        setAvailableTo(_availableTo);
        validateAvailability();
    }

    // ========== VALIDATION METHODS ==========
    
    /**
     * Checks if a date is null.
     * 
     * @param date the date to check
     * @return true if the date is null, false otherwise
     */
    private boolean isDateNull(SimpleDate date) {
        return date == null;
    }

    private boolean isPersonNull(Object person) {
        return person == null;
    }

    /**
     * Compares two SimpleDate objects lexicographically.
     * 
     * @param date1 the first date
     * @param date2 the second date
     * @return negative if date1 < date2, zero if equal, positive if date1 > date2
     */
    private int compareDates(SimpleDate date1, SimpleDate date2) {
        if (date1.getYear() != date2.getYear()) {
            return date1.getYear() - date2.getYear();
        }
        if (date1.getMonth() != date2.getMonth()) {
            return date1.getMonth() - date2.getMonth();
        }
        if (date1.getDay() != date2.getDay()) {
            return date1.getDay() - date2.getDay();
        }
        return date1.getHour() * 60 + date1.getMinute() - (date2.getHour() * 60 + date2.getMinute());
    }

    /**
     * Validates that the volunteer's availability period is valid.
     * 
     * Ensures:
     * - availableFrom must be strictly before availableTo
     * 
     * Note: Validation that availability falls within event dates happens in Volunteer.applyToEvent()
     * 
     * @throws IllegalArgumentException if availableFrom >= availableTo
     */
    private void validateAvailability() {
        // availableFrom must be before availableTo
        if (compareDates(availableFrom, availableTo) >= 0) {
            throw new IllegalArgumentException("Available from date must be before available to date.");
        }
    }

    // Getters
    public Event getEvent() {
        return event;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public SimpleDate getAvailableFrom() {
        return availableFrom;
    }

    public SimpleDate getAvailableTo() {
        return availableTo;
    }

    // Setters
    public void setEvent(Event _event) {
        if (isPersonNull(_event)) {
            throw new IllegalArgumentException("Event cannot be null.");
        }
        this.event = _event;
    }

    public void setVolunteer(Volunteer _volunteer) {
        if (isPersonNull(_volunteer)) {
            throw new IllegalArgumentException("Volunteer cannot be null.");
        }
        this.volunteer = _volunteer;
    }

    public void setAvailableFrom(SimpleDate _availableFrom) {
        if (isDateNull(_availableFrom)) {
            throw new IllegalArgumentException("Available from date cannot be null.");
        }
        this.availableFrom = _availableFrom;
    }

    public void setAvailableTo(SimpleDate _availableTo) {
        if (isDateNull(_availableTo)) {
            throw new IllegalArgumentException("Available to date cannot be null.");
        }
        this.availableTo = _availableTo;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EventVolunteerAvailability other = (EventVolunteerAvailability) obj;
        return event != null && event.equals(other.event) && volunteer != null && volunteer.equals(other.volunteer);
    }

    @Override
    public int hashCode() {
        int result = event != null ? event.hashCode() : 0;
        result = 31 * result + (volunteer != null ? volunteer.hashCode() : 0);
        return result;
    }

        @Override
    public String toString() {
        return volunteer.getFirstName() + " " + volunteer.getLastName() +
               " available from " + availableFrom + " to " + availableTo;
    }
}
