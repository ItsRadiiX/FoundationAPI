package net.astralounge.foundationapi.paper.menumanager.v1;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class PlayerMenuUtility {

    private final Player owner;

    private final Map<String, Object> additionalDataMap = new HashMap<>();
    private final Stack<Menu> history = new Stack<>();

    public PlayerMenuUtility(Player player) {
        this.owner = player;
    }

    public Player getOwner() {
        return owner;
    }

    public void setData(String identifier, Object data) {
        additionalDataMap.put(identifier, data);
    }

    public void setData(Enum<?> identifier, Object data) {
        additionalDataMap.put(identifier.toString(), data);
    }

    public Object getData(String identifier) {
        return additionalDataMap.get(identifier);
    }

    public Object getData(Enum<?> identifier) {
        return additionalDataMap.get(identifier.toString());
    }

    public <T> T getData(String identifier, Class<T> classRef) {
        Object object = additionalDataMap.get(identifier);

        return object == null ? null : classRef.cast(object);
    }

    public <T> T getData(Enum<?> identifier, Class<T> classRef) {
        Object object = additionalDataMap.get(identifier.toString());

        return object == null ? null : classRef.cast(object);
    }

    /**
     * Adds a menu to the navigation history.
     */
    public void pushMenu(Menu menu) {
        history.push(menu);
    }

    /**
     * Returns the previous menu and removes it from history.
     */
    public Menu popMenu() {
        if (history.isEmpty()) {
            return null;
        }

        return history.pop();
    }

    /**
     * Returns the previous menu without removing it.
     */
    public Menu peekMenu() {
        if (history.isEmpty()) {
            return null;
        }

        return history.peek();
    }

    public boolean hasPreviousMenu() {
        return !history.isEmpty();
    }

    public void clearHistory() {
        history.clear();
    }
}
