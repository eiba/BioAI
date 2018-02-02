public class ProposedSolution {

    private double durationScore;
    private int customerScore;
    private double fitnessScore;
    private Car[] cars;

    public ProposedSolution(Car[] cars, int customerScore) {

        this.customerScore = customerScore;
        this.cars = cars;
        for (Car car : cars) {
            durationScore += car.getCurrentDuration();
        }
        this.fitnessScore = (this.customerScore+1)*this.durationScore;

    }

    public double getFitnessScore() {
        return fitnessScore;
    }

    public void setFitnessScore(double fitnessScore) {
        this.fitnessScore = fitnessScore;
    }

    public Car[] getCars() {
        return cars;
    }

    public void setCars(Car[] cars) {
        this.cars = cars;
    }

    public int getCustomerScore() {
        return customerScore;
    }

    public void setCustomerScore(int customerScore) {
        this.customerScore = customerScore;
    }

    public double getDurationScore() {
        return durationScore;
    }

    public void setDurationScore(double durationScore) {
        this.durationScore = durationScore;
    }
}
