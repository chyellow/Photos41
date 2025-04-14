package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import model.Album;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Controller for the Copy/Move Photo popup window.
 * Allows users to select an album and choose whether to copy or move a photo.
 */
public class CopyMovePhotoPopupController {

    @FXML
    private ListView<String> albumListView;

    @FXML
    private Button confirmButton;

    @FXML
    private Button cancelButton;

    @FXML
    private RadioButton copyRadioButton;

    @FXML
    private RadioButton moveRadioButton;

    private List<Album> albums;
    private BiConsumer<Album, Boolean> onConfirm; // (targetAlbum, isMove)

    /**
     * Initializes the popup controller.
     * Sets up the toggle group and event handlers for buttons.
     */
    public void initialize() {
        ToggleGroup toggleGroup = new ToggleGroup();
        copyRadioButton.setToggleGroup(toggleGroup);
        moveRadioButton.setToggleGroup(toggleGroup);

        confirmButton.setOnAction(e -> handleConfirm());
        cancelButton.setOnAction(e -> ((Stage) cancelButton.getScene().getWindow()).close());
    }

    /**
     * Sets the list of albums to display in the ListView.
     *
     * @param albums The list of albums available for selection.
     */
    public void setAlbums(List<Album> albums) {
        this.albums = albums;
        for (Album album : albums) {
            albumListView.getItems().add(album.getName());
        }
    }

    /**
     * Sets the callback to be invoked when the user confirms the action.
     *
     * @param callback A BiConsumer that takes (Album selectedAlbum, boolean isMove).
     */
    public void setOnConfirm(BiConsumer<Album, Boolean> callback) {
        this.onConfirm = callback;
    }

    /**
     * Handles the confirm button click.
     * Determines the selected album and whether the user chose copy or move,
     * and invokes the callback with the chosen options.
     */
    private void handleConfirm() {
        int selectedIndex = albumListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < albums.size()) {
            Album selectedAlbum = albums.get(selectedIndex);
            boolean isMove = moveRadioButton.isSelected();
            if (onConfirm != null) {
                onConfirm.accept(selectedAlbum, isMove);
            } else {
                confirmButton.setDisable(true);
            }
            ((Stage) confirmButton.getScene().getWindow()).close();
        }
    }
}
