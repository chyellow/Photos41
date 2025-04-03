package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import model.Album;
import model.User;
import model.UserManager;

import java.io.IOException;
import java.util.List;

public class AlbumController {

    @FXML
    private TextField newAlbumTextField;
    @FXML
    private Button createAlbumButton;
    @FXML
    private FlowPane albumGridPane;
    @FXML
    private Label statusLabel;
    @FXML
    private Button logoutButton;

    private UserManager userManager;
    private User currentUser;
    private List<Album> userAlbums;

    public void setUser(User user) {
        System.out.println("setUser() called");
        this.currentUser = user;
        this.userAlbums = currentUser.getAlbums();
        loadAlbums();
    }

    /**
     * Sets the user manager.
     * 
     * @param userManager The user manager.
     */
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
        //refreshUserList();
    }

    @FXML
    public void initialize() {
        userAlbums = null; // Ensure it's null until setUser() is called
        createAlbumButton.setOnAction(this::handleCreateAlbum);
        logoutButton.setOnAction(this::handleLogout);
    }

    private void handleCreateAlbum(ActionEvent event) {
        String albumName = newAlbumTextField.getText().trim();
        if (albumName.isEmpty()) {
            statusLabel.setText("Album name cannot be empty.");
            return;
        }

        for (Album album : userAlbums) {
            if (album.getName().equalsIgnoreCase(albumName)) {
                statusLabel.setText("An album with this name already exists.");
                return;
            }
        }

        Album newAlbum = new Album(albumName);
        userAlbums.add(newAlbum);
        currentUser.addAlbum(newAlbum);
        

        newAlbumTextField.clear();
        statusLabel.setText("Album created: " + albumName);
        loadAlbums();
    }

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
            //showAlert("Error", "Failed to load login view.");
        }
    }

    private void loadAlbums() {
        if (userAlbums == null) {
            return; // Exit if userAlbums is not initialized
        }

        albumGridPane.getChildren().clear();
        for (Album album : userAlbums) {
            AlbumTileController tileController = new AlbumTileController(album);
            albumGridPane.getChildren().add(tileController.getAlbumTile());
        }
    }
}