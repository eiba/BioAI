import java.text.DecimalFormat;
import java.util.ArrayList;

public class PrintSolution {

    //prints the a possible solution
    public void Print(ProposedSolution n){
        DecimalFormat numberFormat = new DecimalFormat("#.00");

        SolutionLine[] f = n.getSolution();
        System.out.println(n.getScore());

        for(int i = 0; i < f.length;i++){
            SolutionLine line = f[i];
            String s = line.getDepot_nr() + " " + line.getCar_nr() + " " + numberFormat.format(line.getDuration()) + " " + line.getLoad();
            String customer_sequence = " ";
            for(int m = 0; m < line.getSequence().length; m++){

                customer_sequence += line.getSequence()[m] +" ";
            }
            System.out.println(s+customer_sequence);

        }
    }
}
