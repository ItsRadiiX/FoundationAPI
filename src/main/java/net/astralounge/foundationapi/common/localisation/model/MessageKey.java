package net.astralounge.foundationapi.common.localisation.model;

import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Typed ID for messages so multiple plugins can safely share the same key.
 * Name convention: "pluginId:key", e.g. "foundation:generic.no-permission".
 */
public final class MessageKey {

    private final String id;

    private MessageKey(@NotNull String id) {
        this.id = id;
    }

    /**
     * Create a namespaced key using a plugin and a value key string.
     * Example:
     *   MessageKey.of(plugin, "generic.no-permission")
     * results in id = "ultimatechatmanager:generic.no-permission"
     * if plugin.getPluginName() == "ultimatechatmanager".
     */
    public static @NotNull MessageKey of(
            @NotNull FoundationDefaultPlugin<?> plugin,
            @NotNull String valueKey
    ) {
        String pluginId = plugin.getPluginName();
        return new MessageKey(pluginId + ":" + valueKey);
    }

    /**
     * Create a key from a raw ID string, e.g. "foundation:generic.no-permission".
     * Use this for messages that are truly global across plugins.
     */
    public static @NotNull MessageKey of(@NotNull String id) {
        return new MessageKey(id);
    }

    public @NotNull String id() {
        return id;
    }

    @Override
    public String toString() {
        return "MessageKey[" + id + ']';
    }
}
