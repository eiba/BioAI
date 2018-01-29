import java.util.ArrayList;

public class ProposedSolution {

    private double score;
    private ArrayList<SolutionLine> solution;

    public ProposedSolution(ArrayList<SolutionLine> solution) {
        this.score = score;
        this.solution = solution;
    }

    public ArrayList<SolutionLine> getSolution() {
        return solution;
    }

    public void setSolution(ArrayList<SolutionLine> solution) {
        this.solution = solution;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
