import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

public class Solution {

    public double score;
    public double edgeValue;
    public double overallDeviation;
    public int dominationRank;
    public double crowdingDistance;

    final Segment[] segments;
    final ArrayList<PixelEdge>[] pixelEdges;

    Solution(ArrayList<PixelEdge>[] pixelEdges, Segment[] segments){
        this.segments = segments;
        this.pixelEdges = pixelEdges;
    }

    Solution(Solution parent1, Solution parent2, int splitPoint, Pixel[][] pixels) {
        pixelEdges = new ArrayList[parent1.pixelEdges.length];
        final boolean[][] hasEdge = new boolean[pixels.length][pixels[0].length];

        // Filling out the new pixelEdges - Genotype
        for (int i = 0; i < splitPoint; i ++) {
            pixelEdges[i] = new ArrayList<>(parent1.pixelEdges[i]);
        }
        for (int i = splitPoint; i < pixelEdges.length; i ++) {
            pixelEdges[i] = new ArrayList<>(parent2.pixelEdges[i]);
        }

        // The hard part, defining segments - Phenotype

        // Removing duplicate edges from new solution
        HashSet<PixelEdge> edges = new HashSet<>();
        int index;
        for (int i = 0; i < pixels[0].length; i ++) {
            index = splitPoint - 1 - i;
            if (index < 0) {
                break;
            }
            edges.addAll(pixelEdges[index]);
        }
        //Adding edges that were incoming in parent2
        final HashSet<PixelEdge> parent2LostEdges = new HashSet<>();
        for (int i = 0; i < pixels[0].length; i ++) {
            index = splitPoint - 1 - i;
            if (index < 0) {
                break;
            }
            parent2LostEdges.addAll(parent2.pixelEdges[index]);
        }
        for (int i = 0; i < pixels[0].length; i ++) {
            final ArrayList<PixelEdge> removeList = new ArrayList<>();
            index = splitPoint + i;
            if (index >= pixelEdges.length) {
                break;
            }
            final int indexRow = index / pixels[0].length;
            final int indexCol = index % pixels[0].length;
            for (PixelEdge pixelEdge : pixels[indexRow][indexCol].edgeList) {
                if (parent2LostEdges.contains(pixelEdge)) {
                    pixelEdges[index].add(pixelEdge);
                }
            }
            for (PixelEdge pixelEdge : pixelEdges[index]) {
                // If duplicate edge
                if (edges.contains(pixelEdge)) {
                    removeList.add(pixelEdge);
                }
            }
//            //Merging further to not get to many segments
//            if (pixelEdges[index].isEmpty()) {
//                final int indexRow = index / pixels[0].length;
//                final int indexCol = index % pixels[0].length;
//                PixelEdge bestEdge = null;
//                for (PixelEdge pixelEdge : pixels[indexRow][indexCol].edgeList) {
//                    if (bestEdge == null || bestEdge.distance > pixelEdge.distance) {
//                        if (!edges.contains(pixelEdge)) {
//                            bestEdge = pixelEdge;
//
//                        }
//                    }
//                }
//                pixelEdges[index].add(bestEdge);
//            }
        }

        for (int i = 0; i < pixels[0].length; i ++) {
            final ArrayList<PixelEdge> removeList = new ArrayList<>();
            index = splitPoint + i;
            if (index >= pixelEdges.length) {
                break;
            }
            for (PixelEdge pixelEdge : pixelEdges[index]) {
                // If duplicate edge
                if (edges.contains(pixelEdge)) {
                    removeList.add(pixelEdge);
                }
            }
//            //Merging further to not get to many segments
//            if (pixelEdges[index].isEmpty()) {
//                final int indexRow = index / pixels[0].length;
//                final int indexCol = index % pixels[0].length;
//                PixelEdge bestEdge = null;
//                for (PixelEdge pixelEdge : pixels[indexRow][indexCol].edgeList) {
//                    if (bestEdge == null || bestEdge.distance > pixelEdge.distance) {
//                        if (!edges.contains(pixelEdge)) {
//                            bestEdge = pixelEdge;
//
//                        }
//                    }
//                }
//                pixelEdges[index].add(bestEdge);
//            }
            pixelEdges[index].removeAll(removeList);
        }



        final class Phenotype {
            final ArrayList<Pixel> pixels = new ArrayList<>();
//            Segment segment = new Segment();
            final ArrayList<Phenotype> connections = new ArrayList<>();
        }


        final HashMap<Pixel, Phenotype> phenotypeMap = new HashMap<>();
        final ArrayList<Segment> segments = new ArrayList<>();
        final ArrayList<Phenotype> phenotypes = new ArrayList<>();

        for (ArrayList<PixelEdge> pixelEdges : pixelEdges) {
            for (PixelEdge pixelEdge : pixelEdges) {

                final Phenotype phenotypeA = phenotypeMap.get(pixelEdge.pixelA);
                final Phenotype phenotypeB = phenotypeMap.get(pixelEdge.pixelB);

                //If both have different Phenotype we need to link them together
                if (phenotypeA != null && phenotypeB != null) {
                    phenotypeA.connections.add(phenotypeB);
                    phenotypeB.connections.add(phenotypeA);
//                    final Segment segment = new Segment();
//                    phenotypeA.segment = segment;
//                    phenotypeB.segment = segment;
                    continue;
                }

                if (phenotypeA != null) {
//                    phenotypeA.edges.add(new Edge(pixelEdge.pixelA, pixelEdge.pixelB, pixelEdge));
                    phenotypeA.pixels.add(pixelEdge.pixelB);
                    phenotypeMap.put(pixelEdge.pixelB, phenotypeA);
                    hasEdge[pixelEdge.pixelB.row][pixelEdge.pixelB.column] = true;
                    continue;
                }

                if (phenotypeB != null) {
//                    phenotypeB.edges.add(new Edge(pixelEdge.pixelB, pixelEdge.pixelA, pixelEdge));
                    phenotypeB.pixels.add(pixelEdge.pixelA);
                    hasEdge[pixelEdge.pixelA.row][pixelEdge.pixelA.column] = true;
                    phenotypeMap.put(pixelEdge.pixelA, phenotypeB);
                    continue;
                }

                final Phenotype phenotype = new Phenotype();
//                phenotype.edges.add(new Edge(pixelEdge.pixelA, pixelEdge.pixelB, pixelEdge));
                phenotype.pixels.add(pixelEdge.pixelA);
                phenotype.pixels.add(pixelEdge.pixelB);
                hasEdge[pixelEdge.pixelA.row][pixelEdge.pixelA.column] = true;
                hasEdge[pixelEdge.pixelB.row][pixelEdge.pixelB.column] = true;
//                phenotype.pixelEdges.add(pixelEdge);
                phenotypeMap.put(pixelEdge.pixelA, phenotype);
                phenotypeMap.put(pixelEdge.pixelB, phenotype);
                phenotypes.add(phenotype);
            }
        }

        // Fixing pixels not having any segment
        for (int i = 0; i < hasEdge.length; i ++) {
            for (int j = 0; j < hasEdge[0].length; j ++) {
                if (!hasEdge[i][j]) {
                    PixelEdge bestEdge = null;
                    for (PixelEdge pixelEdge : pixels[i][j].edgeList) {
                        if (bestEdge == null || bestEdge.distance > pixelEdge.distance) {
                            if (phenotypeMap.containsKey(pixelEdge.pixelA) || phenotypeMap.containsKey(pixelEdge.pixelB)) {
                                bestEdge = pixelEdge;
                            }
                        }
                    }
                    pixelEdges[i * hasEdge[0].length + j].add(bestEdge);
                    final Phenotype phenotypeA = phenotypeMap.get(bestEdge.pixelA);
                    final Phenotype phenotypeB = phenotypeMap.get(bestEdge.pixelB);
                    if (phenotypeA != null) {
                        phenotypeA.pixels.add(bestEdge.pixelB);
                        phenotypeMap.put(bestEdge.pixelB, phenotypeA);
                    }
                    else {
                        phenotypeB.pixels.add(bestEdge.pixelA);
                        phenotypeMap.put(bestEdge.pixelA, phenotypeB);
                    }
//                    else {
//                        final Phenotype phenotype = new Phenotype();
//                        phenotype.pixels.add(bestEdge.pixelA);
//                        phenotype.pixels.add(bestEdge.pixelB);
//                        phenotypeMap.put(bestEdge.pixelA, phenotype);
//                        phenotypeMap.put(bestEdge.pixelB, phenotype);
//                        phenotypes.add(phenotype);
//                    }
                }
            }
        }

        // Creating the Segments from Phenotypes
        final HashSet<Phenotype> visited = new HashSet<>();
        for (Phenotype phenotype : phenotypes) {
            if (visited.contains(phenotype)) {
                continue;
            }
            visited.add(phenotype);
            final Segment segment = new Segment();
            for (Pixel pixel : phenotype.pixels) {
               segment.add(pixel);
            }
            ArrayList<Phenotype> connections = phenotype.connections;
            while (!connections.isEmpty()) {
                final Phenotype phenotype1 = connections.remove(0);
                if (visited.contains(phenotype1)) {
                    continue;
                }
                visited.add(phenotype1);
                connections.addAll(phenotype1.connections);

                for (Pixel pixel : phenotype1.pixels) {
                    segment.add(pixel);
                }
            }

            segments.add(segment);
        }
        this.segments = segments.toArray(new Segment[0]);

//        final HashMap<Pixel, PixelEdge> parentMap = new HashMap<>();
//        final HashMap<Pixel, ArrayList<PixelEdge>> childMap = new HashMap<>();
//        final HashSet<Pixel> reassignSet = new HashSet<>();
//        //[Height][Width]
//        final boolean[][] hasParent = new boolean[pixels.length][pixels[0].length];
//        int pixelParentCount = 0;
//
//        // Creating a map of each pixels incoming PixelEdge
//        for (int i = 0; i < splitPoint; i ++) {
//            for (PixelEdge pixelEdge : parent1.pixelEdges[i]) {
//                parentMap.put(pixelEdge.pixelB, pixelEdge);
//                childMap.computeIfAbsent(pixelEdge.pixelA, k -> new ArrayList<>()).add(pixelEdge);
//                hasParent[pixelEdge.pixelB.row][pixelEdge.pixelB.column] = true;
//                pixelParentCount ++;
//            }
//        }
//
//        // Checking that no pixels has outgoing edges to the same pixel
//        for (int i = splitPoint; i < pixelEdges.length; i ++) {
//            for (PixelEdge pixelEdge : parent2.pixelEdges[i]) {
//
//                final PixelEdge oldEdge = parentMap.get(pixelEdge.pixelB);
//
//                // Parent1 also has an outgoing edge to the pixelB (Segment merge collides)
//                if (oldEdge != null) {
//
//
//                    // Edges are compared and the best edge by distance is kept
////                    if (pixelEdge.distance < oldEdge.distance) {
////                        childMap.get(oldEdge.pixelA).remove(oldEdge);
////                        parentMap.put(pixelEdge.pixelB, pixelEdge);
////                        childMap.computeIfAbsent(pixelEdge.pixelA, k -> new ArrayList<>()).add(pixelEdge);
//                }
//                else {
//                    parentMap.put(pixelEdge.pixelB, pixelEdge);
//                    childMap.computeIfAbsent(pixelEdge.pixelA, k -> new ArrayList<>()).add(pixelEdge);
//                    hasParent[pixelEdge.pixelB.row][pixelEdge.pixelB.column] = true;
//                    pixelParentCount ++;
//                }
//            }
//        }
//
//
//        //Creating Segments from parentMap
//        segments = new Segment[pixelEdges.length - pixelParentCount];
//        int index = 0;
//        for (int i = 0; i < hasParent.length; i ++) {
//            for (int j = 0; j < hasParent[i].length; j ++) {
//                // Found a root pixel
//                if (!hasParent[i][j]) {
//                    segments[index] = new Segment(pixels[i][j]);
//                    // getOrDefault in case pixel has no children
//                    ArrayList<PixelEdge> children = new ArrayList<>(childMap.getOrDefault(pixels[i][j], new ArrayList<>()));
//                    while (!children.isEmpty()) {
//                        final PixelEdge pixelEdge = children.remove(0);
////                        segments[index].add(pixelEdge);
//                        children.addAll(childMap.getOrDefault(pixelEdge.pixelB, new ArrayList<>()));
//                    }
//                    index ++;
//                }
//            }
//        }


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

class weightedSumComparator implements Comparator<Solution>
{
    @Override
    public int compare(Solution x, Solution y)
    {
        if (x.score > y.score)
        {
            return 1;
        }
        if (x.score < y.score)
        {
            return -1;
        }
        return 0;
    }
}

class overallDeviationComparator implements Comparator<Solution>
{
    @Override
    public int compare(Solution x, Solution y)
    {
        if (x.overallDeviation > y.overallDeviation)
        {
            return 1;
        }
        if (x.overallDeviation < y.overallDeviation)
        {
            return -1;
        }
        return 0;
    }
}

class edgeValueComparator implements Comparator<Solution>
{
    @Override
    public int compare(Solution x, Solution y)
    {
        if (x.edgeValue < y.edgeValue)
        {
            return 1;
        }
        if (x.edgeValue > y.edgeValue)
        {
            return -1;
        }
        return 0;
    }
}
