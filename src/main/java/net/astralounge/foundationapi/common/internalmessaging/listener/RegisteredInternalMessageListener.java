package net.astralounge.foundationapi.common.internalmessaging.listener;

import net.astralounge.foundationapi.common.exceptions.InternalMessageException;
import net.astralounge.foundationapi.common.internalmessaging.executor.InternalMessageExecutor;
import net.astralounge.foundationapi.common.internalmessaging.messages.InternalMessage;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;

public record RegisteredInternalMessageListener(
        InternalMessageListener listener,
        InternalMessageExecutor executor,
        FoundationDefaultPlugin<?> ownerPlugin) {
    public void callInternalMessage(final InternalMessage internalMessage) throws InternalMessageException {
        executor.execute(listener, internalMessage);
    }
}
