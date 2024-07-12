package com.itsradiix.foundationapi.paper.menumanager.menuitems;

import com.itsradiix.foundationapi.paper.itemmanager.ItemStackCreator;
import com.itsradiix.foundationapi.paper.menumanager.MenuManager;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public abstract class MenuItem {

    private ItemStack item;

    public MenuItem() {}

    public abstract @NotNull ItemStackCreator makeItemStack();

    public abstract void onClick(InventoryClickEvent e);

    public @NotNull ItemStack getItemStack() {
        if (item == null) {
            ItemStackCreator creator = makeItemStack();
            creator.addPersistentData(
                    MenuManager.getNamespacedKey(),
                    PersistentDataType.STRING,
                    this.getClass().getSimpleName());
            item = creator.result();
        }
        return item;
    }

}
