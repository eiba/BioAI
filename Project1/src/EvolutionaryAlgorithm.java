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

    ProposedSolution[] iterate(int populationSize, int iterations) {

        // Step One: Generate the initial population of individuals randomly. (First generation)
        ProposedSolution[] proposedSolutions = population.generateInitialPopulation(populationSize);

        // Step Two: Evaluate the fitness of each individual in that population (time limit, sufficient fitness achieved, etc.)
        // @TODO void population.evaluate(ProposedSolution[] ps)

        // Step Three: Repeat the following regeneration steps until termination:
        for (int i = 0; i < iterations; i ++) {
            // Select the best-fit individuals for reproduction. (Parents)
            ProposedSolution[] selectedParents = population.selectParent(proposedSolutions);

            // Breed new individuals through crossover and mutation operations to give birth to offspring.
            // @TODO Change Crossover from Class to method inside Population class
            ProposedSolution[] offspring = new Crossover(processFile).Crossover(selectedParents);

            // Evaluate the individual fitness of new individuals.
            // @TODO void population.evaluate(ProposedSolution[] ps)

            // Replace least-fit population with new individuals.
            // @TODO ProposedSolution[] population.select(ProposedSolution[] parents, ProposedSolution[] offspring)
        }

        return proposedSolutions;
    }
}
