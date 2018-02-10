import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ProcessFile {

    private int line_number = 0;
    private int depots_finished = 0;

    public int vehicle_count;
    public int customerCount;
    public int depotCount;
    public Depot depots[];
    public Customer customers[];
    public Car cars[];
    public int minX = Integer.MAX_VALUE;
    public int minY = Integer.MAX_VALUE;
    public int maxX = Integer.MIN_VALUE;
    public int maxY = Integer.MIN_VALUE;
    public double optimalFitness;

    //This class processes a dataset, extracts the data and creates objects and variables

    ProcessFile(String Filename){

        //read dataset
        try (Stream<String> stream = Files.lines(Paths.get(Filename))) {

            stream.forEach(this::processLine);

            //iterate over all the lines in the dataset
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        //read solution file and get the optimal fitness
        try{
            BufferedReader optimalFitnessLine = new BufferedReader(new FileReader(Filename+".res"));
            try{
                this.optimalFitness = Double.parseDouble(optimalFitnessLine.readLine());
            }catch (IOException e){
                e.printStackTrace();
            }

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

    }

    //process a line in the data set
    private void processLine(String line){
        if(this.line_number == 0){
            //First line
            String[] first_line = line.split(" ");

            this.vehicle_count = Integer.parseInt(first_line[0]);
            this.customerCount = Integer.parseInt(first_line[1]);
            this.depotCount = Integer.parseInt(first_line[2]);
            this.depots = new Depot[this.depotCount];
            this.customers = new Customer[this.customerCount];
            this.cars = new Car[this.vehicle_count*this.depotCount];

        }
        else if(line_number > 0 && line_number < depotCount + 1){
            String[] depot_line = line.split(" ");
            Depot depot = new Depot(Integer.parseInt(depot_line[0]),Integer.parseInt(depot_line[1]));
            this.depots[line_number -1] = depot;
        }
        else if(line_number > depotCount && line_number < depotCount + customerCount + 1){
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
            int customerX = customer_line[1];
            int customerY = customer_line[2];
            Customer customer = new Customer(customer_line[0],customerX,customerY,customer_line[3],customer_line[4]);

            if(customerX > this.maxX){
                this.maxX = customerX;
            }
            if(customerX < this.minX){
                this.minX = customerX;
            }

            if(customerY > this.maxY){
                this.maxY = customerY;
            }
            if(customerY < this.minY){
                this.minY = customerY;
            }

            this.customers[line_number - depotCount - 1] = customer;
        }
        else if(line_number > depotCount + customerCount && line_number < (depotCount *2) + customerCount + 1){
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
            Depot depot =  depots[this.depots_finished];
            int depotX = depot_line[1];
            int depotY = depot_line[2];

            depot.setX(depotX);
            depot.setY(depotY);
            depot.setDepot_nr(depots_finished +1);

            //check to see whether these are min or max coordinates
            if(depotX >this. maxX){
                this.maxX = depotX;
            }
            if(depotX < this.minX){
                this.minX = depotX;
            }

            if(depotY > this.maxY){
                this.maxY = depotY;
            }
            if(depotY < this.minY){
                this.minY = depotY;
            }


            Car[] cars = new Car[this.vehicle_count];

            for(int i =0; i < this.vehicle_count;i++){
                Car car = new Car(i +1,depot.getMaximum_load(),depot.getMaximumDuration(),depot);
                cars[i] = car;
                this.cars[i + ((depot.getDepotNr()-1)*this.vehicle_count)] = car;
            }
            depot.setCars(cars);

            depots_finished ++;
        }
        this.line_number++;

    }

    Depot[] getDepots() {
        Depot[] copy = new Depot[depots.length];

        for (int i = 0; i < depots.length; i ++) {
            Depot depot = depots[i];
            copy[i] = new Depot(depot,true);
        }

        return copy;
    }

}
