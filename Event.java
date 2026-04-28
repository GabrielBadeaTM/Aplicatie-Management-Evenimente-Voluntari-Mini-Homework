import java.util.ArrayList;

public class Event {

    private String name;
    private EventDate eventDate;

    private Admin admin;
    private ArrayList<Coordinator> coordinators;

    // Default constructor
    public Event() {
        this.name = "Default Event";
        this.eventDate = new EventDate();
        this.admin = new Admin();

        this.coordinators = new ArrayList<>();
    }

    // Constructor with parameters
    public Event(String _name,
                 EventDate _eventDate,
                 Admin _admin,
                 ArrayList<Coordinator> _coordinators) {

        this.name = _name;
        this.eventDate = _eventDate;
        this.admin = _admin;
        this.coordinators = _coordinators;
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

    public ArrayList<Coordinator> getCoordinators() {
        return coordinators;
    }

    // Setters
    public void setName(String _name) {
        this.name = _name;
    }

    public void setEventDate(EventDate _eventDate) {
        this.eventDate = _eventDate;
    }

    public void setAdmin(Admin _admin) {
        this.admin = _admin;
    }

    public void setCoordinators(ArrayList<Coordinator> _coordinators) {
        this.coordinators = _coordinators;
    }

    // Add coordinator helper (foarte util)
    public void addCoordinator(Coordinator _coordinator) {
        this.coordinators.add(_coordinator);
    }

    // Display
    public void display() {
        System.out.println("Event Name: " + name);

        System.out.println("\nEvent Dates:");
        eventDate.display();

        System.out.println("\nAdmin:");
        System.out.println(admin);

        System.out.println("\nCoordinators:");
        for (Coordinator c : coordinators) {
            System.out.println(c);
        }
    }

    @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", eventDate=" + eventDate +
                ", admin=" + admin +
                ", coordinators=" + coordinators +
                '}';
    }
}