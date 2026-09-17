package exception;

/**
 * Custom checked exception thrown when a login attempt fails.
 * Thrown by UserService.login() when no matching email and password combination is found.
 */
public class UserNotFoundException extends Exception {
    public UserNotFoundException(String message) {
        super(message);
    }
}