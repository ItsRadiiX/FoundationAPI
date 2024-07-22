package com.itsradiix.foundationapi.common.internalmessaging.executor;

import com.itsradiix.foundationapi.common.exceptions.InternalMessageException;
import com.itsradiix.foundationapi.common.internalmessaging.listener.InternalMessageListener;
import com.itsradiix.foundationapi.common.internalmessaging.message.InternalMessage;

public interface InternalMessageExecutor {
    void execute(InternalMessageListener listener, InternalMessage message) throws InternalMessageException;
}
