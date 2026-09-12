// language: java
package net.astralounge.foundationapi.common.internalmessaging;

import net.astralounge.foundationapi.common.exceptions.InternalMessageException;
import net.astralounge.foundationapi.common.internalmessaging.executor.InternalMessageExecutor;
import net.astralounge.foundationapi.common.internalmessaging.listener.InternalMessageListener;
import net.astralounge.foundationapi.common.internalmessaging.listener.RegisteredInternalMessageListener;
import net.astralounge.foundationapi.common.internalmessaging.messages.InternalMessage;
import net.astralounge.foundationapi.common.logger.loggers.FoundationLogger;
import net.astralounge.foundationapi.common.logger.service.FoundationLoggerService;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public final class InternalMessageService implements InternalMessageDistributor {

    private static InternalMessageService INSTANCE;

    private final FoundationLogger logger;

    private InternalMessageService() {
        INSTANCE = this;
        this.logger = FoundationLoggerService.getGlobalLogger();
    }

    public static InternalMessageService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InternalMessageService();
        }
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // InternalMessageDistributor API
    // -------------------------------------------------------------------------

    @Override
    public void registerListener(FoundationDefaultPlugin<?> ownerPlugin,
                                 InternalMessageListener listener) {
        try {
            Map<Class<? extends InternalMessage>, Set<RegisteredInternalMessageListener>> listeners =
                    createRegisteredListeners(ownerPlugin, listener);

            for (Map.Entry<Class<? extends InternalMessage>, Set<RegisteredInternalMessageListener>> entry
                    : listeners.entrySet()) {
                getInternalMessageListeners(getRegistrationClass(entry.getKey()))
                        .registerAll(entry.getValue());
            }
        } catch (InternalMessageException e) {
            throw new RuntimeException("Failed to register internal message listener " + listener, e);
        }
    }

    @Override
    public void unregisterListener(InternalMessageListener listener) {
        InternalMessageHandler.unregisterAll(listener);
    }

    @Override
    public void callMessage(InternalMessage message) {
        callMessageToPlugins(message, null);
    }

    @Override
    public void callMessageToPlugin(InternalMessage message,
                                    FoundationDefaultPlugin<?> targetPlugin) {
        callMessageToPlugins(
                message,
                targetPlugin == null ? null : Collections.singletonList(targetPlugin)
        );
    }

    @Override
    public void callMessageToPlugins(InternalMessage message,
                                     Collection<FoundationDefaultPlugin<?>> targetPlugins) {

        if (message.isAsynchronous()) {
            if (Thread.holdsLock(this)) {
                throw new IllegalStateException(
                        message.getName()
                                + " cannot be triggered asynchronously from inside synchronized code.");
            }
            fireInternalMessage(message, targetPlugins);
        } else {
            synchronized (this) {
                fireInternalMessage(message, targetPlugins);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Core implementation
    // -------------------------------------------------------------------------

    private void fireInternalMessage(InternalMessage internalMessage,
                                     Collection<FoundationDefaultPlugin<?>> targetPlugins) {

        InternalMessageHandler handlers = internalMessage.getHandlers();
        RegisteredInternalMessageListener[] listeners = handlers.getRegisteredInternalMessageListener();

        boolean broadcast = (targetPlugins == null || targetPlugins.isEmpty());
        Set<FoundationDefaultPlugin<?>> targetSet = broadcast ? null : new HashSet<>(targetPlugins);

        for (RegisteredInternalMessageListener registration : listeners) {
            FoundationDefaultPlugin<?> owner = registration.ownerPlugin();

            if (!broadcast) {
                if (owner == null || !targetSet.contains(owner)) {
                    continue;
                }
            }

            try {
                registration.callInternalMessage(internalMessage);
            } catch (Throwable ex) {
                logger.error("Could not pass internalMessage {}", internalMessage.getName());
                logger.error(ex.getMessage());
            }
        }
    }

    private Map<Class<? extends InternalMessage>, Set<RegisteredInternalMessageListener>>
    createRegisteredListeners(FoundationDefaultPlugin<?> ownerPlugin,
                              InternalMessageListener listener) throws InternalMessageException {

        if (listener == null) {
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
            logger.warn("Failed to register Internal Message for {} because {} does not exist.",
                    listener.getClass(), e.getMessage());
            return ret;
        }

        for (final Method method : methods) {
            final OnInternalMessage eh = method.getAnnotation(OnInternalMessage.class);
            if (eh == null) continue;

            final Class<?> checkClass;
            if (method.getParameterTypes().length != 1
                    || !InternalMessage.class.isAssignableFrom(checkClass = method.getParameterTypes()[0])) {
                logger.warn("Attempted to register an invalid InternalMessageHandler method signature {} in {}",
                        method.toGenericString(), listener.getClass());
                continue;
            }

            final Class<? extends InternalMessage> internalMessageClass =
                    checkClass.asSubclass(InternalMessage.class);
            method.setAccessible(true);

            Set<RegisteredInternalMessageListener> listenerSet =
                    ret.computeIfAbsent(internalMessageClass, k -> new HashSet<>());

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

            listenerSet.add(new RegisteredInternalMessageListener(listener, executor, ownerPlugin));
        }

        return ret;
    }

    private InternalMessageHandler getInternalMessageListeners(Class<? extends InternalMessage> type)
            throws InternalMessageException {
        try {
            Method method = getRegistrationClass(type)
                    .getDeclaredMethod("getHandlerList"); // note: ensure your message classes expose this
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
}