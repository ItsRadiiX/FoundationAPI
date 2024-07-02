package nl.bryansuk.foundationapi.common.filemanager.handlers;

import nl.bryansuk.foundationapi.common.filemanager.converter.Converter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class ConfigurationHandler {
    protected final FileHandler<Map<String,Object>> configFileHandler;

    /**
     * Default constructor for Config.
     */
    public ConfigurationHandler(String path, Converter<Map<String, Object>> converter, boolean defaultResource, boolean isAutoReloading) {
        configFileHandler = new FileHandler<>(path, converter, defaultResource, isAutoReloading);
        configFileHandler.read();

        checkIfAutoReloading();
    }

    public ConfigurationHandler(String path, Converter<Map<String,Object>> converter, boolean defaultResource) {
        this(path, converter, defaultResource, false);
    }

    /**
     * Default constructor for Config.
     */
    public ConfigurationHandler(String path, Converter<Map<String,Object>> converter) {
        this(path, converter, false, false);
    }

    public String getPath(){
        return configFileHandler.getPath();
    }

    /**
     * Retrieves the value associated with the specified key from the configuration.
     *
     * @param key The key to look up in the configuration.
     * @return The value associated with the specified key.
     */
    public @Nullable Object get(String key) {
        Map<String, ?> config = getConfiguration();
        if (config == null) return null;
        return config.get(key);
    }

    /**
     * Retrieves the value associated with the specified key from the configuration.
     *
     * @param key The key to look up in the configuration.
     * @return The value associated with the specified key.
     */
    public @Nullable Object get(String key, Object defaultObject) {
        Map<String, ?> config = getConfiguration();
        if (config == null) return null;

        Object returnObject = config.get(key);
        return returnObject != null ? returnObject : defaultObject;
    }

    /**
     * Retrieves the value associated with the specified key from the configuration.
     *
     * @param key The key to look up in the configuration.
     * @return The value associated with the specified key.
     */
    public @NotNull <T> T get(String key, T defaultObject, Class<T> classType) {
        Map<String, ?> config = getConfiguration();
        if (config == null) return defaultObject;

        T returnObject = castObjectToType(defaultObject, classType);
        return returnObject != null ? returnObject : defaultObject;
    }

    private @Nullable <T> T castObjectToType(@Nullable Object object, @NotNull Class<T> type){
        try {
            return type.cast(object);
        } catch (Exception e){
            return null;
        }
    }

    /**
     * Retrieves the value associated with the specified key from the configuration.
     *
     * @param key The key to look up in the configuration.
     * @return The value associated with the specified key.
     */
    public @NotNull Object getObject(String key, @NotNull Object defaultObject) {
        Object object = get(key);

        if (object == null){
            Map<String,Object> map = configFileHandler.getObject();
            if (map != null) {
                map.put(key, defaultObject);
            }
            return defaultObject;
        }

        return object;
    }

    public @Nullable String getString(String key){
        return getText(key);
    }

    public @NotNull String getString(String key, @NotNull String defaultString){
        return getText(key, defaultString);
    }

    /**
     * Retrieves the value associated with the specified key as a String.
     *
     * @param key The key to look up in the configuration.
     * @return The String value associated with the specified key.
     */
    public @Nullable String getText(String key){
        return (get(key) instanceof String string) ? string : null;
    }

    /**
     * Retrieves the value associated with the specified key as a String.
     *
     * @param key The key to look up in the configuration.
     * @return The String value associated with the specified key.
     */
    public @NotNull String getText(String key, @NotNull String defaultValue){
        return (get(key) instanceof String string) ? string : defaultValue;
    }

    /**
     * Retrieves the value associated with the specified key as a Number.
     *
     * @param key The key to look up in the configuration.
     * @return The Number value associated with the specified key.
     */
    public @Nullable Number getNumber(String key){
        return (get(key) instanceof Number number) ? number : null;
    }

    /**
     * Retrieves the value associated with the specified key as a Number.
     *
     * @param key The key to look up in the configuration.
     * @return The Number value associated with the specified key.
     */
    public @NotNull Number getNumber(String key, @NotNull Number defaultValue){
        return (get(key) instanceof Number number) ? number : defaultValue;
    }

    /**
     * Retrieves the value associated with the specified key as a Number.
     *
     * @param key The key to look up in the configuration.
     * @return The Number value associated with the specified key.
     */
    public @Nullable Integer getInteger(String key){
        return (get(key) instanceof Integer number) ? number : null;
    }

    /**
     * Retrieves the value associated with the specified key as a Number.
     *
     * @param key The key to look up in the configuration.
     * @return The Number value associated with the specified key.
     */
    public @NotNull Integer getInteger(String key, int defaultValue){
        return (get(key) instanceof Integer number) ? number : defaultValue;
    }

    /**
     * Retrieves the value associated with the specified key as a Number.
     *
     * @param key The key to look up in the configuration.
     * @return The Number value associated with the specified key.
     */
    public @Nullable Double getDouble(String key){
        return (get(key) instanceof Double number) ? number : null;
    }

    /**
     * Retrieves the value associated with the specified key as a Number.
     *
     * @param key The key to look up in the configuration.
     * @return The Number value associated with the specified key.
     */
    public @NotNull Double getDouble(String key, double defaultValue){
        return (get(key) instanceof Double number) ? number : defaultValue;
    }

    /**
     * Retrieves the value associated with the specified key as a Number.
     *
     * @param key The key to look up in the configuration.
     * @return The Number value associated with the specified key.
     */
    public @Nullable Long getLong(String key){
        return (get(key) instanceof Long number) ? number : null;
    }

    /**
     * Retrieves the value associated with the specified key as a Number.
     *
     * @param key The key to look up in the configuration.
     * @return The Number value associated with the specified key.
     */
    public @NotNull Long getLong(String key, long defaultValue){
        return (get(key) instanceof Long number) ? number : defaultValue;
    }

    /**
     * Retrieves the value associated with the specified key as a Number.
     *
     * @param key The key to look up in the configuration.
     * @return The Number value associated with the specified key.
     */
    public @Nullable Short getShort(String key){
        return (get(key) instanceof Short number) ? number : null;
    }

    /**
     * Retrieves the value associated with the specified key as a Number.
     *
     * @param key The key to look up in the configuration.
     * @return The Number value associated with the specified key.
     */
    public @NotNull Short getShort(String key, short defaultValue){
        return (get(key) instanceof Short number) ? number : defaultValue;
    }

    /**
     * Retrieves the value associated with the specified key as a Number.
     *
     * @param key The key to look up in the configuration.
     * @return The Number value associated with the specified key.
     */
    public @Nullable Byte getByte(String key){
        return (get(key) instanceof Byte number) ? number : null;
    }

    /**
     * Retrieves the value associated with the specified key as a Number.
     *
     * @param key The key to look up in the configuration.
     * @return The Number value associated with the specified key.
     */
    public @NotNull Byte getByte(String key, byte defaultValue){
        return (get(key) instanceof Byte number) ? number : defaultValue;
    }

    /**
     * Retrieves the value associated with the specified key as a Number.
     *
     * @param key The key to look up in the configuration.
     * @return The Number value associated with the specified key.
     */
    public @Nullable Float getFloat(String key){
        return (get(key) instanceof Float number) ? number : null;
    }

    /**
     * Retrieves the value associated with the specified key as a Number.
     *
     * @param key The key to look up in the configuration.
     * @return The Number value associated with the specified key.
     */
    public @NotNull Float getFloat(String key, float defaultValue){
        return (get(key) instanceof Float number) ? number : defaultValue;
    }

    /**
     * Retrieves the value associated with the specified key as a boolean.
     *
     * @param key The key to look up in the configuration.
     * @return The boolean value associated with the specified key.
     */
    public @Nullable Boolean getBoolean(String key) {
        return (get(key) instanceof Boolean bool) ? bool : null;
    }

    /**
     * Retrieves the value associated with the specified key as a boolean.
     *
     * @param key The key to look up in the configuration.
     * @param defaultValue The default value if no key was found.
     * @return The boolean value associated with the specified key.
     */
    public @NotNull Boolean getBoolean(String key, @NotNull Boolean defaultValue) {
        return (get(key) instanceof Boolean bool) ? bool : defaultValue;
    }

    public @Nullable List<?> getList(String key){
        return (get(key) instanceof List<?> list) ? list : null;
    }

    public @NotNull List<?> getList(String key, @NotNull List<?> defaultList){
        return (get(key, defaultList) instanceof List<?> list) ? list : defaultList;
    }

    public @Nullable List<String> getStringList(String key){
        List<?> list = getList(key);
        if (list == null) return null;
        return list.stream()
                .map(String::valueOf)
                .toList();
    }

    public @NotNull List<String> getStringList(String key, @NotNull List<String> defaultValue){
        List<?> list = getList(key, defaultValue);
        return list.stream()
                .map(String::valueOf)
                .toList();
    }

    public @Nullable List<Number> getNumberList(String key){
        List<?> list = getList(key);
        if (list == null) return null;
        return list.stream()
                .filter(Number.class::isInstance)
                .map(Number.class::cast)
                .toList();
    }

    public @NotNull List<Number> getNumberList(String key, @NotNull List<Number> defaultValue   ){
        List<?> list = getList(key, defaultValue);
        return list.stream()
                .filter(Number.class::isInstance)
                .map(Number.class::cast)
                .toList();
    }

    public @Nullable Map<?, ?> getMap(String key){
        return (get(key) instanceof Map<?,?> map) ? map : null;
    }

    public @NotNull Map<?, ?> getMap(String key, @NotNull Map<?, ?> defaultMap){
        return (get(key, defaultMap) instanceof Map<?,?> map) ? map : defaultMap;
    }

    public @Nullable Map<String, ?> getSection(String key){
        Map<?, ?> map = getMap(key);
        if (map == null) return null;
        return map.entrySet().stream()
                .collect(Collectors.toMap(entry ->
                        String.valueOf(entry.getKey()),
                        Map.Entry::getValue));
    }


    public @Nullable Map<String, ?> getConfiguration(){
        return configFileHandler.getObject();
    }

    public @Nullable Map<String, String> getConfigurationAsStringMap(){
        Map<String, ?> map = getConfiguration();
        if (map == null) return null;
        return map.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry-> String.valueOf(entry.getValue())));
    }

    private void checkIfAutoReloading(){
        Boolean autoReload = getBoolean("autoReload");
        if (autoReload == null) return;
        configFileHandler.setAutoReloading(autoReload);
    }

    public FileHandler<Map<String, Object>> getConfigFileHandler() {
        return configFileHandler;
    }
}
