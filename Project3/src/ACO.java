import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Ant Colony Optimization
 */
class ACO {

    private final Job[] jobs;
    private final int jobCount, machineCount, total;
    private final Vertex root;

    private final double evaporationRate = 0.1;

    ACO(Job[] jobs, int machineCount, int jobCount) {
        this.jobs = jobs;
        this.machineCount = machineCount;
        this.jobCount = jobCount;
        total = machineCount * jobCount;

        root = new Vertex(-1, -1, -1);
        root.edges = new Vertex[jobCount];
        root.pheromones = new double[jobCount];
        for (int i = 0; i < jobCount; i ++) {
            final int machineNumber = jobs[i].requirements[0][0];
            final int timeRequired = jobs[i].requirements[0][1];
            final int jobNumber = jobs[i].jobNumber;
            final Vertex neighbour = new Vertex(machineNumber, jobNumber, timeRequired);
            root.edges[i] = neighbour;
            root.pheromones[i] = 1.0;
        }
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

        int makespan = 0;

        Vertex current = root;
        final ArrayList<Integer> vertexPath = new ArrayList<>();
        final ArrayList<Vertex> choices = new ArrayList<>(Arrays.asList(root.edges));

        while (!choices.isEmpty()) {

            //Selecting a path
            final int index = selectPath(current, jobTime, machineTime, makespan);
            vertexPath.add(index);
            current = current.edges[index];
            choices.remove(current);

            final int machineNumber = current.machineNumber;
            final int jobNumber = current.jobNumber;
            final int timeRequired = current.timeRequired;

            // Start time
            final int startTime = Math.max(jobTime[jobNumber], machineTime[machineNumber]);
            path[machineNumber][jobNumber][0] = startTime;
            // Time required
            path[machineNumber][jobNumber][1] = timeRequired;
            // Updating variables
            final int time = startTime + timeRequired;
            jobTime[jobNumber] = time;
            machineTime[machineNumber] = time;
            if (time > makespan) {
                makespan = time;
            }

            // Adding next option
            if (++ visited[jobNumber] < machineCount) {
                final int neighbourMachineNumber = jobs[jobNumber].requirements[visited[jobNumber]][0];
                final int neighbourTimeRequired = jobs[jobNumber].requirements[visited[jobNumber]][1];
                choices.add(new Vertex(neighbourMachineNumber, jobNumber, neighbourTimeRequired));
            }

            // Updating outgoing edges
            if (current.edges == null) {
                current.edges = new Vertex[choices.size()];
                current.pheromones = new double[current.edges.length];
                choices.toArray(current.edges);
                Arrays.fill(current.pheromones, 1.0);
            }
        }

        return new Solution(path);
    }

    /**
     * Selects a path based on pheromones and edge options
     * @param current current vertex
     * @return index of the path selected
     */
    private int selectPath(Vertex current, int[] jobTime, int[] machineTime, int makespan) {

        double a = 1, b = 1;
        double denominator = 0;
        final double[] probability = new double[current.edges.length];
        for (int i = 0; i < probability.length; i ++) {
            probability[i] = Math.pow(current.pheromones[i], a) * Math.pow((heuristic(current.edges[i], jobTime, machineTime, makespan)), b);
            denominator += probability[i];
        }

        double cumulativeProbability = 0;
        double threshold = Math.random();
        for (int i = 0; i < current.edges.length; i ++) {
            cumulativeProbability += probability[i] / denominator;
            if (threshold <= cumulativeProbability) {
                return i;
            }
        }

        return -1;
    }

    private double heuristic(Vertex vertex, int[] jobTime, int[] machineTime, int makespan) {
        final int startTime = Math.max(jobTime[vertex.jobNumber], machineTime[vertex.machineNumber]);
        return 1.0 / Math.max(startTime + vertex.timeRequired, makespan);
    }

    private class Vertex {
        private final int machineNumber, jobNumber, timeRequired;
        private Vertex[] edges;
        private double[] pheromones;

        private Vertex(int machineNumber, int jobNumber, int timeRequired) {
            this.machineNumber = machineNumber;
            this.jobNumber = jobNumber;
            this.timeRequired = timeRequired;
        }
    }

//    private class Edge implements Comparable<Edge> {
//        private final Vertex current, neighbour;
//        private double phermone = 1;
//
//        private Edge(Vertex current, Vertex neighbour) {
//            this.current = current;
//            this.neighbour = neighbour;
//        }
//
//        @Override
//        public int compareTo(Edge o) {
//            return 0;
//        }
//    }
}
