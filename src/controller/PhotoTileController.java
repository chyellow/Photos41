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

        // Hover and select styles
        photoTile.setOnMouseEntered(this::handleMouseEnter);
        photoTile.setOnMouseExited(this::handleMouseExit);
        //photoTile.setOnMouseClicked(event -> setSelected(!selected));
    }

    private void handleMouseEnter(MouseEvent event) {
        if (!selected) {
            photoTile.setStyle(DEFAULT_STYLE + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);");
        }
    }

    private void handleMouseExit(MouseEvent event) {
        if (!selected) {
            photoTile.setStyle(DEFAULT_STYLE);
        }
    }

   

    public VBox getPhotoTile() {
        return photoTile;
    }

    public Photo getPhoto() {
        return photo;
    }

    public boolean isSelected() {
        return selected;
    }
}
