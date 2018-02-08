import java.util.ArrayList;

public class Car {

    private Depot depot;
    private int currentLoad,  maximumLoad, vehicleNumber, maximumDuration, x, y;
    private double currentDuration;

    private final ArrayList<Customer> customerSequence;

    Car(int vehicleNumber, int maximumLoad, int maximumDuration, Depot depot) {
        this.vehicleNumber = vehicleNumber;
        this.maximumLoad = maximumLoad;
        this.depot = depot;
        this.currentDuration = 0.0;
        this.x = depot.getX();
        this.y = depot.getY();
        this.customerSequence = new ArrayList<>();

        if(maximumDuration == 0){
            this.maximumDuration = Integer.MAX_VALUE;
        }else{
            this.maximumDuration = maximumDuration;
        }
    }

    static Car[] createCopy(Car[] cars, Depot depot) {
        Car[] copy = new Car[cars.length];

        for (int i = 0; i < cars.length; i ++) {
            Car car = cars[i];
            copy[i] = new Car(car.vehicleNumber, car.maximumLoad, car.maximumDuration, depot);
        }

        return copy;
    }

    void addDuration(double duration) {
        currentDuration += duration;
    }

    void addLoad(int load) {
        currentLoad += load;
    }

    void addCustomerVisited(Customer customer) {
        customerSequence.add(customer);
    }

    /**
     * This method is used to determine if the car is eligible to add a customer to its route or not
     * @param customer
     * @return True iff the car is eligible to serve the customer, False otherwise
     */
    boolean isEligible(Customer customer) {
        final boolean durationCheck = currentDuration + Population.euclideanDistance(x, y, customer.getX(), customer.getY()) <= maximumDuration;
        final boolean loadCheck = currentLoad + customer.getDemand() <= maximumLoad;
        return durationCheck && loadCheck;
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

    public ArrayList<Customer> getCustomerSequence() {
        return customerSequence;
    }

//    public void setCustomerSequence(ArrayList<Customer> customerSequence) {
//        this.customerSequence = customerSequence;
//    }

    public int getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(int vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public double getCurrentDuration() {
        return currentDuration;
    }

    public void setCurrentDuration(double currentDuration) {
        this.currentDuration = currentDuration;
    }

    public int getCurrentLoad() {
        return currentLoad;
    }

    public void setCurrentLoad(int currentLoad) {
        this.currentLoad = currentLoad;
    }

    public int getMaximumLoad() {
        return maximumLoad;
    }

    public void setMaximumLoad(int maximumLoad) {
        this.maximumLoad = maximumLoad;
    }

    public int getMaximumDuration() {
        return maximumDuration;
    }

    public void setMaximumDuration(int maximumDuration) {
        this.maximumDuration = maximumDuration;
    }

    public Depot getDepot() {
        return depot;
    }

    public void setDepot(Depot depot) {
        this.depot = depot;
    }
}
