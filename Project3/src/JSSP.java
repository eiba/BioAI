import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.util.HashMap;

/**
 * Job Shop Scheduling Problem
 */
public class JSSP extends Application {

    // JavaFX Variables

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

    // Useful links
    // Ant colonies for TSP: https://www.youtube.com/watch?v=anY6hqBf7Pg
    // Bees algorithm and its application: https://www.youtube.com/watch?v=O9BYK-7hY0s

    // JSSP Variables
    private GUI gui;
    private ACO aco;
    private BA ba;

    private int jobCount, machineCount;
    private Job[] jobs;

    @Override
    public void start(Stage primaryStage) {

        // JavaFX Initialization
        gui = new GUI(primaryStage);

        // JSSP Initialization
        readProblem("1");
        aco = new ACO(jobs);
        ba = new BA(jobs);

        //Dev
        int[][] schedule = new int[][]{
                {},
                {},
                {}
        };
        Solution solution = new Solution(schedule);
//        gui.createGantt(solution, "Test Solution");

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
                jobs[i] = new Job(i, requirements);
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
