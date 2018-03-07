import java.util.ArrayList;
import java.util.HashMap;

public class Solution {

    public double score;
    public double edgeValue;
    public double overallDeviation;
    public int dominationRank;

    final Segment[] segments;
    final ArrayList<PixelEdge>[] pixelEdges;

    public Solution(Segment[] segments, int columns, int rows){
        this.segments = segments;
        pixelEdges = new ArrayList[columns * rows];
        for (Segment segment : segments) {
            for (Pixel pixel : segment.pixels) {
                pixelEdges[pixel.column + pixel.row * columns] = segment.pixelEdgeMap.get(pixel);
            }
        }
    }

    Solution(Solution parent1, Solution parent2, int splitPoint, Pixel[][] pixels) {
        pixelEdges = new ArrayList[parent1.pixelEdges.length];

        // Filling out the new pixelEdges - Genotype
        System.arraycopy(parent1.pixelEdges, 0, pixelEdges, 0, splitPoint);
        System.arraycopy(parent2.pixelEdges, splitPoint, pixelEdges, splitPoint, pixelEdges.length - splitPoint);

        // The hard part, defining segments - Phenotype
        final HashMap<Pixel, PixelEdge> parentMap = new HashMap<>();
        final HashMap<Pixel, ArrayList<PixelEdge>> childMap = new HashMap<>();
        //[Height][Width]
        final boolean[][] hasParent = new boolean[pixels.length][pixels[0].length];
        int pixelParentCount = 0;

        // Creating a map of each pixels incoming PixelEdge
        for (int i = 0; i < splitPoint; i ++) {
            for (PixelEdge pixelEdge : parent1.pixelEdges[i]) {
                parentMap.put(pixelEdge.neighbourPixel, pixelEdge);
                childMap.computeIfAbsent(pixelEdge.currentPixel, k -> new ArrayList<>()).add(pixelEdge);
                hasParent[pixelEdge.neighbourPixel.row][pixelEdge.neighbourPixel.column] = true;
                pixelParentCount ++;
            }
        }

        // Checking that no pixels has outgoing edges to the same pixel
        for (int i = splitPoint; i < pixelEdges.length; i ++) {
            for (PixelEdge pixelEdge : parent2.pixelEdges[i]) {
                // Parent1 also has a outgoing edge to the neighbourPixel
                if (parentMap.containsKey(pixelEdge.neighbourPixel)) {
                    // Edges are compared and the best edge by distance is kept
                    final PixelEdge oldEdge = parentMap.get(pixelEdge.neighbourPixel);
                    if (pixelEdge.distance < oldEdge.distance) {
                        childMap.get(oldEdge.currentPixel).remove(oldEdge);
                        parentMap.put(pixelEdge.neighbourPixel, pixelEdge);
                        childMap.computeIfAbsent(pixelEdge.currentPixel, k -> new ArrayList<>()).add(pixelEdge);
                    }
                }
                else {
                    parentMap.put(pixelEdge.neighbourPixel, pixelEdge);
                    childMap.computeIfAbsent(pixelEdge.currentPixel, k -> new ArrayList<>()).add(pixelEdge);
                    hasParent[pixelEdge.neighbourPixel.row][pixelEdge.neighbourPixel.column] = true;
                    pixelParentCount ++;
                }
            }
        }

        //Creating Segments from parentMap
        segments = new Segment[pixelEdges.length - pixelParentCount];
        int index = 0;
        for (int i = 0; i < hasParent.length; i ++) {
            for (int j = 0; j < hasParent[i].length; j ++) {
                // Found a root pixel
                if (!hasParent[i][j]) {
                    segments[index] = new Segment(pixels[i][j]);
                    // getOrDefault in case pixel has no children
                    ArrayList<PixelEdge> children = new ArrayList<>(childMap.getOrDefault(pixels[i][j], new ArrayList<>()));
                    while (!children.isEmpty()) {
                        final PixelEdge pixelEdge = children.remove(0);
                        segments[index].add(pixelEdge);
                        children.addAll(childMap.getOrDefault(pixelEdge.neighbourPixel, new ArrayList<>()));
                    }
                    index ++;
                }
            }
        }


    }

    public void scoreSolution(double edgeValue, double overallDeviation, double edgeWeight, double overAlldeviationWeight){
        this.edgeValue = edgeValue;
        this.overallDeviation = overallDeviation;

        //Overall deviation should be maximized while edgeValue should be maximized
        //To keep similarity for the score we negate edgeValue, high edgeValue gives low score
        //and low score is good.
        this.score = (overAlldeviationWeight*overallDeviation) - (edgeWeight*edgeValue);
    }

    //check if this solution is dominated by another
    public boolean isDominatedBy(Solution solution){
        //solution is the solution to check if it is dominating this solution

        if(this.overallDeviation > solution.overallDeviation && this.edgeValue <= solution.edgeValue){
            return true;
        }else if(this.overallDeviation >= solution.overallDeviation && this.edgeValue < solution.edgeValue){
            return true;
        }

        //This solution is not dominated
        return false;
    }

}
