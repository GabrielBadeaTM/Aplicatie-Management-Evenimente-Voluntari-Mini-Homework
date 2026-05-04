public class PersonValidationTest {

    public static void runTests() {
        System.out.println("1. EMAIL VALIDATION:");
        testEmailValidation();

        System.out.println("\n2. PHONE VALIDATION:");
        testPhoneValidation();

        System.out.println("\n3. NAME VALIDATION:");
        testNameValidation();
    }

    static void testEmailValidation() {
        String[] validEmails = {"john@example.com", "user.name@domain.co.uk", "test123@test.org"};
        String[] invalidEmails = {"notanemail", "missing@domain", "no.at.sign.com", "@nodomain.com"};

        System.out.println("Valid emails:");
        for (String email : validEmails) {
            try {
                Person p = new Person("John", "Doe", email, "0711111111");
                System.out.println("  ✓ Accepted: " + email);
            } catch (IllegalArgumentException e) {
                System.out.println("  ✗ Rejected: " + email);
            }
        }

        System.out.println("Invalid emails:");
        for (String email : invalidEmails) {
            try {
                Person p = new Person("John", "Doe", email, "0711111111");
                System.out.println("  ✗ Incorrectly accepted: " + email);
            } catch (IllegalArgumentException e) {
                System.out.println("  ✓ Correctly rejected: " + email);
            }
        }
    }

    static void testPhoneValidation() {
        String[] validPhones = {"0711111111", "0744567890", "123456789012345"};
        String[] invalidPhones = {"123", "07-11-11-11", "phone123", "07111 11111"};

        System.out.println("Valid phone numbers:");
        for (String phone : validPhones) {
            try {
                Person p = new Person("John", "Doe", "john@example.com", phone);
                System.out.println("  ✓ Accepted: " + phone);
            } catch (IllegalArgumentException e) {
                System.out.println("  ✗ Rejected: " + phone);
            }
        }

        System.out.println("Invalid phone numbers:");
        for (String phone : invalidPhones) {
            try {
                Person p = new Person("John", "Doe", "john@example.com", phone);
                System.out.println("  ✗ Incorrectly accepted: " + phone);
            } catch (IllegalArgumentException e) {
                System.out.println("  ✓ Correctly rejected: " + phone);
            }
        }
    }

    static void testNameValidation() {
        String[] validNames = {"John", "Maria-Elena", "Alexandru"};
        String[] invalidNames = {"", "A", null};

        System.out.println("Valid names:");
        for (String name : validNames) {
            try {
                Person p = new Person(name, "Doe", "john@example.com", "0711111111");
                System.out.println("  ✓ Accepted: '" + name + "'");
            } catch (IllegalArgumentException | NullPointerException e) {
                System.out.println("  ✗ Rejected: '" + name + "'");
            }
        }

        System.out.println("Invalid names:");
        String[] testNames = {"", "A"};
        for (String name : testNames) {
            try {
                Person p = new Person(name, "Doe", "john@example.com", "0711111111");
                System.out.println("  ✗ Incorrectly accepted: '" + name + "'");
            } catch (IllegalArgumentException e) {
                System.out.println("  ✓ Correctly rejected: '" + name + "'");
            }
        }
    }
}
