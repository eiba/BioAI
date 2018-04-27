import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Ant Colony Optimization
 */
class ACO {

    private final Job[] jobs;
    private final int jobCount, machineCount;

    private final double evaporationRate = 0.1;

    ACO(Job[] jobs, int machineCount, int jobCount) {
        this.jobs = jobs;
        this.machineCount = machineCount;
        this.jobCount = jobCount;
    }

    Solution solve(int iterations, int antCount) {

        for (int i = 0; i < iterations; i ++) {
            final ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            final Solution[] solutions = new Solution[antCount];
            for (int j = 0; j < antCount; j ++) {
                final int index = j;
                pool.execute(() -> solutions[index] = findSolution());
            }
            pool.shutdown();
            try {
                pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            return solutions[0];
        }

        return null;
    }

    private Solution findSolution() {

        final int[] visited = new int[jobCount];
        final int[] jobTime = new int[jobCount];
        final int[] machineTime = new int[machineCount];
        final int[][][] path = new int[machineCount][jobCount][2];

        final PriorityQueue<Edge> choices = new PriorityQueue<>();

        for (int i = 0; i < jobCount; i ++) {
            final int machineNumber = jobs[i].requirements[visited[i]][0];
            final int timeRequired = jobs[i].requirements[visited[i] ++][1];
            choices.add(new Edge(new Vertex(machineNumber, jobs[i].jobNumber, timeRequired)));
        }

        while (!choices.isEmpty()) {
            final Edge currentEdge = choices.remove();
            final int machineNumber = currentEdge.neighbour.machineNumber;
            final int jobNumber = currentEdge.neighbour.jobNumber;
            final int timeRequired = currentEdge.neighbour.timeRequired;

            // Start time
            final int startTime = Math.max(jobTime[jobNumber], machineTime[machineNumber]);
            path[machineNumber][jobNumber][0] = startTime;
            // Time required
            path[machineNumber][jobNumber][1] = timeRequired;
            jobTime[jobNumber] = startTime + timeRequired;
            machineTime[machineNumber] = startTime + timeRequired;

            // Adding next option
            if (visited[jobNumber] < machineCount) {
                final int edgeMachineNumber = jobs[jobNumber].requirements[visited[jobNumber]][0];
                final int edgeTimeRequired = jobs[jobNumber].requirements[visited[jobNumber] ++][1];
                choices.add(new Edge(new Vertex(edgeMachineNumber, jobNumber, edgeTimeRequired)));
            }

        }

        return new Solution(path);
    }

    private class Vertex {
        private final int machineNumber, jobNumber, timeRequired;

        private Vertex(int machineNumber, int jobNumber, int timeRequired) {
            this.machineNumber = machineNumber;
            this.jobNumber = jobNumber;
            this.timeRequired = timeRequired;
        }
    }

    private class Edge implements Comparable<Edge> {
        private final Vertex neighbour;
        private double phermone = 1;

        private Edge(Vertex neighbour) {
            this.neighbour = neighbour;
        }

        @Override
        public int compareTo(Edge o) {
            return 0;
        }
    }
}
