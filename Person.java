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
        this.firstName = _firstName;
        this.lastName = _lastName;
        this.email = _email;
        this.phone = _phone;
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
        this.firstName = _firstName;
    }

    public void setLastName(String _lastName) {
        this.lastName = _lastName;
    }

    public void setEmail(String _email) {
        this.email = _email;
    }

    public void setPhone(String _phone) {
        this.phone = _phone;
    }

    public void display() {
        System.out.println("First Name: " + firstName);
        System.out.println("Last Name: " + lastName);
        System.out.println("Email: " + email);
        System.out.println("Phone: " + phone);
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