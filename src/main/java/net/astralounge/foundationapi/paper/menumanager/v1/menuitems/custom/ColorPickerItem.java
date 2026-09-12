package net.astralounge.foundationapi.paper.menumanager.v1.menuitems.custom;

import net.astralounge.foundationapi.paper.itemmanager.ItemStackCreator;
import net.astralounge.foundationapi.paper.menumanager.v1.Menu;
import net.astralounge.foundationapi.paper.plugin.FoundationPaperPlugin;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ColorPickerItem extends SkullMenuItem {

    private static String COLOR_PICKER_SKULL =
            "http://textures.minecraft.net/texture/c7ff1377754563ab41b8a0305dac03de63e02e5a39a6956afd6ccabf295a96d8";

    public ColorPickerItem(FoundationPaperPlugin<?,?> plugin) {
        super(plugin, COLOR_PICKER_SKULL);
    }

    @Override
    public void onClick(InventoryClickEvent e, Menu menu) {
        e.setCancelled(true);
    }


    @Override
    protected ItemStackCreator makeSkull(ItemStackCreator creator) {
        return creator.setName("<gold>Color Picker");
    }
}
