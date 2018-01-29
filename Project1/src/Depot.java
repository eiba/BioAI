public class Depot {

    private int maximum_duration;
    private int maximum_load;
    private int x;
    private int y;
    private Car cars[];
    private int depot_nr;

    public int getMaximum_duration() {
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

    public int getDepot_nr() {
        return depot_nr;
    }

    public void setDepot_nr(int depot_nr) {
        this.depot_nr = depot_nr;
    }

    public Depot(int maximum_duration, int maximum_load) {
        this.maximum_duration = maximum_duration;
        this.maximum_load = maximum_load;
    }
}
