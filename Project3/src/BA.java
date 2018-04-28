import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Bees Algorithm
 */
class BA {

    private final Job[] jobs;
    private final int machineCount;
    private final int jobCount;

    BA(Job[] jobs, int machineCount, int jobCount) {
        this.machineCount = machineCount;
        this.jobs = jobs;
        this.jobCount = jobCount;

        //initialize some shit
    }

    Solution solve(int iterations,
                   int scoutCount,
                   int eliteSiteCount,
                   int bestSiteCount,
                   Bee[] eliteSiteBees,
                   Bee[] bestSitesBees,
                   int initalNeighbourhoodSize,
                   int stagnationCycleLimit) {


        //Solve this shit yo

        return null;
    }

    Solution[] initialSolutions(int count){
        Solution[] solutions = new Solution[count];

        for(int i=0; i<count;i++){
            //randomly generate solution
            
        }

        return null;
    }

    private class Bee{
        //Bee class
        public int fitness;
        public Solution solution;
    }
}
