import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.util.HashMap;

/**
 * Job Shop Scheduling Problem
 */
public class JSSP extends Application {

    // JavaFX Variables
    private boolean running = false;

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

    void run(String algorithm, String task, int iterations, int bestMakespan) {

        running = true;
        gui.startButton.setDisable(true);
        gui.stopButton.setDisable(false);
        gui.setBottom(null);

        // JSSP Initialization
        readProblem(task);
        aco = new ACO(jobs, machineCount, jobCount, this, gui, bestMakespan);
        ba = new BA(jobs, machineCount, jobCount);

        gui.createStatGraph(iterations);

        if (algorithm.equals("ACO")) {
            Thread acoThread = new Thread(() -> {
                final Solution solution = aco.solve(iterations, 20);
                if (solution != null) {
                    Platform.runLater(() -> {
                        gui.startButton.setDisable(false);
                        gui.stopButton.setDisable(true);
                        gui.createGantt(solution, bestMakespan);
                    });
                }
                else {
                    System.exit(0);
                }
            });
            acoThread.start();
        }
        else {
            Thread baThread = new Thread(() -> {
                Solution solution = ba.solve();

                if (solution != null) {
                    Platform.runLater(() -> {
                        gui.createGantt(solution, bestMakespan);
                    });
                }
                else {
                    System.exit(0);
                }
            });
            baThread.start();
        }

    }

    void stopRunning() {
        running = false;
        gui.startButton.setDisable(false);
        gui.stopButton.setDisable(true);
    }

    synchronized boolean getRunning() {
        return running;
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

    @Override
    public void stop(){
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
