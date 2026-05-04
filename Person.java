public class Person {

    protected String firstName;
    protected String lastName;
    protected String email;
    protected String phone;

    // Default constructor
    public Person() {
        this.firstName = "fn";
        this.lastName = "ln";
        this.email = "fn.ln@example.com";
        this.phone = "0000000000";
    }

    // Constructor with parameters
    public Person(String _firstName, String _lastName, String _email, String _phone) {
        setFirstName(_firstName);
        setLastName(_lastName);
        setEmail(_email);
        setPhone(_phone);
    }

    // ========== VALIDATION METHODS ==========
    private boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() >= 2;
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        // Check for @ and . and basic email format
        return email.contains("@") && email.contains(".") && 
               email.indexOf("@") > 0 && 
               email.lastIndexOf(".") > email.indexOf("@") + 1 &&
               email.length() >= 5;
    }

    private boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        // Phone must contain only digits and be between 7 and 15 characters
        return phone.matches("\\d{7,15}");
    }

    // Getters
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    // Setters
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