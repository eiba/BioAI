import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

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

        //Execute the evolutionary algorithm
        EvolutionaryAlgorithm e = new EvolutionaryAlgorithm("./TestData/p01",100);

        //Get all the data from the data set
        ProcessFile f = e.processFile;

        //Print one of the solutions found
        //new PrintSolution().Print(e.proposedSolutions[0]);

        //Create a new graph
        Graph graph = new Graph(700, 500, f.minX, f.minY, f.maxX, f.maxY);
        BorderPane.setAlignment(graph, Pos.CENTER);
        graph.setDepots(f.depots);
        graph.setCustomers(f.customers);
        borderPane.setCenter(graph);

        graph.setRoutes(e.proposedSolutions[0]);

        primaryStage.getIcons().add(new Image("file:elster2.png"));
        primaryStage.show();
    }
}
