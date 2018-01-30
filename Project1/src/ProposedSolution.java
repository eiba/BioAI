import java.util.ArrayList;

public class ProposedSolution {

    private double score;
    private SolutionLine[] solution;

    public ProposedSolution(SolutionLine[] solution) {
        this.score = score;
        this.solution = solution;
    }

    public SolutionLine[] getSolution() {
        return solution;
    }

    public void setSolution(SolutionLine[] solution) {
        this.solution = solution;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
