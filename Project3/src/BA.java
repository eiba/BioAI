import java.util.*;
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
    private final Random random;
    private final Comparator<TimeSlot> timeSlotComparator;

    BA(Job[] jobs, int machineCount, int jobCount) {
        this.machineCount = machineCount;
        this.jobs = jobs;
        this.jobCount = jobCount;
        this.random = new Random();
        this.timeSlotComparator = new timeSlotComparator();

        //initialize some shit
    }

    Solution solve(/*int iterations,
                   int scoutCount,
                   int eliteSiteCount,
                   int bestSiteCount,
                   Bee[] eliteSiteBees,
                   Bee[] bestSitesBees,
                   int initalNeighbourhoodSize,
                   int stagnationCycleLimit*/) {

        Solution[] initialSolutions = initialSolutions(1);
        //Solve this shit yo

        return null;
    }

    /*Solution[] initialSolutions(int count){
        Solution[] solutions = new Solution[count];
        int[][][] schedule = new int[machineCount][jobCount][2];
        int[] machineTimes = new int[machineCount];
        int[] jobTimes = new int[jobCount];

        for(int i=0; i<count;i++){
            //randomly generate solution

            int randomJobIndex = random.nextInt(jobs.length);
            Job randomJob = jobs[randomJobIndex];

            for(int j=0;j<randomJob.requirements.length;j++){
                int machineNumber = randomJob.requirements[j][0];
                int timeRequired= randomJob.requirements[j][1];

                int currentMachineTime = machineTimes[machineNumber];
                int currentJobTime = jobTimes[randomJobIndex];

                if(currentMachineTime >= currentJobTime){
                    jobTimes[randomJobIndex] = currentMachineTime + timeRequired;
                    machineTimes[machineNumber] += timeRequired;

                    schedule[machineNumber][randomJobIndex][0] = currentMachineTime;
                    schedule[machineNumber][randomJobIndex][1] = timeRequired;
                }
                else{   //currentMachineTime < currentJobTime
                    jobTimes[randomJobIndex] += timeRequired;
                    machineTimes[machineNumber] = currentJobTime + timeRequired;

                    schedule[machineNumber][randomJobIndex][0] = currentJobTime;
                    schedule[machineNumber][randomJobIndex][1] = timeRequired;
                }

                //machineTimes[machineNumber] += currentJobTime;
            }

        }

        return null;
    }*/
    Solution[] initialSolutions(int count){
        Solution[] solutions = new Solution[count];
        int[][][] schedule = new int[machineCount][jobCount][2];
        int[] machineTimes = new int[machineCount];
        int[] jobTimes = new int[jobCount];

        HashMap<Integer,ArrayList<TimeSlot>> timeSlotMap = new HashMap<>();

        for(int i=0; i<machineCount;i++){
            ArrayList<TimeSlot> timeSlots = new ArrayList<>();
            timeSlots.add(new TimeSlot(i,0,Integer.MAX_VALUE));   //add initial timeslot
            timeSlotMap.put(i,timeSlots);
        }

        for(int i=0; i<count;i++) {
            ArrayList<Job> jobArray = new ArrayList(Arrays.asList(jobs));   //Parse to arraylist to make it easier to pick and remove random elements

            while (!jobArray.isEmpty()){

            }
            int randomJobIndex = random.nextInt(jobs.length);
            Job randomJob = jobs[randomJobIndex];
        }

        return null;
    }

    private class Bee{
        //Bee class
        public int fitness;
        public Solution solution;
    }

    /**
     * Time slot class for a machine. A time slot denotes an available execution slot on that machine
     */
    private class TimeSlot{
        public int machineNumber;
        public int startTime;
        public int totalTime;

        public TimeSlot(int machineNumber, int startTime, int totalTime){
            this.machineNumber = machineNumber;
            this.startTime = startTime;
            this.totalTime = totalTime;
        }
        public TimeSlot(int startTime, int totalTime){
            this.startTime = startTime;
            this.totalTime = totalTime;
        }
    }

    private class timeSlotComparator implements Comparator<TimeSlot>
    {
        @Override
        public int compare(TimeSlot x, TimeSlot y)
        {
            if (x.startTime > y.startTime)
            {
                return 1;
            }
            if (x.startTime < y.startTime)
            {
                return -1;
            }
            return 0;
        }
    }
}
