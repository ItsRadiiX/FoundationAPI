package nl.bryansuk.foundationapi.plugin;

import nl.bryansuk.foundationapi.FileManager;
import nl.bryansuk.foundationapi.ItemManager;
import nl.bryansuk.foundationapi.textmanager.TextCreator;
import nl.bryansuk.foundationapi.components.FoundationComponent;
import nl.bryansuk.foundationapi.dependencies.Dependency;
import nl.bryansuk.foundationapi.dependencies.HardDependency;
import nl.bryansuk.foundationapi.dependencies.SoftDependency;
import nl.bryansuk.foundationapi.logging.FoundationLogger;
import nl.bryansuk.foundationapi.startup.LoadError;
import nl.bryansuk.foundationapi.startup.PluginStartupData;
import nl.bryansuk.foundationapi.startup.StartupTask;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class FoundationPlugin extends JavaPlugin {

    private static FoundationPlugin instance;
    private static FoundationLogger logger;

    private PluginStartupData startupData;

    private FileManager fileManager;
    private TextCreator textCreator;
    private ItemManager itemManager;

    private boolean dependenciesLoaded;
    private long startTime ;

    abstract List<Dependency<?>> getDependencies();
    abstract List<FoundationComponent> getComponents();
    abstract List<StartupTask> startupTasks();

    abstract void onPluginLoad();
    abstract void onPluginEnable();
    abstract void onPluginDisable();

    @Override
    public void onLoad() {
        instance = this;
        logger = new FoundationLogger(LogManager.getRootLogger(), this);

        startupData = new PluginStartupData();

        fileManager = new FileManager(this, logger.getLogger());
        textCreator = new TextCreator(this);
        itemManager = new ItemManager(this);

        onPluginLoad();
    }

    @Override
    public void onEnable() {
        startTime = System.nanoTime();

        FoundationServer.setServer(getServer());

        printStartupInfo();

        getSortedTasks().forEach(StartupTask::run);
        
        onPluginEnable();

        finishSetup();
    }

    @Override
    public void onDisable() {
        onPluginDisable();

        disableComponents();
    }

    private List<StartupTask> getSortedTasks(){
        List<StartupTask> allTasks = new ArrayList<>();
        allTasks.addAll(startupTasks());
        allTasks.addAll(getDefaultStartupTasks());

        Collections.sort(allTasks);

        return allTasks;
    }

    private List<StartupTask> getDefaultStartupTasks() {
        List<StartupTask> tasks = new ArrayList<>();

        tasks.add(new StartupTask(1,"Loading Dependencies...", "All  Dependencies have been initialized!", this::checkDependencies));
        tasks.add(new StartupTask(2, this::didDependenciesLoad));

        tasks.add(new StartupTask(10, "Loading Components...", "All Components have been initialized!", this::enableComponents));

        return tasks;
    }

    private void didDependenciesLoad(){
        if (!dependenciesLoaded && startupData.getCriticalLoadErrorsAmount() > 0){
            determineErrors(startTime);
        }
    }

    private void checkDependencies() {
        List<Dependency<?>> dependencies = getDependencies();

        int totalRequiredDependencies = dependencies.stream()
                .filter(HardDependency.class::isInstance)
                .toList().size();
        int requiredDependenciesFound = 0;

        for (Dependency<?> dependency : dependencies) {
            boolean dependencyFound = Bukkit.getPluginManager().getPlugin(dependency.getName()) != null;
            boolean isSoftDependency = dependency instanceof SoftDependency;

            if (!dependencyFound && (!isSoftDependency || startupData.getStartupShowSoftDependencyNotFound())) {
                logDependencyStatus(dependency.getName(), false);
                if (!isSoftDependency) {
                    startupData.addLoadError(new LoadError(LoadError.Level.CRITICAL, "Could not find dependency " + dependency.getName()));
                }
            } else {
                logDependencyStatus(dependency.getName(), true);
                requiredDependenciesFound++;
            }
        }

        if (requiredDependenciesFound != totalRequiredDependencies) {
            logger.startupDebug(String.format("&c%-40s&7|", "Could not find all dependencies!"));
            logger.startupDebug(String.format("&c%-40s&7|", "These plugins are required!"));
        } else {
            dependenciesLoaded = true;
        }
    }

    protected void enableComponents(){
        getComponents().forEach(FoundationComponent::start);
    }

    protected void disableComponents(){
        getComponents().forEach(FoundationComponent::start);
    }

    private void logDependencyStatus(String dependencyName, boolean found) {
        String status = found ? "&aFOUND" : "&cNOT FOUND <<---";
        logger.startupDebug(String.format("&7- &6%-39s&7 | %s", dependencyName, status));
    }

    public void printStartupInfo(){
        logger.startupDebug("&7()=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=()");
        printLogo(getLogo());
        logger.startupDebug("");
        logger.startupDebug("&6Version: " + getPluginMeta().getVersion());
        if (startupData.getStartupPromoteAuthor()) logger.startupDebug("&6Author(s): " + getPluginMeta().getAuthors());
        logger.startupDebug("&6Server Version " + getServer().getMinecraftVersion());
        logger.startupDebug("&7()=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=()");
        logger.startupDebug("");
        logger.startupDebug("&6 " + "getPluginMeta().getName()" + " &7is starting...");
    }

    private void printLogo(List<String> logo) {
        logo.forEach(s -> logger.startupDebug(s));
    }

    private List<String> getLogo(){
        return startupData.getLogo();
    }

    public void finishSetup(){
        // Footer
        logger.startupDebug(String.format("&7%-41s|", "-".repeat(41)));

        // Check if everything loaded successfully
        determineErrors(startTime);
    }

    public void determineErrors(long startTime){
        if (startupData.getCriticalLoadErrorsAmount() > 0){
            logger.startupDebug("&7");
            logger.startupDebug("&6" + getPluginMeta().getName() + " &7could not load due to " + startupData.getLoadErrors() + "&7 error(s)!");
            logger.startupDebug("");
            logger.startupDebug("&cPlease read any information shown to try to understand this error!");
            logger.startupDebug("&6" + getPluginMeta().getName() + " &7will now be disabled!");
            logger.startupDebug("&7()=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=()");
            logger.startupDebug("");
            Bukkit.getScheduler().runTaskAsynchronously(this , () -> Bukkit.getPluginManager().disablePlugin(this));
        } else {
            logger.startupDebug("&7");
            logger.startupDebug("&6" + getPluginMeta().getName() + "&7 has loaded with " + startupData.getLoadErrors() + "&7 error(s)!");
            logger.startupDebug("&7Took: " + determineTimePassed(startTime) + "ms to start plugin. &a=)");
            logger.startupDebug("&7()=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=()");
            logger.startupDebug("");
        }

    }

    private double determineTimePassed(long start) {
        return ((System.nanoTime()-start)/1e6);
    }

    public static FoundationLogger getFoundationLogger() {
        return logger;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public TextCreator getTextCreator() {
        return textCreator;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public static FoundationPlugin getInstance() {
        return instance;
    }

    public PluginStartupData getStartupData() {
        return startupData;
    }


}
