import java.util.ArrayList;

public class ProposedSolution {

    private double score;
    private ArrayList<CarLine> solution;

    public ProposedSolution(double score, ArrayList<CarLine> solution) {
        this.score = score;
        this.solution = solution;
    }

    public ArrayList<CarLine> getSolution() {
        return solution;
    }

    public void setSolution(ArrayList<CarLine> solution) {
        this.solution = solution;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
