package model;

/**
 * Represents a restaurant entity in the system.
 * All fields are final — a restaurant's identity does not change at runtime.
 */
public class Restaurant {
    private final int    id;
    private final String name;
    private final String cuisine;

    public Restaurant(int id, String name, String cuisine) {
        this.id      = id;
        this.name    = name;
        this.cuisine = cuisine;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public int    getId()      { return id; }
    public String getName()    { return name; }
    public String getCuisine() { return cuisine; }

    /**
     * Returns a formatted string representation of the restaurant for display.
     */
    @Override
    public String toString() {
        return String.format("%-5d | %-25s | %-15s", id, name, cuisine);
    }
}