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
     * Constructor that loads the FXML and initializes the controller
     *
     * @param album The album to display
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
     * Configures the album tile with the album's data and interactivity
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

        // Add hover effects
        albumTile.setOnMouseEntered(this::handleMouseEnter);
        albumTile.setOnMouseExited(this::handleMouseExit);

        // Add click to select
        albumTile.setOnMouseClicked(event -> setSelected(!selected));
    }

    private void handleMouseEnter(MouseEvent event) {
        if (!selected) {
            albumTile.setStyle(DEFAULT_STYLE + HOVER_STYLE);
        }
    }

    private void handleMouseExit(MouseEvent event) {
        if (!selected) {
            albumTile.setStyle(DEFAULT_STYLE);
        }
    }

    /**
     * Sets whether this album tile is selected and updates its style.
     *
     * @param selected true to apply selected style; false to revert
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            albumTile.setStyle(SELECTED_STYLE);
        } else {
            albumTile.setStyle(DEFAULT_STYLE);
        }
    }

    private void setDefaultCoverImage() {
        Image defaultImage = new Image(getClass().getResourceAsStream("/stock/default_album.png"));
        if (defaultImage != null) {
            albumCoverImageView.setImage(defaultImage);
        }
    }

    public VBox getAlbumTile() {
        return albumTile;
    }

    public Album getAlbum() {
        return album;
    }

    public boolean isSelected() {
        return selected;
    }
}
