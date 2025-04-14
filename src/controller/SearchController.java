package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import model.Album;
import model.Photo;
import model.UserManager;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchController {

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TextField tagSearchField;

    @FXML
    private Button searchByDateButton;

    @FXML
    private Button searchByTagButton;

    @FXML
    private Button backButton;

    @FXML
    private FlowPane resultsFlowPane;

    private UserManager userManager;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        searchByDateButton.setOnAction(this::handleSearchByDate);
        searchByTagButton.setOnAction(this::handleSearchByTag);
        backButton.setOnAction(this::handleBack);
    }

    /**
     * Sets the user manager for accessing user data.
     */
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * Handles searching photos by date range.
     */
    private void handleSearchByDate(ActionEvent event) {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate == null || endDate == null) {
            return; // require both dates
        }

        List<Photo> matchingPhotos = new ArrayList<>();
        for (Album album : userManager.getCurrentUser().getAlbums()) {
            for (Photo photo : album.getPhotos()) {
                LocalDate photoDate = photo.getDateTime().toLocalDate();
                if ((photoDate.isEqual(startDate) || photoDate.isAfter(startDate)) &&
                    (photoDate.isEqual(endDate) || photoDate.isBefore(endDate))) {
                    if (!matchingPhotos.contains(photo)) {
                        matchingPhotos.add(photo);
                    }
                }
            }
        }

        displayResults(matchingPhotos);
    }

    /**
     * Handles searching photos by tag.
     */
    private void handleSearchByTag(ActionEvent event) {
        String query = tagSearchField.getText().trim();
        if (query.isEmpty()) {
            return;
        }

        List<Photo> matchingPhotos = new ArrayList<>();

        if (query.contains(" AND ")) {
            String[] parts = query.split(" AND ");
            if (parts.length != 2) return;
            String[] first = parts[0].split("=");
            String[] second = parts[1].split("=");

            if (first.length != 2 || second.length != 2) return;

            String tag1Type = first[0].trim();
            String tag1Value = first[1].trim();
            String tag2Type = second[0].trim();
            String tag2Value = second[1].trim();

            for (Album album : userManager.getCurrentUser().getAlbums()) {
                for (Photo photo : album.getPhotos()) {
                    Map<String, String> tags = photo.getTags();
                    if (tagMatches(tags, tag1Type, tag1Value) && tagMatches(tags, tag2Type, tag2Value)) {
                        if (!matchingPhotos.contains(photo)) {
                            matchingPhotos.add(photo);
                        }
                    }
                }
            }
        } else if (query.contains(" OR ")) {
            String[] parts = query.split(" OR ");
            if (parts.length != 2) return;
            String[] first = parts[0].split("=");
            String[] second = parts[1].split("=");

            if (first.length != 2 || second.length != 2) return;

            String tag1Type = first[0].trim();
            String tag1Value = first[1].trim();
            String tag2Type = second[0].trim();
            String tag2Value = second[1].trim();

            for (Album album : userManager.getCurrentUser().getAlbums()) {
                for (Photo photo : album.getPhotos()) {
                    Map<String, String> tags = photo.getTags();
                    if (tagMatches(tags, tag1Type, tag1Value) || tagMatches(tags, tag2Type, tag2Value)) {
                        if (!matchingPhotos.contains(photo)) {
                            matchingPhotos.add(photo);
                        }
                    }
                }
            }
        } else {
            // Single tag-value
            String[] parts = query.split("=");
            if (parts.length != 2) return;

            String tagType = parts[0].trim();
            String tagValue = parts[1].trim();

            for (Album album : userManager.getCurrentUser().getAlbums()) {
                for (Photo photo : album.getPhotos()) {
                    Map<String, String> tags = photo.getTags();
                    if (tagMatches(tags, tagType, tagValue)) {
                        if (!matchingPhotos.contains(photo)) {
                            matchingPhotos.add(photo);
                        }
                    }
                }
            }
        }

        displayResults(matchingPhotos);
    }

    /**
     * Helper: Checks if a photo's tags match a type-value pair.
     */
    private boolean tagMatches(Map<String, String> tags, String type, String value) {
        for (Map.Entry<String, String> entry : tags.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(type) && entry.getValue().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Displays the matching photos in the results pane.
     */
    private void displayResults(List<Photo> photos) {
        resultsFlowPane.getChildren().clear();

        for (Photo photo : photos) {
            PhotoTileController tileController = new PhotoTileController(photo);
            VBox photoTile = tileController.getPhotoTile();

            photoTile.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    previewPhoto(photo);
                }
            });

            resultsFlowPane.getChildren().add(photoTile);
        }
    }

    /**
     * Opens a photo preview.
     */
    private void previewPhoto(Photo photo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PhotoPreview.fxml"));
            Parent root = loader.load();

            List<Photo> singlePhotoList = new ArrayList<>();
            singlePhotoList.add(photo);

            PhotoPreviewController controller = loader.getController();
            controller.setPhotos(singlePhotoList, 0);

            Stage stage = new Stage();
            stage.setTitle("Photo Preview");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles back button to return to Albums view.
     */
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
