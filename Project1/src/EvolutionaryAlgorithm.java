public class EvolutionaryAlgorithm {

    private final Population population;
    private final Statistic statistic;
    private final StatGraph statGraph;
    final ProcessFile processFile;
    public int iterationsUsed;
    private Double mutationRate;

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
        this.mutationRate = mutationRate;
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
        int progressIterations = 0;
        // Step Three: Repeat the following regeneration steps until termination:
        for (int i = 0; i < iterations; i ++) {

            statistic.setUpdate("Crossover and mutation iterations: " + (i+1) + "/" + iterations);

            // Breed new individuals through crossover and mutation operations to give birth to offspring.
            ProposedSolution[] offspring = population.crossover(proposedSolutions, numberOfTournaments, this.mutationRate, (int) (populationSize * 1.5), i);
            for (ProposedSolution proposedSolution : offspring) {
                proposedSolution.evaluateFitness();
            }

            // Replace least-fit population with new individuals.
            proposedSolutions = population.select(proposedSolutions, offspring, maximumAge, populationSize, i);
//

            // Check if stuck in local minimum
            if (proposedSolutions[0].getFitness() < bestFitness) {
                bestIteration = i;
                bestFitness = proposedSolutions[0].getFitness();
                progressIterations +=1;
            }

            // Stuck for over 5% iterations
            if (((double) i - bestIteration) / iterations > 0.03 ) {
                //progressIterations = 0;
                if(this.mutationRate < 0.1){
                    this.mutationRate += 0.01;
                    System.out.println("Mutation rate: "+this.mutationRate);
                }
                bestIteration = i;
                bestFitness = Double.MAX_VALUE;
                final int size = (int) (populationSize * 0.9);
                final ProposedSolution[] newPopulation = population.generateInitialPopulation(size);
//                proposedSolutions = population.generateInitialPopulation(populationSize);

                System.arraycopy(newPopulation, 0, proposedSolutions, populationSize - size, size);
            }
            else if((double)progressIterations/iterations > 0.01){
                progressIterations = 0;
                if(this.mutationRate > mutationRate){
                    this.mutationRate -= 0.01;
                    System.out.println("Mutation rate: "+this.mutationRate);
                }
            }

            statGraph.addIteration(processFile.optimalFitness / proposedSolutions[0].getFitness());

            //If the fitness of the best individual is within 5% of optimal fitness, return
            if(processFile.optimalFitness/proposedSolutions[0].getFitness() >= 0.95){
                iterationsUsed = i+1;   //update the iterations we used
                return proposedSolutions;
            }
        }
        return proposedSolutions;
    }
}
