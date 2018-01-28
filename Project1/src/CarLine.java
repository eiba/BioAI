import java.util.ArrayList;

public class CarLine{

        private int depot_nr;
        private int car_nr;
        private double duration;
        private int load;
        private ArrayList<Customer> sequence;

    public CarLine(int depot_nr, int car_nr, double duration, int load, ArrayList<Customer> sequence) {
        this.depot_nr = depot_nr;
        this.car_nr = car_nr;
        this.duration = duration;
        this.load = load;
        this.sequence = sequence;
    }

    public int getDepot_nr() {
        return depot_nr;
    }

    public void setDepot_nr(int depot_nr) {
        this.depot_nr = depot_nr;
    }

    public int getCar_nr() {
        return car_nr;
    }

    public void setCar_nr(int car_nr) {
        this.car_nr = car_nr;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public int getLoad() {
        return load;
    }

    public void setLoad(int load) {
        this.load = load;
    }

    public ArrayList<Customer> getSequence() {
        return sequence;
    }

    public void setSequence(ArrayList<Customer> sequence) {
        this.sequence = sequence;
    }
}