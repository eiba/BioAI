public class Solution {

    public double score;
    public double edgeValue;
    public double overallDeviation;

    Segment[] segments;
    public Solution(Segment[] segments){
        this.segments = segments;
    }

    public void scoreSolution(double edgeValue, double overallDeviation){
        this.edgeValue = edgeValue;
        this.overallDeviation = overallDeviation;

        //Overall deviation should be maximized while edgeValue should be maximized
        //To keep similarity for the score we negate edgeValue, high edgeValue gives low score
        //and low score is good.
        this.score = overallDeviation - edgeValue;
    }

}
