package com.itsradiix.foundationapi.paper.plugin;

import com.itsradiix.foundationapi.common.datamanagement.files.FileManager;
import com.itsradiix.foundationapi.common.datamanagement.files.handlers.ConfigurationHandler;
import com.itsradiix.foundationapi.common.dependencies.Dependency;
import com.itsradiix.foundationapi.common.dependencies.HardDependency;
import com.itsradiix.foundationapi.common.dependencies.SoftDependency;
import com.itsradiix.foundationapi.common.manager.CommonManager;
import com.itsradiix.foundationapi.common.startup.LoadError;
import com.itsradiix.foundationapi.common.startup.StartupDataManager;
import com.itsradiix.foundationapi.common.startup.StartupTask;
import com.itsradiix.foundationapi.common.textmanager.TextCreator;
import com.itsradiix.foundationapi.paper.filemanager.PaperFileManager;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unused", "UnstableApiUsage"})
public abstract class FoundationPaperPlugin extends JavaPlugin {

    private static FoundationPaperPlugin instance;

    protected FileManager fileManager;
    protected StartupDataManager startupDataManager;
    protected ConfigurationHandler foundationConfiguration;

    protected boolean dependenciesLoaded;
    protected long startTime ;

    protected abstract List<Dependency<?>> getDependencies();
    protected abstract List<CommonManager> getManagers();
    protected abstract List<FoundationPaperComponent> getComponents();
    protected abstract List<StartupTask> startupTasks();

    protected abstract void onPluginLoad() throws Throwable;
    protected abstract void onPluginEnable() throws Throwable;
    protected abstract void onPluginDisable() throws Throwable;

    @Override
    public void onLoad() {
        instance = this;

        getSortedCommonManager().forEach(CommonManager::onLoad);

        try {
            onPluginLoad();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onEnable() {
        startTime = System.nanoTime();

        FoundationPaperServer.setServer(getServer());

        getSortedCommonManager().forEach(CommonManager::onEnable);

        printStartupInfo();

        getSortedTasks().forEach(StartupTask::run);

        try {
            onPluginEnable();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        finishSetup();
    }

    @Override
    public void onDisable() {
        try {
            onPluginDisable();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        disableComponents();
        getSortedCommonManager().forEach(CommonManager::onDisable);
    }

    private List<CommonManager> sortedCommonManagers;
    private List<CommonManager> getSortedCommonManager(){
        if(sortedCommonManagers == null) {
            sortedCommonManagers = new ArrayList<>();

            fileManager = new PaperFileManager(this);
            startupDataManager = new StartupDataManager(getComponentLogger());

            sortedCommonManagers.add(fileManager);
            sortedCommonManagers.add(startupDataManager);
            sortedCommonManagers.addAll(getManagers());
        }
        return sortedCommonManagers;
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

        tasks.add(new StartupTask(1,"Loading Dependencies...",
                "All Dependencies have been initialized!", this::checkDependencies));
        tasks.add(new StartupTask(2, this::didDependenciesLoad));

        tasks.add(new StartupTask(10, "Loading Components...",
                "All Components have been initialized!", this::enableComponents));
        tasks.add(new StartupTask(15, "Loading Commands...",
                "All Command have been initialized!", this::loadCommands));
        return tasks;
    }

    private void didDependenciesLoad(){
        if (!dependenciesLoaded && startupDataManager.getCriticalLoadErrorsAmount() > 0){
            determineErrors(startTime);
        }
    }

    private void loadCommands(){
        LifecycleEventManager<Plugin> manager = this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            for (FoundationPaperComponent component : getComponents()) {
                component.registerCommands(commands);
            }
        });
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

            if (!dependencyFound && (!isSoftDependency || startupDataManager.getStartupShowSoftDependencyNotFound())) {
                logDependencyStatus(dependency.getName(), false);
                if (!isSoftDependency) {
                    startupDataManager.addLoadError(new LoadError(LoadError.Level.CRITICAL,
                            "Could not find dependency " + dependency.getName()));
                }
            } else {
                logDependencyStatus(dependency.getName(), true);
                requiredDependenciesFound++;
            }
        }

        if (requiredDependenciesFound != totalRequiredDependencies) {
            startupDebug(String.format("<red>%-40s&7|", "Could not find all dependencies!"));
            startupDebug(String.format("<red>%-40s&7|", "These plugins are required!"));
        } else {
            dependenciesLoaded = true;
        }
    }

    protected void enableComponents(){
        getComponents().forEach(FoundationPaperComponent::start);
    }

    protected void disableComponents(){
        getComponents().forEach(FoundationPaperComponent::stop);
    }

    private void logDependencyStatus(String dependencyName, boolean found) {
        String status = found ? "<green>FOUND" : "<red>NOT FOUND <<---";
        startupDebug(String.format("<gray>- <gold>%-39s<gray> | %s", dependencyName, status));
    }

    public void printStartupInfo(){
        startupDebug("<gray>()=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=()");
        printLogo(getLogo());
        startupDebug("");
        startupDebug("Version: " + getPluginMeta().getVersion());
        if (startupDataManager.getStartupPromoteAuthor()) startupDebug("Author(s): " + getPluginMeta().getAuthors());
        startupDebug("Server Version " + getServer().getMinecraftVersion());
        startupDebug("<gray>()=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=()");
        startupDebug("");
        startupDebug("<gold>" + getPluginMeta().getName() + "<gray> is starting...");
    }

    private void printLogo(List<String> logo) {
        logo.forEach(this::startupDebug);
    }

    private List<String> getLogo(){
        return startupDataManager.getLogo();
    }

    public void finishSetup(){
        // Footer
        startupDebug(String.format("<gray>%-41s|", "-".repeat(41)));

        // Check if everything loaded successfully
        determineErrors(startTime);
    }

    public void determineErrors(long startTime){
        if (startupDataManager.getCriticalLoadErrorsAmount() > 0){
            startupDebug("");
            startupDebug("<gold>" + getPluginMeta().getName() + "<gray> could not load due to <red>" + startupDataManager.getLoadErrors() + "<gray> error(s)!");
            startupDebug("");
            startupDebug("<red>Please read any information shown to try to understand this error!");
            startupDebug("<gold>>" + getPluginMeta().getName() + "<gray> will now be disabled!");
            startupDebug("<gray>()=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=()");
            startupDebug("");
            Bukkit.getScheduler().runTaskAsynchronously(this , () -> Bukkit.getPluginManager().disablePlugin(this));
        } else {
            startupDebug("<gray>");
            startupDebug("<gold>" + getPluginMeta().getName() + "<gray> has loaded with " + startupDataManager.getLoadErrors() + "<gray> error(s)!");
            startupDebug("<gray>Took: " + determineTimePassed(startTime) + "ms to start plugin. <green>=)");
            startupDebug("<gray>()=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=()");
            startupDebug("");
        }

    }

    protected void startupDebug(String message){
        if(startupDataManager.getStartupDebug()) getComponentLogger().info(TextCreator.create(message));
    }

    private double determineTimePassed(long start) {
        return ((System.nanoTime()-start)/1e6);
    }

    public static FoundationPaperPlugin getInstance() {
        return instance;
    }
}
