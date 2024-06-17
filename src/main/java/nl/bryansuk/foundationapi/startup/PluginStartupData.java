package nl.bryansuk.foundationapi.startup;

import nl.bryansuk.foundationapi.converter.YAMLConverter;
import nl.bryansuk.foundationapi.handlers.ConfigurationHandler;

import java.util.ArrayList;
import java.util.List;

public class PluginStartupData {

    private static PluginStartupData instance;

    private final ConfigurationHandler startupFile;
    private final List<LoadError> loadErrors;

    public PluginStartupData(){
        instance = this;
        startupFile = new ConfigurationHandler("configuration/startup.yml", new YAMLConverter<>(),true,true);
        loadErrors = new ArrayList<>();
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
                .filter(loadError -> loadError.getLevel().equals(level))
                .toList();
    }

    public Boolean getStartupDebug(){
        return startupFile.getBoolean("startupDebug", true);
    }

    public Boolean getStartupPromoteAuthor(){
        return startupFile.getBoolean("startupPromoteAuthor", true);
    }

    public Boolean getStartupShowSoftDependencyNotFound(){
        return startupFile.getBoolean("startupShowSoftDependencyNotFound", true);
    }

    public List<String> getLogo(){
        return startupFile.getStringList("logo", new ArrayList<>());
    }

    public static PluginStartupData getInstance() {
        return instance;
    }
}
