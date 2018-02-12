import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application{

    private Stage primaryStage;

    public static final int PROCESSORS = Runtime.getRuntime().availableProcessors();

    private static final int POPULATION_SIZE = 100;

    private static final int ITERATIONS = 3000;
    private static final int NUMBER_OF_TOURNAMENTS = 2;
    private static final int MAXIMUM_AGE = 7;
    private static final double MUTATION_RATE = 0.05;
    private static final double THRESHOLD = 1;

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

        // Create a new menu for selecting the data sets
        final ChoiceBox<String> taskMenu = new ChoiceBox<>(FXCollections.observableArrayList("p01", "p02", "p03", "p04", "p05", "p06", "p07", "p08", "p09",
                "p10", "p11", "p12", "p13", "p14", "p15", "p16", "p17", "p18", "p19",
                "p20", "p21", "p22", "p23"));
        taskMenu.setOnAction(event -> {

            Thread thread = new Thread(() -> {

                // Create statistics for the graph
                Statistic statistic = new Statistic();
                BorderPane.setAlignment(statistic, Pos.CENTER);
                Platform.runLater(() -> {
                    borderPane.setBottom(statistic);
                });

                // Create new StatGraph for fitness graph
                StatGraph statGraph = new StatGraph(700, 500, ITERATIONS);
                BorderPane.setAlignment(statGraph, Pos.CENTER);
                Platform.runLater(() -> {
                    borderPane.setCenter(statGraph);
                });

                // Initiate the evolutionary algorithm
                EvolutionaryAlgorithm evolutionaryAlgorithm = new EvolutionaryAlgorithm("./TestData/" + taskMenu.getValue(), statistic, statGraph, ITERATIONS);

                // Run the evolutionary algorithm

                ProposedSolution[] solutions = evolutionaryAlgorithm.iterate(POPULATION_SIZE, MUTATION_RATE,ITERATIONS, NUMBER_OF_TOURNAMENTS,MAXIMUM_AGE, THRESHOLD);

                // Get all the data from the data set
                ProcessFile processFile = evolutionaryAlgorithm.processFile;

                // Calling Platform.runLater() for a Thread-safe call to update GUI
                Platform.runLater(() -> {

                    // Create a new graph
                    Graph graph = new Graph(700, 500, processFile.minX, processFile.minY, processFile.maxX, processFile.maxY);
                    BorderPane.setAlignment(graph, Pos.CENTER);
                    graph.setDepots(processFile.depots);
                    graph.setCustomers(processFile.customers);
                    borderPane.setCenter(graph);


                    statistic.setDistance(solutions[0].getFitness(), processFile.optimalFitness, evolutionaryAlgorithm.iterationsUsed);

                    //Display one of the solutions
                    graph.setRoutes(solutions[0]);

//                    for (Car car : solutions[0].cars) {
//                        System.out.println(car.getCurrentDuration());
//                    }
                });
            });
            thread.start();
        });

        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(new Text("Select data set:"), taskMenu);
        taskMenu.setValue("p08");


        borderPane.setTop(hBox);

        primaryStage.getIcons().add(new Image("elster2.png"));
        primaryStage.show();
    }
}
