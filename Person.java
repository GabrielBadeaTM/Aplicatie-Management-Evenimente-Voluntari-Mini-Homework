/**
 * Represents a base Person class with contact information and validation.
 * This is the parent class for Admin and Volunteer.
 * 
 * Features:
 * - Stores firstName, lastName, email, and phone
 * - Validates all input according to specific rules
 * - Email is used as a unique identifier (for equality and hashing)
 */
public class Person {

    protected String firstName;
    protected String lastName;
    protected String email;
    protected String phone;

    // Default constructor - initializes with dummy values
    public Person() {
        this.firstName = "fn";
        this.lastName = "ln";
        this.email = "fn.ln@example.com";
        this.phone = "0000000000";
    }

    /**
     * Constructor with parameters - initializes a Person with provided contact information
     * 
     * @param _firstName person's first name (must be at least 2 characters)
     * @param _lastName person's last name (must be at least 2 characters)
     * @param _email person's email (must contain @ and . and be properly formatted)
     * @param _phone person's phone number (must be 7-15 digits)
     */
    public Person(String _firstName, String _lastName, String _email, String _phone) {
        setFirstName(_firstName);
        setLastName(_lastName);
        setEmail(_email);
        setPhone(_phone);
    }

    // ========== VALIDATION METHODS ==========
    
    /**
     * Validates a person's name.
     * A valid name must not be null, must not be empty, and must be at least 2 characters long.
     * 
     * @param name the name to validate
     * @return true if the name is valid, false otherwise
     */
    private boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() >= 2;
    }

    /**
     * Validates an email address.
     * A valid email must contain '@' and '.' in the correct order and have proper formatting.
     * 
     * @param email the email to validate
     * @return true if the email is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        // Check for @ and . and basic email format
        return email.contains("@") && email.contains(".") && 
               email.indexOf("@") > 0 && 
               email.lastIndexOf(".") > email.indexOf("@") + 1 &&
               email.length() >= 5 &&
               !email.contains(".@");
    }

    /**
     * Validates a phone number.
     * A valid phone must contain only digits and have a length between 7 and 15.
     * 
     * @param phone the phone number to validate
     * @return true if the phone is valid, false otherwise
     */
    private boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        // Phone must contain only digits and be between 7 and 15 characters
        return phone.matches("\\d{7,15}");
    }

    // ===== GETTERS =====
    // These methods return the person's contact information

    /**
     * Gets the person's first name.
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets the person's last name.
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Gets the person's email.
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the person's phone number.
     * @return the phone number
     */
    public String getPhone() {
        return phone;
    }

    // ===== SETTERS =====
    // These methods update the person's contact information with validation
    public void setFirstName(String _firstName) {
        if (!isValidName(_firstName)) {
            throw new IllegalArgumentException("Invalid first name: '" + _firstName + "'. Name must be at least 2 characters long and not empty.");
        }
        this.firstName = _firstName;
    }

    public void setLastName(String _lastName) {
        if (!isValidName(_lastName)) {
            throw new IllegalArgumentException("Invalid last name: '" + _lastName + "'. Name must be at least 2 characters long and not empty.");
        }
        this.lastName = _lastName;
    }

    public void setEmail(String _email) {
        if (!isValidEmail(_email)) {
            throw new IllegalArgumentException("Invalid email: '" + _email + "'. Email must contain '@' and '.' and be properly formatted.");
        }
        this.email = _email;
    }

    public void setPhone(String _phone) {
        if (!isValidPhone(_phone)) {
            throw new IllegalArgumentException("Invalid phone: '" + _phone + "'. Phone must contain only digits and be between 7 and 15 characters.");
        }
        this.phone = _phone;
    }

    public void display() {
        System.out.println("First Name: " + firstName);
        System.out.println("Last Name: " + lastName);
        System.out.println("Email: " + email);
        System.out.println("Phone: " + phone);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person other = (Person) obj;
        return email != null && email.equals(other.email);
    }

    @Override
    public int hashCode() {
        return email != null ? email.hashCode() : 0;
    }

        @Override
    public String toString() {
        return "Person{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}