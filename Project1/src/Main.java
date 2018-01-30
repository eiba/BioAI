import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.text.DecimalFormat;

public class Main extends Application{

    private Stage primaryStage;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("IT3708 - Assignment 1");

        //Read data from file
        ProcessFile f = new ProcessFile("./TestData/p12");
        //Create a new graph
        Graph graph = new Graph(700, 500, );

        //System.out.println(f.depots[1].getMaximum_duration());
        //System.out.println(f.vehicles[13].getX());
        PopulationGenerator g = new PopulationGenerator(f,1);
        PrintSolution p = new PrintSolution();
        p.Print(g.speciemens[0]);
        //System.out.println("------------------------------");
        //p.Print(g.speciemens[1]);

        primaryStage.show();
    }
}
