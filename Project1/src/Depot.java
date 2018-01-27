public class Depot {

    private int maximum_duration;
    private int maximum_load;
    private int x;
    private int y;
    private Car cars[];

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

    public Depot(int maximum_duration, int maximum_load, int x, int y, Car[] cars) {
        this.maximum_duration = maximum_duration;
        this.maximum_load = maximum_load;
        this.x = x;
        this.y = y;
        this.cars = cars;
    }

    public Depot(int maximum_duration, int maximum_load) {
        this.maximum_duration = maximum_duration;
        this.maximum_load = maximum_load;
    }
}
