package service;

import database.InMemoryDB;
import exception.UserNotFoundException;
import model.User;

import java.util.Base64;

/**
 * Handles user account operations: registration and authentication.
 * Passwords are Base64-encoded before storage and compared on login.
 * Throws UserNotFoundException when credentials do not match.
 */
public class UserService {

    private final InMemoryDB db = InMemoryDB.getInstance();
    private int nextUserId = 1;

    public UserService() {
        // Pre-register a default admin account for testing purposes
        register("System Admin", "admin@vit.edu", "admin123");
    }

    /**
     * Registers a new user if the email is not already taken.
     * @param name     Full name of the user (validated by Regex in Main.java)
     * @param email    Email address — must be unique (case-insensitive)
     * @param password Plain-text password — Base64 encoded before storage
     * @return true if registration succeeded; false if email already exists
     */
    public boolean register(String name, String email, String password) {
        // Check for duplicate email (case-insensitive comparison)
        for (User u : db.getUsers()) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return false; // Email already registered
            }
        }

        // Encode password before storing
        String encodedPassword = Base64.getEncoder().encodeToString(password.getBytes());
        db.addUser(new User(nextUserId++, name, email, encodedPassword));
        return true;
    }

    /**
     * Authenticates a user by email and password.
     * @param email    The email entered by the user
     * @param password The plain-text password entered by the user
     * @return The authenticated User object
     * @throws UserNotFoundException if no matching email+password combination is found
     */
    public User login(String email, String password) throws UserNotFoundException {
        // Encode the input password the same way it was stored
        String encodedInput = Base64.getEncoder().encodeToString(password.getBytes());

        for (User user : db.getUsers()) {
            if (user.getEmail().equalsIgnoreCase(email)
                    && user.getPasswordEncoded().equals(encodedInput)) {
                return user; // Credentials match — login successful
            }
        }

        // No match found — throw custom exception
        throw new UserNotFoundException("Invalid email or password. Please try again.");
    }
}