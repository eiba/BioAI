public class ProposedSolution {

    final double durationScore;
    final int customerScore;
    final double fitnessScore;
    final Depot[] depots;
    final Car[] cars;

    public ProposedSolution(Depot[] depots, int customerScore) {
        this.customerScore = customerScore;
        this.depots = depots;
        this.cars = new Car[depots.length * depots[0].getCars().length];

        int count = 0;

        double duration = 0;
        for (Depot depot : depots) {
            for (Car car : depot.getCars()) {
                cars[count ++] = car;
                duration += car.getCurrentDuration();
            }
        }
        durationScore = duration;
        this.fitnessScore = (this.customerScore + 1)* durationScore;
    }
}
