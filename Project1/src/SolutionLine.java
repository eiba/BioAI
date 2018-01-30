import java.util.ArrayList;

public class SolutionLine {

        private int depot_nr;
        private int car_nr;
        private double duration;
        private int load;
        private int[] sequence;

    public SolutionLine(int depot_nr, int car_nr, double duration, int load, int[] sequence) {
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

    public int[] getSequence() {
        return sequence;
    }

    public void setSequence(int[] sequence) {
        this.sequence = sequence;
    }
}