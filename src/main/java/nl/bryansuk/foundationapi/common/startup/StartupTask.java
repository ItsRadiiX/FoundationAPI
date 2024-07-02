package nl.bryansuk.foundationapi.common.startup;

import nl.bryansuk.foundationapi.paper.plugin.FoundationPaperPlugin;
import nl.bryansuk.foundationapi.common.logging.FoundationLogger;
import org.jetbrains.annotations.NotNull;

public class StartupTask implements Comparable<StartupTask> {

    private final FoundationLogger logger;
    private static int newID = 0;
    private int taskID;

    private final int weight;
    private String startupMessage;
    private String completeMessage;
    private final Runnable execute;

    private double timePassed;

    public StartupTask(int weight, String startupMessage, String completeMessage, Runnable execute) {
        this.logger = FoundationPaperPlugin.getFoundationLogger();
        this.weight = weight;
        this.startupMessage = startupMessage;
        this.completeMessage = completeMessage;
        this.execute = execute;

        newID++;
        this.taskID = newID;
    }

    public StartupTask(int weight, Runnable execute) {
        this.logger = FoundationPaperPlugin.getFoundationLogger();
        this.weight = weight;
        this.execute = execute;
    }

    public void run() {

        if (startupMessage != null && !startupMessage.isBlank()) {
            logger.info(String.format("<gray>%-40s | (task %d Started)", startupMessage, taskID));
        }

        long startTime = System.nanoTime();
        execute.run();
        timePassed = determineTimePassed(startTime);

        if (completeMessage != null && !completeMessage.isBlank()) {
            logger.info(String.format("<gray>%-40s | (task %d Completed, took: %fms)", completeMessage, taskID, timePassed));
            logger.info(String.format("<gray>%-40s |", ""));
        }
    }

    public int getWeight() {
        return weight;
    }

    public double getTimePassed() {
        return timePassed;
    }

    /**
     * Method to determine the difference from starting point and current time
     *
     * @param start starting point to be determined from, should be in nanoTime
     * @return double returns time passed since start in ms
     */
    private double determineTimePassed(long start) {
        return ((System.nanoTime() - start) / 1e6);
    }

    public static void resetID(){
        newID = 0;
    }

    @Override
    public int compareTo(@NotNull StartupTask o) {
        return Integer.compare(getWeight(), o.getWeight());
    }
}