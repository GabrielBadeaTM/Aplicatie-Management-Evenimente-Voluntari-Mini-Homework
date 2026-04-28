import java.util.ArrayList;

public class Admin extends Person {

    private ArrayList<Event> createdEvents;

    // Default constructor
    public Admin() {
        super();
        this.createdEvents = new ArrayList<>();
    }

    // Constructor with params
    public Admin(String _firstName, String _lastName, String _email, String _phone) {
        super(_firstName, _lastName, _email, _phone);
        this.createdEvents = new ArrayList<>();
    }

    // CREATE EVENT
    public Event createEvent(String name, EventDate eventDate, ArrayList<Coordinator> coordinators) {

        Event event = new Event(name, eventDate, this, coordinators);
        createdEvents.add(event);

        return event;
    }

    // CANCEL EVENT
    public void cancelEvent(Event event) {
        createdEvents.remove(event);
        System.out.println("Event cancelled: " + event.getName());
    }

    // SELECT COORDINATORS (simplu helper logic)
    public void assignCoordinator(Event event, Coordinator coordinator) {
        event.addCoordinator(coordinator);
    }

    // SELECT MULTIPLE COORDINATORS
    public void assignCoordinators(Event event, ArrayList<Coordinator> coordinators) {
        for (Coordinator coordinator : coordinators) {
            event.addCoordinator(coordinator);
        }
    }

    public ArrayList<Event> getCreatedEvents() {
        return createdEvents;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "name=" + getFirstName() + " " + getLastName() +
                ", email=" + getEmail() +
                '}';
    }
}