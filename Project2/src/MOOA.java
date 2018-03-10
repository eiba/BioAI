import java.io.IOException;
import java.util.ArrayList;

public class MOOA {

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

    //Multi Objective Optimization Algorithm
    MOOA(GUI gui, String filename, int populationSize,int archiveSize, double mutationRate, double crossoverRate, int iterations, int minimumSegmentCount, int maximumSegmentCount, double edgeWeight, double deviationWeight, boolean weightedSum){

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

        //Step 2: parse the image
        try {
            this.img = new ImageParser(filename);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    Solution[] iterate(){
        ImageSegmentation segmentation = new ImageSegmentation(img, gui, this.edgeWeight, this.deviationWeight);

        //Step 3: Create Initial segments with Prim's algorithm

        // Creating initial segments that contains the entire image in one large segment
        gui.out("Creating " + populationSize + " MSTs");
        Solution[] solutions = segmentation.createInitialSolutions(populationSize, minimumSegmentCount, maximumSegmentCount);

//        gui.drawImage(new Solution(new Segment[]{segments[0]}), img.width, img.height);

//        gui.out("Dividing MSTs into smaller segments");
        // Dividing the segments into smaller segments to form a Solution
//        Solution[] solutions = new Solution[populationSize];
//        for (int i = 0; i < populationSize; i ++) {
//            final Solution solution = new Solution(segmentation.divideSegment(segments[i], 8));
//            solutions[i] = solution;
//        }
        /*gui.out("Starting sorting");
        segmentation.nonDominationSorting(solutions,this.populationSize);
        gui.out("Done sorting");*/
        /*ArrayList<Solution> solutions1 = new ArrayList<>();
        for(Solution solution: solutions){
            solutions1.add(solution);
        }
        segmentation.crowdingDistanceSort(solutions1,this.populationSize);*/

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
            gui.out("Score: " + bestScore);
//            segmentation.writeImage(solutions[bestIndex]);
        }
//        else {
//            gui.drawImage(new Solution(new Segment[]{segments[0]}), img.width, img.height);
//        }


//        gui.drawImage(segmentation.singlePointCrossover(solutions, solutions.length)[0], img.width, img.height);
//        gui.drawImage(solutions[0], img.width, img.height);
//        gui.out("Drawing test image");
        //Step 4: run the evolutionary cycle for <iterations> generations

        if(weightedSum){
            //TODO: implement weighted sum
            for(int i=0; i<iterations;i++){

                // Stop button has been pressed, stopping thread
                if (!gui.getLoop()) {
                    break;
                }

                //TODO step 5: parent selection? toutnament? Could be inside crossover too

                //mutation and crossover is the same
                //TODO step 6: Crossover
                //children = segmnetation.Crossover(solutions, archive crossoverRate)

                //TODO step 7: Mutate
                //children = segmnetation.Mutate(children, mutationRate)

                //TODO step 8: Combine parents and offspring
                //solutions = solutions + children

                //TODO: step 9; Survivor selection
                //rank selection
                solutions = segmentation.selectWeightedSum(solutions,  this.populationSize);
            }
        }
        else{
            gui.out("Starting MOEA");
            for (int i = 0; i < iterations; i++){

                // Stop button has been pressed, stopping thread
                if (!gui.getLoop()) {
                    break;
                }

                gui.out("Generation " + (i+1) + "/" + iterations);
                // Crossover
                gui.resetProgress();
                gui.out("Crossover");
                final Solution[] offspring = segmentation.singlePointCrossover(solutions, solutions.length, minimumSegmentCount, maximumSegmentCount);

                //TODO step 6: Mutate
                //children = segmnetation.Mutate(children, mutationRate)

                // Evaluate the new solutions
                gui.resetProgress();
                gui.out("Evaluating new generation");
                segmentation.evaluate(offspring);

                //TODO step 8: select stuff for next generation
                gui.out("Non dominating sort");
                solutions = segmentation.nonDominationSorting(solutions, offspring, this.populationSize);

                gui.drawImage(solutions[0], img.width, img.height);
                gui.out("Score: " + solutions[0].score);
            }
        }

        gui.out("MOEA ended, saving best images to folder");
        for (int i = 0; i < 5; i ++) {
            segmentation.writeImage(solutions[i]);
        }
        gui.moeaStopped();

        return solutions;
    }
}
