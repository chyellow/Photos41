package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
import java.util.List;
import java.util.Map;

/**
 * Controller for viewing and managing photos inside an album.
 * Allows adding, renaming, deleting, copying/moving, tagging, and previewing photos.
 */
public class PhotoViewController {

    @FXML private Button copyMovePhotoButton;
    @FXML private Button previewPhotoButton;
    @FXML private TextField renamePhotoTextField;
    @FXML private Button renamePhotoButton;
    @FXML private Button deletePhotoButton;
    @FXML private Label statusLabel;
    @FXML private Label albumTitleLabel;
    @FXML private FlowPane photoFlowPane;
    @FXML private Button addPhotoButton;
    @FXML private Button backButton;
    @FXML private TextField tagTypeField;
    @FXML private TextField tagValueField;
    @FXML private Button addTagButton;
    @FXML private Button deleteTagButton;
    @FXML private ListView<String> tagListView;

    private Album album;
    private UserManager userManager;
    private Photo selectedPhoto;
    private VBox selectedPhotoTile;

    private static Map<String, Photo> photoCache = new HashMap<>();

    /**
     * Initializes the controller by setting up button actions and disabling buttons initially.
     */
    @FXML
    public void initialize() {
        renamePhotoButton.setDisable(true);
        deletePhotoButton.setDisable(true);
        previewPhotoButton.setDisable(true);
        copyMovePhotoButton.setDisable(true);

        renamePhotoButton.setOnAction(this::handleRenamePhoto);
        deletePhotoButton.setOnAction(this::handleDeletePhoto);
        addTagButton.setOnAction(this::handleAddTag);
        deleteTagButton.setOnAction(this::handleDeleteTag);
    }

    /**
     * Sets the album to be viewed and loads its photos.
     *
     * @param album The album to display.
     */
    public void setAlbum(Album album) {
        this.album = album;
        albumTitleLabel.setText("Album: " + album.getName());
        loadPhotos();
    }

    /**
     * Sets the user manager for the session.
     *
     * @param userManager The user manager instance.
     */
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * Loads the photos of the album into the view.
     */
    private void loadPhotos() {
        photoFlowPane.getChildren().clear();
        selectedPhoto = null;
        selectedPhotoTile = null;

        for (Photo photo : album.getPhotos()) {
            PhotoTileController tileController = new PhotoTileController(photo);
            VBox photoTile = tileController.getPhotoTile();

            photoTile.setOnMouseClicked(event -> selectPhoto(photo, photoTile));
            photoFlowPane.getChildren().add(photoTile);
        }
    }

    /**
     * Handles selecting a photo.
     *
     * @param photo The selected photo.
     * @param photoTile The tile representing the selected photo.
     */
    private void selectPhoto(Photo photo, VBox photoTile) {
        if (selectedPhotoTile != null) {
            selectedPhotoTile.setStyle("-fx-border-color: #dddddd; -fx-border-radius: 5; -fx-background-color: white; " +
                                       "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1); -fx-cursor: hand;");
        }

        selectedPhoto = photo;
        selectedPhotoTile = photoTile;

        previewPhotoButton.setDisable(false);
        renamePhotoButton.setDisable(false);
        deletePhotoButton.setDisable(false);
        copyMovePhotoButton.setDisable(false);

        renamePhotoTextField.setText(photo.getCaption() == null ? "" : photo.getCaption());
        statusLabel.setText("Selected photo: " + (photo.getCaption() == null ? "No Caption" : photo.getCaption()));
        refreshTagList();
    }

    /**
     * Adds a new tag to the selected photo.
     */
    @FXML
    private void handleAddTag(ActionEvent event) {
        if (selectedPhoto == null) {
            statusLabel.setText("No photo selected.");
            return;
        }

        String tagType = tagTypeField.getText().trim();
        String tagValue = tagValueField.getText().trim();

        if (tagType.isEmpty() || tagValue.isEmpty()) {
            statusLabel.setText("Tag type and value cannot be empty.");
            return;
        }

        selectedPhoto.addTag(tagType, tagValue);
        userManager.saveUsers();
        refreshTagList();

        tagTypeField.clear();
        tagValueField.clear();
        statusLabel.setText("Tag added: " + tagType + " = " + tagValue);
    }

