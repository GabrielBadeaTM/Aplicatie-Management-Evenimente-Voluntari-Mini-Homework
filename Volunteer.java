import java.util.ArrayList;

public class Volunteer extends Person {

    private int yearsOfExperience;
    private TShirtSize tShirtSize;

    private ArrayList<Event> appliedEvents;

    private SimpleDate availableFrom;
    private SimpleDate availableTo;

    // Default constructor
    public Volunteer() {
        super();
        this.yearsOfExperience = 0;
        this.tShirtSize = TShirtSize.M;
        this.appliedEvents = new ArrayList<>();
        this.availableFrom = new SimpleDate();
        this.availableTo = new SimpleDate();
    }

    // Constructor with parameters
    public Volunteer(String _firstName, String _lastName, String _email, String _phone,
                      int _yearsOfExperience, TShirtSize _tShirtSize,
                      SimpleDate _availableFrom, SimpleDate _availableTo) {

        super(_firstName, _lastName, _email, _phone);

        this.yearsOfExperience = _yearsOfExperience;
        this.tShirtSize = _tShirtSize;

        this.appliedEvents = new ArrayList<>();

        this.availableFrom = _availableFrom;
        this.availableTo = _availableTo;
    }

    // =========================
    // APPLY METHODS
    // =========================

    public void applyToEvent(Event _event) {
        appliedEvents.add(_event);
    }

    public void applyToEvents(ArrayList<Event> _events) {
        appliedEvents.addAll(_events);
    }

    // =========================
    // CANCEL METHODS
    // =========================

    public void cancelApplication(Event _event) {
        appliedEvents.remove(_event);
    }

    public void cancelAllApplications() {
        appliedEvents.clear();
    }

    // =========================
    // UPDATE METHODS
    // =========================

    public void updateAvailability(SimpleDate _from, SimpleDate _to) {
        this.availableFrom = _from;
        this.availableTo = _to;
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

    public SimpleDate getAvailableFrom() {
        return availableFrom;
    }

    public SimpleDate getAvailableTo() {
        return availableTo;
    }

    // =========================
    // DISPLAY
    // =========================

    public void display() {
        System.out.println("Volunteer: " + firstName + " " + lastName);
        System.out.println("Experience: " + yearsOfExperience + " years");
        System.out.println("T-Shirt Size: " + tShirtSize);

        System.out.println("Availability:");
        System.out.print("From: ");
        availableFrom.displayInline();
        System.out.println();
        System.out.print("To: ");
        availableTo.displayInline();
        System.out.println();

        System.out.println("Applied Events:");
        for (Event e : appliedEvents) {
            System.out.println("- " + e.getName());
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