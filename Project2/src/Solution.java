public class Solution {

    public double score;
    public double edgeValue;
    public double overallDeviation;
    public int dominationRank;

    Segment[] segments;
    public Solution(Segment[] segments){
        this.segments = segments;
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
