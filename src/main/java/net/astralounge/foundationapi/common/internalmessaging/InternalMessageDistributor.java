// language: java
package net.astralounge.foundationapi.common.internalmessaging;

import net.astralounge.foundationapi.common.internalmessaging.listener.InternalMessageListener;
import net.astralounge.foundationapi.common.internalmessaging.messages.InternalMessage;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;

import java.util.Collection;

public interface InternalMessageDistributor {

    /**
     * Register all @OnInternalMessage methods on this listener for the given owner plugin.
     */
    void registerListener(FoundationDefaultPlugin<?> ownerPlugin,
                          InternalMessageListener listener);

    /**
     * Unregister all handlers for this listener across all messages.
     */
    void unregisterListener(InternalMessageListener listener);

    /**
     * Broadcast to all plugins.
     */
    void callMessage(InternalMessage message);

    /**
     * Send only to a single target plugin.
     */
    void callMessageToPlugin(InternalMessage message,
                             FoundationDefaultPlugin<?> targetPlugin);

    /**
     * Send only to a subset of plugins.
     */
    void callMessageToPlugins(InternalMessage message,
                              Collection<FoundationDefaultPlugin<?>> targetPlugins);
}