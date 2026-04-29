/**
 * Represents a Volunteer's availability for a specific Event.
 * This allows volunteers to specify different availability windows for different events.
 */
public class EventVolunteerAvailability {

    private Event event;
    private Volunteer volunteer;
    private SimpleDate availableFrom;
    private SimpleDate availableTo;

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
    private boolean isDateNull(SimpleDate date) {
        return date == null;
    }

    private boolean isPersonNull(Object person) {
        return person == null;
    }

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
    public String toString() {
        return volunteer.getFirstName() + " " + volunteer.getLastName() +
               " available from " + availableFrom + " to " + availableTo;
    }
}
