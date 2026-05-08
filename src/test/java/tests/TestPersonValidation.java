package tests;

import models.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;

public class TestPersonValidation {

    // ==================== EMAIL VALIDATION TESTS ====================
    @Test
    @DisplayName("Email valid cu format corect")
    void testValidEmail() {
        // Nu aruncă excepție
        Person p = new Person("John", "Doe", "john.doe@example.com", "1234567890");
        assertEquals("john.doe@example.com", p.getEmail());
    }

    @Test
    @DisplayName("Email invalid - lipsă @")
    void testInvalidEmailMissingAt() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Person("John", "Doe", "john.doeexample.com", "1234567890");
        });
    }

    @Test
    @DisplayName("Email invalid - lipsă .")
    void testInvalidEmailMissingDot() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Person("John", "Doe", "john@examplecom", "1234567890");
        });
    }

    @Test
    @DisplayName("Email invalid - gol")
    void testInvalidEmailEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Person("John", "Doe", "", "1234567890");
        });
    }

    @Test
    @DisplayName("Email invalid - null")
    void testInvalidEmailNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Person("John", "Doe", null, "1234567890");
        });
    }

    @Test
    @DisplayName("Email invalid - . înainte de @")
    void testInvalidEmailDotBeforeAt() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Person("John", "Doe", "john.@example.com", "1234567890");
        });
    }

    // ==================== PHONE VALIDATION TESTS ====================
    @Test
    @DisplayName("Telefon valid - 10 cifre")
    void testValidPhone10Digits() {
        Person p = new Person("John", "Doe", "john@example.com", "1234567890");
        assertEquals("1234567890", p.getPhone());
    }

    @Test
    @DisplayName("Telefon valid - 15 cifre")
    void testValidPhone15Digits() {
        Person p = new Person("John", "Doe", "john@example.com", "123456789012345");
        assertEquals("123456789012345", p.getPhone());
    }

    @Test
    @DisplayName("Telefon valid - 7 cifre (minim)")
    void testValidPhone7Digits() {
        Person p = new Person("John", "Doe", "john@example.com", "1234567");
        assertEquals("1234567", p.getPhone());
    }

    @Test
    @DisplayName("Telefon invalid - 6 cifre (prea scurt)")
    void testInvalidPhoneTooShort() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Person("John", "Doe", "john@example.com", "123456");
        });
    }

    @Test
    @DisplayName("Telefon invalid - 16 cifre (prea lung)")
    void testInvalidPhoneTooLong() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Person("John", "Doe", "john@example.com", "1234567890123456");
        });
    }

    @Test
    @DisplayName("Telefon invalid - conține caractere")
    void testInvalidPhoneWithCharacters() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Person("John", "Doe", "john@example.com", "123456789a");
        });
    }

    @Test
    @DisplayName("Telefon invalid - gol")
    void testInvalidPhoneEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Person("John", "Doe", "john@example.com", "");
        });
    }

    @Test
    @DisplayName("Telefon invalid - null")
    void testInvalidPhoneNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Person("John", "Doe", "john@example.com", null);
        });
    }

    // ==================== NAME VALIDATION TESTS ====================
    @Test
    @DisplayName("Nume valid - 2 caractere (minim)")
    void testValidNameMinimum() {
        Person p = new Person("Jo", "Do", "john@example.com", "1234567890");
        assertEquals("Jo", p.getFirstName());
        assertEquals("Do", p.getLastName());
    }

    @Test
    @DisplayName("Nume valid - mai mult de 2 caractere")
    void testValidNameNormal() {
        Person p = new Person("John", "Doe", "john@example.com", "1234567890");
        assertEquals("John", p.getFirstName());
        assertEquals("Doe", p.getLastName());
    }

    @Test
    @DisplayName("Nume invalid - 1 caracter")
    void testInvalidNameSingleCharacter() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Person("J", "Doe", "john@example.com", "1234567890");
        });
    }

    @Test
    @DisplayName("Nume invalid - gol")
    void testInvalidNameEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Person("", "Doe", "john@example.com", "1234567890");
        });
    }

    @Test
    @DisplayName("Nume invalid - null")
    void testInvalidNameNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Person(null, "Doe", "john@example.com", "1234567890");
        });
    }

    @Test
    @DisplayName("Nume invalid - doar spații")
    void testInvalidNameOnlySpaces() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Person("   ", "Doe", "john@example.com", "1234567890");
        });
    }

    // ==================== SETTER VALIDATION TESTS ====================
    @Test
    @DisplayName("Setter email valid")
    void testSetValidEmail() {
        Person p = new Person("John", "Doe", "john@example.com", "1234567890");
        p.setEmail("jane@example.com");
        assertEquals("jane@example.com", p.getEmail());
    }

    @Test
    @DisplayName("Setter email invalid aruncă excepție")
    void testSetInvalidEmailThrows() {
        Person p = new Person("John", "Doe", "john@example.com", "1234567890");
        assertThrows(IllegalArgumentException.class, () -> {
            p.setEmail("invalid-email");
        });
    }

    @Test
    @DisplayName("Setter telefon valid")
    void testSetValidPhone() {
        Person p = new Person("John", "Doe", "john@example.com", "1234567890");
        p.setPhone("9876543210");
        assertEquals("9876543210", p.getPhone());
    }

    @Test
    @DisplayName("Setter telefon invalid aruncă excepție")
    void testSetInvalidPhoneThrows() {
        Person p = new Person("John", "Doe", "john@example.com", "1234567890");
        assertThrows(IllegalArgumentException.class, () -> {
            p.setPhone("123");
        });
    }

    @Test
    @DisplayName("Setter nume valid")
    void testSetValidFirstName() {
        Person p = new Person("John", "Doe", "john@example.com", "1234567890");
        p.setFirstName("Jane");
        assertEquals("Jane", p.getFirstName());
    }

    @Test
    @DisplayName("Setter nume invalid aruncă excepție")
    void testSetInvalidFirstNameThrows() {
        Person p = new Person("John", "Doe", "john@example.com", "1234567890");
        assertThrows(IllegalArgumentException.class, () -> {
            p.setFirstName("X");
        });
    }

}
