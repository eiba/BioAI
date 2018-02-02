import java.util.ArrayList;

public class ParentSelection {

    public ParentSelection(){


    }

    public ProposedSolution[] SelectParent(ProposedSolution[] solutions){

        //list containing all selected parents
        //There should be as many selected parents as there are specimen in the population as one parent can eb selected more than once
        ProposedSolution[] selected_parents = new ProposedSolution[solutions.length];

        double score_sum = 0.0;
        for(ProposedSolution solution: solutions){
            score_sum += 1/solution.getFitnessScore();
        }

        //for each iteration add a parent
        for(int i=0;i<solutions.length;i++){

            double p = Math.random();   //random number from 0 to 1

            double cumulativeProbability = 0.0;
            for (ProposedSolution solution : solutions) {
                cumulativeProbability +=  (1 / solution.getFitnessScore()) / score_sum;    //add to the cumulative probability

                if (p <= cumulativeProbability) {
                    selected_parents[i] = solution;
                    break;
                }
            }
        }
        return selected_parents;
    }
}
