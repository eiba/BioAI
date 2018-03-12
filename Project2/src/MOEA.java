import javafx.application.Platform;

import java.io.IOException;
import java.util.ArrayList;

public class MOEA {

    private ImageParser img;
    private GUI gui;
    private final String filename;
    private final int populationSize;
    private final int archiveSize;
    private final double mutationRate;
    private final double crossoverRate;
    private final int iterations;
    private final int minimumSegmentCount;
    private final int maximumSegmentCount;
    private final double edgeWeight;
    private final double deviationWeight;
    private final boolean weightedSum;
    private final int numberOfTournaments;
    private final boolean cielab;

    //Multi Objective Optimization Algorithm
    MOEA(GUI gui, String filename, int populationSize, int archiveSize, double mutationRate, double crossoverRate,
         int iterations, int minimumSegmentCount, int maximumSegmentCount, double edgeWeight, double deviationWeight,
         boolean weightedSum, int numberOfTournaments, boolean cielab){

        this.gui = gui;
        this.filename = filename;

        //Step 1: setup variables for the evolutionary cycle
        this.populationSize = populationSize;
        this.archiveSize = archiveSize;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.iterations = iterations;
        this.minimumSegmentCount = minimumSegmentCount;
        this.maximumSegmentCount = maximumSegmentCount;
        this.edgeWeight = edgeWeight;
        this.deviationWeight = deviationWeight;
        this.weightedSum = weightedSum;
        this.numberOfTournaments = numberOfTournaments;
        this.cielab = cielab;

        //Step 2: parse the image
        try {
            this.img = new ImageParser(filename);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    Solution[] iterate(){
        ImageSegmentation segmentation = new ImageSegmentation(img, gui, this.edgeWeight, this.deviationWeight, cielab);

        //Step 3: Create Initial segments with Prim's algorithm

        // Creating initial segments that contains the entire image in one large segment
        gui.out("Creating " + populationSize + " MSTs");
        Platform.runLater(() -> gui.resetProgress(populationSize));
        Solution[] solutions = segmentation.createInitialSolutions(populationSize, minimumSegmentCount, maximumSegmentCount);


        if (solutions[0].segments[0] != null) {
            int bestIndex = 0;
            double bestScore = solutions[0].score;
            for(int i=1; i<solutions.length;i++){
                if(solutions[i].score < bestScore){
                    bestScore = solutions[i].score;
                    bestIndex = i;
                }
            }
            gui.drawImage(solutions[bestIndex], img.width, img.height);
            gui.out("Weighted sum: " + bestScore);
        }


        //Step 4: run the evolutionary cycle for <iterations> generations
        gui.out("Starting MOEA");
        //segmentation.combineSegment(solutions[0],solutions[0].segments[0]);
        if(weightedSum){
            //TODO: implement weighted sum
            for(int i=0; i<iterations;i++){

                // Stop button has been pressed, stopping thread
                if (!gui.getLoop()) {
                    break;
                }

                gui.out("Generation " + (i+1) + "/" + iterations);
                // Crossover
                gui.resetProgress(solutions.length);
                gui.out("Crossover");
                final Solution[] offspring = segmentation.singlePointCrossover(solutions, solutions.length, minimumSegmentCount, maximumSegmentCount, false, numberOfTournaments, this.mutationRate);

                //TODO step 7: Mutate
                //children = segmnetation.Mutate(children, mutationRate)

                // Evaluate the new solutions
                gui.resetProgress(offspring.length);
                gui.out("Evaluating new generation");
                segmentation.evaluate(offspring);

                // Rank selection
                gui.out("Rank selection");
                solutions = segmentation.selectWeightedSum(solutions, offspring, this.populationSize);

                gui.drawImage(solutions[0], img.width, img.height);
                gui.out("Weighted sum: " + solutions[0].score);
            }
        }
        else{
            for (int i = 0; i < iterations; i++){

                // Stop button has been pressed, stopping thread
                if (!gui.getLoop()) {
                    break;
                }

                gui.out("Generation " + (i+1) + "/" + iterations);
                // Crossover
                gui.resetProgress(solutions.length);
                gui.out("Crossover");
                /*for(Solution solution: solutions){
                    segmentation.mutate(solution,1,1);
                }*/
                final Solution[] offspring = segmentation.singlePointCrossover(solutions, solutions.length, minimumSegmentCount, maximumSegmentCount, true, numberOfTournaments, this.mutationRate);

                //TODO step 6: Mutate
                //children = segmnetation.Mutate(children, mutationRate)

                // Evaluate the new solutions
                gui.resetProgress(offspring.length);
                gui.out("Evaluating new generation");
                segmentation.evaluate(offspring);
                //segmentation.evaluate(solutions);

                //TODO step 8: select stuff for next generation
                gui.out("Non dominating sort");
                solutions = segmentation.nonDominationSorting(solutions, offspring, this.populationSize);

                gui.drawImage(solutions[0], img.width, img.height);
                gui.out("Weighted sum: " + solutions[0].score);
            }
        }

        gui.out("MOEA ended, saving best images to folder");
        for (int i = 0; i < 5; i ++) {
            if (i >= solutions.length) {
                break;
            }
            segmentation.writeImage(solutions[i]);
            gui.out("Solution " + (i + 1));
            gui.out("Segments: " + solutions[i].segments.length);
            gui.out("Edge score: " + solutions[i].edgeValue);
            gui.out("Deviation score: " + solutions[i].overallDeviation);
            gui.out("Weighted sum: " + solutions[i].score);
        }
        gui.moeaStopped();

        return solutions;
    }
}
