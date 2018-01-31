import java.text.DecimalFormat;

public class PrintSolution {

    //prints the a possible solution
    public void Print(ProposedSolution n){
        DecimalFormat numberFormat = new DecimalFormat("#.00");

        Car[] cars = n.getCars();
        System.out.println(n.getDurationScore());
        System.out.println(n.getCustomerScore());

        for (Car car : cars) {
            String s = car.getDepot().getDepot_nr() + " " + car.getVehicleNumber() + " " + numberFormat.format(car.getCurrentDuration()) + " " + car.getCurrentLoad();
            String customer_sequence = " ";
            for(int m = 0; m < car.getCustomerSequence().size(); m++){


                customer_sequence += car.getCustomerSequence().get(m).getCustomer_nr() +" ";
            }
            System.out.println(s+customer_sequence);
        }
    }
}
