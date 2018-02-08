import java.util.ArrayList;
import java.util.Random;

public class Population {

    private final ProcessFile processFile;

    Population(ProcessFile processFile) {
        this.processFile = processFile;
    }

    ProposedSolution[] generateInitialPopulation(int populationSize) {

        // Initiating variables
        final ProposedSolution[] proposedSolutions = new ProposedSolution[populationSize];

        // Generating a random solution for each iteration
        for (int i = 0; i < populationSize; i ++) {

            Depot[] depots = processFile.getDepots();
            final ProposedSolution proposedSolution = new ProposedSolution(depots);

            // Giving every customer a random car
            for (Customer customer : processFile.customers) {

                // Finding the closest depot
                double bestDistance = Double.MAX_VALUE;
                Depot bestDepot = null;
                for (Depot depot : depots) {
                    double distance = euclideanDistance(customer.getX(), customer.getY(), depot.getX(), depot.getY());
                    if (distance < bestDistance || bestDepot == null) {
                        bestDistance = distance;
                        bestDepot = depot;
                    }
                }

                // Selecting a random car
                int randomIndex = new Random().nextInt(bestDepot.getCars().length);
                Car car = bestDepot.getCars()[randomIndex];

                // Updating statistics for the car
                car.addCustomerVisited(customer);
                car.addLoad(customer.getCustomerDemand());
                car.addDuration(euclideanDistance(customer.getX(), customer.getY(), car.getX(), car.getY()));

                // Updating the new car position
                car.setX(customer.getX());
                car.setY(customer.getY());
            }

            // Driving all cars back to the depots from their current positions
            for (Car car : proposedSolution.cars) {
                car.addDuration(euclideanDistance(car.getX(), car.getY(), car.getDepot().getX(), car.getDepot().getY()));
            }

            //Add the solution to the solutions list
            proposedSolutions[i] = new ProposedSolution(depots);
        }

        return proposedSolutions;
    }

    public ProposedSolution[] selectParent(ProposedSolution[] solutions){

        //list containing all selected parents
        //There should be as many selected parents as there are specimen in the population as one parent can eb selected more than once
        ProposedSolution[] selected_parents = new ProposedSolution[solutions.length];

        double score_sum = 0.0;

        for(ProposedSolution solution: solutions){
            score_sum += 1/solution.getFitness();
        }

        //for each iteration add a parent
        for(int i=0;i<solutions.length;i++){

            double p = Math.random();   //random number from 0 to 1

            double cumulativeProbability = 0.0;
            for (ProposedSolution solution : solutions) {
                cumulativeProbability +=  (1 / solution.getFitness()) / score_sum;    //add to the cumulative probability

                if (p <= cumulativeProbability) {
                    selected_parents[i] = solution;
                    break;
                }
            }
        }
        return selected_parents;
    }

    public ProposedSolution[] Crossover(ProposedSolution[] solutions, double mutationRate){

        ProposedSolution[] children = new ProposedSolution[solutions.length*5];
        Random rand = new Random();

        for(int i=0;i<children.length;i++){
            ProposedSolution parent1 = solutions[rand.nextInt(solutions.length)];
            ProposedSolution parent2 = solutions[rand.nextInt(solutions.length)];

            children[i] = copulate(parent1,parent2);
        }

        mutate(children, mutationRate);

        return children;
    }

    public ProposedSolution copulate(ProposedSolution parent1, ProposedSolution parent2){

        Random rand = new Random();

        Depot[] depotsParent1 = new Depot[processFile.depot_count]; //all depots in the first parent
        Depot[] depotsParent2 = new Depot[processFile.depot_count]; //all depots in second parent


        for(int i=0;i<parent1.depots.length;i++){
            depotsParent1[i] = new Depot(parent1.depots[i]);
            depotsParent2[i] = new Depot(parent2.depots[i]);
        }

        Car route1 = depotsParent1[rand.nextInt(processFile.depot_count)].getCars()[rand.nextInt(processFile.vehicle_count)];   //random route frm first parent
        Car route2 = depotsParent2[rand.nextInt(processFile.depot_count)].getCars()[rand.nextInt(processFile.vehicle_count)];   //random route from second parent

        //sequence of customers from random route in parent 1
        ArrayList<Customer> customerSequence1 = route1.getCustomerSequence();
        //System.out.println(customerSequence1.size());

        for(int j=0;j<customerSequence1.size();j++){
            Customer customer = customerSequence1.get(j);

            //sequence of customers from random route in parent 2
            ArrayList<Customer> customerSequence2 = route2.getCustomerSequence();

            for(int k=0; k<customerSequence2.size();k++){
                customerSequence2.add(k,customer);
                //compute stuff;
                customerSequence2.remove(k);
            }
        }

        //Car[] parent1Cars = Car.createCopy(parent1.cars,);
        //Car[] parent2Cars = Car.createCopy(parent2.getCars());


        //Car route1 = Car.createCopy(parent1Cars[rand.nextInt(parent1Cars.length)]);
        //Car route2 = parent2Cars[rand.nextInt(parent2Cars.length)];

        return null;
    }

    //Mutate children
    public void mutate(ProposedSolution[] soulutions, double mutationRate){

        for(ProposedSolution solution: soulutions){
            double p = Math.random();

            //mutate with a probability of mutationRate
            if(mutationRate >= p){
                inverseMutation(solution);
            }
        }
        //return soulutions;
    }

    public void inverseMutation(ProposedSolution solution){

        //implement inverse mutation
    }

    //calculates the euclidean distance from a to b
    private static double euclideanDistance(int x1, int y1, int x2, int y2){

        double x_travelled = Math.abs(x1 - x2);
        double y_travelled = Math.abs(y1 - y2);

        return Math.sqrt(Math.pow(x_travelled, 2) + Math.pow(y_travelled, 2));
    }

}
