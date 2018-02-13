import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application{

    private Stage primaryStage;

    public static final int PROCESSORS = Runtime.getRuntime().availableProcessors();

    public static boolean RUN = true;

    // Default parameters
    private static int POPULATION_SIZE = 300;
    private static int ITERATIONS = 3000;
    private static int NUMBER_OF_TOURNAMENTS = 2;
    private static int MAXIMUM_AGE = 3;
    private static double MUTATION_RATE = 0.02;
    private static double OPTIMAL_VALUE = 0.0;

    private final Button stopButton = new Button("Stop");

    private Thread thread;
    private ProcessFile processFile;
    private EvolutionaryAlgorithm evolutionaryAlgorithm;
    private Statistic statistic;

    private TextField populationSizeInput = new TextField();
    private TextField iterationsInput = new TextField();
    private TextField tournamentInput = new TextField();
    private TextField maxAgeInput = new TextField();
    private TextField mutationRateInput = new TextField();
    private TextField optimalValueInput = new TextField();

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("IT3708 - Assignment 1");

        // Create a new menu for selecting the data sets
        final ChoiceBox<String> taskMenu = new ChoiceBox<>(FXCollections.observableArrayList("p01", "p02", "p03", "p04", "p05", "p06", "p07", "p08", "p09",
                "p10", "p11", "p12", "p13", "p14", "p15", "p16", "p17", "p18", "p19",
                "p20", "p21", "p22", "p23"));
        taskMenu.setOnAction(event -> {

            thread = new Thread(() -> {

                RUN = true;
                taskMenu.setDisable(true);

                // Create statistics for the graph
                statistic = new Statistic();
                BorderPane.setAlignment(statistic, Pos.CENTER);
                Platform.runLater(() -> {
                    borderPane.setBottom(statistic);
                });

                // Create new StatGraph for fitness graph
                StatGraph statGraph = new StatGraph(700, 500, getIterations());
                BorderPane.setAlignment(statGraph, Pos.CENTER);
                Platform.runLater(() -> {
                    borderPane.setCenter(statGraph);
                });

                // Initiate the evolutionary algorithm
                evolutionaryAlgorithm = new EvolutionaryAlgorithm("./TestData/" + taskMenu.getValue(), statistic, statGraph, getIterations());

                processFile = evolutionaryAlgorithm.processFile;

                // Run the evolutionary algorithm
                ProposedSolution[] solutions = evolutionaryAlgorithm.iterate(getPopulationSize(), getMutationRate(), getIterations(), getNumberOfTournaments(), getMaximumAge(), getOptimalValue());

                // Get all the data from the data set
                System.out.println(evolutionaryAlgorithm.currentBest);
                // Calling Platform.runLater() for a Thread-safe call to update GUI
                Platform.runLater(() -> {

                    // Create a new graph
                    Graph graph = new Graph(700, 500, processFile.minX, processFile.minY, processFile.maxX, processFile.maxY);
                    BorderPane.setAlignment(graph, Pos.CENTER);
                    graph.setDepots(processFile.depots);
                    graph.setCustomers(processFile.customers);
                    borderPane.setCenter(graph);


                    statistic.setDistance(evolutionaryAlgorithm.currentBest.getFitness(), processFile.optimalFitness, evolutionaryAlgorithm.iterationsUsed);

                    //Display one of the solutions
                    graph.setRoutes(evolutionaryAlgorithm.currentBest);

//                    for (Car car : solutions[0].cars) {
//                        System.out.println(car.getCurrentDuration());
//                    }
                });
                taskMenu.setDisable(false);
            });
            thread.setDaemon(true);
            thread.start();
        });

        stopButton.setOnAction((e) -> {
            RUN = false;
            taskMenu.setDisable(false);
        });

        VBox optionMenu = new VBox(5);
        optionMenu.setTranslateX(5);
        optionMenu.setAlignment(Pos.CENTER);

        populationSizeInput.setText(String.valueOf(POPULATION_SIZE));
        optionMenu.getChildren().addAll(new Text("Initial Population Size"), populationSizeInput);

        iterationsInput.setText(String.valueOf(ITERATIONS));
        optionMenu.getChildren().addAll(new Text("Total number of iterations"), iterationsInput);

        tournamentInput.setText(String.valueOf(NUMBER_OF_TOURNAMENTS));
        optionMenu.getChildren().addAll(new Text("Number of tournaments"), tournamentInput);

        maxAgeInput.setText(String.valueOf(MAXIMUM_AGE));
        optionMenu.getChildren().addAll(new Text("Maximum population age"), maxAgeInput);

        mutationRateInput.setText(String.valueOf(MUTATION_RATE));
        optionMenu.getChildren().addAll(new Text("Mutation rate"), mutationRateInput);

        optimalValueInput.setText(String.valueOf(OPTIMAL_VALUE));
        optionMenu.getChildren().addAll(new Text("Optimal value"), optimalValueInput);

        borderPane.setLeft(optionMenu);

        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(new Text("Select data set:"), taskMenu, stopButton);
//        taskMenu.setValue("p08");


        borderPane.setTop(hBox);

        primaryStage.getIcons().add(new Image("elster2.png"));
        primaryStage.show();
    }

    private int getPopulationSize() {
        return Integer.valueOf(populationSizeInput.getText());
    }

    private int getIterations() {
        return Integer.valueOf(iterationsInput.getText());
    }

    private int getNumberOfTournaments() {
        return Integer.valueOf(tournamentInput.getText());
    }

    private int getMaximumAge() {
        return Integer.valueOf(maxAgeInput.getText());
    }

    private double getMutationRate() {
        return Double.valueOf(mutationRateInput.getText());
    }

    private double getOptimalValue() {
        return Double.valueOf(optimalValueInput.getText());
    }

    static synchronized boolean getRun() {
        return RUN;
    }
}
