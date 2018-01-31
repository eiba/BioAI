public class EvolutionaryAlgorithm {

    private final int initial_population_count;
    public final ProcessFile processFile;
    public ProposedSolution[] proposedSolutions;

    public EvolutionaryAlgorithm(String filename, int initial_population_count){
        processFile = new ProcessFile(filename);
        this.initial_population_count = initial_population_count;

        //Generate an initial population
        proposedSolutions = new Population(processFile,initial_population_count).generateInitialPopulation();

        //select parents
        ProposedSolution[] selected_parents = new ParentSelection().SelectParent(proposedSolutions);

    }
}
