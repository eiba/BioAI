public class Solution {

    public double score;
    public double edgeValue;
    public double overAlldeviation;

    Segment[] segments;
    public Solution(Segment[] segments){
        this.segments = segments;
    }

    public void scoreSolution(double edgeValue, double overAllDeviation){
        this.edgeValue = edgeValue;
        this.overAlldeviation = overAllDeviation;

        this.score = overAllDeviation - edgeValue;
    }
}
