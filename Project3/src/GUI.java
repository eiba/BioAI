import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.HashMap;

/**
 * This class is dedicated to all GUI related tasks for the JSSP
 */
class GUI extends BorderPane {

    // Test Data Variables
    private final static HashMap<String, Integer> MAKESPAN_VALUES = new HashMap<>();
    static {
        MAKESPAN_VALUES.put("1", 56);
        MAKESPAN_VALUES.put("2", 1059);
        MAKESPAN_VALUES.put("3", 1276);
        MAKESPAN_VALUES.put("4", 1130);
        MAKESPAN_VALUES.put("5", 1451);
        MAKESPAN_VALUES.put("6", 979);
    }

    private final static HashMap<String, Integer> MAKESPAN_OPTIMAL_VALUES = new HashMap<>();
    static {
        MAKESPAN_OPTIMAL_VALUES.put("1", 55);
        MAKESPAN_OPTIMAL_VALUES.put("2", 930);
        MAKESPAN_OPTIMAL_VALUES.put("3", 1165);
        MAKESPAN_OPTIMAL_VALUES.put("4", 1005);
        MAKESPAN_OPTIMAL_VALUES.put("5", 1235);
        MAKESPAN_OPTIMAL_VALUES.put("6", 943);
    }

    private final Stage primaryStage;
    private final JSSP jssp;
    private StatGraph statGraph;

    final Button startButton = new Button("Start");
    final Button stopButton = new Button("Stop");
    private final ChoiceBox<String> taskBox = new ChoiceBox<>(FXCollections.observableArrayList("1", "2", "3", "4", "5", "6"));
    private final ChoiceBox<String> algorithmBox = new ChoiceBox<>(FXCollections.observableArrayList("ACO", "BA"));
    private final CheckBox optimalCheckbox = new CheckBox("Use optimal makespan");
    private final CheckBox stopCheckbox = new CheckBox("Stop on 90%");

    private final TextField inputIterations = new TextField("1000");
    private final TextField inputMakespan = new TextField("REQUIRED");
    private final TextField inputAntBees = new TextField("100");

    private final Color[] colors = new Color[]{Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.PURPLE, Color.ORANGE, Color.DARKBLUE, Color.PINK, Color.LIGHTGRAY, Color.DARKCYAN};

    GUI(Stage primaryStage, JSSP jssp) {
        super();
        this.primaryStage = primaryStage;
        this.jssp = jssp;
        final Scene scene = new Scene(this, 1040, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JSSP - Job Shop Scheduling Problem");

        startButton.setOnAction((e) -> {
            if (inputMakespan.getText().equals("REQUIRED")) {
                inputMakespan.requestFocus();
            }
            else {
                jssp.run(algorithmBox.getValue(), taskBox.getValue(), Integer.valueOf(inputIterations.getText()), Integer.valueOf(inputMakespan.getText()), Integer.valueOf(inputAntBees.getText()), stopCheckbox.isSelected());
            }
        });

        stopButton.setOnAction(event -> jssp.stopRunning());

        stopButton.setDisable(true);

        taskBox.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, number2) -> {
            int makespan;
            if (optimalCheckbox.isSelected()) {
                makespan = MAKESPAN_OPTIMAL_VALUES.getOrDefault(taskBox.getItems().get((int) number2), 0);
            } else {
                makespan = MAKESPAN_VALUES.getOrDefault(taskBox.getItems().get((int) number2), 0);
            }
            if (makespan > 0) {
                inputMakespan.setText(String.valueOf(makespan));
            } else {
                inputMakespan.setText("REQUIRED");
            }
        });


        optimalCheckbox.setSelected(true);
        stopCheckbox.setSelected(true);
        algorithmBox.setValue("ACO");
        taskBox.setValue("5");

        HBox menu = new HBox(10);
        menu.setAlignment(Pos.CENTER);
        menu.getChildren().addAll(algorithmBox, taskBox, optimalCheckbox, stopCheckbox, startButton, stopButton);
        setTop(menu);

        VBox options = new VBox(10);
        options.setPadding(new Insets(5, 5, 5, 5));
        options.setAlignment(Pos.CENTER);
        options.getChildren().addAll(new Text("Iterations:"), inputIterations,
                new Text("Ants / Bees"), inputAntBees,
                new Text("Best makespan:"), inputMakespan);
        setLeft(options);

        primaryStage.show();
    }

    void createGantt(Solution solution, int optimal) {

        final int makespan = solution.getMakespan();
        final int[][][] schedule = solution.getSchedule();
        final int width = 800;
        final double widthTranslate = (double) width / makespan;
        final int height = schedule.length * 50;
        primaryStage.setHeight(200 + height);

//        final Stage stage = new Stage();
        final Pane pane = new Pane();
//        final ScrollPane scrollPane = new ScrollPane(pane);
//        final Scene scene = new Scene(scrollPane, width + 15, height + 15);
//        stage.setScene(scene);
//        stage.setTitle(title);

        pane.setMaxSize(width, height);
        pane.setMinSize(width, height);

        // Creating X-Axis lines
        final int xLines = 10;
        final int xInterval = makespan / xLines;
        for (int i = 0; i < xLines; i ++) {
            final double x = widthTranslate * xInterval * i;
            final Line line = new Line(x, 0, x, height + 5);
            final Text text = new Text(String.valueOf(xInterval * i));
            text.setTranslateY(height + 15);
            text.setTranslateX(x + 5);
            pane.getChildren().addAll(line, text);
        }
        final Line lineLast = new Line(width, 0, width, height + 5);
        final Text textLast = new Text(String.valueOf(makespan));
        textLast.setTranslateY(height + 15);
        textLast.setTranslateX(width + 5);
        pane.getChildren().addAll(lineLast, textLast);

        for (int i = 0; i < schedule.length; i ++) {
            final int y = i * 50;
            for (int j = 0; j < schedule[0].length; j ++) {
                final String name = String.valueOf(j);
                final Task task = new Task(name, schedule[i][j][1] * widthTranslate, colors[j % colors.length]);
                task.setTranslateX(schedule[i][j][0] * widthTranslate);
                task.setTranslateY(y);
                pane.getChildren().add(task);
            }
        }

        setCenter(pane);
        pane.setStyle("-fx-border-color: gray");
        BorderPane.setAlignment(pane, Pos.CENTER);
        String rating = String.format("%.2f%%", (double) optimal / solution.getMakespan() * 100);
        final Text text = new Text("Current makespan: " + solution.getMakespan() + ", optimal: " + optimal + ", rating: " + rating);
        setBottom(text);
        BorderPane.setAlignment(text, Pos.CENTER);
//        stage.show();
    }

    void createStatGraph(int iterations) {
        statGraph = new StatGraph(800, 500, iterations);
        primaryStage.setHeight(700);
        setCenter(statGraph);
    }

    void addIteration(double fitness) {
        statGraph.addIteration(fitness);
    }

    void setBestSolution(double makespan, double percent) {
        statGraph.setBestSolution(makespan, percent * 100);
    }

    private class Task extends StackPane {

        private Task(String text, double width, Color color) {
            super();
            final Rectangle rectangle = new Rectangle(width, 50);
            rectangle.setFill(color);
            getChildren().addAll(rectangle, new Text(text));
        }

    }

}
