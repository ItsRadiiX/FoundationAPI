package net.astralounge.foundationapi.common.startup;

import org.jetbrains.annotations.NotNull;

public class StartupTask implements Comparable<StartupTask> {

    private final StartupManager<?> startupManager;

    private final int weight;
    private String startupMessage;
    private String completeMessage;
    private final Runnable execute;

    private double timePassed;

    public StartupTask(StartupManager<?> startupManager, int weight, String startupMessage, String completeMessage, Runnable execute) {
        this.startupManager = startupManager;
        this.weight = weight;
        this.startupMessage = startupMessage;
        this.completeMessage = completeMessage;
        this.execute = execute;
    }

    public StartupTask(StartupManager<?> startupManager, int weight, Runnable execute) {
        this.startupManager = startupManager;
        this.weight = weight;
        this.execute = execute;
    }

    public void run() {

        if (showMessage(startupMessage)) {
            startupManager.getFoundationLogger().info(startupManager.getTextCreator().create(
                    String.format("<gray>%-40s | (Task Started)", startupMessage)));
        }

        long startTime = System.nanoTime();
        execute.run();
        timePassed = determineTimePassed(startTime);

        if (showMessage(completeMessage)) {
            startupManager.getFoundationLogger().info(startupManager.getTextCreator().create(
                    String.format("<gray>%-40s | (Task Completed, took: %fms)", completeMessage, getTimePassed())));
            startupManager.getFoundationLogger().info(startupManager.getTextCreator().create(
                    String.format("<gray>%-40s |", "")));
        }
    }

    private boolean showMessage(String message) {
        return message != null && !message.isBlank() && startupManager.getStartupReport();
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

    @Override
    public int compareTo(@NotNull StartupTask o) {
        return Integer.compare(getWeight(), o.getWeight());
    }
}