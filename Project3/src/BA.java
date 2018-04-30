import java.util.ArrayList;

/**
 * Bees algorithm, the Stigen way
 */

public class BA {
    private final JSSP jssp;
    private final GUI gui;
    private final Job[] jobs;
    private final int jobCount, machineCount, total, bestPossibleMakespan;
    private final BA.Vertex root;
    private final ArrayList<BA.Vertex> vertices = new ArrayList<>();

    BA(Job[] jobs, int machineCount, int jobCount, JSSP jssp, GUI gui, int bestPossibleMakespan){
        this.jobs = jobs;
        this.machineCount = machineCount;
        this.jobCount = jobCount;
        this.jssp = jssp;
        this.gui = gui;
        this.bestPossibleMakespan = bestPossibleMakespan;
        this.total = machineCount * jobCount;

        root = new BA.Vertex(-1, -1, -1);
        vertices.add(root);
        root.edges = new BA.Vertex[jobCount];
        //root.pheromones = new double[jobCount];
        for (int i = 0; i < jobCount; i ++) {
            final int machineNumber = jobs[i].requirements[0][0];
            final int timeRequired = jobs[i].requirements[0][1];
            final int jobNumber = jobs[i].jobNumber;
            final BA.Vertex neighbour = new BA.Vertex(machineNumber, jobNumber, timeRequired);
            vertices.add(neighbour);
            root.edges[i] = neighbour;
            //root.pheromones[i] = tMax;
        }
    }

    Solution solve(int iterations, int beeCount) {

        Solution solution = findSolution().solution;
        /*for (int i= 0; i<beeCount;i++){

        }*/

        return solution;
    }

        private BeeSolution findSolution() {

        final int[] visited = new int[jobCount];
        final int[] jobTime = new int[jobCount];
        final int[] machineTime = new int[machineCount];
        final int[][][] path = new int[machineCount][jobCount][2];

        int makespan = 0;

        BA.Vertex current = root;
        final ArrayList<Integer> vertexPath = new ArrayList<>();

        while (vertexPath.size() != total) {

            //Selecting a path
            final int index = selectPath(current, jobTime, machineTime, makespan);

            //Fixing random exception
            if (index == -1) {
                return findSolution();
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
                final ArrayList<BA.Vertex> choices = new ArrayList<>();
                for (int i = 0; i < jobCount; i ++) {
                    if (visited[i] < machineCount) {
                        final int neighbourMachineNumber = jobs[i].requirements[visited[i]][0];
                        final int neighbourTimeRequired = jobs[i].requirements[visited[i]][1];
                        final BA.Vertex neighbour = new BA.Vertex(neighbourMachineNumber, jobs[i].jobNumber, neighbourTimeRequired);
                        choices.add(neighbour);
                        addVertex(neighbour);
                    }
                }
                current.edges = new BA.Vertex[choices.size()];
                //current.pheromones = new double[current.edges.length];
                choices.toArray(current.edges);
                //Arrays.fill(current.pheromones, tMax);
            }
        }

        return new BeeSolution(new Solution(path), vertexPath, makespan);
    }

    private synchronized int selectPath(BA.Vertex current, int[] jobTime, int[] machineTime, int makespan) {

        double a = 1.0, b = 1.0;
        double denominator = 0;
        final double[] probability = new double[current.edges.length];
        for (int i = 0; i < probability.length; i ++) {
            probability[i] = /*Math.pow(current.pheromones[i], a) */ Math.pow((heuristic(current.edges[i], jobTime, machineTime, makespan)), b);
            denominator += probability[i];
        }

//        if (denominator == 0.0) {
//            Random random = new Random();
//            return random.nextInt(current.edges.length);
//        }

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

    private synchronized void addVertex(BA.Vertex vertex) {
        vertices.add(vertex);
    }

    private synchronized double heuristic(BA.Vertex vertex, int[] jobTime, int[] machineTime, int makespan) {
        double heuristic = 1.0;
        final int startTime = Math.max(jobTime[vertex.jobNumber], machineTime[vertex.machineNumber]);
        heuristic =  1.0 / Math.max(startTime + vertex.timeRequired, makespan);

        heuristic = makespan - (startTime + vertex.timeRequired);
        if (heuristic < 0.0) {
            return 1;
        }

        return heuristic;
    }

    class Vertex {
        final int machineNumber, jobNumber, timeRequired;
        BA.Vertex[] edges;
        //double[] pheromones;

        private Vertex(int machineNumber, int jobNumber, int timeRequired) {
            this.machineNumber = machineNumber;
            this.jobNumber = jobNumber;
            this.timeRequired = timeRequired;
        }
    }

    class BeeSolution {
        final Solution solution;
        final ArrayList<Integer> path;
        final int makespan;

        private BeeSolution(Solution solution, ArrayList<Integer> path, int makespan) {
            this.solution = solution;
            this.path = path;
            this.makespan = makespan;
        }
    }
}
