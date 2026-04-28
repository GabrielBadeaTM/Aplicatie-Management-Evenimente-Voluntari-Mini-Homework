import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        // =========================
        // 1. CREATE ADMIN
        // =========================
        Admin admin = new Admin("Ion", "Popescu", "ion@mail.com", "0711111111");

        // =========================
        // 2. CREATE DATES
        // =========================
        SimpleDate eventStart = new SimpleDate(2026, 5, 10, 10, 0);
        SimpleDate eventEnd = new SimpleDate(2026, 5, 12, 18, 0);

        SimpleDate regStart = new SimpleDate(2026, 4, 1, 0, 0);
        SimpleDate regEnd = new SimpleDate(2026, 5, 5, 23, 59);

        EventDate eventDate = new EventDate(eventStart, eventEnd, regStart, regEnd);

        // =========================
        // 3. CREATE COORDINATOR
        // =========================
        Coordinator coord = new Coordinator("Ana", "Ionescu", "ana@mail.com", "0722222222");

        ArrayList<Coordinator> coordinators = new ArrayList<>();
        coordinators.add(coord);

        // =========================
        // 4. ADMIN CREATES EVENT
        // =========================
        Event event = admin.createEvent("Tech Conference", eventDate, coordinators);

        // =========================
        // 5. CREATE VOLUNTEER
        // =========================
        SimpleDate availFrom = new SimpleDate(2026, 5, 1, 0, 0);
        SimpleDate availTo = new SimpleDate(2026, 5, 15, 23, 59);

        Volunteer vol = new Volunteer(
                "Maria",
                "Georgescu",
                "maria@mail.com",
                "0733333333",
                2,
                TShirtSize.M,
                availFrom,
                availTo
        );

        // =========================
        // 6. VOLUNTEER APPLIES
        // =========================
        vol.applyToEvent(event);

        // =========================
        // 7. DISPLAY SYSTEM STATE
        // =========================

        System.out.println("===== ADMIN =====");
        System.out.println(admin);

        System.out.println("\n===== EVENT =====");
        event.display();

        System.out.println("\n===== COORDINATOR =====");
        coord.display();

        System.out.println("\n===== VOLUNTEER =====");
        vol.display();
    }
}