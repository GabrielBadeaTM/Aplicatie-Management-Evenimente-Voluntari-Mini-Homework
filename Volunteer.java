import java.util.ArrayList;

public class Volunteer extends Person {

    private int yearsOfExperience;
    private TShirtSize tShirtSize;

    private ArrayList<EventVolunteerAvailability> eventAvailabilities; // la ce poate el sa participe. gen unde s a inscris

    // Default constructor
    public Volunteer() {
        super();
        this.yearsOfExperience = 0;
        this.tShirtSize = TShirtSize.M;
        this.eventAvailabilities = new ArrayList<>();
    }

    // Constructor with parameters
    public Volunteer(String _firstName, String _lastName, String _email, String _phone,
                      int _yearsOfExperience, TShirtSize _tShirtSize) {

        super(_firstName, _lastName, _email, _phone);

        setYearsOfExperience(_yearsOfExperience);
        setTShirtSize(_tShirtSize);

        this.eventAvailabilities = new ArrayList<>();
    }

    // ========== VALIDATION METHODS ==========
    private boolean isValidYearsOfExperience(int years) {
        return years >= 0 && years <= 100;
    }

    private boolean isValidTShirtSize(TShirtSize size) {
        return size != null;
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

    // =========================
    // APPLY METHODS
    // =========================

    // Apply to event with specific availability dates (internal method with current time validation)
    public void applyToEvent(Event _event, SimpleDate _availableFrom, SimpleDate _availableTo, SimpleDate _currentTime) {
        // Validate that volunteer is not already applied to this event
        if (getEventAvailability(_event) != null) {
            throw new IllegalArgumentException("Volunteer has already applied to this event.");
        }
        
        // Validate that application is within registration window
        SimpleDate regStart = _event.getEventDate().getRegistrationStart();
        SimpleDate regEnd = _event.getEventDate().getRegistrationEnd();
        
        if (compareDates(_currentTime, regStart) < 0 || compareDates(_currentTime, regEnd) > 0) {
            throw new IllegalArgumentException("Cannot apply: registration window is closed. Registration period: " + 
                regStart + " to " + regEnd);
        }
        
        // Validate that availability dates are within event date bounds
        SimpleDate eventStart = _event.getEventDate().getStartDate();
        SimpleDate eventEnd = _event.getEventDate().getEndDate();
        
        if (compareDates(_availableFrom, eventStart) < 0 || compareDates(_availableTo, eventEnd) > 0) {
            throw new IllegalArgumentException("Volunteer availability dates must be within the event date range.");
        }
        
        EventVolunteerAvailability availability = new EventVolunteerAvailability(_event, this, _availableFrom, _availableTo);
        eventAvailabilities.add(availability);
        // Register this volunteer in the event's volunteer list
        _event.registerVolunteer(availability);
    }

    // Apply to event (overload for backward compatibility - assumes current time is during registration)
    public void applyToEvent(Event _event, SimpleDate _availableFrom, SimpleDate _availableTo) {
        SimpleDate regStart = _event.getEventDate().getRegistrationStart();
        applyToEvent(_event, _availableFrom, _availableTo, regStart);
    }

    public void applyToEvents(ArrayList<Event> _events, SimpleDate _availableFrom, SimpleDate _availableTo) {
        for (Event event : _events) {
            applyToEvent(event, _availableFrom, _availableTo);
        }
    }

    // =========================
    // CANCEL METHODS
    // =========================

    public void cancelApplication(Event _event) {
        eventAvailabilities.removeIf(av -> av.getEvent().equals(_event));
        // Unregister this volunteer from the event's volunteer list
        _event.unregisterVolunteer(this);}

    public void cancelAllApplications() {
        eventAvailabilities.clear();
    }

    // =========================
    // UPDATE METHODS
    // =========================

    public void updateEventAvailability(Event _event, SimpleDate _from, SimpleDate _to) {
        for (EventVolunteerAvailability av : eventAvailabilities) {
            if (av.getEvent().equals(_event)) {
                av.setAvailableFrom(_from);
                av.setAvailableTo(_to);
                return;
            }
        }
    }

    public void changeTShirtSize(TShirtSize _size) {
        this.tShirtSize = _size;
    }

    // =========================
    // GETTERS
    // =========================

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public TShirtSize getTShirtSize() {
        return tShirtSize;
    }

    public ArrayList<EventVolunteerAvailability> getEventAvailabilities() {
        return eventAvailabilities;
    }

    public EventVolunteerAvailability getEventAvailability(Event _event) {
        for (EventVolunteerAvailability av : eventAvailabilities) {
            if (av.getEvent().equals(_event)) {
                return av;
            }
        }
        return null;
    }

    // =========================
    // SETTERS
    // =========================

    public void setYearsOfExperience(int _yearsOfExperience) {
        if (!isValidYearsOfExperience(_yearsOfExperience)) {
            throw new IllegalArgumentException("Invalid years of experience: " + _yearsOfExperience + 
                    ". Years of experience must be between 0 and 100.");
        }
        this.yearsOfExperience = _yearsOfExperience;
    }

    public void setTShirtSize(TShirtSize _size) {
        if (!isValidTShirtSize(_size)) {
            throw new IllegalArgumentException("T-Shirt size cannot be null.");
        }
        this.tShirtSize = _size;
    }

    // =========================
    // DISPLAY
    // =========================

    public void display() {
        System.out.println("Volunteer: " + firstName + " " + lastName);
        System.out.println("Experience: " + yearsOfExperience + " years");
        System.out.println("T-Shirt Size: " + tShirtSize);

        System.out.println("Applied Events with Availability:");
        for (EventVolunteerAvailability av : eventAvailabilities) {
            System.out.println("- Event: " + av.getEvent().getName());
            System.out.print("  From: ");
            av.getAvailableFrom().displayInline();
            System.out.print("  To: ");
            av.getAvailableTo().displayInline();
            System.out.println();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        // Delegates to Person.equals() which compares firstName, lastName, email, phone
        // This provides a reasonable unique identifier for volunteers
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

        @Override
    public String toString() {
        return "Volunteer{" +
                "name=" + firstName + " " + lastName +
                ", experience=" + yearsOfExperience +
                ", size=" + tShirtSize +
                ", appliedEvents=" + eventAvailabilities.size() +
                '}';
    }
}