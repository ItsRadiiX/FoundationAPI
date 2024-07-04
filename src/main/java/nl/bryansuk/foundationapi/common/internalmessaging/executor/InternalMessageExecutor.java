package nl.bryansuk.foundationapi.common.internalmessaging.executor;

import nl.bryansuk.foundationapi.common.exceptions.InternalMessageException;
import nl.bryansuk.foundationapi.common.internalmessaging.listener.InternalMessageListener;
import nl.bryansuk.foundationapi.common.internalmessaging.message.InternalMessage;

public interface InternalMessageExecutor {
    void execute(InternalMessageListener listener, InternalMessage message) throws InternalMessageException;
}
