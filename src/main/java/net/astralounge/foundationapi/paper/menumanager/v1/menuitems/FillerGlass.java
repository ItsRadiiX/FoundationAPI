package net.astralounge.foundationapi.paper.menumanager.v1.menuitems;

import net.astralounge.foundationapi.paper.itemmanager.ItemStackCreator;
import net.astralounge.foundationapi.paper.menumanager.v1.Menu;
import net.astralounge.foundationapi.paper.plugin.FoundationPaperPlugin;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class FillerGlass extends MenuItem {

    public FillerGlass(FoundationPaperPlugin<?,?> plugin) {
        super(plugin);
    }

    @Override
    public @NotNull ItemStackCreator defineItemStack() {
        return getItemFactory().builder(Material.GRAY_STAINED_GLASS_PANE)
                .setName("<reset> ")
                .setLore("");
    }

    @Override
    public void onClick(InventoryClickEvent e, Menu menu) {
        e.setCancelled(true);
    }
}
