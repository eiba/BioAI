import java.util.*;

public class Population {

    private final ProcessFile processFile;
    private final Random random;
    private int populationSize;
    private ProposedSolution[] currentPopulation;
    private HashMap<Integer, int[]> preferedCustomerDepots;

    Population(ProcessFile processFile) {
        this.processFile = processFile;
        random = new Random();
    }

    ProposedSolution[] generateInitialPopulation(int populationSize) {
        this.populationSize = populationSize;

        // Initiating variables
        final ProposedSolution[] proposedSolutions = new ProposedSolution[populationSize];

        // Grouping customers to depots
        preferedCustomerDepots = new HashMap<>();
        for (Customer customer : processFile.customers) {
            final Depot[] depots = processFile.getDepots();
            final int[] depotNumbers = new int[processFile.depotCount];

            Comparator<Depot> distanceComparator = (o1, o2) -> (int) (euclideanDistance(customer.getX(), customer.getY(), o1.getX(), o1.getY()) - euclideanDistance(customer.getX(), customer.getY(), o2.getX(), o2.getY()));
            Arrays.sort(depots, distanceComparator);

            for (int i = 0; i < depotNumbers.length; i ++) {
                //Checking if a car from the depot even can reach the customer and back
                if (depots[i].getMaximumDuration() == Integer.MAX_VALUE) {
                    depotNumbers[i] = depots[i].getDepotNr();
                }
                else {
                    if (euclideanDistance(customer.getX(), customer.getY(), depots[i].getX(), depots[i].getY()) > 2 * depots[i].getMaximumDuration()) {
                        depotNumbers[i] = -1;
                        break;
                    }
                    else {
                        depotNumbers[i] = depots[i].getDepotNr();
                    }
                }
            }

            preferedCustomerDepots.put(customer.getCustomerNr(), depotNumbers);
        }

        // Generating a random solution for each iteration
        for (int i = 0; i < populationSize; i ++) {
            //Add the solution to the solutions list
            ProposedSolution proposedSolution;
            do {
                proposedSolution = generateSolution();
            }
            while (proposedSolution == null);
            proposedSolutions[i] = proposedSolution;
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

            final int[] preferredDepots = preferedCustomerDepots.get(customer.getCustomerNr());
//                System.out.println(preferredDepots[0]);

            double bestDistance = Double.MAX_VALUE;
            double bestIndex = -1;
            Car bestCar = null;

            // Trying to assign the customer to a preferred depot
            for (final int depotNr : preferredDepots) {
                Depot depot = null;

                // No depot can serve this customer
                if (depotNr == -1) {
                    if (bestCar == null) {
                        return null;
                    }
                    break;
                }

                // Finding the correct depot
                for (Depot depot1 : depots) {
                    if (depot1.getDepotNr() == depotNr) {
                        depot = depot1;
                        break;
                    }
                }

                if (depot == null) {
                    return null;
                }

                // Finding a car from the depot that can add the customer to its route
                for (Car car : depot.getCars()) {
                    // Check if the car is eligible to serve an additional customer
                    if (car.isEligible(customer)) {
                        final double[] carExtraDuration = car.smartCheckExtraDuration(customer);
                        if (carExtraDuration[1] < bestDistance) {
                            bestDistance = carExtraDuration[1];
                            bestCar = car;
                            bestIndex = carExtraDuration[0];
                        }
                    }
                }



                if (bestCar != null) {
                    break;
                }
            }

            if (bestCar == null) {
                // Remove a random Customer from preferred depot in hope of fix
                int depotNr = preferredDepots[0];
                for (Depot depot1 : depots) {
                    if (depot1.getDepotNr() == depotNr) {
                        Car randomCar = depot1.getCars()[random.nextInt(depot1.getCars().length)];
                        randomCar.remove(randomCar.getCustomerSequence().get(random.nextInt(randomCar.getCustomerSequence().size())));
                        break;
                    }
                }
            }
            else {
                bestCar.smartAddCustomerVisited(customer, (int) bestIndex);
            }


        }

        return proposedSolution;
    }

