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

        setStartDate(_startDate);
        setEndDate(_endDate);
        setRegistrationStart(_registrationStart);
        setRegistrationEnd(_registrationEnd);
        validateEventDateLogic();
    }

    // ========== VALIDATION METHODS ==========
    private boolean isDateNull(SimpleDate date) {
        return date == null;
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

    private void validateEventDateLogic() {
        // registrationStart must be before registrationEnd
        if (compareDates(registrationStart, registrationEnd) >= 0) {
            throw new IllegalArgumentException("Registration start date must be before registration end date.");
        }

        // startDate must be before endDate
        if (compareDates(startDate, endDate) >= 0) {
            throw new IllegalArgumentException("Event start date must be before event end date.");
        }

        // registrationEnd must be before or equal to startDate
        if (compareDates(registrationEnd, startDate) > 0) {
            throw new IllegalArgumentException("Registration end date must be before or equal to event start date.");
        }
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
        if (isDateNull(_startDate)) {
            throw new IllegalArgumentException("Start date cannot be null.");
        }
        this.startDate = _startDate;
    }

    public void setEndDate(SimpleDate _endDate) {
        if (isDateNull(_endDate)) {
            throw new IllegalArgumentException("End date cannot be null.");
        }
        this.endDate = _endDate;
    }

    public void setRegistrationStart(SimpleDate _registrationStart) {
        if (isDateNull(_registrationStart)) {
            throw new IllegalArgumentException("Registration start date cannot be null.");
        }
        this.registrationStart = _registrationStart;
    }

    public void setRegistrationEnd(SimpleDate _registrationEnd) {
        if (isDateNull(_registrationEnd)) {
            throw new IllegalArgumentException("Registration end date cannot be null.");
        }
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