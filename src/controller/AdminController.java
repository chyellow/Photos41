package controller;

import java.io.IOException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;
import model.UserManager;

/**
 * Controller for the admin subsystem.
 */
public class AdminController {
    
    @FXML
    private ListView<String> userListView;
    
    @FXML
    private TextField newUsernameTextField;
    
    @FXML
    private Button createUserButton;
    
    @FXML
    private Label selectedUserLabel;
    
    @FXML
    private Button deleteUserButton;
    
    @FXML
    private Button logoutButton;
    
    @FXML
    private Label statusLabel;
    
    private UserManager userManager;
    private ObservableList<String> userList;
    
    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        // Initialize the observable list for users
        userList = FXCollections.observableArrayList();
        userListView.setItems(userList);
        
        // Set up listeners
        userListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedUserLabel.setText(newVal);
                deleteUserButton.setDisable(newVal.equals("admin") || newVal.equals("stock"));
            } else {
                selectedUserLabel.setText("No user selected");
                deleteUserButton.setDisable(true);
            }
        });
        
        // Set up event handlers
        createUserButton.setOnAction(this::handleCreateUser);
        deleteUserButton.setOnAction(this::handleDeleteUser);
        logoutButton.setOnAction(this::handleLogout);
        
        // Disable delete button initially
        deleteUserButton.setDisable(true);
    }
    
    /**
     * Sets the user manager.
     * 
     * @param userManager The user manager.
     */
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
        refreshUserList();
    }
    
    /**
     * Refreshes the list of users.
     */
    private void refreshUserList() {
        userList.clear();
        
        List<User> users = userManager.getAllUsers();
        for (User user : users) {
            userList.add(user.getUsername());
        }
    }
    
    /**
     * Handles the create user button click event.
     * 
     * @param event The action event.
     */
    private void handleCreateUser(ActionEvent event) {
        String username = newUsernameTextField.getText().trim();
        
        if (username.isEmpty()) {
            showAlert("Error", "Username cannot be empty.");
            return;
        }
        
        if (userManager.userExists(username)) {
            showAlert("Error", "User already exists: " + username);
            return;
        }
        
        boolean success = userManager.createUser(username);
        if (success) {
            statusLabel.setText("User created: " + username);
            newUsernameTextField.clear();
            refreshUserList();
        } else {
            showAlert("Error", "Failed to create user.");
        }
    }
    
    /**
     * Handles the delete user button click event.
     * 
     * @param event The action event.
     */
    // Inside handleDeleteUser method in AdminController.java
    private void handleDeleteUser(ActionEvent event) {
        String username = userListView.getSelectionModel().getSelectedItem();
        
        if (username == null || username.isEmpty()) {
            showAlert("Error", "No user selected.");
            return;
        }
        
        if (username.equals("admin")) {
            showAlert("Error", "Cannot delete the admin user.");
            return;
        }
        
        if (username.equals("stock")) {
            showAlert("Error", "Cannot delete the stock user.");
            return;
        }
        
        boolean success = userManager.deleteUser(username);
        if (success) {
            statusLabel.setText("User deleted: " + username);
            refreshUserList();
        } else {
            showAlert("Error", "Failed to delete user.");
        }
    }
    
    /**
     * Handles the logout button click event.
     * 
     * @param event The action event.
     */
    private void handleLogout(ActionEvent event) {
        userManager.logout();
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Photo App - Login");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load login view.");
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