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
 * Controller for the admin subsystem. This class handles user management
 * functionality such as creating, deleting, and listing users, as well as
 * logging out of the admin view.
 */
public class AdminController {

    /** ListView to display the list of users. */
    @FXML
    private ListView<String> userListView;

    /** TextField for entering a new username. */
    @FXML
    private TextField newUsernameTextField;

    /** Button to create a new user. */
    @FXML
    private Button createUserButton;

    /** Label to display the currently selected user. */
    @FXML
    private Label selectedUserLabel;

    /** Button to delete the selected user. */
    @FXML
    private Button deleteUserButton;

    /** Button to log out of the admin view. */
    @FXML
    private Button logoutButton;

    /** Label to display the status of operations. */
    @FXML
    private Label statusLabel;

    /** The UserManager instance for managing users. */
    private UserManager userManager;

    /** ObservableList to hold the list of usernames for the ListView. */
    private ObservableList<String> userList;

    /**
     * Initializes the controller. Sets up the ListView, event handlers, and
     * disables the delete button initially.
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
                deleteUserButton.setDisable(newVal.equals("admin"));
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
     * Sets the UserManager instance for this controller.
     * 
     * @param userManager The UserManager instance.
     */
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
        refreshUserList();
    }

    /**
     * Refreshes the list of users displayed in the ListView.
     */
    private void refreshUserList() {
        userList.clear();

        List<User> users = userManager.getAllUsers();
        for (User user : users) {
            userList.add(user.getUsername());
        }
    }

    /**
     * Handles the creation of a new user. Validates the input and updates the
     * user list if the user is successfully created.
     * 
     * @param event The ActionEvent triggered by the create user button.
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
     * Handles the deletion of a selected user. Validates the selection and
     * updates the user list if the user is successfully deleted.
     * 
     * @param event The ActionEvent triggered by the delete user button.
     */
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


        boolean success = userManager.deleteUser(username);
        if (success) {
            statusLabel.setText("User deleted: " + username);
            refreshUserList();
        } else {
            showAlert("Error", "Failed to delete user.");
        }
    }

    /**
     * Handles the logout action. Navigates back to the login view.
     * 
     * @param event The ActionEvent triggered by the logout button.
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
     * Displays an alert dialog with the specified title and message.
     * 
     * @param title   The title of the alert.
     * @param message The message to display in the alert.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}