package com.itsradiix.foundationapi.common.startup;

import com.itsradiix.foundationapi.common.datamanagement.files.converter.YAMLConverter;
import com.itsradiix.foundationapi.common.datamanagement.files.handlers.ConfigurationHandler;
import com.itsradiix.foundationapi.common.manager.CommonManager;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

import java.util.ArrayList;
import java.util.List;

public class StartupDataManager implements CommonManager {

    private static StartupDataManager instance;

    private ConfigurationHandler startupFile;
    private final List<LoadError> loadErrors;
    private final ComponentLogger logger;

    public StartupDataManager(ComponentLogger logger){
        instance = this;
        this.logger = logger;
        loadErrors = new ArrayList<>();
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    public void addLoadError(LoadError error){
        loadErrors.add(error);
    }

    public int getLoadErrors(){
        return loadErrors.size();
    }

    public int getRiskLoadErrorsAmount(){
        return getLoadErrors(LoadError.Level.RISK).size();
    }

    public int getFatalLoadErrorsAmount(){
        return getLoadErrors(LoadError.Level.FATAL).size();
    }

    public int getCriticalLoadErrorsAmount(){
        return getLoadErrors(LoadError.Level.CRITICAL).size();
    }

    public List<LoadError> getLoadErrors(LoadError.Level level){
        return loadErrors.stream()
                .filter(loadError -> loadError.level().equals(level))
                .toList();
    }

    public Boolean getStartupDebug(){
        return getConfigurationHandler().getBoolean("startupDebug", true);
    }

    public Boolean getStartupPromoteAuthor(){
        return getConfigurationHandler().getBoolean("startupPromoteAuthor", true);
    }

    public Boolean getStartupShowSoftDependencyNotFound(){
        return getConfigurationHandler().getBoolean("startupShowSoftDependencyNotFound", true);
    }

    public List<String> getLogo(){
        return getConfigurationHandler().getStringList("logo", new ArrayList<>());
    }

    private ConfigurationHandler getConfigurationHandler(){
        if (startupFile == null){
            startupFile = new ConfigurationHandler(
                    "configuration/startup.yml",
                    new YAMLConverter<>(),
                    true,
                    true);
        }
        return startupFile;
    }

    public static StartupDataManager getInstance() {
        return instance;
    }

    public ComponentLogger getLogger() {
        return logger;
    }
}
