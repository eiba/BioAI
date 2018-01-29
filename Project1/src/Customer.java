public class Customer {

    private int customer_nr;
    private int x;
    private int y;
    private int serivice_duration;
    private int customer_demand;

    public Customer(int customer_nr, int x, int y, int serivice_duration, int customer_demand){
        this.customer_nr = customer_nr;
        this.x = x;
        this.y = y;
        this.serivice_duration = serivice_duration;
        this.customer_demand = customer_demand;

    }

    public int getCustomer_nr() {
        return customer_nr;
    }

    public void setCustomer_nr(int customer_nr) {
        this.customer_nr = customer_nr;
    }
    public int getCustomer_demand() {
        return customer_demand;
    }

    public void setCustomer_demand(int customer_demand) {
        this.customer_demand = customer_demand;
    }

    public int getSerivice_duration() {
        return serivice_duration;
    }

    public void setSerivice_duration(int serivice_duration) {
        this.serivice_duration = serivice_duration;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

}
