package net.astralounge.foundationapi.common.startup;

import net.astralounge.foundationapi.common.component.FoundationComponent;
import net.astralounge.foundationapi.common.datamanagement.files.handler.implementation.AutoReloadableConfigurationHandler;
import net.astralounge.foundationapi.common.localisation.LocalisationService;
import net.astralounge.foundationapi.common.localisation.TextCreator;
import net.astralounge.foundationapi.common.localisation.managers.MessagesManager;
import net.astralounge.foundationapi.common.logger.loggers.FoundationLogger;
import net.astralounge.foundationapi.common.manager.ConfigurableCommonManager;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;
import net.astralounge.foundationapi.common.startup.configuration.StartupConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StartupManager<T extends StartupConfiguration> extends ConfigurableCommonManager<T> {
    private final List<LoadError> loadErrors;
    private final List<StartupTask> startupTasks;

    public StartupManager(FoundationDefaultPlugin<?> plugin, AutoReloadableConfigurationHandler<T> configuration) {
        super(plugin, configuration);
        this.loadErrors = new ArrayList<>();
        this.startupTasks = new ArrayList<>();
    }

    @Override
    protected void onLoad() throws Exception {}

    @Override
    public void onEnable() throws Exception {

    }

    @Override
    public void onDisable() throws Exception {

    }

    public List<StartupTask> getSortedTasks(long startTime) {
        List<StartupTask> allTasks = new ArrayList<>();
        allTasks.addAll(getDefaultStartupTasks(startTime));
        allTasks.addAll(getPlugin().startupTasks());

        Collections.sort(allTasks);

        return allTasks;
    }

    private List<StartupTask> getDefaultStartupTasks(long startTime) {
        startupTasks.add(new StartupTask(this, 1, "Loading Dependencies...",
                "All Dependencies have been initialized!", () -> getPlugin().getDependencyManager().checkDependencies()));
        startupTasks.add(new StartupTask(this, 2, () -> didDependenciesLoad(startTime)));

        startupTasks.add(new StartupTask(this, 10, "Loading Components...",
                "All Components have been initialized!", () -> getPlugin().getComponents().forEach(FoundationComponent::start)));
        return startupTasks;
    }

    public void printStartupInfo() {
        printStartupReport("<gray>()=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=()");
        printLogo(getLogo());
        printStartupReport("");
        printStartupReport("Version: " + getPlugin().getPluginVersion());
        if (getStartupPromoteAuthor())
            printStartupReport("Author(s): Made by " + getPlugin().getPluginAuthor() + " with love <red>♥");
        printStartupReport("Server Version " + getPlugin().getMinecraftVersion());
        printStartupReport("<gray>()=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=()");
        printStartupReport("");
        printStartupReport("<gold>" + getPlugin().getPluginName() + "<gray> is starting...");
        printStartupReport(String.format("<gray>%-41s|", "-".repeat(41)));
    }

    public void determineErrors(long startTime) {
        if (getCriticalLoadErrorsAmount() > 0) {
            printStartupReport("");
            printStartupReport("<gold>" + getPlugin().getPluginName() + "<gray> could not load due to <red>" + getLoadErrors() + "<gray> error(s)!");
            printStartupReport("");
            getLoadErrors().forEach(
                    startupError -> printStartupReport(startupError.message()));
            printStartupReport("");
            printStartupReport("<red>Please read any information shown to try to understand this error!");
            printStartupReport("<gold>" + getPlugin().getPluginName() + "<gray> will now be disabled! <red>=(");
            printStartupReport("<gray>()=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=()");
            printStartupReport("");
            getPlugin().scheduleDisable();
        } else {
            printStartupReport("<gray>");
            printStartupReport("<gold>" + getPlugin().getPluginName() + "<gray> has loaded with " + getLoadErrorsSize() + "<gray> error(s)!");
            printStartupReport("<gray>Took: " + determineTimePassed(startTime) + "ms to start plugin. <green>=)");
            printStartupReport("<gray>()=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=()");
            printStartupReport("");
        }
    }

    private void didDependenciesLoad(long startTime) {
        if (!getPlugin().getDependencyManager().areDependenciesLoaded() && getCriticalLoadErrorsAmount() > 0) {
            determineErrors(startTime);
        }
    }

    public void finishSetup(long startTime) {
        // Footer
        printStartupReport(String.format("<gray>%-41s|", "-".repeat(41)));

        // Check if everything loaded successfully
        determineErrors(startTime);
    }

    private void printLogo(List<String> logo) {
        logo.forEach(this::printStartupReport);
    }

    public void printStartupReport(String message) {
        getFoundationLogger().startupReport(getStartupReport(), getTextCreator().create((message)));
    }

    public void logDependencyStatus(String dependencyName, boolean found) {
        String status = found ? "<green>FOUND" : "<red>NOT FOUND <<---";
        printStartupReport(String.format("<gray>- <gold>%-39s<gray>| %s", dependencyName, status));
    }

    private double determineTimePassed(long start) {
        return ((System.nanoTime() - start) / 1e6);
    }


    public void addLoadError(LoadError error) {
        loadErrors.add(error);
    }

    public List<LoadError> getLoadErrors() {
        return loadErrors;
    }

    public int getLoadErrorsSize() {
        return loadErrors.size();
    }

    public int getRiskLoadErrorsAmount() {
        return getLoadErrors(LoadError.Level.RISK).size();
    }

    public int getFatalLoadErrorsAmount() {
        return getLoadErrors(LoadError.Level.FATAL).size();
    }

    public int getCriticalLoadErrorsAmount() {
        return getLoadErrors(LoadError.Level.CRITICAL).size();
    }

    public List<LoadError> getLoadErrors(LoadError.Level level) {
        return loadErrors.stream()
                .filter(loadError -> loadError.level().equals(level))
                .toList();
    }

    private StartupConfiguration getConfiguration() {
        return getConfigurationHandler().getConfigurationObject();
    }

    public Boolean getStartupReport() {
        return getConfiguration().enableStartupReport;
    }

    public Boolean getStartupPromoteAuthor() {
        return getConfiguration().enablePromoteAuthor;
    }

    public Boolean getStartupShowSoftDependencyNotFound() {
        return getConfiguration().enableShowSoftDependencyNotFound;
    }

    public List<String> getLogo() {
        return getConfiguration().showLogo;
    }

    public FoundationLogger getFoundationLogger() {
        return getPlugin().getFoundationPluginLogger();
    }

    public MessagesManager<?> getMessagesManager() {
        return LocalisationService.getInstance().getMessagesManager();
    }

    public TextCreator getTextCreator() {
        return getMessagesManager().getTextCreator();
    }

}
