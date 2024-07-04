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
import nl.bryansuk.foundationapi.common.internalmessaging.InternalMessageManager;
import nl.bryansuk.foundationapi.common.logging.FoundationLogger;
import nl.bryansuk.foundationapi.common.playerinfo.PlayerInfoManager;
import nl.bryansuk.foundationapi.common.playerinfo.providers.PlayerInfoFileProvider;
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
import nl.bryansuk.foundationapi.paper.playerinfo.PlayerInfoListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@SuppressWarnings({"unused", "UnstableApiUsage"})
public abstract class FoundationPaperPlugin extends JavaPlugin {

    private static FoundationPaperPlugin instance;
    private static FoundationLogger logger;

    private PluginStartupData startupData;
    private ConfigurationHandler foundationConfiguration;

    private FileManager fileManager;
    private MessagesManager messagesManager;
    private InternalMessageManager internalMessageManager;
    private ItemManager itemManager;
    private MenuManager menuManager;
    private PlayerInfoManager playerInfoManager;

    private TextCreator textCreator;

    private boolean dependenciesLoaded;
    private long startTime ;


    protected abstract List<Dependency<?>> getDependencies();
    protected abstract List<FoundationPaperComponent> getComponents();
    protected abstract List<StartupTask> startupTasks();

    protected abstract void onPluginLoad() throws Throwable;
    protected abstract void onPluginEnable() throws Throwable;
    protected abstract void onPluginDisable() throws Throwable;

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
        logger.setStartupData(startupData);

        internalMessageManager = new InternalMessageManager(logger);

        messagesManager = new MessagesManager(logger,
                new FilesLanguageProvider("locale"),
                Locale.of(foundationConfiguration.getString("defaultLocale", Locale.ENGLISH.getLanguage())));

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

        itemManager = new ItemManager(this);
        menuManager = new MenuManager(this);

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

        fileManager.shutdown();
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

        tasks.add(new StartupTask(1,"Loading Dependencies...",
                "All Dependencies have been initialized!", this::checkDependencies));
        tasks.add(new StartupTask(2, this::didDependenciesLoad));

        tasks.add(new StartupTask(10, "Loading Components...",
                "All Components have been initialized!", this::enableComponents));
        tasks.add(new StartupTask(15, "Loading Commands...",
                "All Command have been initialized!", this::loadCommands));
        tasks.add(new StartupTask(20, "Loading PlayerInfo...",
                "All PlayerInfo have been loaded!",
                () -> {
                    playerInfoManager = new PlayerInfoManager(new PlayerInfoFileProvider("players"));
                    getServer().getPluginManager().registerEvents(new PlayerInfoListener(), this);
                }));
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
                    startupData.addLoadError(new LoadError(LoadError.Level.CRITICAL,
                            "Could not find dependency " + dependency.getName()));
                }
            } else {
                logDependencyStatus(dependency.getName(), true);
                requiredDependenciesFound++;
            }
        }

        if (requiredDependenciesFound != totalRequiredDependencies) {
            logger.startupDebug(String.format("<red>%-40s&7|", "Could not find all dependencies!"));
            logger.startupDebug(String.format("<red>%-40s&7|", "These plugins are required!"));
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
        logger.startupDebug(String.format("<gray>- <gold>%-39s<gray> | %s", dependencyName, status));
    }

    public void printStartupInfo(){
        logger.startupDebug("<gray>()=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=()");
        printLogo(getLogo());
        logger.startupDebug("");
        logger.startupDebug("Version: " + getPluginMeta().getVersion());
        if (startupData.getStartupPromoteAuthor()) logger.startupDebug("Author(s): " + getPluginMeta().getAuthors());
        logger.startupDebug("Server Version " + getServer().getMinecraftVersion());
        logger.startupDebug("<gray>()=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=()");
        logger.startupDebug("");
        logger.startupDebug("<gold>" + getPluginMeta().getName() + "<gray> is starting...");
    }

    private void printLogo(List<String> logo) {
        logo.forEach(s -> logger.startupDebug(s));
    }

    private List<String> getLogo(){
        return startupData.getLogo();
    }

    public void finishSetup(){
        // Footer
        logger.startupDebug(String.format("<gray>%-41s|", "-".repeat(41)));

        // Check if everything loaded successfully
        determineErrors(startTime);
    }

    public void determineErrors(long startTime){
        if (startupData.getCriticalLoadErrorsAmount() > 0){
            logger.startupDebug("");
            logger.startupDebug("<gold>" + getPluginMeta().getName() + "<gray> could not load due to <red>" + startupData.getLoadErrors() + "<gray> error(s)!");
            logger.startupDebug("");
            logger.startupDebug("<red>Please read any information shown to try to understand this error!");
            logger.startupDebug("<gold>>" + getPluginMeta().getName() + "<gray> will now be disabled!");
            logger.startupDebug("<gray>()=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=()");
            logger.startupDebug("");
            Bukkit.getScheduler().runTaskAsynchronously(this , () -> Bukkit.getPluginManager().disablePlugin(this));
        } else {
            logger.startupDebug("<gray>");
            logger.startupDebug("<gold>" + getPluginMeta().getName() + "<gray> has loaded with " + startupData.getLoadErrors() + "<gray> error(s)!");
            logger.startupDebug("<gray>Took: " + determineTimePassed(startTime) + "ms to start plugin. <green>=)");
            logger.startupDebug("<gray>()=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=()");
            logger.startupDebug("");
        }

    }

    private double determineTimePassed(long start) {
        return ((System.nanoTime()-start)/1e6);
    }

    public InternalMessageManager getInternalMessageManager() {
        return internalMessageManager;
    }

    public PlayerInfoManager getPlayerInfoManager() {
        return playerInfoManager;
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
