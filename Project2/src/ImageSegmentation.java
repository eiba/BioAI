import java.awt.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Random;

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
     * Using Prim's algorithm to generate initial segments
     * @param populationSize Number of segments to be returned
     * @return
     */
    public Solution[] createInitialSolutions(int populationSize, int minimumSegmentCount, int maximumSegmentCount){
        //TODO: use prim's algorithm to generate initial segments

        //List of solutions to be returned from initial solution function
        final Solution[] solutions = new Solution[populationSize];

        //Create one solution for each iteration
        for (int i = 0; i < populationSize; i ++) {

            //TODO number of segments in segment list should be between minimumSegmentCount and maximumSegmentCount, not populationsize
            final Segment[] segments = new Segment[populationSize];

            //Create one segment for each iteration. After the segments have been created we can create one solution form this.
            for (int j=0; j< segments.length;j++) {
                // Selecting a random Pixel
                final int rootRow = random.nextInt(imageParser.height);
                final int rootColumn = random.nextInt(imageParser.width);
                final Pixel rootPixel = pixels[rootRow][rootColumn];

                // Creating a new segment
                final Segment segment = new Segment(rootPixel);
                final PriorityQueue<Pixel> priorityQueue = new PriorityQueue<>();

                // Using Prim's algorithm to fill the Segment
                while (!priorityQueue.isEmpty()) {
                    final Pixel currentPixel = priorityQueue.remove();

                }

                segments[i] = segment;
            }
            solutions[i] = new Solution(segments);  //add the segments as one solution
        }
//        final Comparator<Pixel> primPixelComparator = new Comparator<Pixel>() {
//            @Override
//            public int compare(Pixel pixel1, Pixel pixel2) {
//
//
//                return 0;
//            }
//        };
//        final PriorityQueue<Pixel> priorityQueue = new PriorityQueue<>();

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

                // Pixel1 to Pixel2 distance for above and left will have been previously calculated by Pixel2 to Pixel1
                // Has neighbour above
                if (i > 0) {
                    currentPixel.neighbours[0] = pixels[i-1][j];
                    currentPixel.neighbourDistances[0] = pixels[i-1][j].neighbourDistances[2];
                }
                // Has neighbour left
                if (j > 0) {
                    currentPixel.neighbours[3] = pixels[i][j-1];
                    currentPixel.neighbourDistances[3] = pixels[i-1][j].neighbourDistances[1];
                }
                // Has neighbour below
                if (i < imageParser.height - 1) {
                    currentPixel.neighbours[2] = pixels[i+1][j];
                    currentPixel.neighbourDistances[2] = euclideanRGB(currentPixel.color, pixels[i+1][j].color);
                }
                // Has neighbour right
                if (j < imageParser.width - 1) {
                    currentPixel.neighbours[1] = pixels[i][j+1];
                    currentPixel.neighbourDistances[1] = euclideanRGB(currentPixel.color, pixels[i][j+1].color);
                }
            }
        }

        return pixels;
    }

    private double edgeValue(Segment[] segments){
        //@TODO: Calculate edge value here.

        return 0.0;
    }

    private double overallDeviation(Segment[] segments){
        //@TODO: Calculate overall deviation here.

        return 0.0;
    }
}
