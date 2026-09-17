package model;

/**
 * Represents a completed and confirmed food order.
 * All fields are final — once an order is placed, it cannot be modified.
 * toString() formats the order as a single display-ready string for order history output.
 */
public class Order {
    private final int    id;
    private final int    userId;
    private final double totalAmount;
    private final String status;
    private final String date;

    public Order(int id, int userId, double totalAmount, String status, String date) {
        this.id          = id;
        this.userId      = userId;
        this.totalAmount = totalAmount;
        this.status      = status;
        this.date        = date;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public int    getId()          { return id; }
    public int    getUserId()      { return userId; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus()      { return status; }
    public String getDate()        { return date; }

    /**
     * Returns a formatted order summary string for order history display.
     */
    @Override
    public String toString() {
        return String.format("#ORD-%-7d | Rs.%-9.2f | %-12s | %s",
                id, totalAmount, status, date);
    }
}