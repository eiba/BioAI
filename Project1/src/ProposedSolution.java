import java.text.DecimalFormat;

public class ProposedSolution {

    final Depot[] depots;
    final Car[] cars;
    private double fitness = Double.MAX_VALUE;
    public int age = 0;

    ProposedSolution(Depot[] depots) {
        this.depots = depots;
        this.cars = new Car[depots.length * depots[0].getCars().length];
        int count = 0;
        for (Depot depot : depots) {
            for (Car car : depot.getCars()) {
                cars[count ++] = car;
            }
        }
    }

    ProposedSolution(ProposedSolution proposedSolution) {
        final Depot[] depots = new Depot[proposedSolution.depots.length];
        for (int i = 0; i < depots.length; i ++) {
            depots[i] = new Depot(proposedSolution.depots[i], false);
        }
        age = 0;
        this.depots = depots;
        this.cars = new Car[depots.length * depots[0].getCars().length];
        int count = 0;
        for (Depot depot : depots) {
            for (Car car : depot.getCars()) {
                cars[count ++] = car;
            }
        }
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

    @Override
    public String toString() {
        final DecimalFormat numberFormat = new DecimalFormat("#.00");
        final StringBuilder stringBuilder = new StringBuilder();
        
        stringBuilder.append(numberFormat.format(fitness)).append("\n");
        for (Car car : cars) {
            if(car.customerSequence.size() > 0){
                String s = car.getDepot().getDepotNr() + "\t" + car.getVehicleNumber() + "\t" + numberFormat.format(car.getCurrentDuration()) + "\t" + car.getCurrentLoad();
                StringBuilder customerSequence = new StringBuilder(" ");
                for(int m = 0; m < car.getCustomerSequence().size(); m++){
                    customerSequence.append(car.getCustomerSequence().get(m).getCustomerNr()).append(" ");
                }

                stringBuilder.append(s).append("\t0").append(customerSequence.toString()).append("0").append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
