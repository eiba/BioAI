import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;

/**
 * Job Shop Scheduling Problem
 */
public class JSSP extends Application {

    // JavaFX Variables
    private Stage primaryStage;

    // JSSP Variables
    private final GUI gui = new GUI();
    private ACO aco;
    private BA ba;

    private int jobCount, machineCount;
    private Job[] jobs;

    @Override
    public void start(Stage primaryStage) {

        // JavaFX Initialization
        this.primaryStage = primaryStage;
        final Scene scene = new Scene(gui, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JSSP - Job Shop Scheduling Problem");
        primaryStage.show();

        // JSSP Initialization
        readProblem("1");
        aco = new ACO(jobs);
        ba = new BA(jobs);


    }

    private void readProblem(String task) {
        try {
            final BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("./Test Data/" + task + ".txt")));

            String line = bufferedReader.readLine().trim();
            String[] split = line.split("\\s+");
            jobCount = Integer.valueOf(split[0]);
            machineCount = Integer.valueOf(split[1]);
            jobs = new Job[jobCount];

            for (int i = 0; i < jobCount; i ++) {
                line = bufferedReader.readLine().trim();
                split = line.split("\\s+");

                final int[][] requirements = new int[machineCount][2];
                for (int j = 0; j < machineCount; j ++) {
                    final int index = j * 2;
                    requirements[j][0] = Integer.valueOf(split[index]);
                    requirements[j][1] = Integer.valueOf(split[index + 1]);
                }
                jobs[i] = new Job(requirements);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
