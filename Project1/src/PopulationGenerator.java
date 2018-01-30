import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class PopulationGenerator {

    public ProposedSolution[] speciemens;   //list to contain all the specimen

    public PopulationGenerator(ProcessFile f,int specimen_count){
        speciemens = new ProposedSolution[specimen_count];  //list of specimens to create

        //iterate over as many times as we need to create the specimens. For each iteration create a random specimen.
        int number_of_specimen = 0;

        while (number_of_specimen < specimen_count){
            boolean valid_specimen = true;

        //for(int k=0;k<specimen_count;k++) {

            ArrayList<Customer> not_processed_customers = new ArrayList<>(Arrays.asList(f.customers));  //list not processed customers. Will decrease as we process more customers

            int random_customer_index = new Random().nextInt(f.customer_count); //pick a random customer from the list
            Customer customer = not_processed_customers.get(random_customer_index);

            //while there are still customers to precess
            while (not_processed_customers.size() > 0) {


                //attempt fo find the best possible car which is allowed to drive to the customer. If there is no such car, the solution fall through, we reset the cars and attempt a new solution
                Car best_car = null;
                double shortest_duration = Double.MAX_VALUE;
                for (int i = 0; i < f.vehicles.length; i++) {
                    Car vehicle = f.vehicles[i];
                    double duration = duration_calculation(vehicle.getX(), vehicle.getY(), customer.getX(), customer.getY()); //duration for vehicle to get to customer
                    double duration_to_get_home = duration_calculation(customer.getX(), customer.getY(), vehicle.getDepot().getX(), vehicle.getDepot().getY());  //duration for car to get home from customer

                    if (vehicle.getCurrent_load() + customer.getCustomer_demand() <= vehicle.getMaximum_load() && (vehicle.getMaximum_duration() == 0 || (vehicle.getCurrent_duration() + duration + duration_to_get_home <= vehicle.getMaximum_duration()))) {
                        if (shortest_duration > duration) {
                            best_car = vehicle;
                            shortest_duration = duration;
                        }
                    }
                }
                if (shortest_duration == Double.MAX_VALUE) {
                    valid_specimen = false;
                    break;
                }
                best_car.setCurrent_load(best_car.getCurrent_load() + customer.getCustomer_demand());
                best_car.setCurrent_duration(best_car.getCurrent_duration() + shortest_duration);
                best_car.setX(customer.getX());
                best_car.setY(customer.getY());
                best_car.getCustomer_sequence().add(customer);   //add customer to the sequence

                not_processed_customers.remove(random_customer_index);  //remove customer from the not processed queue

                if(not_processed_customers.size() > 0) {    //as long as there are more customers, pick a new one
                    random_customer_index = new Random().nextInt(not_processed_customers.size());
                    customer = not_processed_customers.get(random_customer_index);
                }

            }
            if (!valid_specimen) {
                for(int j=0;j<f.vehicles.length;j++){
                    Car vehicle = f.vehicles[j];
                    vehicle.setY(vehicle.getDepot().getY());
                    vehicle.setX(vehicle.getDepot().getX());
                    vehicle.setCurrent_duration(0);
                    vehicle.setCurrent_load(0);
                    vehicle.setCustomer_sequence(new ArrayList<>());
                }

                continue;
            }
            //After completing all the customers all the cars needs to drive home. add the home drive to the duration.
                for (int j = 0; j < f.vehicles.length; j++) {
                    Car vehicle = f.vehicles[j];
                    if (vehicle.getCurrent_load() > 0) {
                        double duration_to_get_home = duration_calculation(vehicle.getX(), vehicle.getY(), vehicle.getDepot().getX(), vehicle.getDepot().getY());
                        vehicle.setCurrent_duration(vehicle.getCurrent_duration() + duration_to_get_home);
                        vehicle.setY(vehicle.getDepot().getY());
                        vehicle.setX(vehicle.getDepot().getX());
                    }
                }


            //ArrayList to hold lines in current solution
            SolutionLine[] lines = new SolutionLine[f.vehicles.length];

            //iterate over current solution and and add cars to solution
            for(int i = 0; i<f.vehicles.length;i++){
                Car vehicle = f.vehicles[i];
                int[] customer_sequence = new int[vehicle.getCustomer_sequence().size()]; //sequence of customers the car visits

                for (int v = 0; v < vehicle.getCustomer_sequence().size(); v++) { //get the customer number sequence into an int array
                    customer_sequence[v] = vehicle.getCustomer_sequence().get(v).getCustomer_nr();
                }

                SolutionLine line = new SolutionLine(vehicle.getDepot().getDepot_nr(), vehicle.getVehichle_number(), vehicle.getCurrent_duration(), vehicle.getCurrent_load(), customer_sequence);

                lines[i] = line;    //add info for that car into all the lines for the solution

                //reset the solution
                vehicle.setCurrent_duration(0);
                vehicle.setCurrent_load(0);
                vehicle.setCustomer_sequence(new ArrayList<>());
            }

            ProposedSolution solution = new ProposedSolution(lines);

            double cost = calculate_cost(solution);
            solution.setScore(cost);

            speciemens[number_of_specimen] = solution;
            number_of_specimen +=1;
            //}
        }
    }

    //calculates the cost of the solution
    public double calculate_cost(ProposedSolution specimen){
        double total_cost = 0.0;
        for(int i=0;i<specimen.getSolution().length;i++){
            total_cost += specimen.getSolution()[i].getDuration();
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


