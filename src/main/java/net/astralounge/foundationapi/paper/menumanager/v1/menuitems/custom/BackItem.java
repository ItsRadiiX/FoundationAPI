package net.astralounge.foundationapi.paper.menumanager.v1.menuitems.custom;

import net.astralounge.foundationapi.paper.itemmanager.ItemStackCreator;
import net.astralounge.foundationapi.paper.plugin.FoundationPaperPlugin;

public abstract class BackItem extends SkullMenuItem {

    private static String BACK_SKULL =
            "http://textures.minecraft.net/texture/69ea1d86247f4af351ed1866bca6a3040a06c68177c78e42316a1098e60fb7d3";

    public BackItem(FoundationPaperPlugin<?,?> plugin) {
        super(plugin, BACK_SKULL);
    }

    @Override
    protected ItemStackCreator makeSkull(ItemStackCreator creator) {
        return creator.setName("<gold>Click to go back!");
    }
}
