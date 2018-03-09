import javafx.application.Platform;

import java.awt.*;
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

    ImageSegmentation(ImageParser imageParser, GUI gui, double edgeWeight, double overallDeviationWeight){
        this.imageParser = imageParser;
        this.gui = gui;
        this.edgeWeight = edgeWeight;
        this.overallDeviationWeight = overallDeviationWeight;
        pixels = createPixels();
        random = new Random();
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
                final boolean[][] visitedPixels = new boolean[imageParser.height][imageParser.width];
                final int segmentCount = minimumSegmentCount + random.nextInt(maximumSegmentCount - minimumSegmentCount + 1);
                final Segment[] segments = new Segment[segmentCount];
                final ArrayList<PixelEdge>[] pixelEdges = new ArrayList[imageParser.height * imageParser.width];
                for (int j = 0; j < imageParser.height * imageParser.width; j ++) {
                    pixelEdges[j] = new ArrayList<>();
                }

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
                    while (visitedPixels[rootRow][rootColumn]);

                    segments[j] = new Segment(rootPixel);
                    visitedPixels[rootRow][rootColumn] = true;
                    for (PixelEdge pixelEdge : rootPixel.edgeList) {
                        priorityQueue.add(new SegmentPixelEdge(segments[j], pixelEdge));
                    }
                }

                // Using Prim's algorithm to create the Segments for the Solution
                while (!priorityQueue.isEmpty()) {
                    final SegmentPixelEdge segmentPixelEdge = priorityQueue.remove();
                    final Segment segment = segmentPixelEdge.segment;
                    final PixelEdge currentPixelEdge = segmentPixelEdge.pixelEdge;

                    // Check if Pixel is not already in Solution
                    if (!visitedPixels[currentPixelEdge.pixelB.row][currentPixelEdge.pixelB.column]) {
                        // Adding the new Pixel to the Segment
                        segment.add(currentPixelEdge.pixelB);
                        pixelEdges[currentPixelEdge.pixelA.column + currentPixelEdge.pixelA.row * imageParser.width].add(currentPixelEdge);

//                        visitedPixels.add(currentPixelEdge.pixelB);
                        visitedPixels[currentPixelEdge.pixelB.row][currentPixelEdge.pixelB.column] = true;
                        for (PixelEdge pixelEdge : currentPixelEdge.pixelB.edgeList) {
                            priorityQueue.add(new SegmentPixelEdge(segment, pixelEdge));
                        }
                    }
                    else if (!visitedPixels[currentPixelEdge.pixelA.row][currentPixelEdge.pixelA.column]) {
                        // Adding the new Pixel to the Segment
                        segment.add(currentPixelEdge.pixelA);
                        pixelEdges[currentPixelEdge.pixelB.column + currentPixelEdge.pixelB.row * imageParser.width].add(currentPixelEdge);
                        visitedPixels[currentPixelEdge.pixelA.row][currentPixelEdge.pixelA.column] = true;
//                        visitedPixels.add(currentPixelEdge.pixelA);
                        for (PixelEdge pixelEdge : currentPixelEdge.pixelA.edgeList) {
                            priorityQueue.add(new SegmentPixelEdge(segment, pixelEdge));
                        }
                    }

                }

                Solution newSolution = new Solution(pixelEdges, segments);
                solutions[index] = newSolution;
                Platform.runLater(() -> gui.setProgress((double) (index+1) / solutionCount));
                edgeValueAndDeviation(newSolution);
            });
        }

        executorService.shutdown();
        //noinspection StatementWithEmptyBody
        while (!executorService.isTerminated()) {}

        return solutions;
    }

    //Crossover
//    public Solution[] crossover(Solution[] solutions, Solution[] archive, double crossoverRate){
//
//        for(Solution solution: solutions){
//            Genotype genotype = createGenotype(solution);
////            System.out.println(genotype.pixelEdges.length);
////            createPhenotype(genotype);
//        }
//
//        return null;
//    }

