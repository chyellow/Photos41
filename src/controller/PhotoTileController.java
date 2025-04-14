package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import model.Photo;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * Controller for a tile representing a photo in the Photo App.
 * Displays the photo's thumbnail, caption, and date.
 * Handles hover effects for better interactivity.
 */
public class PhotoTileController {

    @FXML
    private VBox photoTile;

    @FXML
    private ImageView photoImageView;

    @FXML
    private Label captionLabel;

    @FXML
    private Label dateLabel;

    private Photo photo;
    private boolean selected = false;

    private static final String DEFAULT_STYLE =
        "-fx-border-color: #dddddd; -fx-border-radius: 5; -fx-background-color: white; " +
        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1); -fx-cursor: hand;";

    /**
     * Constructor that loads the FXML layout and initializes the photo tile.
     *
     * @param photo The photo to display in this tile.
     */
    public PhotoTileController(Photo photo) {
        this.photo = photo;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PhotoTile.fxml"));
            loader.setController(this);
            photoTile = loader.load();
            configurePhotoTile();
        } catch (IOException e) {
            e.printStackTrace();
            photoTile = new VBox();
        }
    }

    /**
     * Configures the photo tile with the photo's image, caption, and date,
     * and sets up hover interactivity.
     */
    private void configurePhotoTile() {
        photoTile.setStyle(DEFAULT_STYLE);

        // Set the photo image
        File file = new File(photo.getFilePath());
        if (file.exists()) {
            Image img = new Image(file.toURI().toString());
            photoImageView.setImage(img);
            photoImageView.setFitWidth(190);
            photoImageView.setPreserveRatio(true);
        } else {
            Image defaultImg = new Image(getClass().getResourceAsStream("/stock/default_photo.png"));
            photoImageView.setImage(defaultImg);
        }

        // Set the caption
        captionLabel.setText(photo.getCaption() == null ? "No Caption" : photo.getCaption());

        // Set the date
        if (photo.getDateTime() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            dateLabel.setText(photo.getDateTime().format(formatter));
        } else {
            dateLabel.setText("Unknown Date");
        }

        // Hover effects
        photoTile.setOnMouseEntered(this::handleMouseEnter);
        photoTile.setOnMouseExited(this::handleMouseExit);
        // Selection on click can be added if needed
    }

    /**
     * Handles mouse entering the tile area to apply hover style.
     *
     * @param event The MouseEvent triggering this action.
     */
    private void handleMouseEnter(MouseEvent event) {
        if (!selected) {
            photoTile.setStyle(DEFAULT_STYLE + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);");
        }
    }

    /**
     * Handles mouse exiting the tile area to remove hover style.
     *
     * @param event The MouseEvent triggering this action.
     */
    private void handleMouseExit(MouseEvent event) {
        if (!selected) {
            photoTile.setStyle(DEFAULT_STYLE);
        }
    }

    /**
     * Returns the VBox node representing this photo tile.
     *
     * @return The VBox of the photo tile.
     */
    public VBox getPhotoTile() {
        return photoTile;
    }

    /**
     * Returns the Photo associated with this tile.
     *
     * @return The Photo object.
     */
    public Photo getPhoto() {
        return photo;
    }

    /**
     * Returns whether this tile is currently selected.
     *
     * @return True if selected, false otherwise.
     */
    public boolean isSelected() {
        return selected;
    }
}
