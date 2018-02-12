import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Population {

    private final ProcessFile processFile;
    private final Statistic statistic;
    private final Random random;
    private final Comparator<ProposedSolution> selectionComparator;
    private final int maxIterations;
    private HashMap<Integer, int[]> preferredCustomerDepots;

    Population(ProcessFile processFile, Statistic statistic, int maxIterations) {
        this.processFile = processFile;
        this.statistic = statistic;
        this.maxIterations = maxIterations;
        random = new Random();
        selectionComparator = (o1, o2) -> {
            if (o1.getFitness() < o2.getFitness()) {
                return -1;
            }
            else if (o1.getFitness() > o2.getFitness()) {
                return 1;
            }
            return 0;
        };
    }

    ProposedSolution[] generateInitialPopulation(int populationSize) {

        // Initiating variables
        final ProposedSolution[] proposedSolutions = new ProposedSolution[populationSize];

        // Grouping customers to depots
        preferredCustomerDepots = new HashMap<>();
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

            preferredCustomerDepots.put(customer.getCustomerNr(), depotNumbers);
        }

        final ExecutorService executor = Executors.newFixedThreadPool(Main.PROCESSORS);
        // Generating a random solution for each iteration
        for (int i = 0; i < populationSize; i ++) {
            final int index = i;
            executor.execute(() -> {
                statistic.setUpdate("Generating valid initial solutions: " + (index+1) + "/" + populationSize);
                //Add the solution to the solutions list
                ProposedSolution proposedSolution;
                do {
                    proposedSolution = generateSolution();
                }
                while (proposedSolution == null);
                proposedSolutions[index] = proposedSolution;
            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
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

            final int[] preferredDepots = preferredCustomerDepots.get(customer.getCustomerNr());
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
                    final double[] smartCheck = car.isEligible(customer);
                    if (smartCheck[0] != -1) {
                        final double newDistance = smartCheck[1] - car.getCurrentDuration();
                        if (newDistance < bestDistance) {
                            bestDistance = newDistance;
                            bestCar = car;
                            bestIndex = smartCheck[0];
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
                        Customer customer1 = randomCar.getCustomerSequence().get(random.nextInt(randomCar.getCustomerSequence().size()));
                        randomCar.remove(customer1);
                        customers.add(customer);
                        customers.add(customer1);
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
        ProposedSolution winner = null;

        for (int i = 0; i < numberOfTournaments; i ++) {
            final ProposedSolution participant = solutions[random.nextInt(solutions.length)];

            if (winner == null || participant.getFitness() < winner.getFitness()) {
                winner = participant;
            }
        }

        return winner;

    }
    
    ProposedSolution[] crossover(ProposedSolution[] parents, int numberOfTournaments, double mutationRate, int populationSize, int iteration) {
        final ProposedSolution[] children = new ProposedSolution[populationSize];
        final ExecutorService executor = Executors.newFixedThreadPool(Main.PROCESSORS);

        for (int i = 0; i < populationSize; i ++) {

            final int index = i;
            executor.execute(() -> {

                ProposedSolution child, parent1, parent2;
                do {
                    parent1 = tournamentSelection(parents, numberOfTournaments);
                    parent2 = tournamentSelection(parents, numberOfTournaments);
                    child = bestCostRouteCrossover(parent1, parent2, iteration);
                }
                while (child == null);
                children[index] = child;

            });
        }

        executor.shutdown();
        while (!executor.isTerminated());

        mutate(children, mutationRate, iteration);

        return children;
    }

    private ProposedSolution bestCostRouteCrossover(ProposedSolution parent1, ProposedSolution parent2, int iteration) {

        // Creating a child based on a deep copy of the parent1 object
        final ProposedSolution child = new ProposedSolution(parent1);
        // Selecting a random route from parent2
        final HashMap<Integer, Integer> customerCarMap = new HashMap<>();
        final Car randomCar =  parent2.cars[random.nextInt(parent2.cars.length)];
        final int depotNr = randomCar.getDepot().getDepotNr();
        final ArrayList<Customer> parentCustomerSequence = randomCar.getCustomerSequence();
        final ArrayList<Customer> childCustomers = new ArrayList<>();

        // Removing the customers from the child's routes
        customerLoop: for (Customer parentCustomer : parentCustomerSequence) {
            for (Car childCar : child.cars) {
                for (Customer childCustomer : childCar.getCustomerSequence()) {
                    if (parentCustomer.getCustomerNr() == childCustomer.getCustomerNr()) {
                        customerCarMap.put(childCustomer.getCustomerNr(), childCar.getVehicleNumber());
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

                if (car.getDepot().getDepotNr() == depotNr) {
                    if (customerCarMap.get(customer.getCustomerNr()) == car.getVehicleNumber()) {
                        double random = Math.random();
                        if (random < (double) iteration / maxIterations) {
                            continue;
                        }
                    }
                }

                final double[] smartCheck = car.isEligible(customer);
                if (smartCheck[0] != -1) {

                    // Check if the additional distance required for adding the car is less than current best
                    if (smartCheck[1] - car.getCurrentDuration() < bestDistance) {
                        bestDistance = smartCheck[1] - car.getCurrentDuration();
                        bestCar = car;
                        bestIndex = (int) smartCheck[0];
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

    //Mutate children
    private void mutate(ProposedSolution[] solutions, double mutationRate, int iteration){

        final ExecutorService executor = Executors.newFixedThreadPool(Main.PROCESSORS);

        for (int i = 0; i < solutions.length; i ++) {
            final int index = i;
            executor.execute(() -> {
                double p = Math.random();
                // Mutate with a probability of mutationRate
                if(mutationRate >= p){
                    stealMutation(solutions[index], iteration);
//                    mergeMutation(solutions[index]);
                }
            });
        }
        executor.shutdown();
        while (!executor.isTerminated());

    }

    private void inverseMutation(ProposedSolution solution){

        // Get random car
        Car originalCar = solution.cars[random.nextInt(solution.cars.length)];

        int iteration = 0;
        // Make sure that we get list with more than 2 customers or else there is no point in inverting
        while (originalCar.getCustomerSequence().size() < 3){
            originalCar = solution.cars[random.nextInt(solution.cars.length)];

            // I give up, return.
            if(iteration > solution.cars.length){
                return;
            }
            iteration++;
        }
        Car car = Car.copyCar(originalCar);
        //route of selected car
        ArrayList<Customer> customerSequence = car.getCustomerSequence();

        int startIndex = random.nextInt(customerSequence.size()); //start index of reverse
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
            inverseCustomerArray[count] = customerSequence.remove(i);
            count++;
        }
        count = 0;
        //add all the elements to the arraylist in reverse order.
        for(int i=startIndex; i < endIndex+1; i++){
            //customerSequence.remove(i);
           customerSequence.add(i,inverseCustomerArray[count]);
           count ++;
        }
        //update the distance traveled for the new route
        car.updateDistance();

        //if the new route is legal, we change the route and distance traveled of the original car, thus altering the solution
        //if the route is not legal nothing is changed and we return
        if(car.getMaximumDuration() >= car.getCurrentDuration()){
            originalCar.customerSequence = car.getCustomerSequence();
            originalCar.currentDuration = car.currentDuration;
        }
    }

    //Simple mutation method that takes a random customer from a random route
    // and swaps it with another random customer from another random route
    public void swapMutation(ProposedSolution solution){


        Car car1Original = solution.cars[random.nextInt(solution.cars.length)];
        Car car2Original = solution.cars[random.nextInt(solution.cars.length)];

        Car car1 = Car.copyCar(car1Original);
        Car car2 = Car.copyCar(car2Original);

        ArrayList<Customer> car1Customers = car1.getCustomerSequence();
        ArrayList<Customer> car2Customers = car2.getCustomerSequence();

        if(car1Customers.size() == 0 || car2Customers.size() == 0) {
            return;
        }

        int car1RemovalIndex = random.nextInt(car1Customers.size());
        int car2RemovalIndex = random.nextInt(car2Customers.size());

        Customer car1Customer = car1Customers.get(car1RemovalIndex);
        Customer car2Customer = car2Customers.get(car2RemovalIndex);

        if(car1Customer == car2Customer){
            car1Customers.add(car1RemovalIndex,car1Customer);
            return;
        }

        car1Customers.add(car1RemovalIndex, car2Customer);
        car2Customers.add(car2RemovalIndex, car1Customer);

        car1.updateDistance();
        car2.updateDistance();

        car1.updateLoad();
        car2.updateLoad();

        if(car1.isValid() && car2.isValid()){
            car1Original.customerSequence = car1.customerSequence;
            car2Original.customerSequence = car2.customerSequence;

            car1Original.currentLoad = car1.currentLoad;
            car2Original.currentLoad = car2.currentLoad;

            car1Original.currentDuration = car1.currentDuration;
            car2Original.currentDuration = car2.currentDuration;
        }

    }

    private void stealMutation(ProposedSolution solution, int iteration) {

        // Get a random depot
//        final Depot depot = solution.depots[random.nextInt(solution.depots.length)];
//        final Car[] cars = depot.getCars();
        final Car[] cars = solution.cars;

        // Get a random car to steal a customer from
        Car car = null;
        do {
            car = cars[random.nextInt(cars.length)];
        }
        while (car == null || car.customerSequence.size() == 0);

        //Get a random customer to give away
        final Customer customer = car.customerSequence.get(random.nextInt(car.customerSequence.size()));

        Car bestCar = null;
        int bestIndex = -1;
        double bestDistance = 0;

        for (Car car1 : cars) {
            final double[] smartCheck = car1.isEligible(customer);
            if (smartCheck[0] != -1) {
                if (smartCheck[1] - car1.currentDuration < bestDistance) {
                    bestCar = car1;
                    bestIndex = (int) smartCheck[0];
                    bestDistance = smartCheck[1] - car1.currentDuration;
                    final double random = Math.random();
                    if (random < (double) iteration / maxIterations) {
                        break;
                    }
                }
            }
        }

        if (bestCar != null) {
            car.remove(customer);
            bestCar.smartAddCustomerVisited(customer, bestIndex);
        }
    }

    void mergeMutation(ProposedSolution solution) {

        final Car[] cars = solution.cars;

        // Get a random car to steal customers from
        Car car;
        do {
            car = cars[random.nextInt(cars.length)];
        }
        while (car == null || car.customerSequence.size() == 0);

        //Get a random customer to give away
        int index = 0;
        for (int i = 0; i < car.customerSequence.size(); i ++) {
            Customer customer = car.customerSequence.get(index);
            Car bestCar = null;
            int bestIndex = -1;
            double bestDistance = 0;

            for (Car car1 : cars) {
                final double[] smartCheck = car1.isEligible(customer);
                if (smartCheck[0] != -1) {
                    if (smartCheck[1] - car1.currentDuration < bestDistance) {
                        bestCar = car1;
                        bestIndex = (int) smartCheck[0];
                        bestDistance = smartCheck[1] - car1.currentDuration;
                    }
                }
            }

            if (bestCar != null) {
                car.remove(customer);
                bestCar.smartAddCustomerVisited(customer, bestIndex);
            }
            else {
                index ++;
            }
        }


    }

    //selects the population size best individuals
    public ProposedSolution[] select(ProposedSolution[] parents, ProposedSolution[] offspring, int maximumAge, int populationSize){

        final ArrayList<ProposedSolution> priorityQueue = new ArrayList<>();

        priorityQueue.addAll(Arrays.asList(parents));
        priorityQueue.addAll(Arrays.asList(offspring));

        //Comment out if we want to remove based on age
        ArrayList<ProposedSolution> solutions_to_remove = new ArrayList<>();

        for(ProposedSolution solution: priorityQueue){
            if (solution.age > maximumAge){
                solutions_to_remove.add(solution);
            }
        }
        priorityQueue.removeAll(solutions_to_remove);

        priorityQueue.sort(selectionComparator);

        // List of survivors, need to be as big as the initial population count
        final ProposedSolution[] survivors = new ProposedSolution[populationSize];

        int index = 0;

        ProposedSolution bestSolution = null;
        Double bestFitness = Double.MAX_VALUE;
        int bestIndex = 0;
        while (index < survivors.length) {
            int rank = priorityQueue.size();
            int rankSum = 0;
            for(int i=priorityQueue.size();i>0;i--){
                rankSum += i;
            }
            Double p = Math.random();
            Double cumulativeProbability = 0.0;
            int listIndex = 0;
            while (!priorityQueue.isEmpty()){

                ProposedSolution solution = priorityQueue.get(listIndex);

                cumulativeProbability += (double) rank/rankSum;

                if(p <= cumulativeProbability){

                    //if the solution is the best solution we've seen, add as such
                    if(bestFitness > solution.getFitness()){
                        bestFitness = solution.getFitness();
                        bestIndex = index;
                        bestSolution = solution;
                    }

                    solution.age++;
                    survivors[index ++] = priorityQueue.remove(listIndex);
                    break;
                }
                listIndex++;
                rank--;
            }
        }

        //Make best solution first in the list
        survivors[bestIndex] = survivors[0];
        survivors[0] = bestSolution;

        return survivors;
    }

//    ProposedSolution[] selectParentOffspring(ProposedSolution[] offspring) {
//        final ProposedSolution[] selection = new ProposedSolution[offspring.length + parentList.size()];
//        System.arraycopy(offspring, 0, selection, 0, offspring.length);
//        ProposedSolution[] parents = new ProposedSolution[parentList.size()];
//        parents =  parentList.toArray(parents);
//        System.arraycopy(parents, 0, selection, offspring.length, parents.length);
//
//        Arrays.sort(selection, selectionComparator);
//
//        return selection;
//    }

    //calculates the euclidean distance from a to b
    static double euclideanDistance(double x1, double y1, double x2, double y2){

        double x_travelled = Math.abs(x1 - x2);
        double y_travelled = Math.abs(y1 - y2);

        return Math.sqrt(Math.pow(x_travelled, 2) + Math.pow(y_travelled, 2));
    }

}
