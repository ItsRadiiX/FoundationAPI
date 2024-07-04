package nl.bryansuk.foundationapi.common.playerinfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import nl.bryansuk.foundationapi.common.playerinfo.models.PlayerDataModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("unused")
public class PlayerInfo {

    private static final Set<PlayerDataModel> defaultPlayerDataModel = new HashSet<>();

    private final UUID uuid;
    private final Set<PlayerDataModel> data;

    public PlayerInfo(@JsonProperty("uuid") UUID uuid) {
        this.uuid = uuid;
        this.data = new HashSet<>(defaultPlayerDataModel);
    }

    public PlayerInfo(@JsonProperty("uuid") UUID uuid, @JsonProperty("data") Set<PlayerDataModel> data) {
        this.uuid = uuid;
        this.data = data;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Set<PlayerDataModel> getDataSet() {
        return data;
    }

    public void setData(PlayerDataModel object){
        data.add(object);
    }

    public @Nullable <T extends PlayerDataModel> T getData(Class<T> classType){
        Optional<PlayerDataModel> returnObject = data.stream().filter(classType::isInstance).findFirst();
        return returnObject.map(classType::cast).orElse(null);
    }

    public @NotNull <T extends PlayerDataModel> T getDataOrDefault(T defaultValue, Class<T> classType){
        Optional<PlayerDataModel> returnObject = data.stream().filter(classType::isInstance).findFirst();

        if (returnObject.isEmpty()){
            data.add(defaultValue);
            return defaultValue;
        } else {
            return classType.cast(returnObject.get());
        }
    }

    private @Nullable <T> T castObjectToType(Object object, Class<T> classType){
        try {
            return classType.cast(object);
        } catch (Exception e){
            return null;
        }
    }

    public static Set<PlayerDataModel> getDefaultPlayerDataModel() {
        return defaultPlayerDataModel;
    }

    public static void addDefaultPlayerDataModel(PlayerDataModel defaultPlayerDataModel) {
        PlayerInfo.defaultPlayerDataModel.add(defaultPlayerDataModel);
    }

    public static void setDefaultPlayerDataModel(Set<PlayerDataModel> defaultPlayerDataModel) {
        PlayerInfo.defaultPlayerDataModel.clear();
        PlayerInfo.defaultPlayerDataModel.addAll(defaultPlayerDataModel);
    }
}
