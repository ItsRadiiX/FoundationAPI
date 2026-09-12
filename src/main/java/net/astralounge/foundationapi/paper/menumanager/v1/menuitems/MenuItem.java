package net.astralounge.foundationapi.paper.menumanager.v1.menuitems;

import net.astralounge.foundationapi.common.localisation.LocalisationService;
import net.astralounge.foundationapi.common.localisation.TextCreator;
import net.astralounge.foundationapi.paper.itemmanager.ItemFactory;
import net.astralounge.foundationapi.paper.itemmanager.ItemStackCreator;
import net.astralounge.foundationapi.paper.menumanager.v1.Menu;
import net.astralounge.foundationapi.paper.menumanager.v1.MenuManager;
import net.astralounge.foundationapi.paper.plugin.FoundationPaperPlugin;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public abstract class MenuItem {

    protected final FoundationPaperPlugin<?,?> plugin;

    private ItemStack item;

    protected MenuItem(FoundationPaperPlugin<?,?> plugin) {
        this.plugin = plugin;
    }

    public abstract @NotNull ItemStackCreator defineItemStack();

    public abstract void onClick(InventoryClickEvent e, Menu menu);

    public @NotNull ItemStack getItemStack() {
        if (item == null) {
            ItemStackCreator creator = defineItemStack();
            creator.addPersistentData(
                    getMenuManager().getNamespacedKey(),
                    PersistentDataType.STRING,
                    this.getClass().getSimpleName());
            item = creator.result();
        }
        return item;
    }

    public TextCreator getTextCreator() {
        return plugin.getMessagesManager().getTextCreator();
    }

    public MenuManager<?> getMenuManager() {
        return plugin.getMenuManager();
    }

    public ItemFactory getItemFactory() {
        return plugin.getItemManager().getItemFactory();
    }


    public static class Builder {
        private ItemStackCreator itemStackCreator;

        private BiFunction<InventoryClickEvent, Menu, Void> onClick = (e, m) -> null;

        public Builder setItemStackCreator(ItemStackCreator itemStackCreator) {
            this.itemStackCreator = itemStackCreator;
            return this;
        }

        public Builder setOnClick(BiFunction<InventoryClickEvent, Menu, Void> onClick) {
            this.onClick = onClick;
            return this;
        }

        public MenuItem build(FoundationPaperPlugin<?,?> plugin) {
            return new MenuItem(plugin) {
                @Override
                public @NotNull ItemStackCreator defineItemStack() {
                    if (itemStackCreator == null) {
                        itemStackCreator = new ItemStackCreator(
                                LocalisationService.getInstance().getTextCreator(),
                                Material.DIRT);
                    }
                    return itemStackCreator;
                }

                @Override
                public void onClick(InventoryClickEvent e, Menu menu) {
                    onClick.apply(e, menu);
                }
            };
        }
    }

}
