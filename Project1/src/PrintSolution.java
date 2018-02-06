import java.text.DecimalFormat;

public class PrintSolution {

    //prints the a possible solution
    public void Print(ProposedSolution proposedSolution){
        DecimalFormat numberFormat = new DecimalFormat("#.00");

        System.out.println(proposedSolution.durationScore);
        System.out.println(proposedSolution.customerScore);

        for (Depot depot : proposedSolution.depots) {
            for (Car car : depot.getCars()) {
                String s = car.getDepot().getDepot_nr() + " " + car.getVehicleNumber() + " " + numberFormat.format(car.getCurrentDuration()) + " " + car.getCurrentLoad();
                String customer_sequence = " ";
                for(int m = 0; m < car.getCustomerSequence().size(); m++){

                    customer_sequence += car.getCustomerSequence().get(m).getCustomer_nr() +" ";
                }
                System.out.println(s+customer_sequence);
            }
        }
    }
}
