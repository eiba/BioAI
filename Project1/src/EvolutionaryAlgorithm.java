public class EvolutionaryAlgorithm {

    private final Population population;
    final ProcessFile processFile;

    EvolutionaryAlgorithm(String filename) {
        // Reading the Multiple Depot Vehicle Routing Problem - MDVRP
        processFile = new ProcessFile(filename);

        // Initiating variables
        population = new Population(processFile);

//        double totalPopulation = 0.0;
//        double totalParent = 0.0;
//
//        for (int i=0; i<proposedSolutions.length;i++){
//            //System.out.println(proposedSolutions[i].getFitnessScore());
//            totalPopulation += proposedSolutions[i].fitnessScore;
//            totalParent += selectedParents[i].fitnessScore;
//        }
//        System.out.println(totalPopulation);
//        System.out.println(totalParent);

    }

    ProposedSolution[] iterate(int populationSize, double mutationRate, int iterations) {

        // Step One: Generate the initial population of individuals randomly. (First generation)
        ProposedSolution[] proposedSolutions = population.generateInitialPopulation(populationSize);

        // Step Two: Evaluate the fitness of each individual in that population (time limit, sufficient fitness achieved, etc.)
        for (ProposedSolution proposedSolution : proposedSolutions) {
            proposedSolution.evaluateFitness();
        }

        // Step Three: Repeat the following regeneration steps until termination:
        for (int i = 0; i < iterations; i ++) {
            // Select the best-fit individuals for reproduction. (Parents)
            ProposedSolution[] selectedParents = population.selectParent(proposedSolutions);

            // Breed new individuals through crossover and mutation operations to give birth to offspring.
            // @TODO make crossover method work!
            ProposedSolution[] offspring = population.crossover(selectedParents, mutationRate);

            // Replace least-fit population with new individuals.
            proposedSolutions = population.select(selectedParents, offspring);

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
