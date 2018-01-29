import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class PopulationGenerator {

    public ProposedSolution[] speciemens;   //list to contain all the specimen

    public PopulationGenerator(ProcessFile f,int specimen_count){
        speciemens = new ProposedSolution[specimen_count];  //list of specimens to create

        //iterate over as many times as we need to create the specimens. For each iteration create a random specimen.
        for(int k=0;k<specimen_count;k++){

        ArrayList<Customer> not_processed_customers = new ArrayList<>(Arrays.asList(f.customers));  //list not processed customers. Will decrease as we process more customers

        int random_customer_index = new Random().nextInt(f.customer_count); //pick a random customer from the list
        Customer customer = not_processed_customers.get(random_customer_index);

        //while there are still customers to precess
        while(not_processed_customers.size() > 0){
            Depot depot = f.depots[new Random().nextInt(f.depot_count)];    //pick random depot

            Car vehicle = depot.getCars()[new Random().nextInt(f.vehicle_count)];   //pick random vehicle

            double duration = duration_calculation(vehicle.getX(),vehicle.getY(),customer.getX(),customer.getY()); //duration for vehicle to get to customer
            double duration_to_get_home = duration_calculation(customer.getX(),customer.getY(),depot.getX(),depot.getY());  //duration for car to get home from customer

            //here we make sure that the current load on the veichle + the load that the customer will add does not exceed the maximum load requirement
            //we also make sure that we have enough duration left to get to customer and the get home after the customer. (unless max duration is 0 meaning that there is no hard requirement)
            if(vehicle.getCurrent_load() + customer.getCustomer_demand() <= vehicle.getMaximum_load() && (vehicle.getMaximum_duration() == 0 || (vehicle.getCurrent_duration() + duration + duration_to_get_home <= vehicle.getMaximum_duration() ))){
                //update values for vehicle
                vehicle.setCurrent_load(vehicle.getCurrent_load() + customer.getCustomer_demand());
                vehicle.setCurrent_duration(vehicle.getCurrent_duration() + duration);
                vehicle.setX(customer.getX());
                vehicle.setY(customer.getY());
                vehicle.getCustomer_sequence().add(customer);   //add customer to the sequence

                not_processed_customers.remove(random_customer_index);  //remove customer from the not processed queue

                if(not_processed_customers.size() > 0) {    //as long as there are more customers, pick a new one
                    random_customer_index = new Random().nextInt(not_processed_customers.size());
                    customer = not_processed_customers.get(random_customer_index);
                }
            }

        }
        //After completing all the customers all the cars needs to drive home. add the home drive to the duration.
        for(int i = 0; i < f.depot_count;i++){
            Depot depot = f.depots[i];
            for (int j = 0; j < f.vehicle_count; j++){
                Car vehicle = depot.getCars()[j];
                if(vehicle.getCurrent_load() > 0){
                    double duration_to_get_home = duration_calculation(vehicle.getX(),vehicle.getY(),depot.getX(),depot.getY());
                    vehicle.setCurrent_duration(vehicle.getCurrent_duration() + duration_to_get_home);
                    vehicle.setY(depot.getY());
                    vehicle.setX(depot.getX());
                }
            }
        }

        //ArrayList to hold lines in current solution
        ArrayList<SolutionLine> lines = new ArrayList<>();

        //iterate over the solution currently founds
            for(int i = 0; i < f.depot_count;i++){
                //iterate over depots
                Depot depot = f.depots[i];
                for (int j = 0; j < f.vehicle_count; j++){
                    //iterate ove vehicles in depot
                    Car vehicle = depot.getCars()[j];
                    int[] customer_sequence = new int[vehicle.getCustomer_sequence().size()]; //sequence of customers the car visits

                    for(int v=0;v < vehicle.getCustomer_sequence().size();v++){ //get the customer number sequence into an int array
                        customer_sequence[v] = vehicle.getCustomer_sequence().get(v).getCustomer_nr();
                    }

                    SolutionLine line = new SolutionLine(depot.getDepot_nr(),vehicle.getVehichle_number(),vehicle.getCurrent_duration(),vehicle.getCurrent_load(),customer_sequence);

                    lines.add(line);    //add info for that car into all the lines for the solution

                    //reset the solution
                    vehicle.setCurrent_duration(0);
                    vehicle.setCurrent_load(0);
                    vehicle.setCustomer_sequence(new ArrayList<>());
                }
            }
            ProposedSolution solution = new ProposedSolution(lines);

            double cost = calculate_cost(solution);
            solution.setScore(cost);

            speciemens[k] = solution;
        }
    }

    //calculates the cost of the solution
    public double calculate_cost(ProposedSolution specimen){
        double total_cost = 0.0;
        for(int i=0;i<specimen.getSolution().size();i++){
            total_cost += specimen.getSolution().get(i).getDuration();
        }
        return total_cost;
    }

    //calcualtes the eucleadian distance from a car to a customer
    public double duration_calculation(int car_x,int car_y,int customer_x,int customer_y){

        double x_travelled = Math.abs(car_x - customer_x);
        double y_travelled = Math.abs(car_y - customer_y);

        return Math.sqrt(Math.pow(x_travelled,2) + Math.pow(y_travelled,2));
    }
}


