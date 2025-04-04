package controller;

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
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
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
    @FXML
    private Button renameAlbumButton;
    @FXML
    private Button deleteAlbumButton;

    private UserManager userManager;
    private User currentUser;
    private List<Album> userAlbums;
    private Album selectedAlbum;
    private VBox selectedAlbumTile;

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
    }

    @FXML
    public void initialize() {
        userAlbums = null; // Ensure it's null until setUser() is called
        createAlbumButton.setOnAction(this::handleCreateAlbum);
        logoutButton.setOnAction(this::handleLogout);
        renameAlbumButton.setOnAction(this::handleRenameAlbum);
        deleteAlbumButton.setOnAction(this::handleDeleteAlbum);
        
        // Disable rename and delete buttons initially
        renameAlbumButton.setDisable(true);
        deleteAlbumButton.setDisable(true);
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
        //userAlbums.add(newAlbum);
        currentUser.addAlbum(newAlbum);
        
        newAlbumTextField.clear();
        statusLabel.setText("Album created: " + albumName);
        loadAlbums();
    }

    private void handleRenameAlbum(ActionEvent event) {
        if (selectedAlbum == null) {
            statusLabel.setText("No album selected.");
            return;
        }
        
        String newName = newAlbumTextField.getText().trim();
        if (newName.isEmpty()) {
            statusLabel.setText("Album name cannot be empty.");
            return;
        }

        // Check if name already exists
        for (Album album : userAlbums) {
            if (album != selectedAlbum && album.getName().equalsIgnoreCase(newName)) {
                statusLabel.setText("An album with this name already exists.");
                return;
            }
        }
        
        selectedAlbum.setName(newName);
        statusLabel.setText("Album renamed to: " + newName);
        loadAlbums();
    }
    
    private void handleDeleteAlbum(ActionEvent event) {
        if (selectedAlbum == null) {
            statusLabel.setText("No album selected.");
            return;
        }
        
        userAlbums.remove(selectedAlbum);
        currentUser.removeAlbum(selectedAlbum);
        
        statusLabel.setText("Album deleted: " + selectedAlbum.getName());
        selectedAlbum = null;
        selectedAlbumTile = null;
        
        // Disable buttons since no album is selected
        renameAlbumButton.setDisable(true);
        deleteAlbumButton.setDisable(true);
        
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
        }
    }

    private void loadAlbums() {
        if (userAlbums == null) {
            return; // Exit if userAlbums is not initialized
        }

        albumGridPane.getChildren().clear();
        selectedAlbum = null;
        selectedAlbumTile = null;
        
        // Disable buttons since no album is selected
        renameAlbumButton.setDisable(true);
        deleteAlbumButton.setDisable(true);
        
        for (Album album : userAlbums) {
            AlbumTileController tileController = new AlbumTileController(album);
            VBox albumTile = tileController.getAlbumTile();
            
            // Set up click handling for selection
            albumTile.setOnMouseClicked(event -> {
                selectAlbum(album, albumTile);
            });
            
            albumGridPane.getChildren().add(albumTile);
        }
    }
    
    private void selectAlbum(Album album, VBox albumTile) {
        // Deselect previous selection if any
        if (selectedAlbumTile != null) {
            selectedAlbumTile.getStyleClass().remove("selected-album");
        }
        
        // Update selection
        selectedAlbum = album;
        selectedAlbumTile = albumTile;
        
        // Apply selection style
        albumTile.getStyleClass().add("selected-album");
        
        // Enable buttons
        renameAlbumButton.setDisable(false);
        deleteAlbumButton.setDisable(false);
        
        // Pre-fill text field with current album name for easy renaming
        newAlbumTextField.setText(album.getName());
        statusLabel.setText("Selected album: " + album.getName());
    }
    
    public void openAlbum(Album album) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PhotoView.fxml"));
            Parent root = loader.load();
            
            //PhotoViewController controller = loader.getController();
            //controller.setUserManager(userManager);
            //controller.setUser(currentUser);
           // controller.setAlbum(album);
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) albumGridPane.getScene().getWindow();
            stage.setTitle("Photo App - " + album.getName());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error opening album.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}