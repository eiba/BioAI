import java.util.Random;

public class Population {

    private final ProcessFile processFile;

    Population(ProcessFile processFile) {
        this.processFile = processFile;
    }

    ProposedSolution[] generateInitialPopulation(int populationSize) {
        final ProposedSolution[] proposedSolutions = new ProposedSolution[populationSize];

        //Generating a random solution for each iteration
        for (int i = 0; i < populationSize; i ++) {
            int illegalMoves = 0;  //number of moves made that exceeds duration and load limits

            Depot[] depots = processFile.getDepots();

            //Giving every customer a random car
            for (Customer customer : processFile.customers) {


                //Finding the closest depot
                double bestDistance = Double.MAX_VALUE;
                Depot bestDepot = null;
                for (Depot depot : depots) {
                    double distance = euclideanDistance(customer.getX(), customer.getY(), depot.getX(), depot.getY());
                    if (distance < bestDistance || bestDepot == null) {
                        bestDistance = distance;
                        bestDepot = depot;
                    }
                }

                //Selecting a random Car
                int randomIndex = new Random().nextInt(bestDepot.getCars().length);
                Car car = bestDepot.getCars()[randomIndex];

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
            for (Depot depot : depots) {
                for (Car car : depot.getCars()) {
                    car.addDuration(euclideanDistance(car.getX(), car.getY(), car.getDepot().getX(), car.getDepot().getY()));

                    //Checking how many Customers that cannot be visited by the car assigned
                    if (car.getCurrentDuration() > car.getMaximumDuration() || car.getCurrentLoad() > car.getMaximumLoad()) {
                        illegalMoves += car.getCustomerSequence().size();
                    }
                }
            }

            //Add the solution to the solutions list
            proposedSolutions[i] = new ProposedSolution(depots, illegalMoves);
        }

        return proposedSolutions;
    }

    public ProposedSolution[] selectParent(ProposedSolution[] solutions){

        //list containing all selected parents
        //There should be as many selected parents as there are specimen in the population as one parent can eb selected more than once
        ProposedSolution[] selected_parents = new ProposedSolution[solutions.length];

        double score_sum = 0.0;

        for(ProposedSolution solution: solutions){
            score_sum += 1/solution.fitnessScore;
        }

        //for each iteration add a parent
        for(int i=0;i<solutions.length;i++){

            double p = Math.random();   //random number from 0 to 1

            double cumulativeProbability = 0.0;
            for (ProposedSolution solution : solutions) {
                cumulativeProbability +=  (1 / solution.fitnessScore) / score_sum;    //add to the cumulative probability

                if (p <= cumulativeProbability) {
                    selected_parents[i] = solution;
                    break;
                }
            }
        }
        return selected_parents;
    }

    //calculates the euclidean distance from a to b
    private static double euclideanDistance(int x1, int y1, int x2, int y2){

        double x_travelled = Math.abs(x1 - x2);
        double y_travelled = Math.abs(y1 - y2);

        return Math.sqrt(Math.pow(x_travelled, 2) + Math.pow(y_travelled, 2));
    }

}
