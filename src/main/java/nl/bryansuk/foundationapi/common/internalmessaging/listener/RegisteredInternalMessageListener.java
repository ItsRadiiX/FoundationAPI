package nl.bryansuk.foundationapi.common.internalmessaging.listener;

import nl.bryansuk.foundationapi.common.exceptions.InternalMessageException;
import nl.bryansuk.foundationapi.common.internalmessaging.executor.InternalMessageExecutor;
import nl.bryansuk.foundationapi.common.internalmessaging.message.InternalMessage;

public record RegisteredInternalMessageListener(InternalMessageListener listener, InternalMessageExecutor executor) {
    public void callInternalMessage(final InternalMessage internalMessage) throws InternalMessageException {
        executor.execute(listener, internalMessage);
    }
}
