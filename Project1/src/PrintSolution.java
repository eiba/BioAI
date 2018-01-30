import java.text.DecimalFormat;
import java.util.ArrayList;

public class PrintSolution {

    //prints the a possible solution
    public void Print(ProposedSolution n){
        DecimalFormat numberFormat = new DecimalFormat("#.00");

        Car[] cars = n.getCars();
        System.out.println(n.getDurationScore());
        System.out.println(n.getCustomerScore());

        for (Car car : cars) {
            String s = car.getDepot().getDepot_nr() + " " + car.getVehichle_number() + " " + numberFormat.format(car.getCurrent_duration()) + " " + car.getCurrent_load();
            String customer_sequence = " ";
            for(int m = 0; m < car.getCustomer_sequence().size(); m++){


                customer_sequence += car.getCustomer_sequence().get(m).getCustomer_nr() +" ";
            }
            System.out.println(s+customer_sequence);
        }
    }
}
