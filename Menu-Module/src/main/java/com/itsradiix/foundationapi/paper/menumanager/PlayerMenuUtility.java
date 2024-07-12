package com.itsradiix.foundationapi.paper.menumanager;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class PlayerMenuUtility {

    private final Player owner;
    private final Map<String, Object> dataMap = new HashMap<>();
    private final Stack<Menu> history = new Stack<>();

    public PlayerMenuUtility(Player p) {
        this.owner = p;
    }

    public Player getOwner() {
        return owner;
    }

    /**
     * @param identifier A key to store the data by
     * @param data       The data itself to be stored
     */
    public void setData(String identifier, Object data) {
        this.dataMap.put(identifier, data);
    }

    public void setData(@SuppressWarnings("rawtypes") Enum identifier, Object data) {
        this.dataMap.put(identifier.toString(), data);
    }

    /**
     * @param identifier The key for the data stored in the PMC
     * @return The retrieved value or null if not found
     */
    public Object getData(String identifier) {
        return this.dataMap.get(identifier);
    }

    public Object getData(@SuppressWarnings("rawtypes") Enum identifier) {
        return this.dataMap.get(identifier.toString());
    }

    public <T> T getData(String identifier, Class<T> classRef) {

        Object obj = this.dataMap.get(identifier);

        if (obj == null) {
            return null;
        } else {
            return classRef.cast(obj);
        }
    }

    public <T> T getData(@SuppressWarnings("rawtypes") Enum identifier, Class<T> classRef) {

        Object obj = this.dataMap.get(identifier.toString());

        if (obj == null) {
            return null;
        } else {
            return classRef.cast(obj);
        }
    }

    /**
     * @return Get the previous menu that was opened for the player
     */
    public Menu lastMenu() {
        return this.history.pop();
    }

    public void pushMenu(Menu menu) {
        this.history.push(menu);
    }
}
