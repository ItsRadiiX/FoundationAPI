package net.astralounge.foundationapi.paper.menumanager.v1;

import net.astralounge.foundationapi.paper.menumanager.v1.menuitems.MenuItem;
import net.astralounge.foundationapi.paper.menumanager.v1.revealpatterns.MenuReveal;
import net.astralounge.foundationapi.paper.menumanager.v1.revealpatterns.RevealArea;
import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class BaseMenu implements InventoryHolder {

    public abstract Component title();

    public abstract int size();

    public abstract @NotNull Inventory getInventory();

    public abstract Map<Integer, MenuItem> items();

    protected abstract void customOnClick(InventoryClickEvent e);

    /**
     * Starting slot for reveal animations.
     * Override in concrete menus to customize.
     */
    public int revealStartSlot() {
        return 0; // default: top-left
    }

    /**
     * Reveal area for this menu.
     * Override in concrete menus to customize.
     */
    public RevealArea revealArea() {
        return RevealArea.SIX_BY_NINE; // sensible default for full 6x9 menus
    }

    protected BukkitRunnable createRevealAnimation(MenuReveal reveal) {
        return reveal.play(this, revealStartSlot(), revealArea());
    }

    public int getRows() {
        return size() / 9;
    }
}