    /**
     * Parent Selection based on Tournament Selection
     * @param solutions List of all possible participants
     * @param numberOfTournaments Number of tournaments to be held
     * @return a list of parents selected in the tournament selection
     */
    private ProposedSolution tournamentSelection(ProposedSolution[] solutions, int numberOfTournaments){

        final Random random = new Random();
        ProposedSolution winner = null;

        for (int i = 0; i < numberOfTournaments; i ++) {
            final ProposedSolution participant = solutions[random.nextInt(solutions.length)];

            if (winner == null || participant.getFitness() < winner.getFitness()) {
                winner = participant;
            }
        }

        return winner;

//        func tournament_selection(pop, k):
//        best = null
//        for i=1 to k
//        ind = pop[random(1, N)]
//        if (best == null) or fitness(ind) > fitness(best)
//        best = ind
//        return best



//        this.currentPopulation = solutions;
//        //list containing all selected parents
//        //There should be as many selected parents as there are specimen in the population as one parent can eb selected more than once
//        ProposedSolution[] selected_parents = new ProposedSolution[solutions.length];
//
//        double score_sum = 0.0;
//
//        for(ProposedSolution solution : solutions){
//            score_sum += 1/solution.getFitness();
//        }
//
//        //for each iteration add a parent
//        for(int i=0;i<solutions.length;i++){
//
//            double p = Math.random();   //random number from 0 to 1
//
//            double cumulativeProbability = 0.0;
//            for (ProposedSolution solution : solutions) {
//                cumulativeProbability +=  (1 / solution.getFitness()) / score_sum;    //add to the cumulative probability
//
//                if (p <= cumulativeProbability) {
//                    selected_parents[i] = solution;
//                    break;
//                }
//            }
//        }
    }

    public ProposedSolution[] crossover(ProposedSolution[] solutions, double mutationRate){

        ProposedSolution[] children = new ProposedSolution[solutions.length];
        Random rand = new Random();

        for(int i=0;i<children.length;i++){
            ProposedSolution parent1 = solutions[rand.nextInt(solutions.length)];
            ProposedSolution parent2 = solutions[rand.nextInt(solutions.length)];

            children[i] = copulate(parent1,parent2);
        }

        // mutate the children
        mutate(solutions, mutationRate);

        //after creating and mutating children, we score them.
        for(ProposedSolution child: children){

            scoreSolution(child);
        }

        return children;
    }

    
    ProposedSolution[] crossoverMartin(ProposedSolution[] parents, int numberOfTournaments, double mutationRate, int populationSize) {
        final ProposedSolution[] children = new ProposedSolution[populationSize];

        int index = 0;
        while (index != populationSize) {
            final ProposedSolution parent1 = tournamentSelection(parents, numberOfTournaments);
            final ProposedSolution parent2 = tournamentSelection(parents, numberOfTournaments);
            final ProposedSolution child = bestCostRouteCrossover(parent1, parent2);
            if (child != null) {
                children[index ++] = child;
            }
        }

        mutate(children, mutationRate);

        return children;
    }

    private ProposedSolution bestCostRouteCrossover(ProposedSolution parent1, ProposedSolution parent2) {

        // Creating a child based on a deep copy of the parent1 object
        final ProposedSolution child = new ProposedSolution(parent1);

        // Selecting a random route from parent2
        final Random random = new Random();
        final ArrayList<Customer> parentCustomerSequence = parent2.cars[random.nextInt(parent2.cars.length)].getCustomerSequence();
        final ArrayList<Customer> childCustomers = new ArrayList<>();

        // Removing the customers from the child's routes
        customerLoop: for (Customer parentCustomer : parentCustomerSequence) {
            for (Car childCar : child.cars) {
                for (Customer childCustomer : childCar.getCustomerSequence()) {
                    if (parentCustomer.getCustomerNr() == childCustomer.getCustomerNr()) {
                        childCar.remove(childCustomer);
                        childCustomers.add(childCustomer);
                        continue customerLoop;
                    }
                }
            }
        }

        // Finding the best route to add the customers
        for (Customer customer : childCustomers) {

            double bestDistance = Double.MAX_VALUE;
            Car bestCar = null;
            int bestIndex = -1;

            // Looping through all car routes
            for (Car car : child.cars) {

                if (car.isEligible(customer)) {
                    final double[] stats = car.smartCheckExtraDuration(customer);

                    // Check if the additional distance required for adding the car is less than current best
                    if (stats[1] - car.getCurrentDuration() < bestDistance) {
                        bestDistance = stats[1] - car.getCurrentDuration();
                        bestCar = car;
                        bestIndex = (int) stats[0];
                    }
                }

            }

            if (bestCar == null) {
                return null;
            }

            bestCar.smartAddCustomerVisited(customer, bestIndex);
        }

        return child;
    }

