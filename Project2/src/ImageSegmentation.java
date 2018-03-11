import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageSegmentation {

    private final ImageParser imageParser;
    private final GUI gui;
    final Random random;
    final Pixel[][] pixels;
    final double edgeWeight;
    final double overallDeviationWeight;
    private final Comparator<Solution> weightedSumComparator;
    private final Comparator<Solution> overallDeviationComparator;
    private final Comparator<Solution> edgeValueComparator;
    private final Comparator<Solution> crowdingDistanceComparator;

    int imagesWritten = 0;

    ImageSegmentation(ImageParser imageParser, GUI gui, double edgeWeight, double overallDeviationWeight){
        this.imageParser = imageParser;
        this.gui = gui;
        this.edgeWeight = edgeWeight;
        this.overallDeviationWeight = overallDeviationWeight;
        pixels = createPixels();
        random = new Random();
        this.weightedSumComparator = new weightedSumComparator();
        this.overallDeviationComparator = new overallDeviationComparator();
        this.edgeValueComparator = new edgeValueComparator();
        this.crowdingDistanceComparator = new crowdingDistanceComparator();
    }

//    /**
//     * Create initial segments that contains the entire image
//     * @param populationSize Number of segments to be returned
//     * @return segments that each contains the entire image
//     */
//    Segment[] createInitialSegments(int populationSize){
//
//        final Segment[] segments = new Segment[populationSize];
//
//        //Create one solution for each iteration
//        final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//
//        for (int i = 0; i < populationSize; i ++) {
//            final int index = i;
//            executorService.execute(() -> {
//
//                // Selecting a random Pixel
//                final int rootRow = random.nextInt(imageParser.height);
//                final int rootColumn = random.nextInt(imageParser.width);
//                final Pixel rootPixel = pixels[rootRow][rootColumn];
//
//                // Creating a new segment
//                final Segment segment = new Segment(rootPixel);
//                final PriorityQueue<PixelEdge> priorityQueue = new PriorityQueue<>(rootPixel.edgeList);
//
//                // Using Prim's algorithm to create the Segment
//                while (!priorityQueue.isEmpty()) {
//                    final PixelEdge currentPixelEdge = priorityQueue.remove();
//
//                    // Check if Pixel is already in Segment
//                    if (segment.contains(currentPixelEdge.pixelB)) {
//                        continue;
//                    }
//
//                    // Adding the new Pixel to the Segment
//                    segment.add(currentPixelEdge);
//                    priorityQueue.addAll(currentPixelEdge.pixelB.edgeList);
//
//                }
//
//                segments[index] = segment;
//
//            });
//        }
//
//        executorService.shutdown();
//        //noinspection StatementWithEmptyBody
//        while (!executorService.isTerminated()) {}
//
//        return segments;
//    }
//
//    Segment[] divideSegment(Segment segment, int numberOfSegments) {
//        final Segment[] segments = new Segment[numberOfSegments];
//        final PixelEdge[] weakestPixelEdges = new PixelEdge[numberOfSegments - 1];
//
//        final PriorityQueue<PixelEdge> priorityQueue = new PriorityQueue<>(Collections.reverseOrder());
//        priorityQueue.addAll(segment.edges);
//
//        // Finding the PixelEdges with highest distance in segment
//        for (int i = 0; i < weakestPixelEdges.length; i ++) {
//            PixelEdge pixelEdge = priorityQueue.remove();
//            while (segment.getSize(pixelEdge.pixelB) < (double) segment.pixelCount / 10) {
//                pixelEdge = priorityQueue.remove();
//            }
////            System.out.println(segment.getSize(pixelEdge.pixelB));
//            weakestPixelEdges[i] = pixelEdge;
//        }
//
//        // Segment 0 is the entire segment minus the other segments
//        segments[0] = segment.copyFrom(segment.root);
//
//        // Making a copy of the other segments
//        for (int i = 0; i < weakestPixelEdges.length; i ++) {
//            segments[i+1] = segment.copyFrom(weakestPixelEdges[i].pixelB);
//        }
//
//        return segments;
//    }

    Solution[] createInitialSolutions(int solutionCount, int minimumSegmentCount, int maximumSegmentCount) {
        final Solution[] solutions = new Solution[solutionCount];

        // Create one solution for each iteration
        final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        // Creating a wrapper class to bind PixelEdge objects to a Segment
        final class SegmentPixelEdge implements Comparable<SegmentPixelEdge>{

            private final Segment segment;
            private final PixelEdge pixelEdge;

            private SegmentPixelEdge(Segment segment, PixelEdge pixelEdge) {
                this.segment = segment;
                this.pixelEdge = pixelEdge;
            }

            @Override
            public int compareTo(SegmentPixelEdge o) {
                return this.pixelEdge.compareTo(o.pixelEdge);
            }
        }

        for (int i = 0; i < solutionCount; i ++) {
            final int index = i;
            executorService.execute(() -> {
                final HashMap<Pixel, Segment> visitedPixels = new HashMap<>();
                final int segmentCount = minimumSegmentCount + random.nextInt(maximumSegmentCount - minimumSegmentCount + 1);
                final Segment[] segments = new Segment[segmentCount];
                final boolean[][][] pixelEdges = new boolean[imageParser.height][imageParser.width][4];

                final PriorityQueue<SegmentPixelEdge> priorityQueue = new PriorityQueue<>();

                for (int j = 0; j < segmentCount; j ++) {

                    // Selecting a random Pixel, not visited before, as root for a new MST
                    int rootRow, rootColumn;
                    Pixel rootPixel;
                    do {
                        rootRow = random.nextInt(imageParser.height);
                        rootColumn = random.nextInt(imageParser.width);
                        rootPixel = pixels[rootRow][rootColumn];
                    }
                    while (visitedPixels.containsKey(rootPixel));

                    segments[j] = new Segment(rootPixel);
                    visitedPixels.put(rootPixel, segments[j]);
                    for (PixelEdge pixelEdge : rootPixel.edges) {
                        if (pixelEdge != null) {
                            priorityQueue.add(new SegmentPixelEdge(segments[j], pixelEdge));
                        }
                    }
                }

                // Using Prim's algorithm to create the Segments for the Solution
                while (!priorityQueue.isEmpty()) {
                    final SegmentPixelEdge segmentPixelEdge = priorityQueue.remove();
                    final Segment segment = segmentPixelEdge.segment;
                    final PixelEdge currentPixelEdge = segmentPixelEdge.pixelEdge;

                    // Check if Pixel is not already in Solution
                    if (!visitedPixels.containsKey(currentPixelEdge.pixelB)) {
                        // Adding the new Pixel to the Segment
                        segment.add(currentPixelEdge.pixelB);
                        visitedPixels.put(currentPixelEdge.pixelB, segment);
                        for (PixelEdge pixelEdge : currentPixelEdge.pixelB.edges) {
                            if (pixelEdge != null) {
                                priorityQueue.add(new SegmentPixelEdge(segment, pixelEdge));
                            }
                        }
                    }
                    else if (!visitedPixels.containsKey(currentPixelEdge.pixelA)) {
                        // Adding the new Pixel to the Segment
                        segment.add(currentPixelEdge.pixelA);
//                        final int pos = currentPixelEdge.pixelA.getEdgeIndex(currentPixelEdge);
//                        pixelEdges[currentPixelEdge.pixelA.row][currentPixelEdge.pixelA.column][pos] = true;
//                        pixelEdges[currentPixelEdge.pixelB.row][currentPixelEdge.pixelB.column][(pos + 2) % 4] = true;
                        visitedPixels.put(currentPixelEdge.pixelA, segment);
                        for (PixelEdge pixelEdge : currentPixelEdge.pixelA.edges) {
                            if (pixelEdge != null) {
                                priorityQueue.add(new SegmentPixelEdge(segment, pixelEdge));
                            }
                        }
                    }

                }

                //Linking PixelEdges to Pixels in Segments
                for (Segment segment : segments) {
                    for (Pixel pixel : segment.pixels) {
                        final Pixel neighbourUp = pixel.pixels[0];
                        final Pixel neighbourLeft = pixel.pixels[3];
                        for (int j = 0; j < 4; j ++) {
                            final Pixel neighbour = pixel.pixels[j];
                            if (neighbour != null) {
                                if (visitedPixels.get(neighbour) == segment) {
                                    pixelEdges[pixel.row][pixel.column][j] = true;
                                    pixelEdges[neighbour.row][neighbour.column][Pixel.mapDirection(j)] = true;
                                }
                            }
                        }
                    }
                }

                Solution newSolution = new Solution(pixelEdges, segments);
                solutions[index] = newSolution;
                Platform.runLater(gui::addProgress);
                edgeValueAndDeviation(newSolution);
            });
        }

        executorService.shutdown();
        //noinspection StatementWithEmptyBody
        while (!executorService.isTerminated()) {}

        return solutions;
    }

    Solution[] singlePointCrossover(Solution[] solutions, int offspringCount, int minSegmentCount, int maxSegmentCount, boolean nsga2, int numberOfTournaments, double mutationRate) {
        final Solution[] offspring = new Solution[offspringCount];
        final int size = imageParser.height * imageParser.width;

        final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < offspringCount; i ++) {

            final int index = i;

            executorService.execute(() -> {

                Solution child = null;
                while (child == null) {
                    // Selecting a random point for single point crossover
                    final int splitPoint = random.nextInt(size);
                    // Selecting two parents
                    Solution parent1, parent2;
                    parent1 = tournamentSelection(solutions, numberOfTournaments);
                    parent2 = tournamentSelection(solutions, numberOfTournaments);
                    child = new Solution(parent1, parent2, splitPoint, pixels);
                    if (child.segments.length < minSegmentCount || child.segments.length > maxSegmentCount) {
                        child = null;
                    }

                }
                double p = Math.random();
                if(p <= mutationRate) {
                    mutate(child, minSegmentCount, maxSegmentCount);
                }
                offspring[index] = child;
                Platform.runLater(gui::addProgress);
            });
        }

        executorService.shutdown();
        //noinspection StatementWithEmptyBody
        while (!executorService.isTerminated()) {}

        return offspring;
    }

    private Solution tournamentSelection(Solution[] solutions, int numberOfTournaments) {
        int bestIndex = Integer.MAX_VALUE;

        for (int i = 0; i < numberOfTournaments; i ++) {
            final int contender = random.nextInt(solutions.length);
            if (contender < bestIndex) {
                bestIndex = contender;
            }
        }

        return solutions[bestIndex];
    }

    //euclidean distance in RGB color space
    public double euclideanRGB(Color Color1, Color Color2){

        int differenceRed = Color1.getRed() - Color2.getRed();
        int differenceGreen = Color1.getGreen() - Color2.getGreen();
        int differenceBlue = Color1.getBlue() - Color2.getBlue();

        return Math.sqrt(Math.pow(differenceRed, 2) + Math.pow(differenceGreen, 2) + Math.pow(differenceBlue, 2));

    }
    public void NSGA2(Solution[] solutions){

        for(Solution solution:solutions){
            
        }

    }

    void evaluate(Solution[] solutions) {
        final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < solutions.length; i ++) {
            final int index = i;

            executorService.execute(() -> {
                edgeValueAndDeviation(solutions[index]);
                gui.addProgress();
            });
        }

        executorService.shutdown();
        //noinspection StatementWithEmptyBody
        while (!executorService.isTerminated()) {}
    }

    private Pixel[][] createPixels() {

        final Pixel[][] pixels = new Pixel[imageParser.height][imageParser.width];

        // Creating the Pixel objects
        for (int i = 0; i < imageParser.height; i ++) {
            for (int j = 0; j < imageParser.width; j ++) {
                pixels[i][j] = new Pixel(i, j, imageParser.pixels[i][j]);
            }
        }

        // Calculating Pixel neighbours and distances
        for (int i = 0; i < imageParser.height; i ++) {
            for (int j = 0; j < imageParser.width; j ++) {

                final Pixel currentPixel = pixels[i][j];

                // Has neighbour above
                if (i > 0) {
                    final PixelEdge pixelEdge = pixels[i-1][j].edges[2];
                    currentPixel.pixels[0] = pixels[i-1][j];
                    pixels[i-1][j].pixels[2] = currentPixel;
                    currentPixel.edges[0] = pixelEdge;
                }
                // Has neighbour left
                if (j > 0) {
                    final PixelEdge pixelEdge = pixels[i][j-1].edges[1];
                    currentPixel.pixels[3] = pixels[i][j-1];
                    pixels[i][j-1].pixels[1] = currentPixel;
                    currentPixel.edges[3] = pixelEdge;
                }
                // Has neighbour below
                if (i < imageParser.height - 1) {
                    final PixelEdge pixelEdge = new PixelEdge(currentPixel, pixels[i+1][j], euclideanRGB(currentPixel.color, pixels[i+1][j].color));
                    currentPixel.edges[2] = pixelEdge;
                }
                // Has neighbour right
                if (j < imageParser.width - 1) {
                    final PixelEdge pixelEdge = new PixelEdge(currentPixel, pixels[i][j+1], euclideanRGB(currentPixel.color, pixels[i][j+1].color));
                    currentPixel.edges[1] = pixelEdge;
                }

//                currentPixel.createEdgeList();
            }
        }

        return pixels;
    }

    //Calculated the edge Values and overall deviation for a solution
    public void edgeValueAndDeviation(Solution solution){
        double edgeValue = 0.0;
        double overAllDeviation = 0.0;

        for(Segment segment:solution.segments){
            segment.overallDeviation = 0.0;
            segment.edgeValue = 0.0;

            for(Pixel pixel: segment.pixels){
                //Calculate overall deviation (summed rbg distance from current pixel to centroid of segment)
                double o = euclideanRGB(pixel.color, segment.getColor());
                overAllDeviation += o;
                segment.overallDeviation += o;
                //Calculate the edgeValues. Calculate rgb distance between the current pixel and all its neighbours
                //that are not in the same segment
                for(int i = 0; i < 4; i ++){
                    PixelEdge pixelEdge = pixel.edges[i];
                    if (pixelEdge == null) {
                        continue;
                    }
                    //Not connected to neighbour
                    Pixel neighbourPixel = pixelEdge.pixelA;
                    if (neighbourPixel == pixel) {
                        neighbourPixel = pixelEdge.pixelB;
                    }

                    if(!segment.pixels.contains(neighbourPixel)){
                        double e = pixelEdge.distance;
                        edgeValue += e;    //add the distance of the edge if the neighbouring pixels are not in the same segment
                        segment.edgeValue += e;
                    }
                }
            }
        }
        solution.scoreSolution(edgeValue,overAllDeviation, this.edgeWeight,this.overallDeviationWeight);
    }

    public Solution[] nonDominationSorting(Solution[] solutions, Solution[] offspring, int populationSize){

        //The list of returned solutions
        final Solution[] returnedSolutions = new Solution[populationSize];
        int returnedSolutionCount = 0;

        final Solution[] population = new Solution[solutions.length + offspring.length];
        System.arraycopy(solutions, 0, population, 0, solutions.length);
        System.arraycopy(offspring, 0, population, solutions.length, offspring.length);

        //Add all the solutions of the different ranks in this hashmap and prioritize ranks from low to high
        //Se Stigen! Jeg bruker hashmap og priority queue! :) Flink gutt ;)
        HashMap<Integer,ArrayList<Solution>> dominationMap = new HashMap<>();
        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>();

        for(Solution solution: population){
            //create domination ranks for every solution
            int dominationRank = dominationRank(solutions, solution);
            solution.dominationRank = dominationRank;

            //If the dominationrank already exist, add to the arraylist or if not create the arraylist which contains a domination edge
            if(dominationMap.containsKey(dominationRank)){
                dominationMap.get(dominationRank).add(solution);
            }
            else{
                //create new hashmap input
                ArrayList<Solution> solutionList = new ArrayList<>();
                solutionList.add(solution);
                dominationMap.put(dominationRank,solutionList);
                priorityQueue.add(dominationRank);

            }
        }
        Iterator itr = priorityQueue.iterator();

        //iterate over the edges, from best edge to worst
        while (itr.hasNext()){
            int nextRank = priorityQueue.poll();

            ArrayList<Solution> dominationEdge = dominationMap.get(nextRank);

            if(returnedSolutionCount + dominationEdge.size() <= returnedSolutions.length){
                for(Solution solution: dominationEdge){
                    returnedSolutions[returnedSolutionCount] = solution;
                    returnedSolutionCount += 1;
                }
            }else{
                //The number of solutions we need to select from the current dominationEdge
                int neededSolutions = returnedSolutions.length - returnedSolutionCount;
                Solution[] lastNeededSolutions = crowdingDistanceSort(dominationEdge,neededSolutions);

                for(Solution solution: lastNeededSolutions){
                    returnedSolutions[returnedSolutionCount] = solution;
                    returnedSolutionCount += 1;
                }
            }

            //we've found all our solutions
            if(returnedSolutionCount == returnedSolutions.length){
                break;
            }
        }
        return returnedSolutions;
    }

    public void mutate(Solution solution, int minSegCount, int maxSegCount){
        /*double worstEdgeValue = Double.MAX_VALUE;
        Segment worstEdgeValueSegment = null;

        for(Segment segment:solution.segments){
            double averageEdge = segment.edgeValue / (double) segment.pixels.size();

            if(averageEdge < worstEdgeValue){
                worstEdgeValue = averageEdge;
                worstEdgeValueSegment = segment;
            }
        }
        combineSegment(solution,worstEdgeValueSegment);*/
       int currentSegCount = solution.segments.length;

        if(currentSegCount == minSegCount){
            //split segment
            double worstOverallDeviation = Double.MIN_VALUE;
            Segment worstOverallDeviationSegment = null;

            for(Segment segment:solution.segments){
                double averageDeviation = segment.overallDeviation / (double) segment.pixels.size();
                if(averageDeviation > worstOverallDeviation){
                    worstOverallDeviation = averageDeviation;
                    worstOverallDeviationSegment = segment;
                }
            }
            splitSegment(solution,worstOverallDeviationSegment);

        }else if(currentSegCount == maxSegCount){
            //combine segment
            double worstEdgeValue = Double.MAX_VALUE;
            Segment worstEdgeValueSegment = null;

            for(Segment segment:solution.segments){
                double averageEdge = segment.edgeValue / (double) segment.pixels.size();

                if(averageEdge < worstEdgeValue){
                    worstEdgeValue = averageEdge;
                    worstEdgeValueSegment = segment;
                }
            }
            combineSegment(solution,worstEdgeValueSegment);
        }else{
            double p = Math.random();

            if(p <= 0.5){
                //combine
                double worstEdgeValue = Double.MAX_VALUE;
                Segment worstEdgeValueSegment = null;

                for(Segment segment:solution.segments){
                    double averageEdge = segment.edgeValue / (double) segment.pixels.size();

                    if(averageEdge < worstEdgeValue){
                        worstEdgeValue = averageEdge;
                        worstEdgeValueSegment = segment;
                    }
                }
                combineSegment(solution,worstEdgeValueSegment);
            }else{
                //split
                double worstOverallDeviation = Double.MIN_VALUE;
                Segment worstOverallDeviationSegment = null;

                for(Segment segment:solution.segments){
                    double averageDeviation = segment.overallDeviation / (double) segment.pixels.size();
                    if(averageDeviation > worstOverallDeviation){
                        worstOverallDeviation = averageDeviation;
                        worstOverallDeviationSegment = segment;
                    }
                }
                splitSegment(solution,worstOverallDeviationSegment);
            }

        }
    }

    public void splitSegment(Solution solution, Segment segment){

    }

    //combines the segment with the neighbouring segment with the lowest border contrast
    public void combineSegment(Solution solution, Segment segment){
        //Only one segment, no point in combining anything
        if(solution.segments.length == 1){
            return;
        }
        boolean[][][] genoType = solution.pixelEdges;

        HashMap<Segment,Double> notConnectedNeighbours = new HashMap<>();
        HashMap<Segment,ArrayList<PixelEdge>> border = new HashMap<>();   //determines the segment border

        for(Pixel pixel: segment.pixels){
            for(int i=0; i<pixel.edges.length; i++){
                if(pixel.edges[i] != null && !genoType[pixel.row][pixel.column][i]){
                    //we know that the pixels neighbour is not null (it has a neighbour) and it is also not connected to its neighbour which we want it to be

                    Pixel connectingPixel = pixel.pixels[i];    //find the pixel we want to connect to

                    //find the segment with connectingPixel in it. Add the segment to notConnectedNeighbours if it is not already there
                    for(Segment neighbourSegment: solution.segments){
                        if(neighbourSegment == segment){
                            continue;
                        }
                        if(neighbourSegment.pixels.contains(connectingPixel)){
                            //if neighboursegment does not exist add the edge distance, if it does add to the sum
                            //Also add the bordering pixels to the border map
                            if(!notConnectedNeighbours.containsKey(neighbourSegment)){
                                notConnectedNeighbours.put(neighbourSegment,pixel.edges[i].distance);

                                ArrayList<PixelEdge> borderPixels = new ArrayList<>();
                                borderPixels.add(pixel.edges[i]);
                                border.put(neighbourSegment,borderPixels);
                            }else{
                                //notConnectedNeighbours.put(neighbourSegment,notConnectedNeighbours.get(neighbourSegment)+pixel.edges[i].distance);
                                border.get(neighbourSegment).add(pixel.edges[i]);
                                notConnectedNeighbours.merge(neighbourSegment,pixel.edges[i].distance,Double::sum);
                            }
                            break;
                        }
                    }
                }
            }
        }

        Segment[] newSegmentList = new Segment[solution.segments.length - 1];

        //we want to merge the segment with the segment that has the lowest border contrast(most similar)
        Segment lowestEdgeValueSegment = null;
        Double lowestEdgeValue = Double.MAX_VALUE;

        for(Segment segment1: notConnectedNeighbours.keySet()){

            //To not favor small borders over large borders we divide the edgeValue by the size of the border
            double averageContrast = notConnectedNeighbours.get(segment1) / (double) border.get(segment1).size();
            if(lowestEdgeValue > averageContrast){
                lowestEdgeValueSegment = segment1;
                lowestEdgeValue = averageContrast;
            }
        }
        //now we have the segments we want to merge the border!

        //create the new combined segment
        Segment newSegment = new Segment();

        for(Pixel pixel: segment.pixels){
            newSegment.add(pixel);
        }
        for(Pixel pixel: lowestEdgeValueSegment.pixels){
            newSegment.add(pixel);
        }

        int segmentCounter = 0;
        for(Segment segment1: solution.segments){
            if(segment1 == segment || segment1 == lowestEdgeValueSegment){
                continue;
            }
            newSegmentList[segmentCounter++] = segment1;
        }

        newSegmentList[newSegmentList.length - 1] = newSegment;
        //set the new segmentlist
        solution.segments = newSegmentList;

        //change the genotype so that the pixels are connected (change boolean values to true)
        for (PixelEdge borderPixels: border.get(lowestEdgeValueSegment)){
            Pixel pixel1 = borderPixels.pixelB;
            Pixel pixel2 = borderPixels.pixelA;

            for(int i=0;i<pixel1.pixels.length;i++){
                if(pixel1.pixels[i] == pixel2){
                    genoType[pixel1.row][pixel1.column][i] = true;
                    genoType[pixel2.row][pixel2.column][(i + 2) % 4] = true;
                }
            }
        }

        }

    public Solution[] crowdingDistanceSort(ArrayList<Solution> dominationEdge, int neededSolutions){
        Solution[] returnedSolutions = new Solution[neededSolutions];

        ArrayList<Solution> edgeSort = new ArrayList<>(dominationEdge);
        ArrayList<Solution> deviationSort = new ArrayList<>(dominationEdge);

        edgeSort.sort(edgeValueComparator);
        deviationSort.sort(overallDeviationComparator);

        PriorityQueue<Solution> priorityQueue = new PriorityQueue<>(crowdingDistanceComparator);

        //reset values for solution
        for(Solution solution: dominationEdge){
            solution.crowdingDistance = 0;
        }

        //setting the crowding distance of edges as far as possible.
        deviationSort.get(0).crowdingDistance = Double.MAX_VALUE;
        deviationSort.get(deviationSort.size() - 1).crowdingDistance = Double.MAX_VALUE;
        priorityQueue.add(deviationSort.get(0));
        priorityQueue.add(deviationSort.get(deviationSort.size() - 1));


        double deviationMax = deviationSort.get(deviationSort.size()-1).overallDeviation;
        double deviationMin = deviationSort.get(0).overallDeviation;

        /*double edgeValueMax = edgeSort.get(0).edgeValue;
        double edgeValueMin = edgeSort.get(edgeSort.size()-1).edgeValue;*/

        double edgeValueMax = -edgeSort.get(edgeSort.size()-1).edgeValue;
        double edgeValueMin = -edgeSort.get(0).edgeValue;

        for(int i=1; i < deviationSort.size() - 1;i++){
            deviationSort.get(i).crowdingDistance = Math.abs((deviationSort.get(i-1).overallDeviation / deviationSort.get(i+1).overallDeviation)/(deviationMax-deviationMin)) + Math.abs(-deviationSort.get(i-1).edgeValue / (-deviationSort.get(i+1).edgeValue)/(edgeValueMax-edgeValueMin));
            //System.out.println(deviationSort.get(i).crowdingDistance);
            priorityQueue.add(deviationSort.get(i));
        }

        for(int i=0; i<returnedSolutions.length;i++){
            returnedSolutions[i] = priorityQueue.poll();
        }

        return returnedSolutions;
    }

    //Returns the dominations rank (1 + number of solutions dominating the solution) for a solution based on the population
    public int dominationRank(Solution[] population, Solution solution){

        int dominationRank = 1;

        for(Solution someSolution: population){
            if(solution == someSolution){
                continue;
            }
            if(solution.isDominatedBy(someSolution)){
                dominationRank += 1;
            }
        }
        return dominationRank;
    }

    //implements rank selection for the weighted sum
    public Solution[] selectWeightedSum(Solution[] population, Solution[] offspring, int populationSize){

        ArrayList<Solution> priorityQueue = new ArrayList<>(Arrays.asList(population));
        Collections.addAll(priorityQueue, offspring);
        priorityQueue.sort(weightedSumComparator);

        //final Solution[] survivors = new Solution[populationSize];
        final ArrayList<Solution> survivors = new ArrayList<>();
        Solution[] survivorArray = new Solution[populationSize];

        int index = 0;

        while (index < populationSize) {
            double p = Math.random();

            int rank = priorityQueue.size();

            int rankSum = 0;
            for(int i=priorityQueue.size();i>0;i--){
                rankSum += i;
            }
            Double cumulativeProbability = 0.0;
            int listIndex = 0;

            while (!priorityQueue.isEmpty()) {
                //Solution solution = priorityQueue.get(listIndex);
                cumulativeProbability += (double) rank/rankSum;

                if(p <= cumulativeProbability){
                    survivors.add(priorityQueue.remove(listIndex));
                    index++;
                    //survivors[index ++] = priorityQueue.remove(listIndex);
                    break;
                }
                listIndex++;
                rank--;
            }
        }
        survivors.sort(weightedSumComparator);

        return survivors.toArray(survivorArray);
    }

    void writeImage(Solution solution) {
        //Black White Image
        final int width = solution.pixelEdges[0].length;
        final int height = solution.pixelEdges.length;
        final WritableImage imageBlack = new WritableImage(width, height);
        final PixelWriter pixelWriterBlack = imageBlack.getPixelWriter();

        for (Segment segment : solution.segments) {
            for (Pixel pixel : segment.pixels) {
                if (pixel.column == 0 || pixel.column == width-1 || pixel.row == 0 || pixel.row == height-1) {
                    pixelWriterBlack.setColor(pixel.column, pixel.row, javafx.scene.paint.Color.BLACK);
                }
                else if (!segment.containsAllNeighbours(pixel)) {
                    pixelWriterBlack.setColor(pixel.column, pixel.row, javafx.scene.paint.Color.BLACK);
                }
                else {
                    pixelWriterBlack.setColor(pixel.column, pixel.row, javafx.scene.paint.Color.WHITE);
                }
            }
        }

        BufferedImage bImage = SwingFXUtils.fromFXImage(imageBlack, null);
        imagesWritten ++;
        File file = new File("./Student Images/" + imagesWritten + "_segments=" + solution.segments.length + ".png");
        try {
            ImageIO.write(bImage, "png", file);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

