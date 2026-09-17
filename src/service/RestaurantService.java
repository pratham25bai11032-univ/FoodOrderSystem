package service;

import database.InMemoryDB;
import model.MenuItem;
import model.Restaurant;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles restaurant and menu catalogue operations.
 * Provides methods to retrieve all restaurants, filter menus by availability,
 * and look up individual menu items by ID.
 */
public class RestaurantService {

    private final InMemoryDB db = InMemoryDB.getInstance();

    /**
     * Returns all restaurants in the system.
     * @return Unmodifiable list of all Restaurant objects.
     */
    public List<Restaurant> getAllRestaurants() {
        return db.getRestaurants();
    }

    /**
     * Returns only the AVAILABLE menu items for a given restaurant.
     * Items where isAvailable() == false are intentionally excluded
     * so users can never see or order out-of-stock items from this view.
     *
     * @param restaurantId The unique ID of the restaurant.
     * @return Filtered list of available MenuItem objects for that restaurant.
     */
    public List<MenuItem> getMenu(int restaurantId) {
        List<MenuItem> specificMenu = new ArrayList<>();
        for (MenuItem item : db.getMenuItems()) {
            if (item.getRestaurantId() == restaurantId && item.isAvailable()) {
                specificMenu.add(item);
            }
        }
        return specificMenu;
    }

    /**
     * Finds a single menu item by its ID regardless of availability.
     * Availability is checked separately in OrderService.addToCart()
     * so that a clear ItemNotAvailableException can be thrown.
     *
     * @param itemId The unique ID of the menu item.
     * @return The matching MenuItem, or null if not found.
     */
    public MenuItem getMenuItemById(int itemId) {
        for (MenuItem item : db.getMenuItems()) {
            if (item.getId() == itemId) {
                return item;
            }
        }
        return null; // Not found — caller handles the null check
    }
}