package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import model.Photo;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Controller for the photo preview window.
 * Displays a photo, its caption, date taken, and associated tags.
 * Allows manual navigation through photos using previous and next buttons.
 */
public class PhotoPreviewController {

    @FXML
    private ImageView photoImageView;

    @FXML
    private Label captionLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private VBox tagsVBox;

    @FXML
    private Button prevButton;

    @FXML
    private Button nextButton;

    private List<Photo> photos;
    private int currentIndex = 0;

    /**
     * Initializes the controller by setting up event handlers for navigation buttons.
     */
    public void initialize() {
        prevButton.setOnAction(e -> showPreviousPhoto());
        nextButton.setOnAction(e -> showNextPhoto());
    }

    /**
     * Sets the list of photos to preview and displays the starting photo.
     *
     * @param photos The list of photos.
     * @param startIndex The index of the initial photo to display.
     */
    public void setPhotos(List<Photo> photos, int startIndex) {
        this.photos = photos;
        this.currentIndex = startIndex;
        displayPhoto(photos.get(currentIndex));
    }

    /**
     * Displays the previous photo in the list.
     * Wraps around to the last photo if at the beginning.
     */
    private void showPreviousPhoto() {
        if (photos == null || photos.isEmpty()) return;
        currentIndex = (currentIndex - 1 + photos.size()) % photos.size(); // wrap around
        displayPhoto(photos.get(currentIndex));
    }

    /**
     * Displays the next photo in the list.
     * Wraps around to the first photo if at the end.
     */
    private void showNextPhoto() {
        if (photos == null || photos.isEmpty()) return;
        currentIndex = (currentIndex + 1) % photos.size(); // wrap around
        displayPhoto(photos.get(currentIndex));
    }

    /**
     * Displays the given photo, updating the image view, caption, date, and tags.
     *
     * @param photo The photo to display.
     */
    private void displayPhoto(Photo photo) {
        if (photo == null) return;

        // Load and display the image
        File file = new File(photo.getFilePath());
        if (file.exists()) {
            Image image = new Image(file.toURI().toString());
            photoImageView.setImage(image);
        } else {
            photoImageView.setImage(null);
        }

        // Set caption
        captionLabel.setText("Caption: " + (photo.getCaption().isEmpty() ? "No caption" : photo.getCaption()));

        // Set date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        dateLabel.setText("Date Taken: " + photo.getDateTime().format(formatter));

        // Display tags
        tagsVBox.getChildren().clear();
        for (Map.Entry<String, String> entry : photo.getTags().entrySet()) {
            Label tagLabel = new Label(entry.getKey() + ": " + entry.getValue());
            tagsVBox.getChildren().add(tagLabel);
        }

        if (photo.getTags().isEmpty()) {
            Label noTags = new Label("No tags");
            tagsVBox.getChildren().add(noTags);
        }
    }
}
