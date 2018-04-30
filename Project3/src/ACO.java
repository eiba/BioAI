import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Ant Colony Optimization
 */
class ACO {

    private final JSSP jssp;
    private final GUI gui;
    private final Job[] jobs;
    private final int jobCount, machineCount, total, bestPossibleMakespan;
    private final Vertex root;
//    private final ArrayList<Vertex> vertices = new ArrayList<>();
    private AntSolution bestGlobalAntSolution = null;

    private final double evaporationRate = 0.1;
    private final double p = 1.0 - evaporationRate;

    double tMax = 0.0, tMin;
    final double pDec, tMinBase;

    ACO(Job[] jobs, int machineCount, int jobCount, JSSP jssp, GUI gui, int bestPossibleMakespan) {
        this.jobs = jobs;
        this.machineCount = machineCount;
        this.jobCount = jobCount;
        this.jssp = jssp;
        this.gui = gui;
        this.bestPossibleMakespan = bestPossibleMakespan;
        total = machineCount * jobCount;

        pDec = 1.0 / jobCount;
        tMinBase = (1 - pDec) / (jobCount - 1) * pDec;

        root = new Vertex(-1, -1, -1);
//        vertices.add(root);
        root.edges = new Vertex[jobCount];
        root.pheromones = new double[jobCount];
        for (int i = 0; i < jobCount; i ++) {
            final int machineNumber = jobs[i].requirements[0][0];
            final int timeRequired = jobs[i].requirements[0][1];
            final int jobNumber = jobs[i].jobNumber;
            final Vertex neighbour = new Vertex(machineNumber, jobNumber, timeRequired);
//            vertices.add(neighbour);
            root.edges[i] = neighbour;
            root.pheromones[i] = tMax;
        }
    }

