import java.awt.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ImageSegmentation {

    final Random random;
    final Pixel[][] pixels;
    ImageParser imageParser;

    ImageSegmentation(ImageParser imageParser){
        this.imageParser = imageParser;
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
                    segment.add(currentPixelEdge.currentPixel, currentPixelEdge.neighbourPixel);
                    priorityQueue.addAll(currentPixelEdge.neighbourPixel.edgeList);

                }

                segments[index] = segment;

            });
        }

        executorService.shutdown();
        while (!executorService.isTerminated()) {}

        return segments;
    }

    Segment[] divideSegment(Segment segment, int numberOfSegments) {
        final Segment[] segments = new Segment[numberOfSegments];

        //@TODO Divide segment into specified number of segments

        return segments;
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
    private double[] edgeValueandDeviation(Solution solution){
        //@TODO: Calculate edge value and deviation here.
        double edgeValue = 0.0;
        double overAllDeviation = 0.0;

        for(Segment segment:solution.segments){
            for(Pixel pixel: segment.pixelArray){
                overAllDeviation += euclideanRGB(pixel.color,segment.centroid);
            }
        }

        double[] returnValues = new double[2];
        returnValues[0] = edgeValue;
        returnValues[1] = overAllDeviation;

        return returnValues;
    }

}
