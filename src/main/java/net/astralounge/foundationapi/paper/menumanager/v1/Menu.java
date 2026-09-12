package net.astralounge.foundationapi.paper.menumanager.v1;

import net.astralounge.foundationapi.paper.menumanager.v1.menuitems.MenuItem;
import net.astralounge.foundationapi.paper.menumanager.v1.templates.Template;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public abstract class Menu extends BaseMenu {

    protected final MenuManager<?> menuManager;

    protected Inventory inventory;
    protected Player player;
    protected PlayerMenuUtility playerMenuUtility;

    protected boolean silentClose;

    public abstract Sound getOpeningSound();

    public abstract Sound getClosingSound();

    public abstract Sound getSwitchSound();

    public abstract Template getTemplate();

    protected abstract void afterOpenLogic();

    protected abstract void afterCloseLogic();

    public Menu(MenuManager<?> menuManager, Player player) {
        this.menuManager = menuManager;
        this.playerMenuUtility = menuManager.getPlayerMenuUtility(player);
        this.player = playerMenuUtility.getOwner();
    }

    public void open() {
        open(false);
    }

    public void switchOpen() {
        open(true);
    }

    public void open(boolean switchOpen) {
        inventory = Bukkit.createInventory(this, size(), title());

        if (switchOpen) {
            playSound(getSwitchSound());
        } else {
            playSound(getOpeningSound());
        }

        player.openInventory(inventory);

        setAllItems();
        afterOpenLogic();
    }

    protected void setAllItems() {
        getAllDisplayableItems().forEach(this::setItem);
    }

    protected Map<Integer, MenuItem> getAllDisplayableItems() {
        Map<Integer, MenuItem> combinedMap = new HashMap<>();

        if (getTemplate() != null){
            combinedMap.putAll(getTemplate().getMenuItems());
        }

        combinedMap.putAll(items());

        return combinedMap;
    }

    public @Nullable MenuItem getMenuItem(int slot) {
        MenuItem item = items().get(slot);

        if (item != null) {
            return item;
        }

        if (getTemplate() != null && getTemplate().getMenuItems() != null) {
            return getTemplate().getMenuItems().get(slot);
        }

        return null;
    }

    public void setItem(int slot, MenuItem menuItem) {
        if (menuItem == null) {
            inventory.setItem(slot, null);
            return;
        }

        setItem(slot, menuItem.getItemStack());
    }

    public void setItem(int slot, ItemStack itemStack) {
        if (slot < 0 || slot >= inventory.getSize()) {
            throw new IllegalArgumentException(
                    "Invalid inventory slot " + slot +
                            " for inventory size " + inventory.getSize()
            );
        }

        inventory.setItem(slot, itemStack);
    }

    /**
     * Closes the inventory.
     *
     * The actual close event is handled by InventoryCloseEvent.
     */
    public void close() {
        if (inventory != null) {
            inventory.close();
        }
    }

    public void close(boolean silentClose) {
        this.silentClose = silentClose;
        close();
    }

    public void silentClose() {
        close(true);
    }

    /**
     * Called by MenuListener when the inventory actually closes.
     */
    public void handleClose() {
        if (!silentClose) {
            playSound(getClosingSound());
        }
        afterCloseLogic();
    }

    /**
     * Opens the previous menu.
     */
    public void back() {
        back(false);
    }

    public void back(boolean switchOpen) {

        if (!playerMenuUtility.hasPreviousMenu()) {
            return;
        }

        Menu previousMenu = playerMenuUtility.popMenu();

        if (previousMenu == null) {
            return;
        }

        previousMenu.open(switchOpen);
    }

    protected void reloadAllItems() {
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, null);
        }
    }

    protected void reload() {
        player.closeInventory();
        menuManager.openMenu(getClass(), player);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    private void playSound(Sound sound) {
        if (sound != null) {
            player.playSound(sound);
        }
    }
}
