package model;

/**
 * Represents a registered user of the Food Order Management System.
 * Extends the abstract Person class, inheriting id and name.
 * Passwords are stored as Base64-encoded strings (academic simulation).
 */
public class User extends Person {
    private final String email;
    private final String passwordEncoded; // Base64 encoding — simulates password hashing for this academic project

    public User(int id, String name, String email, String passwordEncoded) {
        super(id, name); // Calls the Person constructor via 'super'
        this.email           = email;
        this.passwordEncoded = passwordEncoded;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public String getEmail()           { return email; }
    public String getPasswordEncoded() { return passwordEncoded; }

    /**
     * Returns a formatted string with the user's ID, name, and email.
     * Implements the abstract getDetails() method from Person.
     */
    @Override
    public String getDetails() {
        return "User ID: " + id + " | Name: " + name + " | Email: " + email;
    }
}