package database;

import model.MenuItem;
import model.Order;
import model.Restaurant;
import model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simulates a centralized in-memory database using the Singleton Design Pattern.
 * The private constructor prevents external instantiation.
 * The static synchronized getInstance() guarantees one shared instance
 * across all service classes, preventing data desynchronization.
 */
public class InMemoryDB {

    private static InMemoryDB instance;

    // Private database "tables" — only accessible through controlled getter methods
    private final List<User>       users       = new ArrayList<>();
    private final List<Restaurant> restaurants = new ArrayList<>();
    private final List<MenuItem>   menuItems   = new ArrayList<>();
    private final List<Order>      orders      = new ArrayList<>();

    // Private constructor — external classes cannot call 'new InMemoryDB()'
    private InMemoryDB() {
        seedInitialData();
    }

    /**
     * Thread-safe Singleton accessor.
     * 'synchronized' ensures only one thread creates the instance,
     * preventing duplicate objects in concurrent scenarios.
     */
    public static synchronized InMemoryDB getInstance() {
        if (instance == null) {
            instance = new InMemoryDB();
        }
        return instance;
    }

    // ── Controlled Accessors (Encapsulation) ──────────────────────────────────

    /** Returns a read-only view of users to prevent external structural modification. */
    public List<User> getUsers() { return Collections.unmodifiableList(users); }

    /** Returns a read-only view of restaurants. */
    public List<Restaurant> getRestaurants() { return Collections.unmodifiableList(restaurants); }

    /** Returns a read-only view of menu items. */
    public List<MenuItem> getMenuItems() { return Collections.unmodifiableList(menuItems); }

    /** Returns a read-only view of orders. */
    public List<Order> getOrders() { return Collections.unmodifiableList(orders); }

    // ── Controlled Mutators (Write operations go through explicit methods) ────

    public void addUser(User user)         { users.add(user); }
    public void addOrder(Order order)      { orders.add(order); }

    // ── Seed Data ─────────────────────────────────────────────────────────────

    /**
     * Pre-populates the database with mock restaurants and menu items.
     * Item ID 3 (Tandoori Roti) is intentionally set to unavailable
     * to test the ItemNotAvailableException handling.
     */
    private void seedInitialData() {
        // Mock Restaurants
        restaurants.add(new Restaurant(1, "The Spice Route", "North Indian"));
        restaurants.add(new Restaurant(2, "Bhopal Bites",   "Street Food"));
        restaurants.add(new Restaurant(3, "Oven Story",     "Italian/Pizza"));

        // Restaurant 1 — The Spice Route
        menuItems.add(new MenuItem(1, 1, "Paneer Butter Masala", 250.0, true));
        menuItems.add(new MenuItem(2, 1, "Garlic Naan",          40.0,  true));
        menuItems.add(new MenuItem(3, 1, "Tandoori Roti",        20.0,  false)); // Unavailable — for testing

        // Restaurant 2 — Bhopal Bites
        menuItems.add(new MenuItem(4, 2, "Poha Jalebi Combo", 60.0, true));
        menuItems.add(new MenuItem(5, 2, "Samosa Chaat",      45.0, true));

        // Restaurant 3 — Oven Story
        menuItems.add(new MenuItem(6, 3, "Margherita Pizza",    299.0, true));
        menuItems.add(new MenuItem(7, 3, "Cheesy Garlic Bread", 129.0, true));
    }
}