//    public Genotype createGenotype(Solution solution){
//
//        PixelEdge[] pixelsEdges = new PixelEdge[imageParser.width*imageParser.height];
//
//        for (Segment segment:solution.segments){
//            for(Pixel pixel: segment.pixels){
//
//                //Select a random index for the edge
//                int edgeIndex = random.nextInt(pixel.edgeList.size());
//
//                //pixelEdge to insert into pixelEdges list
//                PixelEdge pixelEdge = null;
//
//                //The pixeledge we select must be in same segment as the current pixel
//                for(int i=0; i<pixel.edgeList.size();i++){
//                    //Get the pixelEdge
//                    PixelEdge somePixelEdge = pixel.edgeList.get(edgeIndex);
//
//                    //make sure that selected pixeledge must have both pixels in same segment and the pixels cannot point to each other.
//                    if(segment.pixelEdgeMap.containsKey(somePixelEdge.pixelB) && (pixelsEdges[imageParser.height*somePixelEdge.pixelB.column + somePixelEdge.pixelB.row] == null || pixelsEdges[imageParser.height*somePixelEdge.pixelB.column + somePixelEdge.pixelB.row].pixelB != pixel)){
//                        if(pixelEdge == null || somePixelEdge.distance < pixelEdge.distance){   //take edge with lowest distance?
//                         pixelEdge = somePixelEdge; //we found a pixel
//                        }
//                         //break;
//                    }
//
//                    //get next index, given that edgeindex not always starts at 0 we need modulo
//                    edgeIndex = (edgeIndex +1) % pixel.edgeList.size();
//                }
//
//                //if we found no eligible pixel during the loop pixelEdge points to itself (is null)
//                pixelsEdges[imageParser.height*pixel.column + pixel.row] = pixelEdge;
//            }
//        }
//
//        /*int nullCounter = 0;
//        for(PixelEdge pixelEdge:pixelsEdges){
//            if(pixelEdge == null){
//                nullCounter +=1;
//            }
//        }
//        System.out.println(nullCounter);
//        System.out.println(solution.segments.length);*/
//
//        return new Genotype(pixelsEdges);
//    }