    /**
     * Deletes the selected tag from the selected photo.
     */
    @FXML
    private void handleDeleteTag(ActionEvent event) {
        if (selectedPhoto == null) {
            statusLabel.setText("No photo selected.");
            return;
        }

        String selectedTagEntry = tagListView.getSelectionModel().getSelectedItem();
        if (selectedTagEntry == null) {
            statusLabel.setText("No tag selected.");
            return;
        }

        int colonIndex = selectedTagEntry.indexOf(":");
        if (colonIndex == -1) {
            statusLabel.setText("Invalid tag format.");
            return;
        }

        String tagType = selectedTagEntry.substring(0, colonIndex).trim();
        selectedPhoto.removeTag(tagType);
        userManager.saveUsers();
        refreshTagList();
        statusLabel.setText("Tag deleted: " + tagType);
    }

    /**
     * Refreshes the tag list view for the selected photo.
     */
    private void refreshTagList() {
        tagListView.getItems().clear();
        if (selectedPhoto != null) {
            for (Map.Entry<String, String> entry : selectedPhoto.getTags().entrySet()) {
                tagListView.getItems().add(entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    /**
     * Previews the currently selected photo.
     */
    @FXML
    private void handlePreviewPhoto(ActionEvent event) {
        if (selectedPhoto == null) {
            statusLabel.setText("No photo selected.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PhotoPreview.fxml"));
            Parent root = loader.load();

            PhotoPreviewController previewController = loader.getController();
            List<Photo> albumPhotos = album.getPhotos();
            int startIndex = albumPhotos.indexOf(selectedPhoto);

            previewController.setPhotos(albumPhotos, startIndex);

            Stage stage = new Stage();
            stage.setTitle("Photo Preview");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles copying or moving the selected photo to another album.
     */
    @FXML
    private void handleCopyMovePhoto(ActionEvent event) {
        if (selectedPhoto == null) {
            statusLabel.setText("No photo selected.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CopyMovePhotoPopup.fxml"));
            Parent root = loader.load();

            CopyMovePhotoPopupController popupController = loader.getController();

            List<Album> targetAlbums = userManager.getCurrentUser().getAlbums().stream()
                .filter(a -> !a.equals(album))
                .filter(a -> !a.getPhotos().contains(selectedPhoto))
                .toList();

            popupController.setAlbums(targetAlbums);

            popupController.setOnConfirm((targetAlbum, isMove) -> {
                targetAlbum.addPhoto(selectedPhoto);
                if (isMove) {
                    album.removePhoto(selectedPhoto);
                    loadPhotos();
                }
                statusLabel.setText(isMove ? "Photo moved to: " + targetAlbum.getName()
                                           : "Photo copied to: " + targetAlbum.getName());
                userManager.saveUsers();
            });

            Stage stage = new Stage();
            stage.setTitle("Copy or Move Photo");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Renames the selected photo.
     */
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

        selectedPhoto.setCaption(newCaption);
        statusLabel.setText("Photo renamed to: " + newCaption);
        renamePhotoTextField.clear();
        userManager.saveUsers();
        loadPhotos();
    }

    /**
     * Deletes the selected photo from the current album.
     */
    @FXML
    private void handleDeletePhoto(ActionEvent event) {
        if (selectedPhoto == null) {
            statusLabel.setText("No photo selected.");
            return;
        }

        album.removePhoto(selectedPhoto);
        selectedPhoto = null;
        selectedPhotoTile = null;

        renamePhotoButton.setDisable(true);
        deletePhotoButton.setDisable(true);

        statusLabel.setText("Photo deleted.");
        loadPhotos();
        userManager.saveUsers();
    }

    /**
     * Finds an existing photo with the given file path.
     *
     * @param path The file path to search for.
     * @return The matching Photo, or null if not found.
     */
    private Photo findExistingPhoto(String path) {
        for (Album a : userManager.getCurrentUser().getAlbums()) {
            for (Photo p : a.getPhotos()) {
                if (p.getFilePath().equals(path)) {
                    return p;
                }
            }
        }
        return null;
    }

    /**
     * Handles adding a new photo to the current album.
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
            Photo existingPhoto = findExistingPhoto(path);
            Photo photo = (existingPhoto != null) ? existingPhoto : new Photo(path, LocalDateTime.now());

            if (!album.getPhotos().contains(photo)) {
                album.addPhoto(photo);
            }
            userManager.saveUsers();
            loadPhotos();
        }
    }

    /**
     * Handles returning to the Albums view.
     */
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Albums.fxml"));
            Scene scene = new Scene(loader.load());

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
