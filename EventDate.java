public class EventDate {

    private SimpleDate startDate;
    private SimpleDate endDate;
    private SimpleDate registrationStart;
    private SimpleDate registrationEnd;

    // Default constructor (dummy values)
    public EventDate() {
        this.startDate = new SimpleDate();
        this.endDate = new SimpleDate();
        this.registrationStart = new SimpleDate();
        this.registrationEnd = new SimpleDate();
    }

    // Constructor with parameters
    public EventDate(SimpleDate _startDate,
                     SimpleDate _endDate,
                     SimpleDate _registrationStart,
                     SimpleDate _registrationEnd) {

        this.startDate = _startDate;
        this.endDate = _endDate;
        this.registrationStart = _registrationStart;
        this.registrationEnd = _registrationEnd;
    }

    // Getters
    public SimpleDate getStartDate() {
        return startDate;
    }

    public SimpleDate getEndDate() {
        return endDate;
    }

    public SimpleDate getRegistrationStart() {
        return registrationStart;
    }

    public SimpleDate getRegistrationEnd() {
        return registrationEnd;
    }

    // Setters
    public void setStartDate(SimpleDate _startDate) {
        this.startDate = _startDate;
    }

    public void setEndDate(SimpleDate _endDate) {
        this.endDate = _endDate;
    }

    public void setRegistrationStart(SimpleDate _registrationStart) {
        this.registrationStart = _registrationStart;
    }

    public void setRegistrationEnd(SimpleDate _registrationEnd) {
        this.registrationEnd = _registrationEnd;
    }

    // Display
    public void display() {
        System.out.println("Event period:");

        System.out.print("Start: ");
        startDate.display();

        System.out.print("End: ");
        endDate.display();

        System.out.println("Registration period:");

        System.out.print("Start: ");
        registrationStart.display();

        System.out.print("End: ");
        registrationEnd.display();
    }

    @Override
    public String toString() {
        return "EventDate{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                ", registrationStart=" + registrationStart +
                ", registrationEnd=" + registrationEnd +
                '}';
    }
}