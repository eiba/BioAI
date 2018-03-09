import java.io.IOException;
import java.util.ArrayList;

public class MOOA {

    private ImageParser img;
    private GUI gui;
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
        ImageSegmentation segmentation = new ImageSegmentation(img,this.edgeWeight,this.deviationWeight);

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
        }*/
        //segmentation.crowdingDistanceSort(solutions1,50);

        if (solutions[0].segments[0] != null) {
            int bestIndex = 0;
            double bestScore = solutions[0].score;
            for(int i=1; i<solutions.length;i++){
                if(solutions[i].score < bestScore){
                    bestScore = solutions[i].score;
                    bestIndex = i;
                }
            }
//            gui.drawImage(solutions[bestIndex], img.width, img.height);
            gui.out("Score: "+bestScore);
        }
//        else {
//            gui.drawImage(new Solution(new Segment[]{segments[0]}), img.width, img.height);
//        }


        gui.out("Starting crossover");
        gui.drawImage(segmentation.singlePointCrossover(solutions, solutions.length)[0], img.width, img.height);
        gui.out("Drawing test image");
//        gui.drawImage(solutions[0], img.width, img.height);
        //Step 4: run the evolutionary cycle for <iterations> generations

        if(weightedSum){
            //TODO: implement weighted sum
            for(int i=0; i<iterations;i++){

            }
        }
        else{
            for(int i=0; i< iterations;i++){

                //TODO step 5: Crossover
                //children = segmnetation.Crossover(solutions, archive crossoverRate)

                //TODO step 6: Mutate
                //children = segmnetation.Mutate(children, mutationRate)

                //TODO step 7: Evaluate the new solutions
                //solutions = solutions + children

                //TODO step 8: select stuff for next generation
                solutions = segmentation.nonDominationSorting(solutions, this.populationSize);
            }
        }
        return solutions;
    }
}
