package exception;

/**
 * Custom checked exception thrown when a user tries to add an unavailable
 * or non-existent menu item to their cart.
 * Thrown by OrderService.addToCart() when item is null or item.isAvailable() is false.
 */
public class ItemNotAvailableException extends Exception {
    public ItemNotAvailableException(String message) {
        super(message);
    }
}