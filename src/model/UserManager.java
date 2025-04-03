package model;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages users in the Photo App, including authentication and persistence.
 */
public class UserManager {
    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = DATA_DIR + File.separator + "users.ser";
    private static final String STOCK_FOLDER = "stock";
    
    private List<User> users;
    private User currentUser;
    
    /**
     * Creates a new UserManager and loads existing users from disk.
     */
    public UserManager() {
        users = new ArrayList<>();
        loadUsers();
        
        // Ensure admin user exists
        if (!userExists("admin")) {
            createUser("admin");
        }

        if (!userExists("stock")) {
            createUser("stock");
            initializeStockUser();
        }

    }
    
    private void initializeStockUser() {
    User stockUser = getUser("stock");
    if (stockUser != null && stockUser.getAlbums().isEmpty()) {
        Album dylanAlbum = new Album("dylan");
        stockUser.addAlbum(dylanAlbum);

        // Predefined file paths for stock photos
        String[] fileLoc = {
            "src/stock/dylan1.jpg", "src/stock/dylan2.jpg", "src/stock/dylan3.jpg",
            "src/stock/dylan4.png", "src/stock/dylan5.png"
        };

        // Add photos to the "dylan" album
        for (String filePath : fileLoc) {
            File photoFile = new File(filePath);
            if (photoFile.exists()) {
                // Create a Photo object with the file path and current date/time
                Photo photo = new Photo(photoFile.getAbsolutePath(), LocalDateTime.now());
                dylanAlbum.addPhoto(photo);
            } else {
                //System.out.println("File not found: " + filePath);
            }
        }

        // Save the updated stock user
        saveUsers();
    }
}

    /**
     * Checks if a user with the specified username exists.
     * 
     * @param username The username to check.
     * @return true if a user with the specified username exists, false otherwise.
     */
    public boolean userExists(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Creates a new user with the specified username.
     * 
     * @param username The username for the new user.
     * @return true if the user was created successfully, false otherwise.
     */
    public boolean createUser(String username) {
        if (userExists(username)) {
            return false;
        }
        
        User newUser = new User(username);
        users.add(newUser);
        saveUsers();
        return true;
    }
    
    /**
     * Deletes a user with the specified username.
     * 
     * @param username The username of the user to delete.
     * @return true if the user was deleted successfully, false otherwise.
     */
    public boolean deleteUser(String username) {
        // Don't allow deleting the admin user
        if (username.equals("admin") || username.equals("stock")) {
            return false;
        }
        
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(username)) {
                users.remove(i);
                saveUsers();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gets a user by their username.
     * 
     * @param username The username of the user to retrieve.
     * @return The user with the specified username, or null if no such user exists.
     */
    public User getUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
    
    /**
     * Gets all users.
     * 
     * @return The list of all users.
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
    
    /**
     * Logs in a user with the specified username.
     * 
     * @param username The username of the user to log in.
     * @return true if the login was successful, false otherwise.
     */
    public boolean login(String username) {
        User user = getUser(username);
        if (user != null) {
            currentUser = user;
            return true;
        }
        return false;
    }
    
    /**
     * Logs out the current user.
     */
    public void logout() {
        if (currentUser != null) {
            saveUsers(); // Save user data before logging out
            currentUser = null;
        }
    }
    
    /**
     * Gets the currently logged-in user.
     * 
     * @return The current user, or null if no user is logged in.
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Checks if the current user is the admin.
     * 
     * @return true if the current user is the admin, false otherwise.
     */
    public boolean isAdmin() {
        return currentUser != null && currentUser.getUsername().equals("admin");
    }
    
    /**
     * Loads users from disk.
     */
    @SuppressWarnings("unchecked")
    private void loadUsers() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        File file = new File(USERS_FILE);
        if (file.exists() && file.length() > 0) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                users = (List<User>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                users = new ArrayList<>();
            }
        }
    }
    
    /**
     * Saves users to disk.
     */
    private void saveUsers() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}