import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ProcessFile {

    private int line_number = 0;
    private int depots_finished = 0;

    public int vehicle_count;
    public int customer_count;
    public int depot_count;
    public Depot depots[];
    public Customer customers[];


    public ProcessFile(String Filename){
        //readTextFileUsingFileReader("./TestData/p01");

        try (Stream<String> stream = Files.lines(Paths.get(Filename))) {

            stream.forEach(k -> ProcessLine(k));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void ProcessLine(String line){
        if(this.line_number == 0){
            //First line
            String[] first_line = line.split(" ");

            this.vehicle_count = Integer.parseInt(first_line[0]);
            this.customer_count = Integer.parseInt(first_line[1]);
            this.depot_count = Integer.parseInt(first_line[2]);
            this.depots = new Depot[this.depot_count];
            this.customers = new Customer[this.customer_count];

        }else if(line_number > 0 && line_number < depot_count + 1){
            String[] depot_line = line.split(" ");

            Depot depot = new Depot(Integer.parseInt(depot_line[0]),Integer.parseInt(depot_line[1]));
            this.depots[line_number -1] = depot;
        }
        else if(line_number > depot_count && line_number < depot_count + customer_count + 1){
            String[] customer_line_unprocessed = line.split(" ");
            int[] customer_line = new int[5];
            int count = 0;
            for(int i=0; i < customer_line_unprocessed.length; i++){
                    if(!customer_line_unprocessed[i].equals("")){
                        customer_line[count] = Integer.parseInt(customer_line_unprocessed[i]);

                        count++;
                        if(count == 5){
                            break;
                        }
                    }

            }
            Customer customer = new Customer(customer_line[0],customer_line[1],customer_line[2],customer_line[3],customer_line[4]);

            this.customers[line_number - depot_count - 1] = customer;
        }
        else if(line_number > depot_count + customer_count && line_number < (depot_count*2) + customer_count + 1){
            String[] depot_line_unprocessed = line.split(" ");
            int[] depot_line = new int[3];

            int count = 0;
            for(int i=0; i < depot_line_unprocessed.length; i++){
                if(!depot_line_unprocessed[i].equals("")){
                    depot_line[count] = Integer.parseInt(depot_line_unprocessed[i]);

                    count++;
                    if(count == 3){
                        break;
                    }
                }
            }
            depots[this.depots_finished].setX(depot_line[1]);
            depots[this.depots_finished].setY(depot_line[2]);
            depots_finished ++;
        }
        this.line_number++;

    }

}
