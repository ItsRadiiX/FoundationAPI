package nl.bryansuk.foundationapi.common.playerinfo;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerInfo {
    private final UUID uuid;
    private final Map<String, Object> data;

    public PlayerInfo(UUID uuid) {
        this.uuid = uuid;
        this.data = new HashMap<>();
    }

    public PlayerInfo(UUID uuid, Map<String, Object> data) {
        this.uuid = uuid;
        this.data = data;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Map<String, Object> getDataMap() {
        return data;
    }

    public void setData(String key, Object object){
        data.put(key, object);
    }

    public @Nullable <T> T getData(String key, Class<T> classType){
        return castObjectToType(data.get(key), classType);
    }

    public @NotNull <T> T getData(String key, T defaultValue, Class<T> classType){
        return getDataOrDefault(key, defaultValue, classType);
    }

    public @NotNull <T> T getDataOrDefault(String key, T defaultValue, Class<T> classType){
        Object object = data.get(key);
        if (object == null) return defaultValue;

        T returnObject = castObjectToType(data.get(key), classType);
        return returnObject != null ? returnObject : defaultValue;
    }

    private @Nullable <T> T castObjectToType(Object object, Class<T> classType){
        try {
            return classType.cast(object);
        } catch (Exception e){
            return null;
        }
    }

}
