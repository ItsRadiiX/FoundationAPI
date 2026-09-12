package net.astralounge.foundationapi.paper.translator;

import net.astralounge.foundationapi.common.localisation.placeholders.*;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class PlaceholderAPIHook extends PlaceholderExpansion implements Relational, Function<PlaceholderInformation, String> {

    private final FoundationDefaultPlugin<?> plugin;

    public PlaceholderAPIHook(FoundationDefaultPlugin<?> plugin) {
        this.plugin = plugin;
        this.plugin.getPlaceholderManager().addParser(this);
        register();
    }

    @Override
    public @NotNull String getIdentifier() {
        return plugin.getPluginName().toLowerCase();
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getPluginAuthor();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getPluginVersion();
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String apply(PlaceholderInformation placeholderInformation) {
        if (placeholderInformation.playerOne() != null) {
            if (placeholderInformation.playerTwo() != null) {
                // We have 2 players
                return PlaceholderAPI.setRelationalPlaceholders(Bukkit.getPlayer(placeholderInformation.playerOne()),
                        Bukkit.getPlayer(placeholderInformation.playerTwo()),
                        placeholderInformation.payload());

            } else
                // We have at least one player
                return PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(placeholderInformation.playerOne()),
                        placeholderInformation.payload());
        }

        // We have no Player
        return PlaceholderAPI.setPlaceholders(null, placeholderInformation.payload());
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        // Map %pluginid_params% to the namespaced internal id,
        // so %ultimatechatmanager_tag% -> internal "ultimatechatmanager_tag".
        String internalId = PlaceholderId.namespaced(plugin, params);

        // PlaceholderAPI checks per parameter, we can send null if we don't know the result
        if (player != null) {
            FoundationComplexPlaceholder complexPlaceholder = plugin.getPlaceholderManager().getComplexPlaceholder(internalId);
            if(complexPlaceholder != null) {
                return complexPlaceholder.resolveData(player.getUniqueId());
            }
        }

        FoundationPlaceholder placeholder = plugin.getPlaceholderManager().getPlaceholder(internalId);

        if(placeholder != null) {
            return placeholder.resolveStatic();
        }

        return null;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        return onRequest(player, params);
    }

    @Override
    public String onPlaceholderRequest(Player one, Player two, String identifier) {
        // Map %pluginid_params% to the namespaced internal id,
        // so %ultimatechatmanager_tag% -> internal "ultimatechatmanager_tag".
        String internalId = PlaceholderId.namespaced(plugin, identifier);

        FoundationComplexRelationalPlaceholder complexPlaceholder =
                plugin.getPlaceholderManager().getComplexRelationalPlaceholder(internalId);

        if(complexPlaceholder != null) {
            return complexPlaceholder.resolveData(one.getUniqueId(), two.getUniqueId());
        }
        return null;
    }
}
