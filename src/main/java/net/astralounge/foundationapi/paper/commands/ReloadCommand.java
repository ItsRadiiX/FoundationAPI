package net.astralounge.foundationapi.paper.commands;

import net.astralounge.foundationapi.common.datamanagement.files.handler.abstraction.AutoReloadableHandler;
import net.astralounge.foundationapi.common.datamanagement.files.handler.abstraction.Handler;
import net.astralounge.foundationapi.common.datamanagement.files.handler.abstraction.ReloadableHandler;
import net.astralounge.foundationapi.common.datamanagement.files.handler.implementation.FolderHandler;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.function.Supplier;

public class ReloadCommand {

    private final FoundationDefaultPlugin<?> plugin;

    public ReloadCommand(FoundationDefaultPlugin<?> plugin) {
        this.plugin = plugin;

        new CommandAPICommand(plugin.getPluginName().toLowerCase())
                .withSubcommand(createReloadSubcommand())
                .withSubcommand(createAutoReloadSubcommand())
                .register();
    }

    private CommandAPICommand createReloadSubcommand() {
        return new CommandAPICommand("reload")
                .withPermission(getPermission("reload"))
                .withArguments(handlerArgument(plugin.getFileManager()::getReloadableHandlers))
                .executes((sender, args) -> {
                    Handler handler = getHandlerOrError(sender, args);
                    if (handler == null) return;

                    if (!(handler instanceof ReloadableHandler reloadableHandler)) {
                        sender.sendMessage("Handler is not reloadable!");
                        return;
                    }

                    reloadableHandler.reload();
                    String type = (handler instanceof FolderHandler<?>) ? "folder" : "file";
                    sender.sendMessage("Reloaded " + type + ": " + handler.getPath());
                });
    }

    private CommandAPICommand createAutoReloadSubcommand() {
        return new CommandAPICommand("autoreload")
                .withPermission(getPermission("autoreload"))
                .withArguments(handlerArgument(plugin.getFileManager()::getAutoReloadingHandlers))
                .withArguments(new BooleanArgument("value"))
                .executes((sender, args) -> {
                    Handler handler = getHandlerOrError(sender, args);
                    if (handler == null) return;

                    if (!(handler instanceof AutoReloadableHandler autoReloadableHandler)) {
                        sender.sendMessage("Handler is not auto-reloadable!");
                        return;
                    }

                    boolean enabled = (boolean) args.get("value");
                    autoReloadableHandler.setAutoReloadEnabled(enabled);
                    sender.sendMessage("Set auto-reload for " + handler.getPath() + " to " + enabled);
                });
    }

    private Argument<String> handlerArgument(Supplier<Collection<? extends Handler>> handlerSupplier) {
        return new TextArgument("handlers")
                .replaceSuggestions(ArgumentSuggestions.strings(
                        handlerSupplier.get().stream()
                                .map(h -> "\"" + h.getPath() + "\"")
                                .toList()));
    }

    private Handler getHandlerOrError(CommandSender sender, CommandArguments args) {
        String path = (String) args.get("handlers");
        Handler handler = plugin.getFileManager().getHandler(path);
        if (handler == null) {
            sender.sendMessage("No such handler registered!");
        }
        return handler;
    }

    private String getPermission(String sub) {
        return plugin.getPluginName().toLowerCase() + "." + sub;
    }
}
