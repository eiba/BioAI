import java.util.*;

public class Population {

    private final ProcessFile processFile;
    private final Statistic statistic;
    private final Random random;
    private final Comparator<ProposedSolution> selectionComparator;
    private int populationSize;
    private ProposedSolution[] currentPopulation;
    private HashMap<Integer, int[]> preferedCustomerDepots;

    Population(ProcessFile processFile, Statistic statistic) {
        this.processFile = processFile;
        this.statistic = statistic;
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
            statistic.setUpdate("Generating valid initial solutions: " + (i+1) + "/" + populationSize);
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
                        final double newDistance = carExtraDuration[1] - car.getCurrentDuration();
                        if (newDistance < bestDistance) {
                            bestDistance = newDistance;
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
    private ProposedSolution tournamentSelection(ProposedSolution[] solutions, int numberOfTournaments, Double threshold){

        final Random random = new Random();
        ProposedSolution winner = null;
        Double p = Math.random();

        for (int i = 0; i < numberOfTournaments; i ++) {
            final ProposedSolution participant = solutions[random.nextInt(solutions.length)];

            if (winner == null || participant.getFitness() < winner.getFitness() || p > threshold) {
                winner = participant;
            }
        }

        return winner;

    }
    
    ProposedSolution[] crossover(ProposedSolution[] parents, int numberOfTournaments, double mutationRate, int populationSize, Double threshold) {
        final ProposedSolution[] children = new ProposedSolution[populationSize];

        int index = 0;
        while (index != populationSize) {
            final ProposedSolution parent1 = tournamentSelection(parents, numberOfTournaments, threshold);
            final ProposedSolution parent2 = tournamentSelection(parents, numberOfTournaments, threshold);
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

    //Mutate children
    public void mutate(ProposedSolution[] solutions, double mutationRate){

        for(ProposedSolution solution: solutions){
            double p = Math.random();

            // Mutate with a probability of mutationRate
            if(mutationRate >= p){
                inverseMutation(solution);
            }
        }
    }

    public void inverseMutation(ProposedSolution solution){
        //implement inverse mutation

        //get random car
        Car originalCar = solution.cars[random.nextInt(solution.cars.length)];

        int iteration = 0;
        //make sure that we get list with more than 2 customers or else there is no point in inverting
        while (originalCar.getCustomerSequence().size() < 3){
            originalCar = solution.cars[random.nextInt(solution.cars.length)];

            //I give up, return.
            if(iteration > solution.cars.length){
                return;
            }
            iteration++;
        }
        Car car = Car.copyCar(originalCar);
        //route of selected car
        ArrayList<Customer> customerSequence = car.getCustomerSequence();

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

    //selects the population size best individuals
    public ProposedSolution[] select(ProposedSolution[] parents, ProposedSolution[] offspring, int maximumAge){

        final PriorityQueue<ProposedSolution> priorityQueue = new PriorityQueue<>(selectionComparator);

        priorityQueue.addAll(Arrays.asList(parents));
        priorityQueue.addAll(Arrays.asList(offspring));

        // List of survivors, need to be as big as the initial population count
        final ProposedSolution[] survivors = new ProposedSolution[this.populationSize];

        int index = 0;
        while (index < survivors.length) {
            final ProposedSolution selected = priorityQueue.remove();
            if (selected.age < maximumAge) {
                selected.age ++;
                survivors[index ++] = selected;
            }
        }
        
        return survivors;
    }

    //calculates the euclidean distance from a to b
    static double euclideanDistance(double x1, double y1, double x2, double y2){

        double x_travelled = Math.abs(x1 - x2);
        double y_travelled = Math.abs(y1 - y2);

        return Math.sqrt(Math.pow(x_travelled, 2) + Math.pow(y_travelled, 2));
    }

}
