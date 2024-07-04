package nl.bryansuk.foundationapi.common.internalmessaging;

import nl.bryansuk.foundationapi.common.internalmessaging.listener.InternalMessageListener;
import nl.bryansuk.foundationapi.common.internalmessaging.listener.RegisteredInternalMessageListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InternalMessageHandler {

    private static final List<InternalMessageHandler> allHandlers = new ArrayList<>();

    private final List<RegisteredInternalMessageListener> listToRegister = new ArrayList<>();
    private volatile RegisteredInternalMessageListener[] registeredListeners = null;

    public InternalMessageHandler() {
        synchronized (allHandlers) {
            allHandlers.add(this);
        }
    }

    public synchronized void bake() {
        if (registeredListeners != null) return; // Do not re-bake when we are still valid
        registeredListeners = listToRegister.toArray(new RegisteredInternalMessageListener[0]);
    }

    public static void bakeAll(){
        synchronized (allHandlers) {
            for (InternalMessageHandler handler : allHandlers) {
                handler.bake();
            }
        }
    }

    public synchronized void register(RegisteredInternalMessageListener listener) {
        if (listToRegister.contains(listener)) throw new IllegalStateException("This listener is already registered");
        registeredListeners = null;
        listToRegister.add(listener);
    }

    public void registerAll(Collection<RegisteredInternalMessageListener> listeners) {
        for (RegisteredInternalMessageListener listener : listeners) {
            register(listener);
        }
    }

    public synchronized void unregister(RegisteredInternalMessageListener listener) {
        if (listToRegister.remove(listener)) {
            registeredListeners = null;
        }
    }

    public synchronized void unregister(InternalMessageListener listener) {
        boolean found = false;
        for (RegisteredInternalMessageListener list : listToRegister) {
            if (list.listener().equals(listener)) {
                found = true;
                listToRegister.remove(list);
                break;
            }
        }
        if (found) registeredListeners = null;
    }

    public static void unregisterAll() {
        synchronized (allHandlers) {
            for (InternalMessageHandler handler : allHandlers) {
                synchronized (handler) {
                    handler.listToRegister.clear();
                    handler.registeredListeners = null;
                }
            }
        }
    }

    public static void unregisterAll(InternalMessageListener listener) {
        synchronized (allHandlers) {
            for (InternalMessageHandler h : allHandlers) {
                h.unregister(listener);
            }
        }
    }

    public RegisteredInternalMessageListener[] getRegisteredInternalMessageListener() {
        RegisteredInternalMessageListener[] handlers;
        while ((handlers = this.registeredListeners) == null) bake(); // This prevents fringe cases of returning null
        return handlers;
    }

    public static List<InternalMessageHandler> getHandlerLists() {
        synchronized (allHandlers) {
            return allHandlers.stream().toList();
        }
    }

}
