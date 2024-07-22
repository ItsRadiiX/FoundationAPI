package com.itsradiix.foundationapi.common.startup;

import com.itsradiix.foundationapi.common.textmanager.TextCreator;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;

public class StartupTask implements Comparable<StartupTask> {

    private final ComponentLogger logger;
    private static int newID = 0;
    private int taskID;

    private final int weight;
    private String startupMessage;
    private String completeMessage;
    private final Runnable execute;

    private double timePassed;

    public StartupTask(int weight, String startupMessage, String completeMessage, Runnable execute) {
        this.logger = StartupDataManager.getInstance().getLogger();
        this.weight = weight;
        this.startupMessage = startupMessage;
        this.completeMessage = completeMessage;
        this.execute = execute;

        newID++;
        this.taskID = newID;
    }

    public StartupTask(int weight, Runnable execute) {
        this.logger = StartupDataManager.getInstance().getLogger();
        this.weight = weight;
        this.execute = execute;
    }

    public void run() {

        if (showMessage(startupMessage)) {
            logger.info(TextCreator.create(
                    String.format("<gray>%-40s | (task %d Started)", startupMessage, taskID)));
        }

        long startTime = System.nanoTime();
        execute.run();
        timePassed = determineTimePassed(startTime);

        if (showMessage(completeMessage)) {
            logger.info(TextCreator.create(
                    String.format("<gray>%-40s | (task %d Completed, took: %fms)", completeMessage, taskID, timePassed)));
            logger.info(TextCreator.create(
                    String.format("<gray>%-40s |", "")));
        }
    }

    private boolean showMessage(String message){
        return message != null && !message.isBlank() && StartupDataManager.getInstance().getStartupDebug();
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