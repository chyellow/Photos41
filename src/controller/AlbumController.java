package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Album;
import model.User;
import model.UserManager;

import java.io.IOException;
import java.util.List;

/**
 * Controller class for managing albums in the Photo App.
 * Allows users to create, rename, delete, search, and select albums.
 * Supports navigation to login and photo viewing screens.
 */
public class AlbumController {

    @FXML
    private TextField newAlbumTextField;
    @FXML
    private TextField renameAlbumTextField;
    @FXML
    private Button createAlbumButton;
    @FXML
    private FlowPane albumGridPane;
    @FXML
    private Label statusLabel;
    @FXML
    private Button logoutButton;
    @FXML
    private Button renameAlbumButton;
    @FXML
    private Button deleteAlbumButton;
    @FXML
    private Button searchButton;

    private UserManager userManager;
    private User currentUser;
    private List<Album> userAlbums;
    private Album selectedAlbum;
    private VBox selectedAlbumTile;

    /**
     * Sets the current user and loads their albums.
     * 
     * @param user The user to set as the current user.
     */
    public void setUser(User user) {
        this.currentUser = user;
        this.userAlbums = currentUser.getAlbums();
        loadAlbums();
    }

    /**
     * Sets the UserManager instance.
     *
     * @param userManager The UserManager to use.
     */
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * Initializes the controller.
     * Sets up event handlers for buttons and disables certain buttons initially.
     */
    @FXML
    public void initialize() {
        userAlbums = null;
        createAlbumButton.setOnAction(this::handleCreateAlbum);
        logoutButton.setOnAction(this::handleLogout);
        renameAlbumButton.setOnAction(this::handleRenameAlbum);
        deleteAlbumButton.setOnAction(this::handleDeleteAlbum);
        searchButton.setOnAction(this::handleSearch);

        renameAlbumButton.setDisable(true);
        deleteAlbumButton.setDisable(true);
    }

    /**
     * Handles creating a new album.
     *
     * @param event The ActionEvent triggering this handler.
     */
    private void handleCreateAlbum(ActionEvent event) {
        String albumName = newAlbumTextField.getText().trim();
        if (albumName.isEmpty()) {
            statusLabel.setText("Album name cannot be empty.");
            return;
        }

        for (Album album : userAlbums) {
            if (album.getName().equalsIgnoreCase(albumName)) {
                statusLabel.setText("An album with this name already exists.");
                return;
            }
        }

        Album newAlbum = new Album(albumName);
        currentUser.addAlbum(newAlbum);

        newAlbumTextField.clear();
        statusLabel.setText("Album created: " + albumName);
        loadAlbums();
        userManager.saveUsers();
    }

    /**
     * Handles renaming the selected album.
     *
     * @param event The ActionEvent triggering this handler.
     */
    private void handleRenameAlbum(ActionEvent event) {
        if (selectedAlbum == null) {
            statusLabel.setText("No album selected.");
            return;
        }

        String newName = renameAlbumTextField.getText().trim();
        if (newName.isEmpty()) {
            statusLabel.setText("Album name cannot be empty.");
            return;
        }

        for (Album album : userAlbums) {
            if (album != selectedAlbum && album.getName().equalsIgnoreCase(newName)) {
                statusLabel.setText("An album with this name already exists.");
                return;
            }
        }

        selectedAlbum.setName(newName);
        statusLabel.setText("Album renamed to: " + newName);
        renameAlbumTextField.clear();
        loadAlbums();
        userManager.saveUsers();
    }

    /**
     * Handles deleting the selected album.
     *
     * @param event The ActionEvent triggering this handler.
     */
    private void handleDeleteAlbum(ActionEvent event) {
        if (selectedAlbum == null) {
            statusLabel.setText("No album selected.");
            return;
        }

        userAlbums.remove(selectedAlbum);
        currentUser.removeAlbum(selectedAlbum);

        statusLabel.setText("Album deleted: " + selectedAlbum.getName());
        selectedAlbum = null;
        selectedAlbumTile = null;

        renameAlbumButton.setDisable(true);
        deleteAlbumButton.setDisable(true);

        loadAlbums();
        userManager.saveUsers();
    }

    /**
     * Handles navigating to the Search screen.
     *
     * @param event The ActionEvent triggering this handler.
     */
    private void handleSearch(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Search.fxml"));
            Parent root = loader.load();

            SearchController searchController = loader.getController();
            searchController.setUserManager(userManager);

            Stage stage = (Stage) searchButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Photo App - Search");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles logging out the current user and returning to the Login screen.
     *
     * @param event The ActionEvent triggering this handler.
     */
    private void handleLogout(ActionEvent event) {
        userManager.logout();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Photo App - Login");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads and displays the albums for the current user.
     */
    private void loadAlbums() {
        if (userAlbums == null) {
            return;
        }

        albumGridPane.getChildren().clear();
        selectedAlbum = null;
        selectedAlbumTile = null;

        renameAlbumButton.setDisable(true);
        deleteAlbumButton.setDisable(true);

        for (Album album : userAlbums) {
            AlbumTileController tileController = new AlbumTileController(album);
            VBox albumTile = tileController.getAlbumTile();

            albumTile.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    openAlbum(album);
                } else {
                    selectAlbum(album, albumTile);
                }
            });

            albumGridPane.getChildren().add(albumTile);
        }
    }

    /**
     * Selects the specified album and highlights it in the UI.
     *
     * @param album The album to select.
     * @param albumTile The tile associated with the album.
     */
    private void selectAlbum(Album album, VBox albumTile) {
        selectedAlbum = album;
        selectedAlbumTile = albumTile;

        renameAlbumButton.setDisable(false);
        deleteAlbumButton.setDisable(false);

        renameAlbumTextField.setText(album.getName());
        statusLabel.setText("Selected album: " + album.getName());
    }

    /**
     * Opens the PhotoView screen for the specified album.
     *
     * @param album The album to open.
     */
    public void openAlbum(Album album) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PhotoView.fxml"));
            Parent root = loader.load();

            PhotoViewController controller = loader.getController();
            controller.setUserManager(userManager);
            controller.setAlbum(album);

            Scene scene = new Scene(root);
            Stage stage = (Stage) albumGridPane.getScene().getWindow();
            stage.setTitle("Photo App - " + album.getName());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error opening album.");
        }
    }
}
