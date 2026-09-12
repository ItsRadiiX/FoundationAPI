package net.astralounge.foundationapi.paper.plugin;

import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public record FoundationPaperServer(Server server) {

    public List<String> getOnlinePlayerNames() {
        return server().getOnlinePlayers().stream().map(Player::getName).toList();
    }

    public List<Component> getOnlinePlayerDisplayNames() {
        return server().getOnlinePlayers().stream().map(Player::displayName).toList();
    }

    public List<String> getTabCompletionPlayerNames(CommandSender sender, String[] args, boolean filterSender) {
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
}
