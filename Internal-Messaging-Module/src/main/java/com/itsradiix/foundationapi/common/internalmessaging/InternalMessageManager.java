package com.itsradiix.foundationapi.common.internalmessaging;

import com.itsradiix.foundationapi.common.internalmessaging.executor.InternalMessageExecutor;
import com.itsradiix.foundationapi.common.internalmessaging.listener.InternalMessageListener;
import com.itsradiix.foundationapi.common.exceptions.InternalMessageException;
import com.itsradiix.foundationapi.common.internalmessaging.listener.RegisteredInternalMessageListener;
import com.itsradiix.foundationapi.common.internalmessaging.message.InternalMessage;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class InternalMessageManager {

    private static InternalMessageManager instance;
    private final ComponentLogger logger;

    public InternalMessageManager(ComponentLogger logger) {
        if (instance != null) throw new InternalMessageException("InternalMessageManager is already instantiated");
        this.logger = logger;
        instance = this;
    }

    public void callInternalMessage(InternalMessage internalMessage) throws IllegalStateException {
        if (internalMessage.isAsynchronous()) {
            if (Thread.holdsLock(this)) {
                throw new IllegalStateException(internalMessage.getName() + " cannot be triggered asynchronously from inside synchronized code.");
            }
            fireInternalMessage(internalMessage);
        } else {
            synchronized (this) {
                fireInternalMessage(internalMessage);
            }
        }
    }

    private void fireInternalMessage(InternalMessage internalMessage) {
        InternalMessageHandler handlers = internalMessage.getHandlers();
        RegisteredInternalMessageListener[] listeners = handlers.getRegisteredInternalMessageListener();

        for (RegisteredInternalMessageListener registration : listeners) {

            try {
                registration.callInternalMessage(internalMessage);
            }  catch (Throwable ex) {
                logger.error("Could not pass internalMessage {}", internalMessage.getName());
                logger.error(ex.getMessage());
            }
        }
    }

    public void registerInternalMessage(Class<? extends InternalMessage> internalMessage,
                                        InternalMessageListener listener,
                                        InternalMessageExecutor executor) throws InternalMessageException {
        getInternalMessageListeners(internalMessage).register(new RegisteredInternalMessageListener(listener,  executor));
    }

    public Map<Class<? extends InternalMessage>, Set<RegisteredInternalMessageListener>>
    createRegisteredListeners(InternalMessageListener listener) throws InternalMessageException {

        if (listener == null){
            throw new InternalMessageException("Couldn't register Listener as it was null.");
        }

        Map<Class<? extends InternalMessage>, Set<RegisteredInternalMessageListener>> ret = new HashMap<>();
        Set<Method> methods;

        try {
            Method[] publicMethods = listener.getClass().getMethods();
            methods = new HashSet<>(publicMethods.length, Float.MAX_VALUE);

            Collections.addAll(methods, publicMethods);

            Collections.addAll(methods, listener.getClass().getDeclaredMethods());

        } catch (NoClassDefFoundError e) {
            logger.warn("Failed to register Internal Message for {} because {} does not exist.", listener.getClass(), e.getMessage());
            return ret;
        }

        for (final Method method : methods) {
            final OnInternalMessage eh =
                    method.getAnnotation(OnInternalMessage.class);
            if (eh == null) continue;
            final Class<?> checkClass;
            if (method.getParameterTypes().length != 1 || !InternalMessage.class.isAssignableFrom(checkClass = method.getParameterTypes()[0])) {
                logger.warn("Attempted to register an invalid InternalMessageHandler method signature {} in {}", method.toGenericString(), listener.getClass());
                continue;
            }
            final Class<? extends InternalMessage> internalMessageClass = checkClass.asSubclass(InternalMessage.class);
            method.setAccessible(true);
            Set<RegisteredInternalMessageListener> listenerSet = ret.computeIfAbsent(internalMessageClass, k -> new HashSet<>());

            InternalMessageExecutor executor = (tmpListener, internalMessage) -> {
                try {
                    if (!internalMessageClass.isAssignableFrom(internalMessage.getClass())) {
                        return;
                    }
                    method.invoke(tmpListener, internalMessage);
                } catch (InvocationTargetException ex) {
                    throw new InternalMessageException(ex.getCause());
                } catch (Throwable t) {
                    throw new InternalMessageException(t);
                }
            };
            listenerSet.add(new RegisteredInternalMessageListener(listener, executor));
        }
        return ret;
    }

    public void registerInternalMessages(InternalMessageListener listener) throws InternalMessageException {
        for (Map.Entry<Class<? extends InternalMessage>,
                Set<RegisteredInternalMessageListener>> entry : createRegisteredListeners(listener).entrySet()) {
            getInternalMessageListeners(getRegistrationClass(entry.getKey())).registerAll(entry.getValue());
        }
    }

    public InternalMessageHandler getInternalMessageListeners(Class<? extends InternalMessage> type)
            throws InternalMessageException {
        try {
            Method method = getRegistrationClass(type)
                    .getDeclaredMethod("getHandlersList");
            method.setAccessible(true);

            return (InternalMessageHandler) method.invoke(null);

        } catch (Exception e) {
            throw new InternalMessageException(e);
        }
    }

    private Class<? extends InternalMessage> getRegistrationClass(Class<? extends InternalMessage> clazz)
            throws InternalMessageException {
        try {
            clazz.getDeclaredMethod("getHandlers");
            return clazz;
        } catch (NoSuchMethodException e) {
            if (clazz.getSuperclass() != null
                    && !clazz.getSuperclass().equals(InternalMessage.class)
                    && InternalMessage.class.isAssignableFrom(clazz.getSuperclass())) {
                return getRegistrationClass(clazz.getSuperclass().asSubclass(InternalMessage.class));
            } else {
                throw new InternalMessageException("Unable to find handler list for internalMessage " + clazz.getName());
            }
        }
    }

    public static InternalMessageManager getInstance() {
        return instance;
    }
}
