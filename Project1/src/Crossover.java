import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class Crossover {

    final ProcessFile processFile;
    public Crossover(ProcessFile processFile){
        this.processFile = processFile;

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

        Depot[] depotsParent1 = new Depot[processFile.depot_count]; //all depots in the first parent
        Depot[] depotsParent2 = new Depot[processFile.depot_count]; //all depots in second parent


        for(int i=0;i<parent1.depots.length;i++){
            depotsParent1[i] = new Depot(parent1.depots[i]);
            depotsParent2[i] = new Depot(parent2.depots[i]);
        }

        Car route1 = depotsParent1[rand.nextInt(processFile.depot_count)].getCars()[rand.nextInt(processFile.vehicle_count)];   //random route frm first parent
        Car route2 = depotsParent2[rand.nextInt(processFile.depot_count)].getCars()[rand.nextInt(processFile.vehicle_count)];   //random route from second parent

        //sequence of customers from random route in parent 1
        ArrayList<Customer> customerSequence1 = route1.getCustomerSequence();
        //System.out.println(customerSequence1.size());

        for(int j=0;j<customerSequence1.size();j++){
            Customer customer = customerSequence1.get(j);

            //sequence of customers from random route in parent 2
            ArrayList<Customer> customerSequence2 = route2.getCustomerSequence();

            for(int k=0; k<customerSequence2.size();k++){
                customerSequence2.add(k,customer);
                //compute stuff;
                customerSequence2.remove(k);
                }
        }

        //Car[] parent1Cars = Car.createCopy(parent1.cars,);
        //Car[] parent2Cars = Car.createCopy(parent2.getCars());


        //Car route1 = Car.createCopy(parent1Cars[rand.nextInt(parent1Cars.length)]);
        //Car route2 = parent2Cars[rand.nextInt(parent2Cars.length)];


        return null;
    }
}
