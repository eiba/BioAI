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
        gui = new GUI(primaryStage, this);


        //Dev
//        run("ACO", "1");
    }

    void run(String algorithm, String task) {

        // JSSP Initialization
        readProblem(task);
        aco = new ACO(jobs, machineCount, jobCount);
        ba = new BA(jobs, machineCount, jobCount);

        if (algorithm.equals("ACO")) {
            Solution solution = aco.solve(100, 1000);

            if (solution != null) {
                gui.createGantt(solution, MAKESPAN_VALUES.get(task));
            }
            else {
                System.exit(0);
            }
        }
        else {

        }
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
