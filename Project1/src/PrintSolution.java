import java.text.DecimalFormat;
import java.util.ArrayList;

public class PrintSolution {

    public PrintSolution(ProposedSolution n){

        DecimalFormat numberFormat = new DecimalFormat("#.00");

        ArrayList<CarLine> f = n.getSolution();
        System.out.println(n.getScore());

        for(int i = 0; i < f.size();i++){
            CarLine line = f.get(i);
            String s = line.getDepot_nr() + " " + line.getCar_nr() + " " + numberFormat.format(line.getDuration()) + " " + line.getLoad();
            String customer_sequence = " ";
            for(int m = 0; m < line.getSequence().size(); m++){
                Customer customer = line.getSequence().get(m);

                customer_sequence += customer.getCustomer_nr() +" ";
            }
            System.out.println(s+customer_sequence);

            /*
            Depot depot = f.depots[i];
            for (int j = 0; j < f.vehicle_count; j++){
                Car vehicle = depot.getCars()[j];
                if(vehicle.getCurrent_load() > 0){
                    String s = (i + 1) + " " + vehicle.getVehichle_number() + " " + numberFormat.format(vehicle.getCurrent_duration()) + " " + vehicle.getCurrent_load();
                    String customer_sequence = "";
                    for(int m = 0; m < vehicle.getCustomer_sequence().size(); m++){
                        customer_sequence += vehicle.getCustomer_sequence().get(m).getCustomer_nr() +" ";
                    }
                    s += "    " + customer_sequence;
                    System.out.println(s);
                }
            }*/
        }
    }
}
