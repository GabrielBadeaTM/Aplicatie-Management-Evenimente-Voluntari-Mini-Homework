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
        setYear(_year);
        setMonth(_month);
        setDay(_day);
        setHour(_hour);
        setMinute(_minute);
    }

    // ========== VALIDATION METHODS ==========
    private boolean isValidYear(int year) {
        return year >= 1900 && year <= 2100;
    }

    private boolean isValidMonth(int month) {
        return month >= 1 && month <= 12;
    }

    private boolean isValidDay(int day) {
        if (day < 1 || day > 31) {
            return false;
        }
        // Additional validation considering month
        int[] daysInMonth = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        
        // Check for leap year
        if (month == 2 && isLeapYear(year)) {
            return day <= 29;
        }
        
        return day <= daysInMonth[month];
    }

    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    private boolean isValidHour(int hour) {
        return hour >= 0 && hour <= 23;
    }

    private boolean isValidMinute(int minute) {
        return minute >= 0 && minute <= 59;
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
        if (!isValidYear(_year)) {
            throw new IllegalArgumentException("Invalid year: " + _year + ". Year must be between 1900 and 2100.");
        }
        this.year = _year;
    }

    public void setMonth(int _month) {
        if (!isValidMonth(_month)) {
            throw new IllegalArgumentException("Invalid month: " + _month + ". Month must be between 1 and 12.");
        }
        this.month = _month;
    }

    public void setDay(int _day) {
        if (!isValidDay(_day)) {
            throw new IllegalArgumentException("Invalid day: " + _day + " for month " + month + " in year " + year);
        }
        this.day = _day;
    }

    public void setHour(int _hour) {
        if (!isValidHour(_hour)) {
            throw new IllegalArgumentException("Invalid hour: " + _hour + ". Hour must be between 0 and 23.");
        }
        this.hour = _hour;
    }

    public void setMinute(int _minute) {
        if (!isValidMinute(_minute)) {
            throw new IllegalArgumentException("Invalid minute: " + _minute + ". Minute must be between 0 and 59.");
        }
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