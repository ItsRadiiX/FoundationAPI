package net.astralounge.foundationapi.common.localisation.placeholders;

import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Utility for creating globally unique placeholder identifiers.
 * <p>
 * Uses convention: "{pluginNameLower}_{localIdLower}".
 * Example:
 *   PlaceholderId.namespaced(plugin, "tag") -> "ultimatechatmanager_tag"
 *   if plugin.getPluginName() == "UltimateChatManager".
 */
public final class PlaceholderId {

    private PlaceholderId() {
        // utility
    }

    /**
     * Create a namespaced placeholder ID for the given plugin and local identifier.
     *
     * @param plugin  the plugin owning the placeholder
     * @param localId short/local identifier, e.g. "tag"
     * @return globally unique ID, e.g. "ultimatechatmanager_tag"
     */
    public static @NotNull String namespaced(
            @NotNull FoundationDefaultPlugin<?> plugin,
            @NotNull String localId
    ) {
        return (plugin.getPluginName() + "_" + localId).toLowerCase();
    }
}