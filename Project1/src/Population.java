import java.util.Random;

public class Population {

    private final ProcessFile processFile;
    private final int populationSize;

    Population(ProcessFile processFile, int populationSize) {
        this.processFile = processFile;
        this.populationSize = populationSize;
    }

    ProposedSolution[] generateInitialPopulation() {
        final ProposedSolution[] proposedSolutions = new ProposedSolution[populationSize];

        //Generating a random solution for each iteration
        for (int i = 0; i < populationSize; i ++) {

            Car[] cars = Car.createCopy(processFile.vehicles);

            //Giving every customer a random car
            for (Customer customer : processFile.customers) {

                //Selecting a random Car
                int randomIndex = new Random().nextInt(processFile.vehicles.length);
                Car car = cars[randomIndex];

                car.addCustomerVisited(customer);
                car.addLoad(customer.getCustomer_demand());
                car.addDuration(eucledianDistance(customer.getX(), customer.getY(), car.getX(), car.getY()));
                car.setX(customer.getX());
                car.setY(customer.getY());
            }

            //Driving all cars home
            for (Car car : processFile.vehicles) {
                car.addDuration(eucledianDistance(car.getX(), car.getY(), car.getDepot().getX(), car.getDepot().getY()));
            }

            proposedSolutions[i] = new ProposedSolution(cars);
        }

        return proposedSolutions;
    }

    static double eucledianDistance(int x1, int y1, int x2, int y2){

        double x_travelled = Math.abs(x1 - x2);
        double y_travelled = Math.abs(y1 - y2);

        return Math.sqrt(Math.pow(x_travelled, 2) + Math.pow(y_travelled, 2));
    }

}
