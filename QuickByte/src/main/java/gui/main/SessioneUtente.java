package gui.main;

public class SessioneUtente {
    private static String email;

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        SessioneUtente.email = email;
    }
}
