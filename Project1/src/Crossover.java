import java.util.ArrayList;
import java.util.Random;

public class Crossover {

    public Crossover(){

    }

    public ProposedSolution[] Crossover(ProposedSolution[] solutions){

        ProposedSolution[] children = new ProposedSolution[solutions.length*5];
        Random rand = new Random();

        for(int i=0;i<children.length;i++){
            ProposedSolution parent1 = solutions[rand.nextInt(solutions.length)];
            ProposedSolution parent2 = solutions[rand.nextInt(solutions.length)];

            children[i] = copulate(parent1,parent2);
        }
        return null;
    }

    public ProposedSolution copulate(ProposedSolution parent1, ProposedSolution parent2){

        Random rand = new Random();
        //Car[] parent1Cars = parent1.getCars();
        //Car[] parent2Cars = Car.createCopy(parent2.getCars());


        //Car route1 = Car.createCopy(parent1Cars[rand.nextInt(parent1Cars.length)]);
        //Car route2 = parent2Cars[rand.nextInt(parent2Cars.length)];


        return null;
    }
}
