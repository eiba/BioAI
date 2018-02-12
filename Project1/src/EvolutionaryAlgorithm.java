public class EvolutionaryAlgorithm {

    private final Population population;
    private final Statistic statistic;
    private final StatGraph statGraph;
    final ProcessFile processFile;
    public int iterationsUsed;

    EvolutionaryAlgorithm(String filename, Statistic statistic, StatGraph statGraph, int iterations) {
        // Reading the Multiple Depot Vehicle Routing Problem - MDVRP
        processFile = new ProcessFile(filename);
        this.statistic = statistic;
        this.statGraph = statGraph;

        // Initiating variables
        population = new Population(processFile, statistic, iterations);

    }

    /**
     * The core of the genetic algorithm
     * @param populationSize initial population
     * @param mutationRate
     * @param iterations
     * @return the solutions found after the specified number of iterations
     */
    ProposedSolution[] iterate(int populationSize, double mutationRate, int iterations, int numberOfTournaments, int maximumAge, double threshold) {

        // Step One: Generate the initial population of individuals randomly. (First generation)
        ProposedSolution[] proposedSolutions = population.generateInitialPopulation(populationSize);

        // Step Two: Evaluate the fitness of each individual in that population (time limit, sufficient fitness achieved, etc.)
        for (ProposedSolution proposedSolution : proposedSolutions) {
            proposedSolution.evaluateFitness();
        }

        //Iterations for display in GUI later
        iterationsUsed = iterations;

        //Variables used to check if stuck in local minimum
        int bestIteration = -1;
        double bestFitness = Double.MAX_VALUE;

        // Step Three: Repeat the following regeneration steps until termination:
        for (int i = 0; i < iterations; i ++) {

            statistic.setUpdate("Crossover and Mutation iterations: " + (i+1) + "/" + iterations);

            // Breed new individuals through crossover and mutation operations to give birth to offspring.
            ProposedSolution[] offspring = population.crossover(proposedSolutions, numberOfTournaments, mutationRate, populationSize, i);
            for (ProposedSolution proposedSolution : offspring) {
                proposedSolution.evaluateFitness();
            }

            // Replace least-fit population with new individuals.
            proposedSolutions = population.select(proposedSolutions, offspring, maximumAge, populationSize, i);
//            proposedSolutions = population.selectParentOffspring(offspring);
//            proposedSolutions = offspring;
            //proposedSolutions = population.select(proposedSolutions, offspring, maximumAge, populationSize);

            // Check if stuck in local minimum
            if (proposedSolutions[0].getFitness() < bestFitness) {
                bestIteration = i;
                bestFitness = proposedSolutions[0].getFitness();
            }

            // Stuck for over 20 iterations
            if (i - bestIteration > 20 ) {
                bestIteration = i;
                bestFitness = Double.MAX_VALUE;
                final int size = (int) (populationSize * 0.9);
                final ProposedSolution[] newPopulation = population.generateInitialPopulation(size);
//                proposedSolutions = population.generateInitialPopulation(populationSize);

                System.arraycopy(newPopulation, 0, proposedSolutions, populationSize - size, size);
            }

            statGraph.addIteration(processFile.optimalFitness / proposedSolutions[0].getFitness());

            //If the fitness of the best individual is within 5% of optimal fitness, return
            if(processFile.optimalFitness/proposedSolutions[0].getFitness() >= 0.95){
                iterationsUsed = i+1;   //update the iterations we used
                return proposedSolutions;
            }
            // Evaluate the individual fitness of new individuals.

            //We probably don't need to do this here, as we need to do it after crossover anyway.
//            for (ProposedSolution proposedSolution : offspring) {
            /*for (ProposedSolution proposedSolution : proposedSolutions) {
                proposedSolution.evaluateFitness();
            }*/

        }
        return proposedSolutions;
    }
}
