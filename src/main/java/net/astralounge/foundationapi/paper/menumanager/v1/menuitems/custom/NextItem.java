package net.astralounge.foundationapi.paper.menumanager.v1.menuitems.custom;

import net.astralounge.foundationapi.paper.itemmanager.ItemStackCreator;
import net.astralounge.foundationapi.paper.menumanager.v1.Menu;
import net.astralounge.foundationapi.paper.menumanager.v1.PaginatedMenu;
import net.astralounge.foundationapi.paper.plugin.FoundationPaperPlugin;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class NextItem extends SkullMenuItem {

    private static String NEXT_SKULL =
            "https://textures.minecraft.net/texture/8271a47104495e357c3e8e80f511a9f102b0700ca9b88e88b795d33ff20105eb";

    public NextItem(FoundationPaperPlugin<?,?> plugin) {
        super(plugin, NEXT_SKULL);
    }

    @Override
    public void onClick(InventoryClickEvent e, Menu menu) {
        if (menu instanceof PaginatedMenu page) {
            page.nextPage();
            Player player = (Player) e.getWhoClicked();
            player.playSound(Sound.sound(Key.key("ui.loom.select_pattern"), Sound.Source.UI, 0.5f, 1f));
        }
        e.setCancelled(true);
    }

    @Override
    protected ItemStackCreator makeSkull(ItemStackCreator creator) {
        return creator.setName("<gold>Next Page");
    }
}
