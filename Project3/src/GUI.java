import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * This class is dedicated to all GUI related tasks for the JSSP
 */
class GUI extends BorderPane {

    private final Stage primaryStage;

    GUI(Stage primaryStage) {
        super();
        this.primaryStage = primaryStage;
        final Scene scene = new Scene(this, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JSSP - Job Shop Scheduling Problem");
        primaryStage.show();
    }

    void createGantt(Solution solution, String title) {

        final int makespan = solution.getMakespan();
        final int[][] schedule = solution.getSchedule();
        final int width = makespan;
        final int height = schedule.length * 50;

        final Stage stage = new Stage();
        final Pane pane = new Pane();
        final ScrollPane scrollPane = new ScrollPane(pane);
        final Scene scene = new Scene(scrollPane);
        stage.setScene(scene);
        stage.setTitle(title);

        pane.setMaxSize(width, height);
        pane.setMinSize(width, height);

        stage.show();
    }

}
