package nl.bryansuk.foundationapi.paper.plugin;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import nl.bryansuk.foundationapi.common.dependencies.Dependency;
import nl.bryansuk.foundationapi.common.dependencies.HardDependency;
import nl.bryansuk.foundationapi.common.dependencies.SoftDependency;
import nl.bryansuk.foundationapi.common.filemanager.FileManager;
import nl.bryansuk.foundationapi.common.filemanager.converter.YAMLConverter;
import nl.bryansuk.foundationapi.common.filemanager.handlers.ConfigurationHandler;
import nl.bryansuk.foundationapi.common.filemanager.handlers.FileHandler;
import nl.bryansuk.foundationapi.common.logging.FoundationLogger;
import nl.bryansuk.foundationapi.common.startup.LoadError;
import nl.bryansuk.foundationapi.common.startup.PluginStartupData;
import nl.bryansuk.foundationapi.common.startup.StartupTask;
import nl.bryansuk.foundationapi.common.textmanager.MessagesManager;
import nl.bryansuk.foundationapi.common.textmanager.TextCreator;
import nl.bryansuk.foundationapi.common.textmanager.languages.providers.FilesLanguageProvider;
import nl.bryansuk.foundationapi.paper.components.FoundationPaperComponent;
import nl.bryansuk.foundationapi.paper.filemanager.PaperFileManager;
import nl.bryansuk.foundationapi.paper.itemmanager.ItemManager;
import nl.bryansuk.foundationapi.paper.menumanager.MenuManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.print.Paper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public abstract class FoundationPaperPlugin extends JavaPlugin {

    private static FoundationPaperPlugin instance;
    private static FoundationLogger logger;

    private PluginStartupData startupData;
    private ConfigurationHandler foundationConfiguration;

    private FileManager fileManager;
    private MessagesManager messagesManager;
    private ItemManager itemManager;
    private MenuManager menuManager;

    private TextCreator textCreator;

    private boolean dependenciesLoaded;
    private long startTime ;


    protected abstract List<Dependency<?>> getDependencies();
    protected abstract List<FoundationPaperComponent> getComponents();
    protected abstract List<StartupTask> startupTasks();

    protected abstract void onPluginLoad();
    protected abstract void onPluginEnable();
    protected abstract void onPluginDisable();

    @Override
    public void onLoad() {
        instance = this;
        logger = new FoundationLogger(getComponentLogger());

        fileManager = new PaperFileManager(this, logger);
        foundationConfiguration = new ConfigurationHandler("configuration/main_config.yml",
                new YAMLConverter<>(),
                true,
                true);
        startupData = new PluginStartupData();

        messagesManager = new MessagesManager(logger,
                new FilesLanguageProvider("locale"),
                Locale.of(foundationConfiguration.getString("defaultLocale", Locale.ENGLISH.getLanguage())));

        onPluginLoad();
    }

    @Override
    public void onEnable() {
        startTime = System.nanoTime();

        FoundationPaperServer.setServer(getServer());

        itemManager = new ItemManager(this);
        menuManager = new MenuManager(this);

        printStartupInfo();

        getSortedTasks().forEach(StartupTask::run);
        
        onPluginEnable();

        finishSetup();
    }

    @Override
    public void onDisable() {
        onPluginDisable();

        saveAllFiles();
        disableComponents();
    }

    private void saveAllFiles() {
        FileManager.getHandlers()
                .stream()
                .filter(handler -> handler instanceof FileHandler<?>)
                .map(handler -> (FileHandler<?>) handler)
                .forEach(fileHandler -> {
                    Object o = fileHandler.getObject();
                    if (o != null) fileHandler.write(o);
                });
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
        tasks.add(new StartupTask(15, "Loading Commands...", "All Command have been initialized!", this::loadCommands));
        return tasks;
    }

    private void didDependenciesLoad(){
        if (!dependenciesLoaded && startupData.getCriticalLoadErrorsAmount() > 0){
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
        getComponents().forEach(FoundationPaperComponent::start);
    }

    protected void disableComponents(){
        getComponents().forEach(FoundationPaperComponent::stop);
    }

    private void logDependencyStatus(String dependencyName, boolean found) {
        String status = found ? "&aFOUND" : "&cNOT FOUND <<---";
        logger.startupDebug(String.format("&7- &6%-39s&7 | %s", dependencyName, status));
    }

    public void printStartupInfo(){
        logger.startupDebug("&7()=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=()");
        printLogo(getLogo());
        logger.startupDebug("");
        logger.startupDebug("Version: " + getPluginMeta().getVersion());
        if (startupData.getStartupPromoteAuthor()) logger.startupDebug("Author(s): " + getPluginMeta().getAuthors());
        logger.startupDebug("Server Version " + getServer().getMinecraftVersion());
        logger.startupDebug("&7()=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=()");
        logger.startupDebug("");
        logger.startupDebug(" " + getPluginMeta().getName() + " is starting...");
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

    public ConfigurationHandler getFoundationConfiguration() {
        return foundationConfiguration;
    }

    public MessagesManager getMessagesManager() {
        return messagesManager;
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

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public static FoundationPaperPlugin getInstance() {
        return instance;
    }

    public PluginStartupData getStartupData() {
        return startupData;
    }


}
