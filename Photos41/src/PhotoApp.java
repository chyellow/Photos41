import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class PhotoApp extends Application {
    private ImageView imageView;

    @Override
    public void start(Stage primaryStage) {
        imageView = new ImageView();
        imageView.setFitWidth(400);
        imageView.setPreserveRatio(true);

        Button uploadButton = new Button("Upload Photo");
        Button removeButton = new Button("Remove Photo");
        
        uploadButton.setOnAction(e -> uploadPhoto(primaryStage));
        removeButton.setOnAction(e -> imageView.setImage(null));

        VBox layout = new VBox(10, uploadButton, removeButton, imageView);
        layout.setStyle("-fx-padding: 20px; -fx-alignment: center;");

        Scene scene = new Scene(layout, 500, 500);
        primaryStage.setTitle("Photo App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void uploadPhoto(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            imageView.setImage(image);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
