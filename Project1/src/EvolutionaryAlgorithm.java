public class EvolutionaryAlgorithm {

    private final int initial_population_count;
    public final ProcessFile processFile;
    public ProposedSolution[] proposedSolutions;

    public EvolutionaryAlgorithm(String filename, int initial_population_count){
        processFile = new ProcessFile(filename);
        // Hei hei Stigen. hvor flink dere er! hilsen Sigve :D
        this.initial_population_count = initial_population_count;

        //Generate an initial population
        proposedSolutions = new Population(processFile,initial_population_count).generateInitialPopulation();

        //select parents
        ProposedSolution[] selected_parents = new ParentSelection().SelectParent(proposedSolutions);

        ProposedSolution[] offspring = new Crossover().Crossover(selected_parents);


        double totalPop = 0.0;
        double totalparent = 0.0;

        for (int i=0; i<proposedSolutions.length;i++){
            //System.out.println(proposedSolutions[i].getFitnessScore());
            totalPop += proposedSolutions[i].getFitnessScore();
            totalparent += selected_parents[i].getFitnessScore();
        }
        System.out.println(totalPop);
        System.out.println(totalparent);

    }
}
