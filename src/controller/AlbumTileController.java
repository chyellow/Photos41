package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import model.Album;

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

    public AlbumTileController(Album album) {
        this.album = album;
    }

    // Initializes the album tile with data
    @FXML
    public void initialize() {
        albumNameLabel.setText(album.getName());
        photoCountLabel.setText("Photos: " + album.getPhotoCount());
        
        // Set date range
        if (album.getEarliestDate() != null && album.getLatestDate() != null) {
            dateRangeLabel.setText("Date range: " + album.getEarliestDate() + " to " + album.getLatestDate());
        } else {
            dateRangeLabel.setText("Date range: N/A");
        }

        // Optionally set an album cover (if you have one, this could be dynamic)
        albumCoverImageView.setImage(null); // Set an actual image here based on your data
    }

    public VBox getAlbumTile() {
        return albumTile;
    }
}
