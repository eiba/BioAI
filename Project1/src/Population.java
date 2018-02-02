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
            int illegalMoves = 0;  //number of moves made that exceeds duration and load limits

            Car[] cars = Car.createCopy(processFile.vehicles);

            //Giving every customer a random car
            for (Customer customer : processFile.customers) {

                //Selecting a random Car
                int randomIndex = new Random().nextInt(processFile.vehicles.length);
                Car car = cars[randomIndex];

                //duration needed for car to drive home from customer
//                double duration_to_get_home = eucledianDistance(car.getX(), car.getY(), car.getDepot().getX(), car.getDepot().getY());
                car.addCustomerVisited(customer);
                car.addLoad(customer.getCustomer_demand());
                car.addDuration(euclideanDistance(customer.getX(), customer.getY(), car.getX(), car.getY()));
                car.setX(customer.getX());
                car.setY(customer.getY());

                //The car driving to the customer is actually an illegal move
//                if((car.getMaximumDuration() != 0 && car.getMaximumDuration() < car.getCurrentDuration() + duration_to_get_home) || car.getMaximumDuration() < car.getCurrentDuration()){
//                    illigal_moves += 1;
//                }
            }

            //Driving all cars home
            for (Car car :cars) {
                car.addDuration(euclideanDistance(car.getX(), car.getY(), car.getDepot().getX(), car.getDepot().getY()));

                //Checking how many Customers that cannot be visited by the car assigned
                if (car.getCurrentDuration() > car.getMaximumDuration() || car.getCurrentLoad() > car.getMaximumLoad()) {
                    illegalMoves += car.getCustomerSequence().size();
                }
            }

            //Add the solution to the solutions list
            proposedSolutions[i] = new ProposedSolution(cars, illegalMoves);
        }

        return proposedSolutions;
    }


    //calculates the euclidean distance from a to b
    private static double euclideanDistance(int x1, int y1, int x2, int y2){

        double x_travelled = Math.abs(x1 - x2);
        double y_travelled = Math.abs(y1 - y2);

        return Math.sqrt(Math.pow(x_travelled, 2) + Math.pow(y_travelled, 2));
    }

}
