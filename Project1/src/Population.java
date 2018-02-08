import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Population {

    private final ProcessFile processFile;
    private final Random random;
    private int populationSize;
    private ProposedSolution[] currentPopulation;

    Population(ProcessFile processFile) {
        this.processFile = processFile;
        random = new Random();
    }

    ProposedSolution[] generateInitialPopulation(int populationSize) {
        this.populationSize = populationSize;
        // Initiating variables
        final ProposedSolution[] proposedSolutions = new ProposedSolution[populationSize];

        // Generating a random solution for each iteration
        for (int i = 0; i < populationSize; i ++) {

            //Add the solution to the solutions list
            ProposedSolution proposedSolution;
            do {
                proposedSolution = generateSolution();
            }
            while (proposedSolution == null);
            proposedSolutions[i] = generateSolution();
        }

        return proposedSolutions;
    }

    private ProposedSolution generateSolution() {

        final Depot[] depots = processFile.getDepots();
        final ProposedSolution proposedSolution = new ProposedSolution(depots);

        final ArrayList<Customer> customers = new ArrayList<>(Arrays.asList(processFile.customers));
        while (!customers.isEmpty()) {

            // Selecting a random customer
            Customer customer = customers.remove(random.nextInt(customers.size()));

            // Finding the closest eligible car
            double bestDistance = Double.MAX_VALUE;
            Car bestCar = null;
            for (Car car : proposedSolution.cars) {

                double distance = euclideanDistance(customer.getX(), customer.getY(), car.getX(), car.getY());
                if (distance < bestDistance || bestCar == null) {

                    // Check if the car currently in consideration is actually eligible to serve the customer
                    if (car.isEligible(customer)) {
                        bestDistance = distance;
                        bestCar = car;
                    }
                }
            }

            // Check if no car was eligible to serve the customer
            if (bestCar == null) {
                return null;
            }

            // Updating statistics for the car
            bestCar.addCustomerVisited(customer);
            bestCar.addLoad(customer.getDemand());
            bestCar.addDuration(euclideanDistance(customer.getX(), customer.getY(), bestCar.getX(), bestCar.getY()));

            // Updating the new car position
            bestCar.setX(customer.getX());
            bestCar.setY(customer.getY());
        }

        // Driving all cars back to the depots from their current positions
        for (Car car : proposedSolution.cars) {
            car.addDuration(euclideanDistance(car.getX(), car.getY(), car.getDepot().getX(), car.getDepot().getY()));
        }

        return proposedSolution;
    }

    public ProposedSolution[] selectParent(ProposedSolution[] solutions){

        this.currentPopulation = solutions;
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

        // mutate the children
        mutate(children, mutationRate);

        return children;
    }

    //@TODO proper copulate method. Create new ProposedSolution objects as we need the old ones for survivor selection
    public ProposedSolution copulate(ProposedSolution parent1, ProposedSolution parent2){

        //Random rand = new Random();

        Depot[] depotsParent1 = new Depot[processFile.depot_count]; //all depots in the first parent
        Depot[] depotsParent2 = new Depot[processFile.depot_count]; //all depots in second parent


        for(int i=0;i<parent1.depots.length;i++){
            depotsParent1[i] = new Depot(parent1.depots[i]);
            depotsParent2[i] = new Depot(parent2.depots[i]);
        }

        Car route1 = depotsParent1[random.nextInt(processFile.depot_count)].getCars()[random.nextInt(processFile.vehicle_count)];   //random route frm first parent
        Car route2 = depotsParent2[random.nextInt(processFile.depot_count)].getCars()[random.nextInt(processFile.vehicle_count)];   //random route from second parent

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
        Random rand = new Random();

        //get random car
        Car car = solution.cars[rand.nextInt(solution.cars.length)];

        //route of selected car
        ArrayList<Customer> customerSequence = car.getCustomerSequence();

        //if there is only one car, two cars or no cars in the sequence, no point in inversing, we just return
        if(customerSequence.size() < 3){
            return;
        }

        int startIndex = rand.nextInt(customerSequence.size()); //startindex of reverse
        int endIndex = rand.nextInt(customerSequence.size());   //end index

        //if start index is greater than the end index we selected, swap them
        if(startIndex > endIndex){
            int tmp = startIndex;
            startIndex = endIndex;
            endIndex = tmp;
        }
        else if(startIndex == endIndex){  //if they are equal we increase the end index by one if possible (as long as it's not already at the last array element, then we decrease the start index instead)
            if(endIndex < customerSequence.size() - 1){
                endIndex++;
            }
            else{
                startIndex--;
            }
        }
        //no point in reversing the whole route. Start index should then be
        if(startIndex == 0 && endIndex == customerSequence.size() -1){
            startIndex ++;
        }

        //create an array with the inverse route section.
        Customer[] inverseCustomerArray = new Customer[endIndex-startIndex + 1];
        int count = 0;
        for(int i=endIndex; i > startIndex-1; i--){
            inverseCustomerArray[count] = customerSequence.get(i);
            count++;
        }
        count = 0;
        //add all the elements to the arraylist in reverse order.
        for(int i=startIndex; i < endIndex+1; i++){
           customerSequence.add(i,inverseCustomerArray[count]);
           count ++;
        }
    }

    //selects the populationsize best individuals
    public ProposedSolution[] select(ProposedSolution[] parents, ProposedSolution[] offspring){

        //list of survivors, need to be as big as the initial population count
        ProposedSolution[] survivors = new ProposedSolution[this.populationSize];
        ProposedSolution[] selectionPool = new ProposedSolution[parents.length+offspring.length];

        //add both parents and offspring into one combined array
        int index = offspring.length;
        for (int i = 0; i < offspring.length; i++) {
            selectionPool[i] = offspring[i];
        }
        for (int i = 0; i < parents.length; i++) {
            selectionPool[i + index] = parents[i];
        }

        //iterate over parent and offspring lists, each time select the best individual
        for(int i=0; i<survivors.length;i++){

            //current best solution
            ProposedSolution bestSolution = null;
            Double bestFitness = Double.MAX_VALUE;
            int bestSolutionIndex = -1;

            //iterate over the selectionPool (parents and offspring) and select the best one each time
            for(int j=0;j<selectionPool.length;j++){

                ProposedSolution solution = selectionPool[j];
                if(solution != null && solution.getFitness() < bestFitness){
                    bestSolution = solution;
                    bestFitness = solution.getFitness();
                    bestSolutionIndex = j;
                }
            }
            survivors[i] = bestSolution;
            selectionPool[bestSolutionIndex] = null;
        }

        return survivors;
    }

    //calculates the euclidean distance from a to b
    static double euclideanDistance(int x1, int y1, int x2, int y2){

        double x_travelled = Math.abs(x1 - x2);
        double y_travelled = Math.abs(y1 - y2);

        return Math.sqrt(Math.pow(x_travelled, 2) + Math.pow(y_travelled, 2));
    }

}
