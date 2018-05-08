import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

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
    private final Comparator<BeeSolution> makespanComparator;
    private BeeSolution bestGlobalBeeSolution = null;
    private final Random random;
    BA(Job[] jobs, int machineCount, int jobCount, JSSP jssp, GUI gui, int bestPossibleMakespan){
        this.jobs = jobs;
        this.machineCount = machineCount;
        this.jobCount = jobCount;
        this.jssp = jssp;
        this.gui = gui;
        this.bestPossibleMakespan = bestPossibleMakespan;
        this.total = machineCount * jobCount;
        this.makespanComparator = new makespanComparator();
        this.random = new Random();

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

        //Initial population
        ArrayList<BeeSolution> flowerPatches = new ArrayList<>();
        for(int i=0; i<beeCount;i++){
            flowerPatches.add(findSolution(null,0));
        }
        flowerPatches.sort(makespanComparator);

        bestGlobalBeeSolution = flowerPatches.get(0);

        //iterations
        for(int i=0; i<iterations;i++){
            if (!jssp.getRunning()) {
                return bestGlobalBeeSolution.solution;
            }

            double bestSiteCount =  0.2 * flowerPatches.size();  //number of best sites are 40% of the population
            double eliteSiteCount =  0.1 * flowerPatches.size();    //number of best sites are 10% of the population
            double bestSiteBees = 0.8 * beeCount;
            double eliteSiteBees = 0.6 * bestSiteBees;
            /*Let bees do bee stuff*/
            for (int j=0; j<flowerPatches.size();j++){
                if(j <= eliteSiteCount){
                    flowerPatches.get(j).neighbourhood = (int)(total * 0.1);
                }
                else if(j > eliteSiteCount  && j<= eliteSiteCount + bestSiteCount){
                    flowerPatches.get(j).neighbourhood = (int)(total * 0.2);
                }else{
                    flowerPatches.get(j).neighbourhood = (int)(total * 0.5);
                }
            }

            ArrayList<BeeSolution> newSolutions = new ArrayList<>();
            for (int j=0;j<beeCount;j++){
                /*if(j <= eliteSiteBees){
                    int beesPerSite = (int) (eliteSiteBees / eliteSiteCount);
                    for(int k=0; k<beesPerSite;k++){
                        newSolutions.add(findSolution(flowerPatches.get(j),random.nextInt(flowerPatches.get(j).neighbourhood)+1));
                    }
                }
                else if(j > eliteSiteBees && j <= bestSiteBees - eliteSiteBees){
                    int beesPerSite = (int) (bestSiteBees - eliteSiteBees / bestSiteCount);
                    for(int k=0; k<beesPerSite;k++){
                        newSolutions.add(findSolution(flowerPatches.get(j),random.nextInt(flowerPatches.get(j).neighbourhood)+1));
                    }
                }else{
                    newSolutions.add(findSolution(null,0));
                    //newSolutions.add(findSolution(flowerPatches.get(j),random.nextInt(flowerPatches.get(j).neighbourhood)+1));
                }

                if (newSolutions.size() >= beeCount){
                    break;
                }*/
                //flowerPatches.remove(j);
                BeeSolution flowerPatch = flowerPatches.get(j);
                if (j > bestSiteBees){
                    double p = Math.random() + (i / iterations);
                    if(p > 0.5){
                        newSolutions.add(findSolution(null,0));
                    }else{
                        BeeSolution randomBeeSolution = flowerPatches.get(random.nextInt(flowerPatches.size()));
                        newSolutions.add(findSolution(randomBeeSolution,randomBeeSolution.neighbourhood));
                    }
                    //newSolutions.add(findSolution(null,0));
                    //newSolutions.add(findSolution(flowerPatches.get(j),random.nextInt(flowerPatches.get(j).neighbourhood)+1));
                }/*else if(j <= eliteSiteCount){

                }*/
                else{
                    newSolutions.add(findSolution(flowerPatch,random.nextInt(flowerPatch.neighbourhood)+1));
                }
            }
            flowerPatches = newSolutions;
            //sort and add to graph
            flowerPatches.sort(makespanComparator);
            if (bestGlobalBeeSolution.makespan >= flowerPatches.get(0).makespan) {
                bestGlobalBeeSolution = flowerPatches.get(0);
                final double percent = (double) bestPossibleMakespan / bestGlobalBeeSolution.makespan;
                /*if (percent >= 0.9) {
                    return bestGlobalBeeSolution.solution;
                }*/
                gui.setBestSolution(bestGlobalBeeSolution.makespan, percent);
            }
            gui.addIteration((double) bestPossibleMakespan / flowerPatches.get(0).solution.getMakespan());
        }

        System.out.println(vertices.size());
        return bestGlobalBeeSolution.solution;
    }

        private BeeSolution findSolution(BeeSolution beeSolution, int neighbourhood) {

        final int[] visited = new int[jobCount];
        final int[] jobTime = new int[jobCount];
        final int[] machineTime = new int[machineCount];
        final int[][][] path = new int[machineCount][jobCount][2];

        int makespan = 0;

        BA.Vertex current = root;
        final ArrayList<Integer> vertexPath = new ArrayList<>();

        //if we are performing neighbourhood search, do this first
        if(beeSolution != null){
            for(int k = 0; k<beeSolution.path.size() - neighbourhood;k++){
                vertexPath.add(beeSolution.path.get(k));
                current = current.edges[beeSolution.path.get(k)];
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
            }
        }

        while (vertexPath.size() != total) {

            //Selecting a path
            final int index = selectPath(current, jobTime, machineTime, makespan);

            //Fixing random exception
            if (index == -1) {
                return findSolution(null,0);
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

        BeeSolution newSolution = new BeeSolution(new Solution(path), vertexPath, makespan);

        /*if(beeSolution == null || newSolution.makespan <= beeSolution.makespan || beeSolution.age > 1){
            return newSolution;
        }else{
            beeSolution.age++;
            return beeSolution;
        }*/
        return newSolution;
        //return new BeeSolution(new Solution(path), vertexPath, makespan);
    }

    private synchronized int selectPath(BA.Vertex current, int[] jobTime, int[] machineTime, int makespan) {

        double a = 1.0, b = 1.0;
        double denominator = 0;
        final double[] probability = new double[current.edges.length];
        for (int i = 0; i < probability.length; i ++) {
            probability[i] = /*Math.pow(current.pheromones[i], a) */ Math.pow((heuristic(current.edges[i], jobTime, machineTime, makespan)), b);
            denominator += probability[i];
        }

        if (denominator == 0.0) {
            //Random random = new Random();
            return random.nextInt(current.edges.length);
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

    private synchronized void addVertex(BA.Vertex vertex) {
        vertices.add(vertex);
    }

    private synchronized double heuristic(BA.Vertex vertex, int[] jobTime, int[] machineTime, int makespan) {
        final int startTime = Math.max(jobTime[vertex.jobNumber], machineTime[vertex.machineNumber]);
//        heuristic =  1.0 / Math.max(startTime + vertex.timeRequired, makespan);

        double heuristic = makespan - (startTime + vertex.timeRequired);
        if (heuristic < 0.0) {
            return 0;
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
        public int neighbourhood;
        public int age = 0;

        private BeeSolution(Solution solution, ArrayList<Integer> path, int makespan) {
            this.solution = solution;
            this.path = path;
            this.makespan = makespan;
        }
    }

    class makespanComparator implements Comparator<BeeSolution>
    {
        @Override
        public int compare(BeeSolution x, BeeSolution y)
        {
            if (x.makespan > y.makespan)
            {
                return 1;
            }
            if (x.makespan < y.makespan)
            {
                return -1;
            }
            return 0;
        }
    }
}
