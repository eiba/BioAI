public class ProposedSolution {

    final double durationScore;
    final int customerScore;
    final double fitnessScore;
    final Depot[] depots;

    public ProposedSolution(Depot[] depots, int customerScore) {
        this.customerScore = customerScore;
        this.depots = depots;

        double duration = 0;
        for (Depot depot : depots) {
            for (Car car : depot.getCars()) {
                duration += car.getCurrentDuration();
            }
        }
        durationScore = duration;
        this.fitnessScore = (this.customerScore + 1)* durationScore;
    }
}
