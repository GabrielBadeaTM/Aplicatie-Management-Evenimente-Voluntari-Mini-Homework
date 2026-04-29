import java.util.ArrayList;

public class Volunteer extends Person {

    private int yearsOfExperience;
    private TShirtSize tShirtSize;

    private ArrayList<Event> appliedEvents;
    private ArrayList<EventVolunteerAvailability> eventAvailabilities;

    // Default constructor
    public Volunteer() {
        super();
        this.yearsOfExperience = 0;
        this.tShirtSize = TShirtSize.M;
        this.appliedEvents = new ArrayList<>();
        this.eventAvailabilities = new ArrayList<>();
    }

    // Constructor with parameters
    public Volunteer(String _firstName, String _lastName, String _email, String _phone,
                      int _yearsOfExperience, TShirtSize _tShirtSize) {

        super(_firstName, _lastName, _email, _phone);

        setYearsOfExperience(_yearsOfExperience);
        setTShirtSize(_tShirtSize);

        this.appliedEvents = new ArrayList<>();
        this.eventAvailabilities = new ArrayList<>();
    }

    // ========== VALIDATION METHODS ==========
    private boolean isValidYearsOfExperience(int years) {
        return years >= 0 && years <= 100;
    }

    private boolean isValidTShirtSize(TShirtSize size) {
        return size != null;
    }

    // =========================
    // APPLY METHODS
    // =========================

    // Apply to event with specific availability dates
    public void applyToEvent(Event _event, SimpleDate _availableFrom, SimpleDate _availableTo) {
        if (!appliedEvents.contains(_event)) {
            appliedEvents.add(_event);
            EventVolunteerAvailability availability = new EventVolunteerAvailability(_event, this, _availableFrom, _availableTo);
            eventAvailabilities.add(availability);
        }
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
        appliedEvents.remove(_event);
        eventAvailabilities.removeIf(av -> av.getEvent().equals(_event));
    }

    public void cancelAllApplications() {
        appliedEvents.clear();
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

    public ArrayList<Event> getAppliedEvents() {
        return appliedEvents;
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
    public String toString() {
        return "Volunteer{" +
                "name=" + firstName + " " + lastName +
                ", experience=" + yearsOfExperience +
                ", size=" + tShirtSize +
                ", appliedEvents=" + appliedEvents.size() +
                '}';
    }
}