    // @TODO proper copulate method. Create new ProposedSolution objects as we need the old ones for survivor selection
    public ProposedSolution copulate(ProposedSolution parent1, ProposedSolution parent2){


        Depot[] depotsParent1 = new Depot[processFile.depotCount]; //all depots in the first parent
        Depot[] depotsParent2 = new Depot[processFile.depotCount]; //all depots in second parent


        //We need to create depots for a new solution without altering the old one.
        for(int i=0;i<parent1.depots.length;i++){
            depotsParent1[i] = new Depot(parent1.depots[i],false);
            depotsParent2[i] = new Depot(parent2.depots[i],false);

        }

        ProposedSolution parent1Copy = new ProposedSolution(depotsParent1);
        ProposedSolution parent2Copy = new ProposedSolution(depotsParent2);

        int route1Index = random.nextInt(parent1Copy.cars.length);
        Car route1Car = parent1Copy.cars[route1Index];   //random route from first parent

        int route2Index = random.nextInt(parent2Copy.cars.length);
        Car route2Car = parent2Copy.cars[route2Index];   //random route from second parent

        ArrayList<Customer> route1Sequence = route1Car.getCustomerSequence();
        ArrayList<Customer> route2Sequence = route2Car.getCustomerSequence();

        //route1Sequence.removeAll(route2Sequence);
        ArrayList<Customer> insertionRoute = new ArrayList<>();
        for(Customer customer: route2Sequence){
            if(!route1Sequence.contains(customer)){
                insertionRoute.add(customer);
            }
        }
        if(insertionRoute.size() == 0){
            return parent1Copy;
        }


        /*for(Car car: parent1Copy.cars){
            ArrayList<Customer> customersToRemove = new ArrayList<>();
            for(Customer customer1: car.getCustomerSequence()){
                for(Customer customer2: insertionRoute){
                    if(customer1 == customer2){
                        customersToRemove.add(customer1);
                    }
                }
            }
            car.getCustomerSequence().removeAll(customersToRemove);
        }*/

        Car[] newCars = new Car[route1Sequence.size() +1];
        for(int i=0;i<route1Sequence.size()+1;i++){
            route1Sequence.addAll(i,insertionRoute);
            newCars[i] = evaluateRoute(route1Sequence,route1Car);
            route1Sequence.removeAll(insertionRoute);

        }

        Car bestCar = null;
        Double bestDuration = Double.MAX_VALUE;

        for(Car car: newCars){
            if(car.getCurrentDuration() < bestDuration){
                bestDuration = car.getCurrentDuration();
                bestCar = car;
            }
        }
//        bestCar.setCurrentLoad(0);
        bestCar.setCurrentDuration(0.0);

        parent1Copy.cars[route1Index] = bestCar;

        return parent1Copy;
    }



    //evaluates one customer route
    public Car evaluateRoute(ArrayList<Customer> route, Car car){

        Car newCar = new Car(car.getVehicleNumber(),car.getMaximumLoad(),car.getMaximumDuration(),car.getDepot());

        for(Customer customer: route){
            newCar.addDuration(euclideanDistance(newCar.getX(),newCar.getY(),customer.getX(),customer.getY()));
//            newCar.addLoad(customer.getDemand());
            newCar.setX(customer.getX());
            newCar.setY(customer.getY());
            newCar.addCustomerVisited(customer);
        }
        newCar.addDuration(euclideanDistance(car.getX(),car.getY(),car.getDepot().getX(),car.getDepot().getY()));
        newCar.setX(car.getDepot().getX());
        newCar.setY(car.getDepot().getY());

        return newCar;
    }

