public class ProposedSolution {

    private double durationScore;
    private double customerScore;

    public ProposedSolution(Car[] cars) {

        for (Car car : cars) {
            durationScore += car.getCurrent_duration();
        }

    }

    public double getDurationScore() {
        return durationScore;
    }

    public void setDurationScore(double durationScore) {
        this.durationScore = durationScore;
    }
}
