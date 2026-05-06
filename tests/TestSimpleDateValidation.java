import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class TestSimpleDateValidation {

    // ==================== YEAR VALIDATION TESTS ====================
    @Test
    @DisplayName("An valid - 2000")
    void testValidYear2000() {
        SimpleDate d = new SimpleDate(2000, 1, 1, 10, 30);
        assertEquals(2000, d.getYear());
    }

    @Test
    @DisplayName("An valid - 1900 (minim)")
    void testValidYearMinimum() {
        SimpleDate d = new SimpleDate(1900, 1, 1, 0, 0);
        assertEquals(1900, d.getYear());
    }

    @Test
    @DisplayName("An valid - 2100 (maxim)")
    void testValidYearMaximum() {
        SimpleDate d = new SimpleDate(2100, 1, 1, 0, 0);
        assertEquals(2100, d.getYear());
    }

    @Test
    @DisplayName("An invalid - 1899 (prea mic)")
    void testInvalidYearTooSmall() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleDate(1899, 1, 1, 0, 0);
        });
    }

    @Test
    @DisplayName("An invalid - 2101 (prea mare)")
    void testInvalidYearTooLarge() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleDate(2101, 1, 1, 0, 0);
        });
    }

    // ==================== MONTH VALIDATION TESTS ====================
    @Test
    @DisplayName("Lună valid - ianuarie (1)")
    void testValidMonthJanuary() {
        SimpleDate d = new SimpleDate(2000, 1, 1, 0, 0);
        assertEquals(1, d.getMonth());
    }

    @Test
    @DisplayName("Lună valid - decembrie (12)")
    void testValidMonthDecember() {
        SimpleDate d = new SimpleDate(2000, 12, 1, 0, 0);
        assertEquals(12, d.getMonth());
    }

    @Test
    @DisplayName("Lună invalid - 0")
    void testInvalidMonthZero() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleDate(2000, 0, 1, 0, 0);
        });
    }

    @Test
    @DisplayName("Lună invalid - 13")
    void testInvalidMonthThirteen() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleDate(2000, 13, 1, 0, 0);
        });
    }

    // ==================== DAY VALIDATION TESTS ====================
    @Test
    @DisplayName("Zi valid - 1 februarie 2000 (an bisect)")
    void testValidDayFebruaryLeapYear() {
        SimpleDate d = new SimpleDate(2000, 2, 29, 0, 0);
        assertEquals(29, d.getDay());
    }

    @Test
    @DisplayName("Zi invalid - 30 februarie 2000 (an bisect)")
    void testInvalidDayFebruaryLeapYear() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleDate(2000, 2, 30, 0, 0);
        });
    }

    @Test
    @DisplayName("Zi invalid - 29 februarie 2001 (an non-bisect)")
    void testInvalidDayFebruaryNonLeapYear() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleDate(2001, 2, 29, 0, 0);
        });
    }

    @Test
    @DisplayName("Zi valid - februarie 2020 (an bisect)")
    void testValidDayFebruary2020() {
        SimpleDate d = new SimpleDate(2020, 2, 29, 0, 0);
        assertEquals(29, d.getDay());
    }

    @Test
    @DisplayName("Zi invalid - 0")
    void testInvalidDayZero() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleDate(2000, 1, 0, 0, 0);
        });
    }

    @Test
    @DisplayName("Zi invalid - 32 pentru ianuarie")
    void testInvalidDay32January() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleDate(2000, 1, 32, 0, 0);
        });
    }

    @Test
    @DisplayName("Zi valid - 30 aprilie")
    void testValidDay30April() {
        SimpleDate d = new SimpleDate(2000, 4, 30, 0, 0);
        assertEquals(30, d.getDay());
    }

    @Test
    @DisplayName("Zi invalid - 31 aprilie")
    void testInvalidDay31April() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleDate(2000, 4, 31, 0, 0);
        });
    }

    // ==================== LEAP YEAR TESTS ====================
    @Test
    @DisplayName("An bisect - 2000 (divizibil cu 400)")
    void testLeapYear2000() {
        SimpleDate d = new SimpleDate(2000, 2, 29, 0, 0);
        assertEquals(29, d.getDay());
    }

    @Test
    @DisplayName("An non-bisect - 1900 (divizibil cu 100, nu cu 400)")
    void testNotLeapYear1900() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleDate(1900, 2, 29, 0, 0);
        });
    }

    @Test
    @DisplayName("An bisect - 2020 (divizibil cu 4, nu cu 100)")
    void testLeapYear2020() {
        SimpleDate d = new SimpleDate(2020, 2, 29, 0, 0);
        assertEquals(29, d.getDay());
    }

    @Test
    @DisplayName("An bisect - 2024 (divizibil cu 4, nu cu 100)")
    void testLeapYear2024() {
        SimpleDate d = new SimpleDate(2024, 2, 29, 0, 0);
        assertEquals(29, d.getDay());
    }

    @Test
    @DisplayName("An non-bisect - 2001 (nu e divizibil cu 4)")
    void testNotLeapYear2001() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleDate(2001, 2, 29, 0, 0);
        });
    }

    // ==================== HOUR VALIDATION TESTS ====================
    @Test
    @DisplayName("Oră valid - 0 (miezul nopții)")
    void testValidHourZero() {
        SimpleDate d = new SimpleDate(2000, 1, 1, 0, 0);
        assertEquals(0, d.getHour());
    }

    @Test
    @DisplayName("Oră valid - 23 (maxim)")
    void testValidHourMaximum() {
        SimpleDate d = new SimpleDate(2000, 1, 1, 23, 0);
        assertEquals(23, d.getHour());
    }

    @Test
    @DisplayName("Oră invalid - 24")
    void testInvalidHour24() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleDate(2000, 1, 1, 24, 0);
        });
    }

    @Test
    @DisplayName("Oră invalid - -1")
    void testInvalidHourNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleDate(2000, 1, 1, -1, 0);
        });
    }

    // ==================== MINUTE VALIDATION TESTS ====================
    @Test
    @DisplayName("Minut valid - 0")
    void testValidMinuteZero() {
        SimpleDate d = new SimpleDate(2000, 1, 1, 0, 0);
        assertEquals(0, d.getMinute());
    }

    @Test
    @DisplayName("Minut valid - 59 (maxim)")
    void testValidMinuteMaximum() {
        SimpleDate d = new SimpleDate(2000, 1, 1, 0, 59);
        assertEquals(59, d.getMinute());
    }

    @Test
    @DisplayName("Minut invalid - 60")
    void testInvalidMinute60() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleDate(2000, 1, 1, 0, 60);
        });
    }

    @Test
    @DisplayName("Minut invalid - -1")
    void testInvalidMinuteNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleDate(2000, 1, 1, 0, -1);
        });
    }

    // ==================== COMPLETE DATE VALIDATION TESTS ====================
    @Test
    @DisplayName("Dată completă validă - 1 ianuarie 2000 10:30")
    void testValidCompleteDate() {
        SimpleDate d = new SimpleDate(2000, 1, 1, 10, 30);
        assertEquals(2000, d.getYear());
        assertEquals(1, d.getMonth());
        assertEquals(1, d.getDay());
        assertEquals(10, d.getHour());
        assertEquals(30, d.getMinute());
    }

    @Test
    @DisplayName("Dată completă validă - 25 decembrie 2023 23:59")
    void testValidCompleteDateEnd() {
        SimpleDate d = new SimpleDate(2023, 12, 25, 23, 59);
        assertNotNull(d);
    }

}
