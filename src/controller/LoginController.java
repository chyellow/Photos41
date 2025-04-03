package controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import model.Album;
import model.UserManager;

public class LoginController {
    
    @FXML
    private TextField usernameTextField;
    
    @FXML
    private Button loginButton;
    
    private UserManager userManager;
    
    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        userManager = new UserManager();
        
        // Set up event handler for the login button
        loginButton.setOnAction(this::handleLogin);
    }
    
    /**
     * Handles the login button click event.
     * 
     * @param event The action event.
     */
    // Inside handleLogin method in LoginController.java
private void handleLogin(ActionEvent event) {
    String username = usernameTextField.getText().trim();
    
    if (username.isEmpty()) {
        showAlert("Error", "Username cannot be empty.");
        return;
    }
    
    if (userManager.userExists(username)) {
        // User exists, proceed with login
        boolean success = userManager.login(username);
        
        if (success) {
            if (username.equals("admin")) {
                // Navigate to admin subsystem
                navigateToAdminView(event);
            } else if (username.equals("stock")) {
                // Stock user is a special case, treat as a regular user
                navigateToUserView(event);
            } else {
                // Navigate to user subsystem
                navigateToUserView(event);
            }
        } else {
            showAlert("Error", "Login failed. Please try again.");
        }
    } else if (username.equals("admin")) {
        // Special case: create admin user if it doesn't exist
        userManager.createUser("admin");
        userManager.login("admin");
        navigateToAdminView(event);
    } else {
        // User doesn't exist
        showAlert("Error", "User not found: " + username);
    }
}
    
    /**
     * Navigates to the admin view.
     * 
     * @param event The action event.
     */
    private void navigateToAdminView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Admin.fxml"));
            Parent root = loader.load();
            
            // Get the controller and pass the user manager
            AdminController controller = loader.getController();
            controller.setUserManager(userManager);
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Photo App - Admin");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load admin view.");
        }
    }
    
    /**
     * Navigates to the user view.
     * 
     * @param event The action event.
     */
    private void navigateToUserView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Albums.fxml"));
            Parent root = loader.load();
            
            AlbumController controller = loader.getController();
            controller.setUserManager(userManager);
            // Get the controller and pass the user manager
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Photo App - " + userManager.getCurrentUser().getUsername());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load user view.");
        }
    }
    
    /**
     * Shows an alert dialog.
     * 
     * @param title The alert title.
     * @param message The alert message.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}