    Solution solve(int iterations, int antCount) {

        int count = 0;

        for (int i = 0; i < iterations; i ++) {

            if (!jssp.getRunning()) {
                return bestGlobalAntSolution.solution;
            }

            final ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            final AntSolution[] solutions = new AntSolution[antCount];
            for (int j = 0; j < antCount; j ++) {
                final int index = j;
//                solutions[index] = findSolution();
                pool.execute(() -> solutions[index] = findSolution());
            }
            pool.shutdown();
            try {
                pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            int bestMakespan = Integer.MAX_VALUE;
            AntSolution bestAntSolution = null;
            for (int j = 0; j < antCount; j ++) {
                final AntSolution antSolution = solutions[j];
                if (bestAntSolution == null || bestMakespan > antSolution.makespan) {
                    bestAntSolution = antSolution;
                    bestMakespan = antSolution.makespan;
                }
            }

            if (bestGlobalAntSolution == null || bestGlobalAntSolution.makespan > bestAntSolution.makespan) {
                bestGlobalAntSolution = bestAntSolution;
                final double percent = (double) bestPossibleMakespan / bestMakespan;
                if (percent >= 0.9) {
                    return bestGlobalAntSolution.solution;
                }
                gui.setBestSolution(bestMakespan, percent);
            }

//            final double delta = 1.0 / bestAntSolution.makespan;
            final double delta = 1.0;
//            System.out.println(delta);
            tMax = 1.0 / evaporationRate * 1 / (bestAntSolution.makespan);
            tMin = tMax * tMinBase;
            System.out.println(tMax + ", " + tMin);

//            for (Vertex vertex : vertices) {
//                if (vertex.edges != null) {
//                    for (int j = 0; j < vertex.edges.length; j ++) {
//                        vertex.pheromones[j] *= p;
//                        if (vertex.pheromones[j] < tMin) {
//                            vertex.pheromones[j] = tMin;
//                        }
//                        else if (vertex.pheromones[j] > tMax) {
//                            vertex.pheromones[j] = tMax;
//                        }
//                    }
//                }
//            }
            Vertex current = root;
            for (int j = 0; j < total; j ++) {
                if (!current.visited) {
                    current.visited = true;
                    count ++;
                }
                final int index = bestAntSolution.path.get(j);
                for (int c = 0; c < current.pheromones.length; c ++) {
                    current.pheromones[c] *= p;
                    if (c == index) {
                        current.pheromones[c] += delta;
                    }
                    if (current.pheromones[c] < tMin) {
                        current.pheromones[c] = tMin;
                    }
                    else if (current.pheromones[c] > tMax) {
                        current.pheromones[c] = tMax;
                    }
                }
                for (double pheromone : current.pheromones) {
//                    System.out.println(pheromone);
                }
//                System.out.println();
//            System.out.println("> " + current.pheromones.length);
//                System.out.println(tMax + ", " + tMin);
                current = current.edges[index];
            }
//            System.out.println(bestGlobalAntSolution.path.size());

            gui.addIteration((double) bestPossibleMakespan / bestMakespan);
//            break;
        }

        System.out.println(count);
//        System.out.println(vertices.size());
        return bestGlobalAntSolution.solution;
    }

//    private void updatePheromones(Vertex current, int depth, ArrayList<Integer> path, double delta) {
//
//        if (current.edges == null) {
//            return;
//        }
//
//        for (int i = 0; i < current.edges.length; i ++) {
//            // Best solution this iteration
//            if (i == path.get(depth)) {
//                current.pheromones[i] = (1.0 - evaporationRate) * current.pheromones[i] + delta;
//            }
//            else {
//                current.pheromones[i] = (1.0 - evaporationRate) * current.pheromones[i];
//            }
//            updatePheromones(current.edges[i], depth + 1, path, delta);
//        }
//    }

    private AntSolution findSolution() {

        final int[] visited = new int[jobCount];
        final int[] jobTime = new int[jobCount];
        final int[] machineTime = new int[machineCount];
        final int[][][] path = new int[machineCount][jobCount][2];

        int makespan = 0;

        Vertex current = root;
        final ArrayList<Integer> vertexPath = new ArrayList<>();

        while (vertexPath.size() != total) {

            //Selecting a path
            final int index = selectPath(current, jobTime, machineTime, makespan);

            //Fixing random exception
            if (index == -1) {
//                return findSolution();
            }

            vertexPath.add(index);
            current = current.edges[index];
            visited[current.jobNumber] ++;

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


            // New Vertex
            if (current.edges == null) {

                // Adding next option
                final ArrayList<Vertex> choices = new ArrayList<>();
                for (int i = 0; i < jobCount; i ++) {
                    if (visited[i] < machineCount) {
                        final int neighbourMachineNumber = jobs[i].requirements[visited[i]][0];
                        final int neighbourTimeRequired = jobs[i].requirements[visited[i]][1];
                        final Vertex neighbour = new Vertex(neighbourMachineNumber, jobs[i].jobNumber, neighbourTimeRequired);
                        choices.add(neighbour);
                        addVertex(neighbour);
                    }
                }
                current.edges = new Vertex[choices.size()];
                current.pheromones = new double[current.edges.length];
                choices.toArray(current.edges);
                Arrays.fill(current.pheromones, tMax);
            }
        }

        return new AntSolution(new Solution(path), vertexPath, makespan);
    }

    /**
     * Selects a path based on pheromones and edge options
     * @param current current vertex
     * @return index of the path selected
     */
    private synchronized int selectPath(Vertex current, int[] jobTime, int[] machineTime, int makespan) {

        double a = 1.0, b = 1.0;
        double denominator = 0.0;
        final double[] probability = new double[current.edges.length];

        if (!current.visited) {
            for (int i = 0; i < probability.length; i ++) {
                probability[i] = heuristic(current.edges[i], jobTime, machineTime, makespan);
                denominator += probability[i];
            }
        }
        else {
            for (int i = 0; i < probability.length; i ++) {
                probability[i] = Math.pow(current.pheromones[i], a) * Math.pow((heuristic(current.edges[i], jobTime, machineTime, makespan)), b);
                denominator += probability[i];
            }
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

    private synchronized void addVertex(Vertex vertex) {
//        vertices.add(vertex);
    }

    private synchronized double heuristic(Vertex vertex, int[] jobTime, int[] machineTime, int makespan) {
//        double heuristic;
        final int startTime = Math.max(jobTime[vertex.jobNumber], machineTime[vertex.machineNumber]);
        return  1.0 / Math.max(startTime + vertex.timeRequired, makespan);
//        return 1.0 / (startTime + vertex.timeRequired);

//        heuristic = makespan - (startTime + vertex.timeRequired);
//        if (heuristic < 0.0) {
//            return 1;
//        }

//        return heuristic;
    }

    class Vertex {
         final int machineNumber, jobNumber, timeRequired;
         Vertex[] edges;
         double[] pheromones;
         boolean visited = false;

        private Vertex(int machineNumber, int jobNumber, int timeRequired) {
            this.machineNumber = machineNumber;
            this.jobNumber = jobNumber;
            this.timeRequired = timeRequired;
        }
    }

    class AntSolution {
         final Solution solution;
         final ArrayList<Integer> path;
         final int makespan;

        private AntSolution(Solution solution, ArrayList<Integer> path, int makespan) {
            this.solution = solution;
            this.path = path;
            this.makespan = makespan;
        }
    }
}
