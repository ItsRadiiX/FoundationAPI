package net.astralounge.foundationapi.common.internalmessaging;

import net.astralounge.foundationapi.common.internalmessaging.listener.InternalMessageListener;
import net.astralounge.foundationapi.common.internalmessaging.messages.InternalMessage;
import net.astralounge.foundationapi.common.manager.CommonManager;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;

import java.util.Collection;
import java.util.Collections;

public class InternalMessageManager extends CommonManager {

    private final InternalMessageDistributor service;

    public InternalMessageManager(FoundationDefaultPlugin<?> plugin) {
        super(plugin);
        // All managers use the same global service instance
        this.service = InternalMessageService.getInstance();
    }

    @Override
    public void onLoad() {
        // nothing special – service is global and not lifecycle-bound to this manager
    }

    @Override
    public void onEnable() {
        // optional: register default listeners for this plugin here
    }

    @Override
    public void onDisable() {
        // optional: unregister this plugin's listeners if you track them
    }

    @Override
    public Collection<Class<? extends CommonManager>> getCommonDependencies() {
        return Collections.emptyList();
    }

    // -------------------------------------------------------------------------
    // Convenience API for this plugin, delegating to the global service
    // -------------------------------------------------------------------------

    public void registerListener(InternalMessageListener listener) {
        service.registerListener(getPlugin(), listener);
    }

    public void unregisterListener(InternalMessageListener listener) {
        service.unregisterListener(listener);
    }

    public void callMessage(InternalMessage message) {
        service.callMessage(message);
    }

    public void callMessageToPlugin(InternalMessage message, FoundationDefaultPlugin<?> targetPlugin) {
        service.callMessageToPlugin(message, targetPlugin);
    }

    public void callMessageToPlugins(InternalMessage message,
                                     Collection<FoundationDefaultPlugin<?>> targetPlugins) {
        service.callMessageToPlugins(message, targetPlugins);
    }
}