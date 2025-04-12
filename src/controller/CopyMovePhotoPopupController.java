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

    public void initialize() {
        ToggleGroup toggleGroup = new ToggleGroup();
        copyRadioButton.setToggleGroup(toggleGroup);
        moveRadioButton.setToggleGroup(toggleGroup);

        confirmButton.setOnAction(e -> handleConfirm());
        cancelButton.setOnAction(e -> ((Stage) cancelButton.getScene().getWindow()).close());
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
        for (Album album : albums) {
            albumListView.getItems().add(album.getName());
        }
    }

    public void setOnConfirm(BiConsumer<Album, Boolean> callback) {
        this.onConfirm = callback;
    }

    private void handleConfirm() {
        int selectedIndex = albumListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < albums.size()) {
            Album selectedAlbum = albums.get(selectedIndex);
            boolean isMove = moveRadioButton.isSelected();
            if (onConfirm != null) {
                onConfirm.accept(selectedAlbum, isMove);
            }
            ((Stage) confirmButton.getScene().getWindow()).close();
        }
    }
}
