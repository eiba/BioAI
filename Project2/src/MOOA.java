import java.awt.*;
import java.io.IOException;

public class MOOA {

    private ImageParser img;
    private final int populationSize;
    private final int archiveSize;
    private final double mutationRate;
    private final double crossoverRate;
    private final int iterations;
    private final int minimumSegmentCount;
    private final int maximumSegmentCount;
    private final double edgeWeight;
    private final double deviationWeight;

    //Multi Objective Optimization Algorithm
    public MOOA(String filename, int populationSize,int archiveSize, double mutationRate, double crossoverRate, int iterations, int minimumSegmentCount, int maximumSegmentCount, double edgeWeight, double deviationWeight){

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

        //Step 2: parse the image
        try {
            this.img = new ImageParser(filename);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    Solution[] iterate(){
        ImageSegmentation segmentation = new ImageSegmentation(img);

        //Step 3: Create Initial segments with Prim's algorithm
        //@TODO: Implement Prim's algorithm to make individualCount number of minimum spanning trees. Each tree is used to create one initial segment.
        Solution[] solutions = segmentation.createInitialSolutions(populationSize, minimumSegmentCount,maximumSegmentCount);
        Solution[] archive = new Solution[archiveSize];

        //Step 4: run the evolutionary cycle for <iterations> generations
        for(int i=0; i< iterations;i++){

            //TODO step 5: Crossover
            //solutions = segmnetation.Crossover(solutions, archive crossoverRate)

            //TODO step 6: Mutate
            //solutions = segmnetation.Mutate(solutions, mutationRate)

            //TODO step 7: Evaluate the new solutions
            //segmentation.scoreSolution(solutions, archive, deviationWeight, edgeWeight)

            //TODO step 8: Archive the best non dominated solutions
            //archive = segmentation.archive(solutions, archive, archiveSize)
        }

        return solutions;
    }
}
