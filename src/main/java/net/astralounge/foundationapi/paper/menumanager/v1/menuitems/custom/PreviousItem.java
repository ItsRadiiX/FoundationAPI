package net.astralounge.foundationapi.paper.menumanager.v1.menuitems.custom;

import net.astralounge.foundationapi.paper.itemmanager.ItemStackCreator;
import net.astralounge.foundationapi.paper.menumanager.v1.Menu;
import net.astralounge.foundationapi.paper.menumanager.v1.PaginatedMenu;
import net.astralounge.foundationapi.paper.plugin.FoundationPaperPlugin;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PreviousItem extends SkullMenuItem {

    private static String BACK_SKULL =
            "http://textures.minecraft.net/texture/69ea1d86247f4af351ed1866bca6a3040a06c68177c78e42316a1098e60fb7d3";

    public PreviousItem(FoundationPaperPlugin<?,?> plugin) {
        super(plugin, BACK_SKULL);
    }

    @Override
    public void onClick(InventoryClickEvent e, Menu menu) {
        if (menu instanceof PaginatedMenu page) {
            page.prevPage();
            Player player = (Player) e.getWhoClicked();
            player.playSound(Sound.sound(Key.key("ui.loom.select_pattern"), Sound.Source.UI, 0.5f, 1f));
        }
        e.setCancelled(true);
    }

    @Override
    protected ItemStackCreator makeSkull(ItemStackCreator creator) {
        return creator.setName("<gold>Previous Page");
    }
}
