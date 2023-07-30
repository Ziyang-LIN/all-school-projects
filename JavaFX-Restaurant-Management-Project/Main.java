import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.nio.file.*;

public class Main extends Application {

    public static void main(String[] args) throws IOException{
        Path path = Paths.get("phase2/src/txt files/log.txt");
        try {
            Files.createFile(path);
        } catch (IOException ignored){}


        Path path2 = Paths.get("phase2/src/txt files/Requests.txt");
        try {
            Files.createFile(path2);
        } catch (IOException ignored){}


        launch(args);
    }

    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/Welcome.fxml"));
            primaryStage.setTitle("My Application");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}