    public HashMap<Customer,Car> convertToGenotype(ProposedSolution solution){

        HashMap<Customer,Car> genotype = new HashMap<>();
        Depot[] depotsParent = new Depot[processFile.depotCount]; //all depots in the parent

        for(int i=0;i<solution.depots.length;i++){
            depotsParent[i] = new Depot(solution.depots[i],false);
        }

        for(Depot depot: depotsParent){
            for(Car car: depot.getCars()){
                for(Customer customer: car.getCustomerSequence()){
                    genotype.put(customer,car);
                }
            }
        }

        return genotype;
    }

    public ProposedSolution convertToPhenotype(HashMap<Customer,Car> genotype){

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
    }

    public void inverseMutation(ProposedSolution solution){
        //implement inverse mutation

        //get random car
        Car car = solution.cars[random.nextInt(solution.cars.length)];
        int iter = 0;
        //make sure that we get list with more than 2 customers or else there is no point in inverting
        while (car.getCustomerSequence().size() < 3){
            car = solution.cars[random.nextInt(solution.cars.length)];

            //I give up, return.
            if(iter > solution.cars.length){
                return;
            }
            iter++;
        }
        //route of selected car
        ArrayList<Customer> customerSequence = car.getCustomerSequence();

        //if there is only one car, two cars or no cars in the sequence, no point in inversing, we just return
        /*if(customerSequence.size() < 3){
            return;
        }*/

        int startIndex = random.nextInt(customerSequence.size()); //startindex of reverse
        int endIndex = random.nextInt(customerSequence.size());   //end index

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
            customerSequence.remove(i);
           customerSequence.add(i,inverseCustomerArray[count]);
           count ++;
        }
        car.updateDistance();
        //solution.evaluateFitness();
    }

    //Simple mutation method that takes a random customer from a random route
    // and swaps it with another random customer from another random route
    public void swapMutation(ProposedSolution solution){


        Car car1 = solution.cars[random.nextInt(solution.cars.length)];
        Car car2 = solution.cars[random.nextInt(solution.cars.length)];

        ArrayList<Customer> car1Customers = car1.getCustomerSequence();
        ArrayList<Customer> car2Customers = car2.getCustomerSequence();

        int car1RemovalIndex = random.nextInt(car1Customers.size());
        int car2RemovalIndex = 0;
        if(car1 == car2){
            car2RemovalIndex = random.nextInt(car2Customers.size()-1);
        }

        if(car1Customers.size() == 0 || car2Customers.size() == 0) {
            return;
        }


            Customer car1Customer = car1Customers.remove(car1RemovalIndex);
            Customer car2Customer = car2Customers.remove(car2RemovalIndex);

            car1Customers.add(car1RemovalIndex, car2Customer);
            car2Customers.add(car2RemovalIndex, car1Customer);


    }

    //selects the population size best individuals
    public ProposedSolution[] select(ProposedSolution[] parents, ProposedSolution[] offspring){

        //list of survivors, need to be as big as the initial population count
        ProposedSolution[] survivors = new ProposedSolution[this.populationSize];
        ProposedSolution[] selectionPool = new ProposedSolution[parents.length + offspring.length];

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

    //Score a solution. Calculate durations, loads and fitness score
    public void scoreSolution(ProposedSolution solution){

        //iterate over all cars in solution, and all customers in each car and add the loads and durations.
        for(Car car: solution.cars){
            //car.setX(car.getDepot().getX());
            //car.setY(car.getDepot().getY());
            ArrayList<Customer> customerSequence = car.getCustomerSequence();

            for (Customer customer:customerSequence){
//                car.addLoad(customer.getDemand());
                car.addDuration(euclideanDistance(customer.getX(), customer.getY(), car.getX(), car.getY()));
                car.setX(customer.getX());
                car.setY(customer.getY());
            }

            //Driving the car home :)
            car.addDuration(euclideanDistance(car.getX(), car.getY(), car.getDepot().getX(), car.getDepot().getY()));
        }

        //evaluate the total fitness of the population
        solution.evaluateFitness();
    }

    //calculates the euclidean distance from a to b
    static double euclideanDistance(double x1, double y1, double x2, double y2){

        double x_travelled = Math.abs(x1 - x2);
        double y_travelled = Math.abs(y1 - y2);

        return Math.sqrt(Math.pow(x_travelled, 2) + Math.pow(y_travelled, 2));
    }

}
