package net.astralounge.foundationapi.paper.menumanager.v1;

import net.astralounge.foundationapi.common.manager.CommonManager;
import net.astralounge.foundationapi.paper.exceptions.MenuManagerException;
import net.astralounge.foundationapi.paper.manager.PaperManager;
import net.astralounge.foundationapi.paper.menumanager.v1.listeners.MenuListener;
import net.astralounge.foundationapi.paper.plugin.FoundationPaperPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MenuManager<T extends FoundationPaperPlugin<T, ?>> extends PaperManager<T> {

    private final NamespacedKey namespacedKey;
    private final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap;

    public MenuManager(T plugin) {
        super(plugin);
        this.namespacedKey = new NamespacedKey(plugin, "menu_item");
        this.playerMenuUtilityMap = new HashMap<>();
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        registerMenuListener(getPlugin());
    }

    @Override
    public void onDisable() {

    }

    @Override
    public Collection<Class<? extends CommonManager>> getCommonDependencies() {
        return List.of();
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

    public void switchToMenu(Class<? extends Menu> menuClass, Player player) {
        openMenu(menuClass, player, true);
    }

    public void openMenu(Class<? extends Menu> menuClass, Player player) {
        openMenu(menuClass, player, false);
    }


    public void openMenu(Class<? extends Menu> menuClass, Player player, boolean switchOpen) {

        try {
            menuClass.getConstructor(FoundationPaperPlugin.class, Player.class)
                    .newInstance(getPlugin(), player)
                    .open(switchOpen);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new MenuManagerException(e);
        }
    }

    public PlayerMenuUtility getPlayerMenuUtility(Player p) {

        PlayerMenuUtility playerMenuUtility;
        if (!(playerMenuUtilityMap.containsKey(p))) {

            playerMenuUtility = new PlayerMenuUtility(p);
            playerMenuUtilityMap.put(p, playerMenuUtility);

            return playerMenuUtility;
        } else {
            return playerMenuUtilityMap.get(p);
        }
    }

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }
}
