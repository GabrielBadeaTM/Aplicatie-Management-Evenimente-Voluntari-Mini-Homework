import java.util.ArrayList;

public class Coordinator extends Person {

    private ArrayList<Volunteer> volunteers;

    // Default constructor
    public Coordinator() {
        super();
        this.volunteers = new ArrayList<>();
    }

    // Constructor with parameters
    public Coordinator(String _firstName, String _lastName, String _email, String _phone) {
        super(_firstName, _lastName, _email, _phone);
        this.volunteers = new ArrayList<>();
    }

    // Add volunteer
    public void addVolunteer(Volunteer _volunteer) {
        this.volunteers.add(_volunteer);
    }

    // Remove volunteer
    public void removeVolunteer(Volunteer _volunteer) {
        this.volunteers.remove(_volunteer);
    }

    // Get volunteers
    public ArrayList<Volunteer> getVolunteers() {
        return volunteers;
    }

    // Display
    public void display() {
        System.out.println("Coordinator: " + firstName + " " + lastName);
        System.out.println("Email: " + email);
        System.out.println("Phone: " + phone);

        System.out.println("Volunteers:");
        for (Volunteer v : volunteers) {
            v.display();
            System.out.println("-----");
        }
    }

    @Override
    public String toString() {
        return "Coordinator{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", volunteersCount=" + volunteers.size() +
                '}';
    }
}