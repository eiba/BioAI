import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        // Creating a new GUI
        GUI gui = new GUI();
        Scene scene = new Scene(gui, 1400, 900);
        primaryStage.setTitle("IT3708 - Assignment 2");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