//    public Solution createPhenotype(Genotype genotype){
//
//        ArrayList<Segment> segments = new ArrayList<>();
//        /*
//        final HashMap<Pixel, ArrayList<PixelEdge>> pixelEdgeMap = new HashMap<>();
//
//        for(PixelEdge pixelEdge: genotype.pixelEdges){
//            if(!pixelEdgeMap.containsKey(pixelEdge.pixelA)){
//                pixelEdgeMap.put(pixelEdge.pixelA,new ArrayList<>());
//            }
//            if(!pixelEdgeMap.containsKey(pixelEdge.pixelB)){
//                pixelEdgeMap.put(pixelEdge.pixelB,new ArrayList<>());
//            }
//
//        }*/
//        //final HashMap<Pixel, Integer> pixelSegmentMap = new HashMap<>();
//
//        for(PixelEdge pixelEdge:genotype.pixelEdges){
//            if(pixelEdge != null){
//
//            boolean foundSegment = false;
//            for(Segment segment:segments){
//                if(segment.pixels.contains(pixelEdge.pixelA)){
//                    segment.pixels.add(pixelEdge.pixelB);
//                    foundSegment = true;
//                }
//            }
//            if(!foundSegment){
////                Segment newSegment = new Segment(pixelEdge.pixelA);
////                newSegment.pixels.add(pixelEdge.pixelB);
////                segments.add(newSegment);
//            }
//            }
//            /*if(pixelSegmentMap.containsKey(pixelEdge.pixelA)){
//                pixelSegmentMap.put(pixelEdge.pixelA,pixelSegmentMap);
//            }
//            if(pixelSegmentMap.containsKey(pixelEdge.pixelB)){
//                pixelSegmentMap.put(pixelEdge.pixelB,new ArrayList<>());
//            }*/
//            //System.out.println("sadfasdf");
//
//        }
//
//        int pixelCount = 0;
//        System.out.println(segments.size());
//        for(Segment segment:segments){
//            pixelCount += segment.pixels.size();
//        }
//        System.out.println(pixelCount);
//        return null;
//    }

    Solution[] singlePointCrossover(Solution[] solutions, int offspringCount) {
        final Solution[] offspring = new Solution[offspringCount];
        final int size = imageParser.height * imageParser.width;

        final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < offspringCount; i += 2) {

            final int index = i;

            executorService.execute(() -> {
                // Selecting two parents
                //@TODO Add a selection method for parent selection
                final Solution parent1 = solutions[random.nextInt(solutions.length)];
                final Solution parent2 = solutions[random.nextInt(solutions.length)];
    //            final Solution parent1 = solutions[0];
    //            final Solution parent2 = solutions[1];

                // Selecting a random point for single point crossover
                final int splitPoint = random.nextInt(size);

                final Solution offspring1 = new Solution(parent1, parent2, splitPoint, pixels);
                final Solution offspring2 = new Solution(parent2, parent1, splitPoint, pixels);

                offspring[index] = offspring1;
                offspring[index+1] = offspring2;
                Platform.runLater(() ->  gui.setProgress((double)(index+2)/offspringCount));
            });
        }

        executorService.shutdown();
        //noinspection StatementWithEmptyBody
        while (!executorService.isTerminated()) {}

        return offspring;
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
                    currentPixel.edges[0] = pixelEdge;
                }
                // Has neighbour left
                if (j > 0) {
                    final PixelEdge pixelEdge = pixels[i][j-1].edges[1];
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

                currentPixel.createEdgeList();
            }
        }

        return pixels;
    }

    //Calculated the edge Values and overall deviation for a solution
    public void edgeValueAndDeviation(Solution solution){
        double edgeValue = 0.0;
        double overAllDeviation = 0.0;

        for(Segment segment:solution.segments){
            for(Pixel pixel: segment.pixels){
                //Calculate overall deviation (summed rbg distance from current pixel to centroid of segment)
                overAllDeviation += euclideanRGB(pixel.color, segment.getColor());

                //Calculate the edgeValues. Calculate rgb distance between the current pixel and all its neighbours
                //that are not in the same segment
                for(PixelEdge pixelEdge: pixel.edgeList){
                    Pixel neighbourPixel = pixelEdge.pixelB;

                    if(!segment.pixels.contains(neighbourPixel)){
                        edgeValue += pixelEdge.distance;    //add the distance of the edge if the neighbouring pixels are not in the same segment
                    }
                }
            }
        }
        solution.scoreSolution(edgeValue,overAllDeviation, this.edgeWeight,this.overallDeviationWeight);
    }

    public Solution[] nonDominationSorting(Solution[] solutions, int populationSize){

        //The list of returned solutions
        Solution[] returnedSolutions = new Solution[populationSize];
        int returnedSolutionCount = 0;

        //Add all the solutions of the different ranks in this hashmap and prioritize ranks from low to high
        //Se Stigen! Jeg bruker hashmap og priority queue! :)
        HashMap<Integer,ArrayList<Solution>> dominationMap = new HashMap<>();
        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>();

        for(Solution solution: solutions){
            //create domination ranks for every solution
            int dominationRank = dominationRank(solutions,solution);
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

                //REMOVE THIS
                //return null;

            }

            //we've found all our solutions
            if(returnedSolutionCount == returnedSolutions.length){
                break;
            }
        }
        return returnedSolutions;
    }

    //TODO: Crowding distance!
    public Solution[] crowdingDistanceSort(ArrayList<Solution> dominationEdge, int neededSolutions){
        Solution[] returnedSolutions = new Solution[neededSolutions];
        Solution[] edgeSort = new Solution[dominationEdge.size()];
        Solution[] deviationSort = new Solution[dominationEdge.size()];

        Comparator<Solution> comparator = new crowdingDistanceComparator();

        PriorityQueue<Solution> priorityQueue = new PriorityQueue<>(comparator);
        //reset values for solution
        for(Solution solution: dominationEdge){
            solution.deviationSelected = false;
            solution.edgeValueSelected = false;
            solution.crowdingDistance = 0;
        }
        for(int i=0; i<dominationEdge.size();i++){

            Double bestDeviation = Double.MAX_VALUE;
            Double bestEdgeValue = Double.MIN_VALUE;
            Solution bestDeviationSoltion = null;
            Solution bestEdgeValueSoltion = null;


            for(Solution solution: dominationEdge){
                if(solution.edgeValue > bestEdgeValue && !solution.edgeValueSelected){
                    bestEdgeValue = solution.edgeValue;
                    bestEdgeValueSoltion = solution;
                }
                if(solution.overallDeviation < bestDeviation && !solution.deviationSelected){
                    bestDeviation = solution.overallDeviation;
                    bestDeviationSoltion = solution;
                }
            }

            if(bestDeviationSoltion == null || bestEdgeValueSoltion == null){
                System.out.println("nullerror");
            }
            bestEdgeValueSoltion.edgeValueSelected = true;
            bestDeviationSoltion.deviationSelected = true;
            edgeSort[i] = bestEdgeValueSoltion;
            deviationSort[i] = bestDeviationSoltion;
        }

        //setting the crowding distance of edges as far as possible.
        deviationSort[0].crowdingDistance = Double.MAX_VALUE;
        deviationSort[deviationSort.length - 1].crowdingDistance = Double.MAX_VALUE;
        priorityQueue.add(deviationSort[0]);
        priorityQueue.add(deviationSort[deviationSort.length - 1]);

        double deviationMax = deviationSort[deviationSort.length-1].overallDeviation;
        double deviationMin = deviationSort[0].overallDeviation;

        double edgeValueMax = edgeSort[0].edgeValue;
        double edgeValueMin = edgeSort[edgeSort.length-1].edgeValue;

        for(int i=1; i < deviationSort.length - 1;i++){
            deviationSort[i].crowdingDistance = Math.abs(deviationSort[i-1].overallDeviation / deviationSort[i+1].overallDeviation)/(deviationMax-deviationMin) + Math.abs(deviationSort[i-1].edgeValue / deviationSort[i+1].edgeValue)/(edgeValueMax-edgeValueMin);
            priorityQueue.add(deviationSort[i]);
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
}

class crowdingDistanceComparator implements Comparator<Solution>
{
    @Override
    public int compare(Solution x, Solution y)
    {
        if (x.crowdingDistance > y.crowdingDistance)
        {
            return -1;
        }
        if (x.crowdingDistance < y.crowdingDistance)
        {
            return 1;
        }
        return 0;
    }
}
