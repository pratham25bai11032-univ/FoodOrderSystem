package service;

import database.InMemoryDB;
import exception.ItemNotAvailableException;
import model.MenuItem;
import model.Order;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Handles the full order lifecycle: cart management, checkout,
 * invoice generation, and order history retrieval.
 * Implements the ICartOperations interface.
 */
public class OrderService implements ICartOperations {

    private final InMemoryDB        db                = InMemoryDB.getInstance();
    private final RestaurantService restaurantService;
    private final List<MenuItem>    cart              = new ArrayList<>();
    private int nextOrderId = 10001;

    public OrderService(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    // ── ICartOperations Implementation ────────────────────────────────────────

    /**
     * Adds a menu item to the session cart by its ID.
     * Validates existence and availability before adding.
     *
     * @param itemId The ID of the item to add.
     * @throws ItemNotAvailableException if item is null or marked unavailable.
     */
    @Override
    public void addToCart(int itemId) throws ItemNotAvailableException {
        MenuItem item = restaurantService.getMenuItemById(itemId);
        if (item == null || !item.isAvailable()) {
            throw new ItemNotAvailableException(
                "Sorry! Item ID " + itemId + " is not available or does not exist.");
        }
        cart.add(item);
    }

    /**
     * Displays a grouped summary of the cart.
     * Identical items are grouped by quantity using Collectors.groupingBy(),
     * which relies on equals() and hashCode() being overridden in MenuItem.
     */
    @Override
    public void showCart() {
        if (cart.isEmpty()) {
            System.out.println("[Notice] Your cart is currently empty.");
            return;
        }
        System.out.println("\n=== YOUR SHOPPING CART ===");
        System.out.printf("%-5s | %-25s | %-10s | %-10s%n", "Qty", "Item Name", "Unit Price", "Subtotal");
        System.out.println("-----------------------------------------------------------");

        Map<MenuItem, Long> grouped = getGroupedCart();
        for (Map.Entry<MenuItem, Long> entry : grouped.entrySet()) {
            MenuItem item = entry.getKey();
            long     qty  = entry.getValue();
            System.out.printf("%-5d | %-25s | Rs.%-7.2f | Rs.%-7.2f%n",
                    qty, item.getName(), item.getPrice(), item.getPrice() * qty);
        }
        System.out.println("-----------------------------------------------------------");
        System.out.printf("SUBTOTAL: Rs.%.2f%n", calculateCartTotal());
    }

    /**
     * Finalises the cart as a confirmed order by delegating to createFinalOrder().
     * @param userId The ID of the logged-in user placing the order.
     */
    @Override
    public void placeOrder(int userId) {
        createFinalOrder(userId);
    }

    // ── Additional Public Methods ──────────────────────────────────────────────

    /**
     * Returns a read-only view of current cart items.
     * Prevents external classes from directly modifying the cart list.
     */
    public List<MenuItem> getCartItems() {
        return Collections.unmodifiableList(cart);
    }

    /**
     * Calculates and returns the total price of all items in the cart.
     * @return Sum of all item prices as a double.
     */
    public double calculateCartTotal() {
        return cart.stream().mapToDouble(MenuItem::getPrice).sum();
    }

    /**
     * Returns cart items grouped by MenuItem with their counts.
     * Used by both showCart() and the invoice display in Main.java.
     * Relies on MenuItem.equals() being overridden to group by item ID.
     */
    public Map<MenuItem, Long> getGroupedCart() {
        return cart.stream().collect(
                Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    /**
     * Creates a confirmed Order from the current cart, saves it to the
     * database, and clears the session cart.
     *
     * @param userId The ID of the user placing the order.
     * @return The newly created Order object, or null if the cart is empty.
     */
    public Order createFinalOrder(int userId) {
        if (cart.isEmpty()) return null;

        double total = calculateCartTotal();

        // Format the current date and time for the invoice
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
        String formattedDate = LocalDateTime.now().format(formatter);

        Order newOrder = new Order(nextOrderId++, userId, total, "CONFIRMED", formattedDate);

        // Save the order to the database
        db.addOrder(newOrder);

        // Clear cart ONLY after the order is saved — preserves cart on aborted checkouts
        cart.clear();

        return newOrder;
    }

    /**
     * Retrieves all past orders for a specific user from the order ledger.
     *
     * @param userId The ID of the user whose history to retrieve.
     * @return List of Order objects belonging to this user.
     */
    public List<Order> getOrderHistory(int userId) {
        List<Order> history = new ArrayList<>();
        for (Order order : db.getOrders()) {
            if (order.getUserId() == userId) {
                history.add(order);
            }
        }
        return history;
    }
}