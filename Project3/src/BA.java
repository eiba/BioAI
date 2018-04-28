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

    private class Bee{
        //Bee class
    }
}
