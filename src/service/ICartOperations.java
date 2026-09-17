package service;

import exception.ItemNotAvailableException;

/**
 * Defines the contract for cart operations.
 * Implemented by OrderService to handle adding items, displaying the cart,
 * and placing orders.
 */
public interface ICartOperations {

    /**
     * Adds a menu item to the cart by its ID.
     * @param itemId The unique ID of the menu item to add.
     * @throws ItemNotAvailableException if the item does not exist or is marked unavailable.
     */
    void addToCart(int itemId) throws ItemNotAvailableException;

    /**
     * Displays the current contents of the cart.
     * Implementation handles grouping identical items by quantity.
     */
    void showCart();

    /**
     * Finalises and places the current cart as a confirmed order.
     * @param userId The ID of the logged-in user placing the order.
     */
    void placeOrder(int userId);
}