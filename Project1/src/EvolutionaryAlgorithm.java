public class EvolutionaryAlgorithm {

    private final int initialPopulationCount;
    private final ProcessFile processFile;
    private final Population population;
    public ProposedSolution[] proposedSolutions;

    EvolutionaryAlgorithm(String filename, int initialPopulationCount){
        this.initialPopulationCount = initialPopulationCount;
        processFile = new ProcessFile(filename);
        population = new Population(processFile, initialPopulationCount);

        //Generate an initial population
        proposedSolutions = population.generateInitialPopulation();

        //Select parents
        ProposedSolution[] selectedParents = population.selectParent(proposedSolutions);

        // @TODO Change Crossover from Class to method inside Population class
        ProposedSolution[] offspring = new Crossover(processFile).Crossover(selectedParents);


        double totalPopulation = 0.0;
        double totalParent = 0.0;

        for (int i=0; i<proposedSolutions.length;i++){
            //System.out.println(proposedSolutions[i].getFitnessScore());
            totalPopulation += proposedSolutions[i].fitnessScore;
            totalParent += selectedParents[i].fitnessScore;
        }
        System.out.println(totalPopulation);
        System.out.println(totalParent);

    }
}
