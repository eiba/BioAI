import java.util.ArrayList;

public class Car {

    private int current_load;
    private int maximum_load;
    private int maximum_duration;
    private Depot depot;
    private int vehichle_number;
    private double current_duration;
    private int x;
    private int y;

    private ArrayList<Customer> customer_sequence;

    public Car(int vehichle_number, int maximum_load, int maximum_duration, Depot depot) {
        this.vehichle_number = vehichle_number;
        this.maximum_load = maximum_load;
        this.maximum_duration = maximum_duration;
        this.depot = depot;
        this.current_duration = 0.0;
        this.x = depot.getX();
        this.y = depot.getY();
        this.customer_sequence = new ArrayList<>();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public ArrayList<Customer> getCustomer_sequence() {
        return customer_sequence;
    }

    public void setCustomer_sequence(ArrayList<Customer> customer_sequence) {
        this.customer_sequence = customer_sequence;
    }

    public int getVehichle_number() {
        return vehichle_number;
    }

    public void setVehichle_number(int vehichle_number) {
        this.vehichle_number = vehichle_number;
    }

    public double getCurrent_duration() {
        return current_duration;
    }

    public void setCurrent_duration(double current_duration) {
        this.current_duration = current_duration;
    }

    public int getCurrent_load() {
        return current_load;
    }

    public void setCurrent_load(int current_load) {
        this.current_load = current_load;
    }

    public int getMaximum_load() {
        return maximum_load;
    }

    public void setMaximum_load(int maximum_load) {
        this.maximum_load = maximum_load;
    }

    public int getMaximum_duration() {
        return maximum_duration;
    }

    public void setMaximum_duration(int maximum_duration) {
        this.maximum_duration = maximum_duration;
    }

    public Depot getDepot() {
        return depot;
    }

    public void setDepot(Depot depot) {
        this.depot = depot;
    }
}
