package model;

import java.util.Objects;

/**
 * Represents a single item on a restaurant's menu.
 * All fields are final — menu items are immutable once created.
 * equals() and hashCode() are overridden to compare by item ID,
 * enabling correct quantity grouping in the cart via Collectors.groupingBy().
 */
public class MenuItem {
    private final int     id;
    private final int     restaurantId;
    private final String  name;
    private final double  price;
    private final boolean available;

    public MenuItem(int id, int restaurantId, String name, double price, boolean available) {
        this.id           = id;
        this.restaurantId = restaurantId;
        this.name         = name;
        this.price        = price;
        this.available    = available;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public int     getId()           { return id; }
    public int     getRestaurantId() { return restaurantId; }
    public String  getName()         { return name; }
    public double  getPrice()        { return price; }
    public boolean isAvailable()     { return available; }

    // ── Object Contract Override ──────────────────────────────────────────────

    /**
     * Two MenuItems are considered equal if their IDs match.
     * This enables Collectors.groupingBy() to group identical cart items by quantity.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuItem menuItem = (MenuItem) o;
        return id == menuItem.id;
    }

    /**
     * hashCode must be consistent with equals().
     * Objects.hash(id) ensures items with the same ID produce the same hash bucket,
     * which is required for correct behaviour in HashMap/groupingBy operations.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Returns a formatted string representation of the menu item for display.
     */
    @Override
    public String toString() {
        return String.format("%-5d | %-25s | Rs.%-7.2f", id, name, price);
    }
}