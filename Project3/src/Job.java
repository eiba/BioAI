public class Job {

    // [machineCount][2]
    // [X][0] = Machine Number
    // [X][1] = Time Required
    // X is ordered by machine order required
    final int[][] requirements;
    final int jobNumber;

    Job(int jobNumber, int[][] requirements) {
        this.jobNumber = jobNumber;
        this.requirements = requirements;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        for (int[] requirement : requirements) {
            sb.append(String.format("%7s", requirement[0] + ":" + requirement[1]));
        }

        return sb.toString();
    }
}
