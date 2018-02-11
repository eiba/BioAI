public class EvolutionaryAlgorithm {

    private final Population population;
    final ProcessFile processFile;

    EvolutionaryAlgorithm(String filename) {
        // Reading the Multiple Depot Vehicle Routing Problem - MDVRP
        processFile = new ProcessFile(filename);

        // Initiating variables
        population = new Population(processFile);

    }

    /**
     * The core of the genetic algorithm
     * @param populationSize initial population
     * @param mutationRate
     * @param iterations
     * @return the solutions found after the specified number of iterations
     */
    ProposedSolution[] iterate(int populationSize, double mutationRate, int iterations, int numberOfTournaments) {

        // Step One: Generate the initial population of individuals randomly. (First generation)
        ProposedSolution[] proposedSolutions = population.generateInitialPopulation(populationSize);

        // Step Two: Evaluate the fitness of each individual in that population (time limit, sufficient fitness achieved, etc.)
        for (ProposedSolution proposedSolution : proposedSolutions) {
            proposedSolution.evaluateFitness();
        }

        // Step Three: Repeat the following regeneration steps until termination:
        for (int i = 0; i < iterations; i ++) {

            // Select the best-fit individuals for reproduction. (Parents)
            ProposedSolution[][] selectedParents = new ProposedSolution[populationSize][2];
            for (int j = 0; j < populationSize; j ++) {
                selectedParents[j][0] = population.tournamentSelection(proposedSolutions, numberOfTournaments);
                selectedParents[j][1] = population.tournamentSelection(proposedSolutions, numberOfTournaments);
            }

            // Breed new individuals through crossover and mutation operations to give birth to offspring.
            // @TODO make crossover method work!
            ProposedSolution[] offspring = population.crossoverMartin(selectedParents,mutationRate);
            for (ProposedSolution proposedSolution : offspring) {
                proposedSolution.evaluateFitness();
            }


            // Replace least-fit population with new individuals.
            proposedSolutions = population.select(proposedSolutions, offspring);

            //If the fitness of the best individual is within 5% of optimal fitness, return
            if(processFile.optimalFitness/proposedSolutions[0].getFitness() >= 0.95){
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
