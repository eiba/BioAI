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

        EvolutionaryAlgorithm evolutionaryAlgorithm = new EvolutionaryAlgorithm("./TestData/p01",100);

        //Get all the data from the data set
        ProcessFile processFile = evolutionaryAlgorithm.processFile;

        //Print one of the solutions found
        //new PrintSolution().Print(e.proposedSolutions[0]);

        //Create a new graph
        Graph graph = new Graph(700, 500, processFile.minX, processFile.minY, processFile.maxX, processFile.maxY);
        BorderPane.setAlignment(graph, Pos.CENTER);
        graph.setDepots(processFile.depots);
        graph.setCustomers(processFile.customers);
        borderPane.setCenter(graph);

        //Create statistics for the graph
        Statistic statistic = new Statistic();
        BorderPane.setAlignment(statistic, Pos.CENTER);
        double fitness = 0;
        for (ProposedSolution proposedSolution : evolutionaryAlgorithm.proposedSolutions) {
            fitness += proposedSolution.fitnessScore;
        }
        fitness /= evolutionaryAlgorithm.proposedSolutions.length;
        statistic.setDistance(fitness);
        borderPane.setBottom(statistic);

        graph.setRoutes(evolutionaryAlgorithm.proposedSolutions[0]);

        primaryStage.getIcons().add(new Image("elster2.png"));
        primaryStage.show();
    }
}
