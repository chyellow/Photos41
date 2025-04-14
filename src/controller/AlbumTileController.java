package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;
import model.Album;
import model.Photo;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * Controller for a tile representing an album in the Photo App.
 * Displays the album name, photo count, date range, and a cover photo.
 * Handles selection and hover effects for the album tile.
 */
public class AlbumTileController {

    @FXML
    private VBox albumTile;

    @FXML
    private ImageView albumCoverImageView;

    @FXML
    private Label albumNameLabel;

    @FXML
    private Label photoCountLabel;

    @FXML
    private Label dateRangeLabel;

    private Album album;

    private boolean selected = false;

    private static final String DEFAULT_STYLE =
        "-fx-border-color: #dddddd; -fx-border-radius: 5; -fx-background-color: white; " +
        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1); -fx-cursor: hand;";

    private static final String HOVER_STYLE =
        "-fx-background-color: #f5f5f5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);";

    private static final String SELECTED_STYLE =
        "-fx-border-color: #cc45ff; -fx-border-width: 2; -fx-background-color: #f9eaff;";

    /**
     * Constructor that loads the FXML layout and initializes the album tile.
     *
     * @param album The album to display.
     */
    public AlbumTileController(Album album) {
        this.album = album;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AlbumTile.fxml"));
            loader.setController(this);
            albumTile = loader.load();
            configureAlbumTile();
        } catch (IOException e) {
            e.printStackTrace();
            albumTile = new VBox();
            albumTile.setPrefSize(200, 200);
        }
    }

    /**
     * Configures the album tile UI with album details and sets up interactivity.
     */
    private void configureAlbumTile() {
        albumTile.setStyle(DEFAULT_STYLE);

        albumNameLabel.setText(album.getName());

        int photoCount = album.getPhotoCount();
        photoCountLabel.setText("Photos: " + photoCount);

        if (photoCount > 0 && album.getEarliestDate() != null && album.getLatestDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            String dateRange = album.getEarliestDate().format(formatter) + " - " +
                               album.getLatestDate().format(formatter);
            dateRangeLabel.setText("Date range: " + dateRange);
        } else {
            dateRangeLabel.setText("Date range: N/A");
        }

        Photo coverPhoto = album.getCoverPhoto();
        if (coverPhoto != null) {
            File imageFile = new File(coverPhoto.getFilePath());
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                albumCoverImageView.setImage(image);
                albumCoverImageView.setPreserveRatio(true);
                albumCoverImageView.setFitWidth(190);
            } else {
                setDefaultCoverImage();
            }
        } else {
            setDefaultCoverImage();
        }

        albumTile.setOnMouseEntered(this::handleMouseEnter);
        albumTile.setOnMouseExited(this::handleMouseExit);
        albumTile.setOnMouseClicked(event -> setSelected(!selected));
    }

    /**
     * Handles mouse enter event to apply hover style.
     *
     * @param event The MouseEvent triggering this action.
     */
    private void handleMouseEnter(MouseEvent event) {
        if (!selected) {
            albumTile.setStyle(DEFAULT_STYLE + HOVER_STYLE);
        }
    }

    /**
     * Handles mouse exit event to remove hover style.
     *
     * @param event The MouseEvent triggering this action.
     */
    private void handleMouseExit(MouseEvent event) {
        if (!selected) {
            albumTile.setStyle(DEFAULT_STYLE);
        }
    }

    /**
     * Sets whether this album tile is selected and updates its visual style.
     *
     * @param selected True to mark as selected, false to deselect.
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            albumTile.setStyle(SELECTED_STYLE);
        } else {
            albumTile.setStyle(DEFAULT_STYLE);
        }
    }

    /**
     * Sets a default cover image if the album does not have a cover photo.
     */
    private void setDefaultCoverImage() {
        Image defaultImage = new Image(getClass().getResourceAsStream("/stock/default_album.png"));
        if (defaultImage != null) {
            albumCoverImageView.setImage(defaultImage);
        }
    }

    /**
     * Returns the VBox representing this album tile.
     *
     * @return The VBox node of the album tile.
     */
    public VBox getAlbumTile() {
        return albumTile;
    }

    /**
     * Returns the album associated with this tile.
     *
     * @return The Album object.
     */
    public Album getAlbum() {
        return album;
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
