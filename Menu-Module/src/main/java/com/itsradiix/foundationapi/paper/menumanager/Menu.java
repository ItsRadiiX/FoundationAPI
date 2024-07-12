package com.itsradiix.foundationapi.paper.menumanager;

import com.itsradiix.foundationapi.paper.menumanager.animations.Animation;
import com.itsradiix.foundationapi.paper.menumanager.menuitems.MenuItem;
import com.itsradiix.foundationapi.paper.menumanager.templates.Template;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public abstract class Menu implements InventoryHolder {

    protected Inventory inventory;
    protected Player player;
    protected PlayerMenuUtility playerMenuUtility;

    public abstract Component getMenuName();
    public abstract int getSlots();

    public abstract Sound getOpeningSound();
    public abstract Sound getClosingSound();
    public abstract Sound getSwitchSound();

    public abstract List<Template> getTemplates();
    public abstract Map<Integer, MenuItem> getMenuItems();

    protected abstract void handleMenu(InventoryClickEvent e);
    protected abstract void afterOpenLogic();
    protected abstract void afterCloseLogic();

    protected abstract Animation getOpeningAnimation();

    public Menu(PlayerMenuUtility playerMenuUtility) {
        this.playerMenuUtility = playerMenuUtility;
        this.player = playerMenuUtility.getOwner();
    }

    public void open() {
        this.open(false, false);
    }

    public void switchOpen() {
        this.open(true, true);
    }

    public void switchOpen(boolean silent){
        this.open(silent, true);
    }

    //When called, an inventory is created and opened for the player
    public void open(boolean silent, boolean switchOpen) {
        //The owner of the inventory created is the Menu itself,
        // so we are able to reverse engineer the Menu object from the
        // inventoryHolder in the MenuListener class when handling clicks
        inventory = Bukkit.createInventory(this, getSlots(), getMenuName());

        if (silent) return;
        if (switchOpen){
            playSound(getSwitchSound());
        } else {
            playSound(getOpeningSound());
        }

        afterOpenLogic();
    }

    protected void setAllItems(){

        Map<Integer, MenuItem> items = getAllDisplayableItems();

        Animation animation = getOpeningAnimation();
        if (animation != null){
            BukkitRunnable runnable = animation.animation(getSlots(),
                    inventory ,
                    items);
            runnable.runTaskTimerAsynchronously(MenuManager.getPlugin(),
                    animation.getDelay(),
                    animation.getPeriod());
        } else {
            items.forEach(this::setItem);
        }
    }

    protected Map<Integer, MenuItem> getAllDisplayableItems(){
        Map<Integer, MenuItem> combinedMap = new HashMap<>();
        for (Template template : getTemplates()){
            combinedMap.putAll(template.getMenuItems());
        }

        combinedMap.putAll(getMenuItems());

        return combinedMap;
    }

    public void setItem(int slot, MenuItem menuItem) {
        if (slot > (getSlots()-1)) {
            inventory.setItem(slot, menuItem.getItemStack());
        }
    }

    public void close(){
        inventory.close();
    }

    public void closeLogic() {
        playerMenuUtility.pushMenu(this);
        playSound(getClosingSound());
        afterCloseLogic();
    }

    public void back(boolean switchOpen, boolean silent) {
        Class<? extends Menu> menuClass = playerMenuUtility.lastMenu().getClass();
        Player owner = playerMenuUtility.getOwner();

        MenuManager.openMenu(menuClass, owner, switchOpen, silent);
    }

    public void back() {
        this.back(false, false);
    }

    protected void reloadItems() {
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, null);
        }

    }

    protected void reload() {
        player.closeInventory();
        MenuManager.openMenu(this.getClass(), player);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    private void playSound(Sound sound){
        if (sound == null) return;
        player.playSound(sound);
    }
}
