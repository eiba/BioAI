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
import java.util.HashMap;

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
    private final Button startButton = new Button("Start");

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

        final HashMap<String, Double> optimalMap = new HashMap<>();
        optimalMap.put("p01", 576.87);
        optimalMap.put("p02", 473.53);
        optimalMap.put("p03", 641.19);
        optimalMap.put("p04", 1001.59);
        optimalMap.put("p05", 750.03);
        optimalMap.put("p06", 876.50);
        optimalMap.put("p07", 885.80);
        optimalMap.put("p08", 4437.68);
        optimalMap.put("p09", 3900.22);
        optimalMap.put("p10", 3663.02);
        optimalMap.put("p11", 3554.18);
        optimalMap.put("p12", 1318.95);
        optimalMap.put("p13", 1318.95);
        optimalMap.put("p14", 1360.12);
        optimalMap.put("p15", 2505.42);
        optimalMap.put("p16", 2572.23);
        optimalMap.put("p17", 2709.09);
        optimalMap.put("p18", 3702.85);
        optimalMap.put("p19", 3827.06);
        optimalMap.put("p20", 4058.07);
        optimalMap.put("p21", 5474.84);
        optimalMap.put("p22", 5702.16);
        optimalMap.put("p23", 6095.46);

        taskMenu.setOnAction(e -> {
            if (optimalMap.containsKey(taskMenu.getValue())) {
                optimalValueInput.setText(String.valueOf(optimalMap.get(taskMenu.getValue())));
            }
            else optimalValueInput.setText("set optimal value");
        });

        startButton.setOnAction(event -> {

            thread = new Thread(() -> {

                RUN = true;
                startButton.setDisable(true);
                stopButton.setDisable(false);

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


                    statistic.setDistance(evolutionaryAlgorithm.currentBest.getFitness(), getOptimalValue(), evolutionaryAlgorithm.iterationsUsed);

                    //Display one of the solutions
                    graph.setRoutes(evolutionaryAlgorithm.currentBest);

//                        System.out.println(car.getCurrentDuration());
//                    }
                });
                startButton.setDisable(false);
                stopButton.setDisable(true);
            });

            thread.setDaemon(true);
            thread.start();
        });

        stopButton.setOnAction((e) -> {
            RUN = false;
            stopButton.setDisable(true);
            startButton.setDisable(false);
        });
        stopButton.setDisable(true);

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
        hBox.getChildren().addAll(new Text("Select data set:"), taskMenu, startButton, stopButton);
        taskMenu.setValue("p08");


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
