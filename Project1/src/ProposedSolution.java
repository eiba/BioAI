public class ProposedSolution {

    final Depot[] depots;
    final Car[] cars;
    private double fitness = Double.MAX_VALUE;

    ProposedSolution(Depot[] depots) {
        this.depots = depots;
        this.cars = new Car[depots.length * depots[0].getCars().length];

        int count = 0;
        for (Depot depot : depots) {
            for (Car car : depot.getCars()) {
                cars[count ++] = car;
            }
        }
//        this.fitnessScore = (this.customerScore + 1)* durationScore;
    }

    void evaluateFitness() {
        double currentFitness = 0;
        for (Car car : cars) {
            currentFitness += car.getCurrentDuration();
        }
        fitness = currentFitness;
    }

    double getFitness() {
        return fitness;
    }
}
