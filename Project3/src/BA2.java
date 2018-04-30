import java.util.ArrayList;

/**
 * Bees algorithm, the Stigen way
 */

public class BA2 {
    private final JSSP jssp;
    private final GUI gui;
    private final Job[] jobs;
    private final int jobCount, machineCount, total, bestPossibleMakespan;
    private final BA2.Vertex root;
    private final ArrayList<BA2.Vertex> vertices = new ArrayList<>();

    BA2(Job[] jobs, int machineCount, int jobCount, JSSP jssp, GUI gui, int bestPossibleMakespan){
        this.jobs = jobs;
        this.machineCount = machineCount;
        this.jobCount = jobCount;
        this.jssp = jssp;
        this.gui = gui;
        this.bestPossibleMakespan = bestPossibleMakespan;
        this.total = machineCount * jobCount;

        root = new BA2.Vertex(-1, -1, -1);
        vertices.add(root);
        root.edges = new BA2.Vertex[jobCount];

    }

    private synchronized void addVertex(BA2.Vertex vertex) {
        vertices.add(vertex);
    }

    private synchronized double heuristic(BA2.Vertex vertex, int[] jobTime, int[] machineTime, int makespan) {
        double heuristic = 1.0;
        final int startTime = Math.max(jobTime[vertex.jobNumber], machineTime[vertex.machineNumber]);
        heuristic =  1.0 / Math.max(startTime + vertex.timeRequired, makespan);

        heuristic = makespan - (startTime + vertex.timeRequired);
        if (heuristic < 0.0) {
            return 1;
        }

        return heuristic;
    }

    private class Vertex {
        final int machineNumber, jobNumber, timeRequired;
        BA2.Vertex[] edges;
        //double[] pheromones;

        private Vertex(int machineNumber, int jobNumber, int timeRequired) {
            this.machineNumber = machineNumber;
            this.jobNumber = jobNumber;
            this.timeRequired = timeRequired;
        }
    }
}
