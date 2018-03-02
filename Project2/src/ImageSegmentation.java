import java.awt.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageSegmentation {

    final Random random;
    final Pixel[][] pixels;
    ImageParser imageParser;
    final double edgeWeight;
    final double overallDeviationWeight;

    ImageSegmentation(ImageParser imageParser, double edgeWeight, double overallDeviationWeight){
        this.imageParser = imageParser;
        this.edgeWeight = edgeWeight;
        this.overallDeviationWeight = overallDeviationWeight;
        pixels = createPixels();
        random = new Random();
    }

    /**
     * Create initial segments that contains the entire image
     * @param populationSize Number of segments to be returned
     * @return segments that each contains the entire image
     */
    Segment[] createInitialSegments(int populationSize){

        final Segment[] segments = new Segment[populationSize];

        //Create one solution for each iteration
        final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < populationSize; i ++) {
            final int index = i;
            executorService.execute(() -> {

                // Selecting a random Pixel
                final int rootRow = random.nextInt(imageParser.height);
                final int rootColumn = random.nextInt(imageParser.width);
                final Pixel rootPixel = pixels[rootRow][rootColumn];

                // Creating a new segment
                final Segment segment = new Segment(rootPixel);
                final PriorityQueue<PixelEdge> priorityQueue = new PriorityQueue<>(rootPixel.edgeList);

                // Using Prim's algorithm to create the Segment
                while (!priorityQueue.isEmpty()) {
                    final PixelEdge currentPixelEdge = priorityQueue.remove();

                    // Check if Pixel is already in Segment
                    if (segment.contains(currentPixelEdge.neighbourPixel)) {
                        continue;
                    }

                    // Adding the new Pixel to the Segment
                    segment.add(currentPixelEdge);
                    priorityQueue.addAll(currentPixelEdge.neighbourPixel.edgeList);

                }

                segments[index] = segment;

            });
        }

        executorService.shutdown();
        //noinspection StatementWithEmptyBody
        while (!executorService.isTerminated()) {}

        return segments;
    }

    Segment[] divideSegment(Segment segment, int numberOfSegments) {
        final Segment[] segments = new Segment[numberOfSegments];
        final PixelEdge[] weakestPixelEdges = new PixelEdge[numberOfSegments - 1];

        final PriorityQueue<PixelEdge> priorityQueue = new PriorityQueue<>(Collections.reverseOrder());
        priorityQueue.addAll(segment.edges);

        // Finding the PixelEdges with highest distance in segment
        for (int i = 0; i < weakestPixelEdges.length; i ++) {
            PixelEdge pixelEdge = priorityQueue.remove();
            while (segment.getSize(pixelEdge.neighbourPixel) < (double) segment.pixelCount / 10) {
                pixelEdge = priorityQueue.remove();
            }
//            System.out.println(segment.getSize(pixelEdge.neighbourPixel));
            weakestPixelEdges[i] = pixelEdge;
        }

        // Segment 0 is the entire segment minus the other segments
        segments[0] = segment.copyFrom(segment.root);

        // Making a copy of the other segments
        for (int i = 0; i < weakestPixelEdges.length; i ++) {
            segments[i+1] = segment.copyFrom(weakestPixelEdges[i].neighbourPixel);
        }

        return segments;
    }

    Solution[] createInitialSolutions(int solutionCount, int minimumSegmentCount, int maximumSegmentCount) {
        final Solution[] solutions = new Solution[solutionCount];

        // Create one solution for each iteration
        final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        // Creating a wrapper class to bind PixelEdge objects to a Segment
        final class SegmentPixelEdge implements Comparable<SegmentPixelEdge>{

            final Segment segment;
            final PixelEdge pixelEdge;

            SegmentPixelEdge(Segment segment, PixelEdge pixelEdge) {
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
                final HashSet<Pixel> visitedPixels = new HashSet<>();
                final int segmentCount = minimumSegmentCount + random.nextInt(maximumSegmentCount - minimumSegmentCount + 1);
                final Segment[] segments = new Segment[segmentCount];

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
                    while (visitedPixels.contains(rootPixel));

                    segments[j] = new Segment(rootPixel);
                    visitedPixels.add(rootPixel);
                    for (PixelEdge pixelEdge : rootPixel.edgeList) {
                        priorityQueue.add(new SegmentPixelEdge(segments[j], pixelEdge));
                    }
                }

                // Using Prim's algorithm to create the Segments for the Solution
                while (!priorityQueue.isEmpty()) {
                    final SegmentPixelEdge segmentPixelEdge = priorityQueue.remove();
                    final Segment segment = segmentPixelEdge.segment;
                    final PixelEdge currentPixelEdge = segmentPixelEdge.pixelEdge;

                    // Check if Pixel is already in Solution
                    if (visitedPixels.contains(currentPixelEdge.neighbourPixel)) {
                        continue;
                    }

                    // Adding the new Pixel to the Segment
                    segment.add(currentPixelEdge);
                    visitedPixels.add(currentPixelEdge.neighbourPixel);
                    for (PixelEdge pixelEdge : currentPixelEdge.neighbourPixel.edgeList) {
                        priorityQueue.add(new SegmentPixelEdge(segment, pixelEdge));
                    }

                }

                solutions[index] = new Solution(segments);
            });
        }

        executorService.shutdown();
        //noinspection StatementWithEmptyBody
        while (!executorService.isTerminated()) {}

        return solutions;
    }

    //euclidean distance in RGB color space
    public double euclideanRGB(Color Color1, Color Color2){

        int differenceRed = Color1.getRed() - Color2.getRed();
        int differenceGreen = Color1.getGreen() - Color2.getGreen();
        int differenceBlue = Color1.getBlue() - Color2.getBlue();

        return Math.sqrt(Math.pow(differenceRed, 2) + Math.pow(differenceGreen, 2) + Math.pow(differenceBlue, 2));

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
                    final PixelEdge pixelEdge = new PixelEdge(currentPixel, pixels[i-1][j], pixels[i-1][j].edges[2].distance);
                    currentPixel.edges[0] = pixelEdge;
                }
                // Has neighbour left
                if (j > 0) {
                    final PixelEdge pixelEdge = new PixelEdge(currentPixel, pixels[i][j-1], pixels[i][j-1].edges[1].distance);
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
    private void edgeValueandDeviation(Solution solution){
        double edgeValue = 0.0;
        double overAllDeviation = 0.0;

        for(Segment segment:solution.segments){
            for(Pixel pixel: segment.pixels){
                //Calculate overall deviation (summed rbg distance from current pixel to centroid of segment)
                overAllDeviation += euclideanRGB(pixel.color, segment.getColor());

                //Calculate the edgeValues. Calculate rgb distance between the current pixel and all its neighbours
                //that are not in the same segment
                for(PixelEdge pixelEdge: pixel.edgeList){
                    Pixel neighbourPixel = pixelEdge.neighbourPixel;

                    if(!segment.pixelEdgeMap.containsKey(neighbourPixel)){
                        edgeValue += pixelEdge.distance;    //add the distance of the edge if the neighbouring pixels are not in the same segment
                    }
                }
            }
        }
        solution.scoreSolution(edgeValue,overAllDeviation, this.edgeWeight,this.overallDeviationWeight);
    }

}
