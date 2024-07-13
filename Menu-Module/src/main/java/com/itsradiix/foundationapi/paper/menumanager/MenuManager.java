package com.itsradiix.foundationapi.paper.menumanager;

import com.itsradiix.foundationapi.paper.exceptions.MenuManagerException;
import com.itsradiix.foundationapi.paper.exceptions.MenuManagerNotSetupException;
import com.itsradiix.foundationapi.paper.manager.PaperManager;
import com.itsradiix.foundationapi.paper.menumanager.listeners.MenuListener;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class MenuManager implements PaperManager {

    private static MenuManager instance;
    private static JavaPlugin plugin;
    private static NamespacedKey namespacedKey;

    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();

    public MenuManager(JavaPlugin plugin) {
        if (instance != null) throw new MenuManagerException("Another instance of MenuManager already exists.");
        instance = this;
        MenuManager.plugin = plugin;
        namespacedKey = new NamespacedKey(plugin, "menu_item");
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        registerMenuListener(plugin);
    }

    @Override
    public void onDisable() {

    }

    private void registerMenuListener(Plugin plugin) {
        boolean isAlreadyRegistered = false;
        for (RegisteredListener rl : InventoryClickEvent.getHandlerList().getRegisteredListeners()) {
            if (rl.getListener() instanceof MenuListener) {
                isAlreadyRegistered = true;
                break;
            }
        }
        if (!isAlreadyRegistered) {
            plugin.getServer().getPluginManager().registerEvents(new MenuListener(), plugin);
        }
    }

    public static void switchToMenu(Class<? extends Menu> menuClass, Player player, boolean silent){
        openMenu(menuClass, player, true, silent);
    }

    public static void switchToMenu(Class<? extends Menu> menuClass, Player player){
        openMenu(menuClass, player, true, false);
    }

    public static void openMenu(Class<? extends Menu> menuClass, Player player){
        openMenu(menuClass, player, false, false);
    }

    public static void openMenu(Class<? extends Menu> menuClass, Player player, boolean silent){
        openMenu(menuClass, player, false, silent);
    }


    public static void openMenu(Class<? extends Menu> menuClass, Player player, boolean switchOpen, boolean silent) {

        if (instance == null) throw new MenuManagerNotSetupException();

        try {
            menuClass.getConstructor(PlayerMenuUtility.class)
                    .newInstance(getPlayerMenuUtility(player))
                    .open(silent, switchOpen);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new MenuManagerException(e);
        }
    }

    public static PlayerMenuUtility getPlayerMenuUtility(Player p) {

        if (instance == null) throw new MenuManagerNotSetupException();

        PlayerMenuUtility playerMenuUtility;
        if (!(playerMenuUtilityMap.containsKey(p))) {

            playerMenuUtility = new PlayerMenuUtility(p);
            playerMenuUtilityMap.put(p, playerMenuUtility);

            return playerMenuUtility;
        } else {
            return playerMenuUtilityMap.get(p);
        }
    }

    public static MenuManager getInstance() {
        return instance;
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public static NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }
}
