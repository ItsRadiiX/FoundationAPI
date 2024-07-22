package com.itsradiix.foundationapi.common.internalmessaging.listener;

import com.itsradiix.foundationapi.common.exceptions.InternalMessageException;
import com.itsradiix.foundationapi.common.internalmessaging.executor.InternalMessageExecutor;
import com.itsradiix.foundationapi.common.internalmessaging.message.InternalMessage;

public record RegisteredInternalMessageListener(InternalMessageListener listener, InternalMessageExecutor executor) {
    public void callInternalMessage(final InternalMessage internalMessage) throws InternalMessageException {
        executor.execute(listener, internalMessage);
    }
}
