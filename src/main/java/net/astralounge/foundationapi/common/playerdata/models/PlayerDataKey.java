package net.astralounge.foundationapi.common.playerdata.models;

import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Typed key for per-player plugin data stored in {@link FoundationPlayer}.
 * Name convention: "pluginId:key", e.g. "ultimatechatmanager:tagProfile".
 */
public final class PlayerDataKey<T> {

    private final String id;
    private final Class<T> type;

    private PlayerDataKey(@NotNull String id, @NotNull Class<T> type) {
        this.id = id;
        this.type = type;
    }

    /**
     * Create a key using a plugin and a value key string.
     * <p>
     * Example:
     *   PlayerDataKey.of(plugin, "PlayerTagProfile", PlayerTagProfile.class)
     * results in id = "ultimatechatmanager:PlayerTagProfile"
     * if plugin.getPluginName() == "ultimatechatmanager".
     */
    public static <T> PlayerDataKey<T> of(
            @NotNull FoundationDefaultPlugin<?> plugin,
            @NotNull String valueKey,
            @NotNull Class<T> type) {
        String pluginId = plugin.getPluginName(); // already exposed in your base plugin
        String id = pluginId + ":" + valueKey;
        return new PlayerDataKey<>(id, type);
    }

    /**
     * Create a key from a raw ID string, e.g. "ultimatechatmanager:PlayerTagProfile".
     */
    public static <T> PlayerDataKey<T> of(
            @NotNull String id,
            @NotNull Class<T> type) {
        return new PlayerDataKey<>(id, type);
    }

    public String id() {
        return id;
    }

    public Class<T> type() {
        return type;
    }

    @Override
    public String toString() {
        return "PlayerDataKey[" + id + ", type=" + type.getSimpleName() + ']';
    }
}
