package net.astralounge.foundationapi.common.internalmessaging.executor;

import net.astralounge.foundationapi.common.exceptions.InternalMessageException;
import net.astralounge.foundationapi.common.internalmessaging.listener.InternalMessageListener;
import net.astralounge.foundationapi.common.internalmessaging.messages.InternalMessage;

public interface InternalMessageExecutor {
    void execute(InternalMessageListener listener, InternalMessage message) throws InternalMessageException;
}
