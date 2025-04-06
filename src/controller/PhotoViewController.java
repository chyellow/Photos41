package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    private Label albumTitleLabel;
    @FXML
    private FlowPane photoFlowPane;
    @FXML
    private Button addPhotoButton;
    @FXML
    private Button backButton;

    private Album album;
    private UserManager userManager;
    
    // Global cache for Photo instances (keyed by file path)
    private static Map<String, Photo> photoCache = new HashMap<>();

    /**
     * Sets the album to display.
     */
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
        for (Photo photo : album.getPhotos()) {
            ImageView imageView = new ImageView();
            File imageFile = new File(photo.getFilePath());
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString(), 150, 0, true, true);
                imageView.setImage(image);
                imageView.setPreserveRatio(true);
                imageView.setFitWidth(150);
            } else {
                // Fallback to default image if file not found
                imageView.setImage(new Image(getClass().getResourceAsStream("/stock/default_album.png")));
            }
            VBox photoContainer = new VBox(imageView);
            photoContainer.setStyle("-fx-border-color: #dddddd; -fx-padding: 5;");
            photoFlowPane.getChildren().add(photoContainer);
        }
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
