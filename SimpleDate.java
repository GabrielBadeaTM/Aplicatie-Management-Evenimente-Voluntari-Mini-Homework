public class SimpleDate {

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    // Default constructor (dummy values)
    public SimpleDate() {
        this.year = 2000;
        this.month = 1;
        this.day = 1;
        this.hour = 0;
        this.minute = 0;
    }

    // Constructor with parameters
    public SimpleDate(int _year, int _month, int _day, int _hour, int _minute) {
        this.year = _year;
        this.month = _month;
        this.day = _day;
        this.hour = _hour;
        this.minute = _minute;
    }

    // Getters
    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    // Setters
    public void setYear(int _year) {
        this.year = _year;
    }

    public void setMonth(int _month) {
        this.month = _month;
    }

    public void setDay(int _day) {
        this.day = _day;
    }

    public void setHour(int _hour) {
        this.hour = _hour;
    }

    public void setMinute(int _minute) {
        this.minute = _minute;
    }

    // Display method
    public void display() {
        System.out.println(
            year + "-" +
            String.format("%02d", month) + "-" +
            String.format("%02d", day) + " " +
            String.format("%02d", hour) + ":" +
            String.format("%02d", minute)
        );
    }

    public void displayInline() {
        System.out.print(
            String.format("%04d", year) + "-" +
            String.format("%02d", month) + "-" +
            String.format("%02d", day) + " " +
            String.format("%02d", hour) + ":" +
            String.format("%02d", minute)
        );
    }
    
    @Override
    public String toString() {
        return year + "-" +
               String.format("%02d", month) + "-" +
               String.format("%02d", day) + " " +
               String.format("%02d", hour) + ":" +
               String.format("%02d", minute);
    }
}