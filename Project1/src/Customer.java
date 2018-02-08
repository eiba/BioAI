public class Customer {

    private int customerNr;
    private int x;
    private int y;
    private int seriviceDuration;
    private int demand;

    public Customer(int customerNr, int x, int y, int seriviceDuration, int demand){
        this.customerNr = customerNr;
        this.x = x;
        this.y = y;
        this.seriviceDuration = seriviceDuration;
        this.demand = demand;

    }

    public int getCustomerNr() {
        return customerNr;
    }

    public void setCustomerNr(int customerNr) {
        this.customerNr = customerNr;
    }

    public int getDemand() {
        return demand;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }

    public int getSeriviceDuration() {
        return seriviceDuration;
    }

    public void setSeriviceDuration(int seriviceDuration) {
        this.seriviceDuration = seriviceDuration;
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
