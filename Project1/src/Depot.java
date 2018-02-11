public class Depot {

    private int maximum_duration;
    private int maximum_load;
    private int x;
    private int y;
    private Car cars[];
    private int depot_nr;

    Depot(int maximum_duration, int maximum_load) {
        if (maximum_duration == 0) {
            this.maximum_duration = Integer.MAX_VALUE;
        }
        else {
            this.maximum_duration = maximum_duration;
        }
        this.maximum_load = maximum_load;
    }

    Depot(Depot depot, boolean cleanCopy) {
        this.maximum_duration = depot.maximum_duration;
        this.maximum_load = depot.maximum_load;
        this.x = depot.x;
        this.y = depot.y;
        this.depot_nr = depot.depot_nr;
        if (cleanCopy) {
            this.cars = Car.createCopy(depot.cars, this);
        }
        else{
            this.cars = Car.createCopyWithCustomers(depot.cars,this);
        }
    }

    public int getMaximumDuration() {
        return maximum_duration;
    }

    public void setMaximum_duration(int maximum_duration) {
        this.maximum_duration = maximum_duration;
    }

    public int getMaximum_load() {
        return maximum_load;
    }

    public void setMaximum_load(int maximum_load) {
        this.maximum_load = maximum_load;
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

    public Car[] getCars() {
        return cars;
    }

    public void setCars(Car[] cars) {
        this.cars = cars;
    }

    public int getDepotNr() {
        return depot_nr;
    }

    public void setDepot_nr(int depot_nr) {
        this.depot_nr = depot_nr;
    }

}
