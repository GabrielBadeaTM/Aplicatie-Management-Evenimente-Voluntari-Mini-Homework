/**
 * Represents the complete date and time information for an Event.
 * 
 * Manages two overlapping date periods:
 * 1. Registration Period: registrationStart to registrationEnd (when volunteers can apply)
 * 2. Event Period: startDate to endDate (when the event actually occurs)
 * 
 * Constraints enforced:
 * - registrationStart must be before registrationEnd
 * - startDate must be before endDate
 * - registrationEnd must be before or equal to startDate (registration must close before event starts)
 */
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
    
    /**
     * Checks if a date is null.
     * 
     * @param date the date to check
     * @return true if the date is null, false otherwise
     */
    private boolean isDateNull(SimpleDate date) {
        return date == null;
    }

    /**
     * Compares two SimpleDate objects.
     * 
     * Comparison Order:
     * 1. Year (earlier years come first)
     * 2. Month (if same year)
     * 3. Day (if same month)
     * 4. Hour and Minute (if same day)
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
     * Validates the logical relationships between the event dates.
     * 
     * Ensures:
     * 1. registrationStart < registrationEnd (registration window is valid)
     * 2. startDate < endDate (event duration is valid)
     * 3. registrationEnd <= startDate (registration must close before event starts)
     * 
     * @throws IllegalArgumentException if any constraint is violated
     */
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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EventDate other = (EventDate) obj;
        return startDate != null && startDate.equals(other.startDate) && endDate != null && endDate.equals(other.endDate);
    }

    @Override
    public int hashCode() {
        int result = startDate != null ? startDate.hashCode() : 0;
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        return result;
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