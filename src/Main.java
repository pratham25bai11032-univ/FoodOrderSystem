import exception.ItemNotAvailableException;
import exception.UserNotFoundException;
import model.MenuItem;
import model.Order;
import model.Restaurant;
import model.User;
import service.OrderService;
import service.RestaurantService;
import service.UserService;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Entry point and terminal UI for the Food Order Management System.
 * Handles all terminal I/O, input validation via Regex, menu routing,
 * and exception display. Business logic is delegated entirely to the service layer.
 */
public class Main {

    // -- ANSI Terminal Colour Codes --------------------------------------------
    private static final String RESET  = "\033[0m";
    private static final String RED    = "\033[0;31m";
    private static final String GREEN  = "\033[0;32m";
    private static final String YELLOW = "\033[0;33m";
    private static final String CYAN   = "\033[0;36m";
    private static final String BOLD   = "\033[1m";

    // -- Input Validation Regex Patterns ---------------------------------------
    // EMAIL: must match standard format - user@domain.tld
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]{2,}$");
    // NAME: letters and spaces only, minimum 3 characters
    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[A-Za-z\\s]{3,}$");

    // -- Application State -----------------------------------------------------
    private static final Scanner         sc                = new Scanner(System.in);
    private static final UserService       userService       = new UserService();
    private static final RestaurantService restaurantService = new RestaurantService();
    private static final OrderService      orderService      = new OrderService(restaurantService);
    private static       User              loggedInUser      = null; // null = no active session

    // -------------------------------------------------------------------------
    // ENTRY POINT
    // -------------------------------------------------------------------------

    public static void main(String[] args) {
        printBanner();
        // Main application loop - runs until System.exit() is called
        while (true) {
            if (loggedInUser == null) showGuestMenu();
            else showMainDashboard();
        }
    }

    // -------------------------------------------------------------------------
    // MENUS / ROUTING
    // -------------------------------------------------------------------------

    private static void showGuestMenu() {
        System.out.println("\n" + YELLOW + BOLD + "+----------------------+");
        System.out.println("|      GUEST MENU      |");
        System.out.println("+----------------------+" + RESET);
        System.out.println("  1. Register New Account");
        System.out.println("  2. Login");
        System.out.println("  3. Exit");
        System.out.print(BOLD + "\nSelect option (1-3): " + RESET);

        switch (getMenuChoice(1, 3)) {
            case 1 -> register();
            case 2 -> login();
            case 3 -> {
                System.out.println(GREEN + "\n  Shutting down safely. Goodbye!" + RESET);
                sc.close(); // Close Scanner resource before exit
                System.exit(0);
            }
        }
    }

    private static void showMainDashboard() {
        System.out.println("\n" + CYAN + BOLD + "+------------------------------+");
        System.out.println("|       MAIN DASHBOARD         |");
        System.out.println("+------------------------------+" + RESET);
        System.out.println(CYAN + "  Welcome back, " + BOLD + loggedInUser.getName() + "!" + RESET);
        System.out.println();
        System.out.println("  1. [R]  View All Restaurants");
        System.out.println("  2. [M]  Browse Restaurant Menu");
        System.out.println("  3. [+]  Add Item to Cart");
        System.out.println("  4. [C]  View My Cart");
        System.out.println("  5. [$]  Checkout & Place Order");
        System.out.println("  6. [H]  My Order History");
        System.out.println("  7. [X]  Logout");
        System.out.print(BOLD + "\nSelect option (1-7): " + RESET);

        switch (getMenuChoice(1, 7)) {
            case 1 -> viewRestaurants();
            case 2 -> viewMenu();
            case 3 -> addToCart();
            case 4 -> showCart();
            case 5 -> placeOrder();
            case 6 -> viewOrderHistory();
            case 7 -> logout();
        }
    }

    // -------------------------------------------------------------------------
    // FEATURE METHODS
    // -------------------------------------------------------------------------

    /** Registers a new user after validating all inputs via Regex. */
    private static void register() {
        System.out.println("\n" + CYAN + BOLD + "-- ACCOUNT REGISTRATION --" + RESET);
        String name     = getValidNameInput  ("  Full Name    : ");
        String email    = getValidEmailInput ("  Email        : ");
        String password = getValidStringInput("  Password (min 3 chars): ");

        if (userService.register(name, email, password)) {
            System.out.println(GREEN + "\n  [OK] Account created! You can now login." + RESET);
        } else {
            System.out.println(RED + "\n  [!!] That email is already registered." + RESET);
        }
        pause();
    }

    /** Authenticates a user and starts their session. */
    private static void login() {
        System.out.println("\n" + CYAN + BOLD + "-- USER LOGIN --" + RESET);
        String email    = getValidEmailInput ("  Email    : ");
        String password = getValidStringInput("  Password : ");

        try {
            loggedInUser = userService.login(email, password);
            System.out.println(GREEN + "\n  [OK] Login successful. Welcome, "
                    + loggedInUser.getName() + "!" + RESET);
        } catch (UserNotFoundException e) {
            System.out.println(RED + "\n  [!!] " + e.getMessage() + RESET);
        }
        pause();
    }

    /** Logs the current user out and clears the session. */
    private static void logout() {
        System.out.println(GREEN + "\n  [OK] Logged out. See you next time, "
                + loggedInUser.getName() + "!" + RESET);
        loggedInUser = null;
        pause();
    }

    /** Displays a formatted table of all available restaurants. */
    private static void viewRestaurants() {
        List<Restaurant> restaurants = restaurantService.getAllRestaurants();
        System.out.println("\n" + CYAN + BOLD + "-- AVAILABLE RESTAURANTS --" + RESET);
        System.out.printf(BOLD + "  %-5s | %-25s | %-15s%n" + RESET, "ID", "Restaurant Name", "Cuisine");
        System.out.println("  " + "-".repeat(50));
        for (Restaurant r : restaurants) {
            System.out.println("  " + r); // Uses Restaurant.toString()
        }
        System.out.println("  " + "-".repeat(50));
        pause();
    }

    /**
     * Prompts for a restaurant ID and displays its available menu.
     * Items with isAvailable=false are filtered out in RestaurantService.getMenu().
     */
    private static void viewMenu() {
        System.out.print("\n  Enter Restaurant ID: ");
        int restaurantId = getValidIntegerInput();

        List<MenuItem> menu = restaurantService.getMenu(restaurantId);
        if (menu.isEmpty()) {
            System.out.println(YELLOW + "\n  [Notice] No available items found for that restaurant ID." + RESET);
            pause();
            return;
        }

        System.out.println("\n" + CYAN + BOLD + "-- MENU --" + RESET);
        System.out.printf(BOLD + "  %-5s | %-25s | %-10s%n" + RESET, "ID", "Item Name", "Price");
        System.out.println("  " + "-".repeat(45));
        for (MenuItem item : menu) {
            System.out.println("  " + item); // Uses MenuItem.toString()
        }
        System.out.println("  " + "-".repeat(45));
        pause();
    }

    /**
     * Adds a menu item to the cart.
     * ItemNotAvailableException is caught and displayed if item is unavailable.
     */
    private static void addToCart() {
        System.out.print("\n  Enter Item ID to add to cart: ");
        int itemId = getValidIntegerInput();

        try {
            orderService.addToCart(itemId);
            System.out.println(GREEN + "  [OK] Item added to your cart!" + RESET);
        } catch (ItemNotAvailableException e) {
            System.out.println(RED + "  [!!] " + e.getMessage() + RESET);
        }
        pause();
    }

    /**
     * Displays current cart items grouped by quantity.
     */
    private static void showCart() {
        List<MenuItem> cartItems = orderService.getCartItems();
        if (cartItems.isEmpty()) {
            System.out.println(YELLOW + "\n  Your cart is empty. Browse a menu and add items first!" + RESET);
            pause();
            return;
        }

        System.out.println("\n" + CYAN + BOLD + "-- YOUR SHOPPING CART --" + RESET);
        System.out.printf(BOLD + "  %-5s | %-25s | %-12s | %-10s%n" + RESET,
                "Qty", "Item Name", "Unit Price", "Subtotal");
        System.out.println("  " + "-".repeat(60));

        Map<MenuItem, Long> groupedCart = orderService.getGroupedCart();
        for (Map.Entry<MenuItem, Long> entry : groupedCart.entrySet()) {
            MenuItem item = entry.getKey();
            long     qty  = entry.getValue();
            System.out.printf("  %-5d | %-25s | Rs.%-9.2f | Rs.%-7.2f%n",
                    qty, item.getName(), item.getPrice(), item.getPrice() * qty);
        }

        System.out.println("  " + "-".repeat(60));
        System.out.printf(BOLD + "  SUBTOTAL: Rs.%.2f%n" + RESET, orderService.calculateCartTotal());
        pause();
    }

    /**
     * Handles the checkout flow: confirms total, places the order,
     * and prints the invoice. Cart is snapshotted before createFinalOrder() clears it.
     */
    private static void placeOrder() {
        if (orderService.getCartItems().isEmpty()) {
            System.out.println(YELLOW + "\n  [Notice] Cart is empty. Add items before checking out." + RESET);
            pause();
            return;
        }

        System.out.printf("%n  Your total is " + BOLD + "Rs.%.2f" + RESET
                + ". Confirm checkout? (Y/N): ", orderService.calculateCartTotal());

        if (!getConfirmInput()) {
            System.out.println(YELLOW + "\n  Checkout cancelled. Your cart items are safe." + RESET);
            pause();
            return;
        }

        // Snapshot the cart before createFinalOrder() clears it
        Map<MenuItem, Long> invoiceSnapshot = orderService.getGroupedCart();
        Order finalOrder = orderService.createFinalOrder(loggedInUser.getId());

        System.out.println("\n" + GREEN + BOLD);
        System.out.println("  +------------------------------------------+");
        System.out.println("  |           ** DIGITAL INVOICE **          |");
        System.out.println("  +------------------------------------------+" + RESET);
        System.out.println("  Order ID  : " + BOLD + "#ORD-" + finalOrder.getId() + RESET);
        System.out.println("  Customer  : " + loggedInUser.getName());
        System.out.println("  Date/Time : " + finalOrder.getDate());
        System.out.println("  " + "-".repeat(50));

        for (Map.Entry<MenuItem, Long> entry : invoiceSnapshot.entrySet()) {
            System.out.printf("  %dx %-28s Rs.%.2f%n",
                    entry.getValue(),
                    entry.getKey().getName(),
                    entry.getKey().getPrice() * entry.getValue());
        }

        System.out.println("  " + "-".repeat(50));
        System.out.printf(BOLD + "  TOTAL PAID : Rs.%.2f%n" + RESET, finalOrder.getTotalAmount());
        System.out.println("  Status     : " + GREEN + BOLD + finalOrder.getStatus() + " " + RESET);
        System.out.println(GREEN + "  ==========================================" + RESET);
        System.out.println(CYAN + "  Thank you for your order! Enjoy your meal!" + RESET);
        pause();
    }

    /** Displays a formatted table of all past orders for the logged-in user. */
    private static void viewOrderHistory() {
        List<Order> history = orderService.getOrderHistory(loggedInUser.getId());
        if (history.isEmpty()) {
            System.out.println(YELLOW + "\n  No past orders found on your account." + RESET);
            pause();
            return;
        }

        System.out.println("\n" + CYAN + BOLD + "-- YOUR ORDER HISTORY --" + RESET);
        System.out.printf(BOLD + "  %-13s | %-13s | %-12s | %-30s%n" + RESET,
                "Order ID", "Total", "Status", "Date");
        System.out.println("  " + "-".repeat(75));
        for (Order o : history) {
            System.out.println("  " + o); // Uses Order.toString()
        }
        System.out.println("  " + "-".repeat(75));
        pause();
    }

    // -------------------------------------------------------------------------
    // UTILITY / VALIDATION METHODS
    // -------------------------------------------------------------------------

    /** Prints the application banner on startup. */
    private static void printBanner() {
        System.out.println(CYAN + BOLD);
        System.out.println("  +-------------------------------------------+");
        System.out.println("  |   *** FOOD ORDER MANAGEMENT SYSTEM ***   |");
        System.out.println("  |        VIT Bhopal - CSE2006               |");
        System.out.println("  +-------------------------------------------+");
        System.out.println(RESET);
    }

    /**
     * Waits for the user to press Enter before continuing.
     * Prevents terminal output from scrolling past before the user reads it.
     */
    private static void pause() {
        System.out.print("\n  Press Enter to continue...");
        sc.nextLine();
    }

    /**
     * Reads a Y/N confirmation from the user.
     * Loops until a valid response is given - accepts Y, YES, N, NO (case-insensitive).
     * @return true for yes, false for no.
     */
    private static boolean getConfirmInput() {
        while (true) {
            String input = sc.nextLine().trim().toUpperCase();
            if (input.equals("Y") || input.equals("YES")) return true;
            if (input.equals("N") || input.equals("NO"))  return false;
            System.out.print(RED + "  Please type Y or N: " + RESET);
        }
    }

    /**
     * Reads an integer within a given range.
     * Calls getValidIntegerInput() for type safety, then checks the range.
     * @param min Minimum accepted value (inclusive).
     * @param max Maximum accepted value (inclusive).
     * @return A valid integer within [min, max].
     */
    private static int getMenuChoice(int min, int max) {
        while (true) {
            int choice = getValidIntegerInput();
            if (choice >= min && choice <= max) return choice;
            System.out.print(RED + "  [Error] Enter a number between " + min + " and " + max + ": " + RESET);
        }
    }

    /**
     * Reads a valid integer from the terminal.
     * If text is entered instead of a number, the bad token is discarded
     * from the Scanner buffer and the prompt is retried to prevent a crash loop.
     * @return A valid integer entered by the user.
     */
    private static int getValidIntegerInput() {
        while (!sc.hasNextInt()) {
            System.out.print(RED + "  [Error] Please enter a number: " + RESET);
            sc.next(); // Discard the invalid token to unblock the scanner
        }
        int value = sc.nextInt();
        sc.nextLine(); // Consume the leftover newline character
        return value;
    }

    /**
     * Prompts for and validates an email address using Regex.
     * Pattern: must follow user@domain.tld format.
     * Loops until a valid email is entered.
     *
     * @param prompt The message to display before input.
     * @return A validated email string.
     */
    private static String getValidEmailInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String email = sc.nextLine().trim();
            if (EMAIL_PATTERN.matcher(email).matches()) return email;
            System.out.println(RED + "  [Error] Invalid email. Example: name@domain.com" + RESET);
        }
    }

    /**
     * Prompts for and validates a name using Regex.
     * Pattern: letters and spaces only, minimum 3 characters.
     * Loops until a valid name is entered.
     *
     * @param prompt The message to display before input.
     * @return A validated name string.
     */
    private static String getValidNameInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String name = sc.nextLine().trim();
            if (NAME_PATTERN.matcher(name).matches()) return name;
            System.out.println(RED + "  [Error] Name must be letters only and at least 3 characters." + RESET);
        }
    }

    /**
     * Prompts for a general string with minimum length of 3 characters.
     * Used for password input where no specific format is required,
     * only a minimum length constraint.
     *
     * @param prompt The message to display before input.
     * @return A string of at least 3 characters.
     */
    private static String getValidStringInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (input.length() >= 3) return input;
            System.out.println(RED + "  [Error] Must be at least 3 characters." + RESET);
        }
    }
}