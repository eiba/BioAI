import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class PopulationGenerator {

    public ProposedSolution[] speciemens;

    public PopulationGenerator(ProcessFile f,int specimen_count){
        speciemens = new ProposedSolution[specimen_count];

        //Generate this shit

        for(int k=0;k<specimen_count;k++){

        ArrayList<Customer> not_processed_customers = new ArrayList<>(Arrays.asList(f.customers));

        int random_customer_index = new Random().nextInt(f.customer_count);
        Customer customer = not_processed_customers.get(random_customer_index);

        while(not_processed_customers.size() > 0){

            Depot depot = f.depots[new Random().nextInt(f.depot_count)];

            Car vehicle = depot.getCars()[new Random().nextInt(f.vehicle_count)];

            double duration = duration_calculation(vehicle.getX(),vehicle.getY(),customer.getX(),customer.getY());
            double duration_to_get_home = duration_calculation(customer.getX(),customer.getY(),depot.getX(),depot.getY());

            if(vehicle.getCurrent_load() + customer.getCustomer_demand() <= vehicle.getMaximum_load() && (vehicle.getMaximum_duration() == 0 || (vehicle.getCurrent_duration() + duration + duration_to_get_home <= vehicle.getMaximum_duration() ))){
                vehicle.setCurrent_load(vehicle.getCurrent_load() + customer.getCustomer_demand());
                vehicle.setCurrent_duration(vehicle.getCurrent_duration() + duration);
                vehicle.setX(customer.getX());
                vehicle.setY(customer.getY());
                vehicle.getCustomer_sequence().add(customer);

                customer.setAssigned(true);

                not_processed_customers.remove(random_customer_index);
                if(not_processed_customers.size() > 0) {
                    random_customer_index = new Random().nextInt(not_processed_customers.size());
                    customer = not_processed_customers.get(random_customer_index);
                }
            }

        }
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

        double cost = calculate_cost(f);


        ArrayList<CarLine> lines = new ArrayList<>();
            for(int i = 0; i < f.depot_count;i++){
                Depot depot = f.depots[i];
                for (int j = 0; j < f.vehicle_count; j++){
                    Car vehicle = depot.getCars()[j];
                    if(vehicle.getCurrent_load() > 0){
                        CarLine line = new CarLine(i+1,j+1,vehicle.getCurrent_duration(),vehicle.getCurrent_load(),(ArrayList)vehicle.getCustomer_sequence().clone());
                        lines.add(line);
                        vehicle.setCurrent_duration(0);
                        vehicle.setCurrent_load(0);
                        vehicle.setCustomer_sequence(new ArrayList<>());
                    }
                }
            }

            ProposedSolution solution = new ProposedSolution(cost,lines);

            speciemens[k] = solution;
        }
    }

    public double calculate_cost(ProcessFile specimen){
        double total_cost = 0.0;
        for(int i = 0; i < specimen.depot_count;i++){
            Depot depot = specimen.depots[i];
            for (int j = 0; j < specimen.vehicle_count; j++){
                Car vehicle = depot.getCars()[j];
                if(vehicle.getCurrent_load() > 0){
                    total_cost += vehicle.getCurrent_duration();
                }
            }
        }
        return total_cost;
    }

    public double duration_calculation(int car_x,int car_y,int customer_x,int customer_y){

        double x_travelled = Math.abs(car_x - customer_x);
        double y_travelled = Math.abs(car_y - customer_y);

        return Math.sqrt(Math.pow(x_travelled,2) + Math.pow(y_travelled,2));
    }
}


