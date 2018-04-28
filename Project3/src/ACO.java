import java.util.ArrayList;
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
        for (int i = 0; i < jobCount; i ++) {
            final int machineNumber = jobs[i].requirements[0][0];
            final int timeRequired = jobs[i].requirements[0][1];
            final int jobNumber = jobs[i].jobNumber;
            final Vertex neighbour = new Vertex(machineNumber, jobNumber, timeRequired);
            root.edges.add(neighbour);
            root.pheromones.add(1.0);
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

        Vertex current = root;
        final ArrayList<Integer> vertexPath = new ArrayList<>();
        final HashSet<Vertex> choices = new HashSet<>(root.edges);

        while (!choices.isEmpty()) {

            //Selecting a path
            final int index = selectPath(current);
            if (index == -1) {
                System.out.println(vertexPath.size());
                System.out.println(current.edges.size());
                System.out.println(choices.size());
            }
            vertexPath.add(index);
            current = current.edges.get(index);
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
            jobTime[jobNumber] = startTime + timeRequired;
            machineTime[machineNumber] = startTime + timeRequired;

            // Adding next option
            if (++ visited[jobNumber] < machineCount) {
                final int neighbourMachineNumber = jobs[jobNumber].requirements[visited[jobNumber]][0];
                final int neighbourTimeRequired = jobs[jobNumber].requirements[visited[jobNumber]][1];
                choices.add(new Vertex(neighbourMachineNumber, jobNumber, neighbourTimeRequired));
            }

            // Updating outgoing edges
            if (current.edges.size() == 0) {
                for (Vertex choice : choices) {
                    current.edges.add(choice);
                    current.pheromones.add(1.0);
                }
            }
        }

        return new Solution(path);
    }

    /**
     * Selects a path based on pheromones and edge options
     * @param current current vertex
     * @return index of the path selected
     */
    private int selectPath(Vertex current) {

        double a = 0, b = 1;
        double denominator = 0;
        for (int i = 0; i < current.edges.size(); i ++) {
            denominator += Math.pow(current.pheromones.get(i), a);
        }

        double cumulativeProbability = 0;
        double threshold = Math.random();
        for (int i = 0; i < current.edges.size(); i ++) {
            cumulativeProbability += Math.pow(current.pheromones.get(i), a) / denominator;
            if (threshold <= cumulativeProbability) {
                return i;
            }
        }

        return -1;
    }

    private class Vertex {
        private final int machineNumber, jobNumber, timeRequired;
        private ArrayList<Vertex> edges = new ArrayList<>();
        private ArrayList<Double> pheromones = new ArrayList<>();

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
