package com.itsradiix.foundationapi.paper.plugin;

import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class FoundationPaperServer {
    private static Server server;

    private FoundationPaperServer() {}

    public static List<String> getOnlinePlayerNames(){
        return getServer().getOnlinePlayers().stream().map(Player::getName).toList();
    }

    public static List<Component> getOnlinePlayerDisplayNames(){
        return getServer().getOnlinePlayers().stream().map(Player::displayName).toList();
    }

    public static List<String> getTabCompletionPlayerNames(CommandSender sender, String[] args, boolean filterSender) {
        List<String> players = new ArrayList<>();
        if (args.length >= 1) {
            for (String s : getOnlinePlayerNames()) {
                if (s.toLowerCase().contains(args[0].toLowerCase())) {
                    players.add(s);
                }
            }
            if (filterSender && sender instanceof Player player) {
                players.remove(player.getName());
            }
        }
        return players;
    }

    public static void setServer(Server server) {
        FoundationPaperServer.server = server;
    }

    public static Server getServer() {
        return server;
    }
}
