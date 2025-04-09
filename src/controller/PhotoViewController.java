package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Album;
import model.Photo;
import model.UserManager;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class PhotoViewController {

    @FXML
    private TextField renamePhotoTextField;
    @FXML
    private Button renamePhotoButton;
    @FXML
    private Button deletePhotoButton;
    @FXML
    private Label statusLabel;
    @FXML
    private Label albumTitleLabel;
    @FXML
    private FlowPane photoFlowPane;
    @FXML
    private Button addPhotoButton;
    @FXML
    private Button backButton;

    private Album album;
    private UserManager userManager;
    private Photo selectedPhoto;            // Selected Photo
    private VBox selectedPhotoTile;          // The actual tile (VBox) selected

    // Global cache for Photo instances (keyed by file path)
    private static Map<String, Photo> photoCache = new HashMap<>();

    /**
     * Sets the album to display.
     */

     @FXML
     public void initialize() {
         renamePhotoButton.setDisable(true);
         deletePhotoButton.setDisable(true);
         renamePhotoButton.setOnAction(this::handleRenamePhoto);
         deletePhotoButton.setOnAction(this::handleDeletePhoto);
     }
     
    public void setAlbum(Album album) {
        this.album = album;
        albumTitleLabel.setText("Album: " + album.getName());
        loadPhotos();
    }

    /**
     * Sets the user manager.
     */
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * Loads and displays the photos from the album.
     */
    private void loadPhotos() {
        photoFlowPane.getChildren().clear();
        selectedPhoto = null;
        selectedPhotoTile = null;
        
        for (Photo photo : album.getPhotos()) {
            PhotoTileController tileController = new PhotoTileController(photo);
            VBox photoTile = tileController.getPhotoTile();
            
            // Set up click handling for selection
            photoTile.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    // Double-click behavior (optional, e.g., view photo full-size)
                } else {
                    // Single-click to select
                    selectPhoto(photo, photoTile);
                }
            });
    
            photoFlowPane.getChildren().add(photoTile);
        }
    }
    
    private void selectPhoto(Photo photo, VBox photoTile) {
        if (selectedPhotoTile != null) {
            selectedPhotoTile.setStyle("-fx-border-color: #dddddd; -fx-border-radius: 5; -fx-background-color: white; " +
                                       "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1); -fx-cursor: hand;");
        }
    
        selectedPhoto = photo;
        selectedPhotoTile = photoTile;
    
    
        // Enable rename and delete buttons
        renamePhotoButton.setDisable(false);
        deletePhotoButton.setDisable(false);
    
        // Fill the rename text field
        renamePhotoTextField.setText(photo.getCaption() == null ? "" : photo.getCaption());
    
        // Update status
        statusLabel.setText("Selected photo: " + (photo.getCaption() == null ? "No Caption" : photo.getCaption()));
    }
    
    
    
    
    @FXML
    private void handleRenamePhoto(ActionEvent event) {
        if (selectedPhoto == null) {
            statusLabel.setText("No photo selected.");
            return;
        }

        String newCaption = renamePhotoTextField.getText().trim();
        if (newCaption.isEmpty()) {
            statusLabel.setText("Caption cannot be empty.");
            return;
        }

        // Update the photo's caption
        selectedPhoto.setCaption(newCaption);

        statusLabel.setText("Photo renamed to: " + newCaption);
        renamePhotoTextField.clear(); // Clear the input field
        userManager.saveUsers(); // Save the updated caption
        loadPhotos(); // Refresh photo tiles
    }

    @FXML
    private void handleDeletePhoto(ActionEvent event) {
        if (selectedPhoto == null) {
            statusLabel.setText("No photo selected.");
            return;
        }

        album.removePhoto(selectedPhoto);  // Remove from album
        
        statusLabel.setText("Photo deleted.");
        selectedPhoto = null;
        selectedPhotoTile = null;

        // Disable buttons since no photo is selected
        renamePhotoButton.setDisable(true);
        deletePhotoButton.setDisable(true);

        loadPhotos();        // Refresh photo tiles
        userManager.saveUsers(); // Save changes
    }

    /**
     * Handles the Add Photo button click.
     * Allows the user to select an image file and adds the photo to the album,
     * reusing an existing Photo instance if one already exists for the selected file.
     */
    @FXML
    private void handleAddPhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        Stage stage = (Stage) addPhotoButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            String path = selectedFile.getAbsolutePath();
            Photo photo = photoCache.get(path);
            if (photo == null) {
                photo = new Photo(path, LocalDateTime.now());
                photoCache.put(path, photo);
            }
            // Check if the photo is already in the album before adding
            if (!album.getPhotos().contains(photo)) {
                album.addPhoto(photo);
            }
            userManager.saveUsers(); // Save changes
            loadPhotos(); // Refresh the photo view
        }
    }

    /**
     * Handles the Back button click.
     * Navigates back to the Albums view.
     */
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Albums.fxml"));
            Scene scene = new Scene(loader.load());
            
            // Reinitialize the AlbumController with the current user and userManager
            AlbumController albumController = loader.getController();
            albumController.setUserManager(userManager);
            albumController.setUser(userManager.getCurrentUser());
            
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Photo App - " + userManager.getCurrentUser().getUsername());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
